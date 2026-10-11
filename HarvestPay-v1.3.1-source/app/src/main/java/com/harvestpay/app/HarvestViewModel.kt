package com.harvestpay.app

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.BiometricLockTiming
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.DatabaseSnapshot
import com.harvestpay.app.data.DieselEntryEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.HarvestRepository
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.ReminderEntity
import com.harvestpay.app.data.SettingsRepository
import com.harvestpay.app.data.ThemePreference
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.data.WorkTypeEntity
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.billingMode
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.AccountArchiveCalculator
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.WorkSummary
import com.harvestpay.app.domain.ServiceCalculator
import com.harvestpay.app.domain.SuccessFeedback
import com.harvestpay.app.domain.reportCompletedSave
import com.harvestpay.app.notification.ReminderWorker
import com.harvestpay.app.security.LocalAuth
import com.harvestpay.app.util.BackupBundle
import com.harvestpay.app.util.BackupCodec
import com.harvestpay.app.util.formatMoney
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.Duration
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

data class AuthState(
    val resolved: Boolean = false,
    val authenticated: Boolean = false,
    val error: String? = null,
)

enum class BiometricSessionState { LOCKED, AUTHENTICATING, UNLOCKED }

private data class LedgerSource(
    val customers: List<CustomerEntity>,
    val fields: List<FieldEntity>,
    val workEntries: List<WorkEntryEntity>,
    val payments: List<PaymentEntity>,
    val workTypes: List<WorkTypeEntity>,
)

class HarvestViewModel(
    application: Application,
    private val repository: HarvestRepository,
    private val settingsRepository: SettingsRepository,
) : AndroidViewModel(application) {
    private val _auth = MutableStateFlow(AuthState())
    val auth: StateFlow<AuthState> = _auth
    private val _biometricSession = MutableStateFlow(BiometricSessionState.LOCKED)
    val biometricSession: StateFlow<BiometricSessionState> = _biometricSession
    private val _biometricAutoPrompt = MutableStateFlow(false)
    val biometricAutoPrompt: StateFlow<Boolean> = _biometricAutoPrompt
    private val _showHoursUpdate = MutableStateFlow(false)
    val showHoursUpdate: StateFlow<Boolean> = _showHoursUpdate
    private val _pendingArchiveYear = MutableStateFlow<Int?>(null)
    val pendingArchiveYear: StateFlow<Int?> = _pendingArchiveYear
    private var hoursPromptHandledThisSession = false
    private var archivePromptHandledThisSession = false

    val settings = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppSettings(),
    )

    private val ledgerSource = combine(
        repository.customers,
        repository.fields,
        repository.workEntries,
        repository.payments,
        repository.workTypes,
    ) { customers, fields, workEntries, payments, workTypes ->
        LedgerSource(customers, fields, workEntries, payments, workTypes)
    }

    private val ledgerRefresh = MutableStateFlow(0)
    private val calendarDay = MutableStateFlow(LocalDate.now().toEpochDay())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = ledgerRefresh.flatMapLatest {
        combine(
            ledgerSource,
            repository.dieselEntries,
            repository.yearArchives,
            repository.customerYearArchives,
            calendarDay,
        ) { source, dieselEntries, yearArchives, customerYearArchives, _ ->
            BusinessCalculator.buildUiState(
                source.customers,
                source.fields,
                source.workEntries,
                source.payments,
                dieselEntries,
                yearArchives,
                customerYearArchives,
            ).copy(workTypes = source.workTypes)
        }.catch {
            emit(HarvestUiState(loading = false, error = AppText.text(R.string.text_could_not_load_your_accounts_please_try_again)))
        }
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        HarvestUiState(),
    )

    val reminders = repository.reminders.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

    private val messagesChannel = Channel<String>(Channel.BUFFERED)
    val messages = messagesChannel.receiveAsFlow()
    private val successChannel = Channel<SuccessFeedback>(Channel.BUFFERED)
    val successes = successChannel.receiveAsFlow()
    private var backgroundAt: Long? = null

    init {
        // A single wake-up at local midnight keeps Received Today correct without polling Room.
        viewModelScope.launch {
            while (isActive) {
                val now = ZonedDateTime.now()
                val nextDay = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
                delay(Duration.between(now, nextDay).toMillis().coerceAtLeast(1L))
                calendarDay.value = LocalDate.now().toEpochDay()
            }
        }
        viewModelScope.launch {
            val initial = settingsRepository.settings.first()
            _biometricSession.value = if (initial.biometricLockEnabled) BiometricSessionState.LOCKED else BiometricSessionState.UNLOCKED
            _biometricAutoPrompt.value = initial.biometricLockEnabled
            _auth.value = AuthState(
                resolved = true,
                authenticated = initial.rememberedAuthenticated,
            )
            repository.ensureDefaultWorkTypes(initial.defaultRate)
            if (initial.rememberedAuthenticated) checkHoursUpdatePrompt(initial)
        }
        viewModelScope.launch {
            combine(auth, uiState) { authState, state -> authState to state }.collect { (authState, state) ->
                if (authState.authenticated && !state.loading && !archivePromptHandledThisSession) {
                    AccountArchiveCalculator.pendingYear(
                        LocalDate.now().year,
                        state.workEntries,
                        state.payments,
                        state.yearArchives,
                    )?.let {
                        archivePromptHandledThisSession = true
                        _pendingArchiveYear.value = it
                    }
                }
            }
        }
    }

    fun login(mobile: String, password: String, remember: Boolean) {
        viewModelScope.launch {
            val credential = settingsRepository.passwordCredential()
            val valid = withContext(Dispatchers.Default) {
                LocalAuth.verify(mobile, password.toCharArray(), credential)
            }
            if (valid) {
                if (LocalAuth.needsFastHashMigration(credential)) {
                    settingsRepository.setPasswordCredential(
                        LocalAuth.createCredential(password.toCharArray()),
                    )
                }
                settingsRepository.setAuthentication(remember, authenticated = true)
                _auth.value = AuthState(resolved = true, authenticated = true)
                archivePromptHandledThisSession = false
                checkHoursUpdatePrompt(settingsRepository.settings.first())
            } else {
                _auth.value = AuthState(
                    resolved = true,
                    authenticated = false,
                    error = AppText.text(R.string.text_invalid_mobile_number_or_password),
                )
            }
        }
    }

    fun clearLoginError() {
        _auth.value = _auth.value.copy(error = null)
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmation: String,
        onChanged: () -> Unit = {},
    ) {
        LocalAuth.passwordChangeError(currentPassword, newPassword, confirmation)?.let {
            postMessage(AppText.label(it))
            return
        }
        viewModelScope.launch {
            val currentCredential = settingsRepository.passwordCredential()
            val currentIsValid = withContext(Dispatchers.Default) {
                LocalAuth.verifyPassword(currentPassword.toCharArray(), currentCredential)
            }
            if (!currentIsValid) {
                messagesChannel.send(AppText.text(R.string.text_current_password_is_incorrect))
                return@launch
            }
            runCatching {
                val newCredential = withContext(Dispatchers.Default) {
                    LocalAuth.createCredential(newPassword.toCharArray())
                }
                settingsRepository.setPasswordCredential(newCredential)
            }.onSuccess {
                messagesChannel.send(AppText.text(R.string.text_password_changed_successfully))
                onChanged()
            }.onFailure {
                messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_could_not_change_password)))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            settingsRepository.clearAuthentication()
            _auth.value = AuthState(resolved = true, authenticated = false)
            _pendingArchiveYear.value = null
            archivePromptHandledThisSession = false
        }
    }

    fun onBackground() {
        backgroundAt = SystemClock.elapsedRealtime()
        if (settings.value.biometricLockEnabled && settings.value.biometricLockTiming == BiometricLockTiming.IMMEDIATE &&
            _biometricSession.value == BiometricSessionState.UNLOCKED) {
            lockBiometric()
        }
    }

    fun onForeground() {
        calendarDay.value = LocalDate.now().toEpochDay()
        val since = backgroundAt ?: return
        backgroundAt = null
        val elapsed = (SystemClock.elapsedRealtime() - since).coerceAtLeast(0L)
        if (settings.value.biometricLockEnabled && elapsed >= settings.value.biometricLockTiming.millis &&
            _biometricSession.value == BiometricSessionState.UNLOCKED) {
            lockBiometric()
        }
        val lockMinutes = settings.value.autoLockMinutes
        if (lockMinutes <= 0) return
        val timeout = lockMinutes * 60_000L
        if (_auth.value.authenticated && elapsed >= timeout) {
            viewModelScope.launch {
                settingsRepository.setAuthentication(settings.value.rememberLogin, authenticated = false)
                _auth.value = AuthState(resolved = true, authenticated = false)
                messagesChannel.send(AppText.text(R.string.text_harvest_pay_locked_after_inactivity))
            }
        }
    }

    private fun lockBiometric() {
        _biometricSession.value = BiometricSessionState.LOCKED
        _biometricAutoPrompt.value = true
    }

    fun beginBiometricAuthentication(): Boolean {
        if (_biometricSession.value != BiometricSessionState.LOCKED) return false
        _biometricAutoPrompt.value = false
        _biometricSession.value = BiometricSessionState.AUTHENTICATING
        return true
    }

    fun biometricAuthenticated() { _biometricSession.value = BiometricSessionState.UNLOCKED }
    fun biometricAuthenticationStopped() { _biometricSession.value = BiometricSessionState.LOCKED }

    fun setBiometricLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBiometricLockEnabled(enabled)
            _biometricAutoPrompt.value = false
            _biometricSession.value = BiometricSessionState.UNLOCKED
        }
    }

    fun setBiometricLockTiming(timing: BiometricLockTiming) = action(null) {
        settingsRepository.setBiometricLockTiming(timing)
    }

    fun saveCustomer(customer: CustomerEntity, onSaved: (Long) -> Unit = {}) {
        saveWithFeedback(
            SuccessFeedback(if (customer.id == 0L) AppText.text(R.string.text_customer_added_successfully) else AppText.text(R.string.text_customer_updated_successfully)),
            AppText.text(R.string.text_could_not_save_customer), onSaved,
        ) {
            val mobile = LocalAuth.normalizeMobile(customer.mobile)
            require(customer.name.isNotBlank()) { AppText.text(R.string.text_customer_name_is_required) }
            require(customer.previousDue >= 0) { AppText.text(R.string.text_previous_due_cannot_be_negative) }
            require(mobile.isBlank() || LocalAuth.isValidIndianMobile(mobile)) { AppText.text(R.string.text_enter_a_valid_indian_mobile_number_or_leave_it_empty) }
            require(mobile.isBlank() || !repository.mobileExists(mobile, customer.id)) { AppText.text(R.string.text_a_customer_with_this_mobile_number_already_exists) }
            repository.saveCustomer(customer.copy(
                name = customer.name.trim(), nickname = customer.nickname.trim(), mobile = mobile,
                previousDue = BusinessCalculator.money(customer.previousDue),
            ))
        }
    }

    fun deleteCustomer(customer: CustomerEntity, password: String, onResult: (Boolean) -> Unit = {}) {
        if (password.isBlank()) { postMessage(AppText.text(R.string.text_enter_the_admin_password)); onResult(false); return }
        viewModelScope.launch {
            val valid = withContext(Dispatchers.Default) { LocalAuth.verifyPassword(password.toCharArray(), settingsRepository.passwordCredential()) }
            if (!valid) { messagesChannel.send(AppText.text(R.string.text_incorrect_admin_password_customer_was_not_deleted)); onResult(false); return@launch }
            runCatching { repository.deleteCustomer(customer) }
                .onSuccess { messagesChannel.send(AppText.text(R.string.text_customer_deleted)); onResult(true) }
                .onFailure { messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_could_not_delete_customer))); onResult(false) }
        }
    }

    fun saveField(field: FieldEntity, onSaved: (Long) -> Unit = {}) {
        if (field.fieldName.isBlank() || !field.sizeBigha.isFinite() || field.sizeBigha <= 0) {
            postMessage(AppText.text(R.string.text_enter_a_field_name_and_a_size_greater_than_zero))
            return
        }
        saveWithFeedback(
            SuccessFeedback(if (field.id == 0L) AppText.text(R.string.text_field_added_successfully) else AppText.text(R.string.text_field_updated_successfully)),
            AppText.text(R.string.text_could_not_save_field), onSaved,
        ) { repository.saveField(field.copy(fieldName = field.fieldName.trim())) }
    }

    fun deleteField(field: FieldEntity) = action(AppText.text(R.string.text_field_deleted)) { repository.deleteField(field) }

    fun saveWork(
        work: WorkEntryEntity,
        initialPaid: Double,
        paymentMethod: String,
        onSaved: (Long) -> Unit = {},
    ) {
        val invalidBillingValues = when (work.billingMode) {
            BillingType.PER_BIGHA -> work.sizeBigha <= 0 || !BusinessCalculator.isSupportedRound(work.roundMultiplier)
            BillingType.PER_MINUTE -> work.durationMinutes <= 0
        }
        if (
            invalidBillingValues || work.ratePerBigha < 0 ||
            work.totalAmount < 0 || initialPaid < 0
        ) {
            postMessage(if (work.billingMode == BillingType.PER_MINUTE) AppText.text(R.string.text_enter_a_valid_duration_rate_and_amount) else AppText.text(R.string.text_enter_valid_work_values_and_choose_round_0_5_1_1_5_or_2))
            return
        }
        saveWithFeedback(
            SuccessFeedback(if (work.id == 0L) AppText.text(R.string.text_work_added_successfully) else AppText.text(R.string.text_work_updated_successfully)),
            AppText.text(R.string.text_could_not_save_work), onSaved,
        ) {
            val payment = initialPaid.takeIf { it > 0 }?.let {
                PaymentEntity(
                    customerId = work.customerId,
                    workEntryId = null,
                    paymentDate = work.workDate,
                    amount = it,
                    paymentMethod = paymentMethod,
                    notes = AppText.text(R.string.text_payment_recorded_with_work_entry),
                )
            }
            repository.saveWorkWithPayment(work, payment)
        }
    }

    fun deleteWork(work: WorkEntryEntity) = action(AppText.text(R.string.text_work_entry_deleted)) { repository.deleteWorkEntry(work) }

    fun addPayment(
        work: WorkSummary,
        amount: Double,
        date: Long,
        method: String,
        notes: String,
        onSaved: () -> Unit = {},
    ) {
        if (amount <= 0) {
            postMessage(AppText.text(R.string.text_payment_amount_must_be_greater_than_zero))
            return
        }
        saveWithFeedback(SuccessFeedback(AppText.text(R.string.text_payment_recorded_successfully)), AppText.text(R.string.text_could_not_record_payment), { _: Long -> onSaved() }) {
            repository.savePayment(
                PaymentEntity(
                    customerId = work.work.customerId,
                    workEntryId = work.work.id,
                    paymentDate = date,
                    amount = BusinessCalculator.money(amount),
                    paymentMethod = method,
                    notes = notes.trim(),
                ),
            )
        }
    }

    fun addCustomerPayment(
        customerId: Long,
        amount: Double,
        date: Long,
        method: String,
        notes: String,
        onSaved: () -> Unit = {},
    ) {
        if (amount <= 0) {
            postMessage(AppText.text(R.string.text_payment_amount_must_be_greater_than_zero))
            return
        }
        val customer = uiState.value.customerSummaries.firstOrNull { it.customer.id == customerId }
        if (customer == null) {
            postMessage(AppText.text(R.string.text_choose_a_customer_first))
            return
        }
        val projectedAdvance = BusinessCalculator.money((customer.advance + amount - customer.pending).coerceAtLeast(0.0))
        saveWithFeedback(
            SuccessFeedback(AppText.text(R.string.text_payment_recorded_successfully), if (projectedAdvance > 0.005) AppText.text(R.string.text_1_s_is_now_advance_credit, formatMoney(projectedAdvance)) else null),
            AppText.text(R.string.text_could_not_record_payment), { _: Long -> onSaved() },
        ) {
            repository.savePayment(
                PaymentEntity(
                    customerId = customerId,
                    workEntryId = null,
                    paymentDate = date,
                    amount = BusinessCalculator.money(amount),
                    paymentMethod = method,
                    notes = notes.trim().ifBlank { AppText.text(R.string.text_customer_account_payment) },
                ),
            )
        }
    }

    fun markWorkPaid(work: WorkSummary, method: String = "Cash") {
        if (work.pending <= 0.005) return
        addPayment(work, work.pending, LocalDate.now().toEpochDay(), method, AppText.text(R.string.text_balance_marked_paid))
    }

    fun markCustomerPaid(customerId: Long, method: String = "Cash") {
        val summary = uiState.value.customerSummaries.firstOrNull { it.customer.id == customerId } ?: return
        if (summary.pending <= 0.005) return
        saveWithFeedback(SuccessFeedback(AppText.text(R.string.text_payment_recorded_successfully), AppText.text(R.string.text_complete_balance_marked_as_paid)), AppText.text(R.string.text_could_not_record_payment)) {
            repository.savePayment(
                PaymentEntity(
                    customerId = customerId,
                    workEntryId = null,
                    paymentDate = LocalDate.now().toEpochDay(),
                    amount = summary.pending,
                    paymentMethod = method,
                    notes = AppText.text(R.string.text_customer_balance_marked_paid),
                ),
            )
        }
    }

    fun saveWorkType(workType: WorkTypeEntity) {
        val name = workType.name.trim()
        if (name.isBlank() || workType.ratePerBigha < 0) {
            postMessage(AppText.text(R.string.text_enter_a_work_type_name_and_a_non_negative_rate))
            return
        }
        viewModelScope.launch {
            when {
                repository.workTypeNameExists(name, workType.id) ->
                    messagesChannel.send(AppText.text(R.string.text_a_work_type_with_this_name_already_exists))
                else -> runCatching {
                    repository.saveWorkType(workType.copy(name = name, hindiName = workType.hindiName.trim()))
                }.onSuccess {
                    messagesChannel.send(AppText.text(R.string.text_work_type_saved))
                }.onFailure {
                    messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_could_not_save_work_type)))
                }
            }
        }
    }

    fun deleteWorkType(workType: WorkTypeEntity) {
        if (uiState.value.workTypes.size <= 1) {
            postMessage(AppText.text(R.string.text_keep_at_least_one_work_type_for_new_work_entries))
            return
        }
        action(AppText.text(R.string.text_work_type_deleted)) { repository.deleteWorkType(workType) }
    }

    fun saveDieselEntry(entry: DieselEntryEntity, onSaved: () -> Unit = {}) {
        val cleanStatus = entry.paymentStatus.trim().replaceFirstChar { it.uppercase() }
        val cleanEntry = entry.copy(
            litres = BusinessCalculator.money(entry.litres),
            totalAmount = BusinessCalculator.money(entry.totalAmount),
            paymentStatus = cleanStatus,
            broughtBy = entry.broughtBy.trim(),
        )
        when {
            cleanEntry.litres <= 0 -> postMessage(AppText.text(R.string.text_diesel_quantity_must_be_greater_than_zero))
            cleanEntry.totalAmount <= 0 -> postMessage(AppText.text(R.string.text_diesel_amount_must_be_greater_than_zero))
            cleanEntry.broughtBy.isBlank() -> postMessage(AppText.text(R.string.text_enter_who_brought_the_diesel))
            cleanStatus !in setOf("Paid", "Unpaid") -> postMessage(AppText.text(R.string.text_choose_paid_or_unpaid_status))
            else -> viewModelScope.launch {
                when {
                    repository.dieselEntryExists(cleanEntry) ->
                        messagesChannel.send(AppText.text(R.string.text_this_diesel_entry_already_exists))
                    else -> runCatching { repository.saveDieselEntry(cleanEntry) }
                        .onSuccess {
                            messagesChannel.send(AppText.text(R.string.text_diesel_entry_saved))
                            onSaved()
                        }
                        .onFailure { messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_could_not_save_diesel_entry))) }
                }
            }
        }
    }

    fun deleteDieselEntry(entry: DieselEntryEntity) =
        action(AppText.text(R.string.text_diesel_entry_deleted)) { repository.deleteDieselEntry(entry) }

    fun deletePayment(payment: PaymentEntity) = action(AppText.text(R.string.text_payment_deleted)) { repository.deletePayment(payment) }

    fun createReminder(customerId: Long, triggerAt: Long, message: String, onSaved: () -> Unit = {}) {
        if (triggerAt <= System.currentTimeMillis()) {
            postMessage(AppText.text(R.string.text_choose_a_reminder_time_in_the_future))
            return
        }
        val customer = uiState.value.customers.firstOrNull { it.id == customerId } ?: return
        viewModelScope.launch {
            runCatching {
                repository.saveReminder(ReminderEntity(customerId = customerId, reminderAt = triggerAt, message = message))
            }.onSuccess { id ->
                ReminderWorker.schedule(getApplication(), id, customer.name, message, triggerAt)
                messagesChannel.send(AppText.text(R.string.text_payment_reminder_scheduled))
                onSaved()
            }.onFailure { messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_could_not_schedule_reminder))) }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) = action(AppText.text(R.string.text_reminder_deleted)) {
        ReminderWorker.cancel(getApplication(), reminder.id)
        repository.deleteReminder(reminder)
    }

    fun setTheme(theme: ThemePreference) = action(null) { settingsRepository.setTheme(theme) }

    fun saveSettings(value: AppSettings) = action(AppText.text(R.string.text_settings_saved)) {
        require(value.defaultRate >= 0) { AppText.text(R.string.text_default_rate_cannot_be_negative) }
        require(value.autoLockMinutes >= 0) { AppText.text(R.string.text_auto_lock_value_is_invalid) }
        settingsRepository.saveBusinessSettings(value)
    }

    fun saveServiceSettings(value: AppSettings) {
        val normalized = value.copy(
            currentTractorHours = BusinessCalculator.money(value.currentTractorHours),
            serviceIntervalHours = BusinessCalculator.money(value.serviceIntervalHours),
            lastServiceHours = BusinessCalculator.money(value.lastServiceHours),
            lastHoursUpdatedAt = if (value.serviceReminderEnabled && value.lastHoursUpdatedAt <= 0L) System.currentTimeMillis() else value.lastHoursUpdatedAt,
        )
        when {
            normalized.currentTractorHours < 0 -> postMessage(AppText.text(R.string.text_current_tractor_hours_cannot_be_negative))
            normalized.serviceReminderEnabled && normalized.serviceIntervalHours <= 0 -> postMessage(AppText.text(R.string.text_service_interval_must_be_greater_than_zero))
            normalized.lastServiceHours < 0 -> postMessage(AppText.text(R.string.text_last_service_hours_cannot_be_negative))
            normalized.lastServiceHours > normalized.currentTractorHours -> postMessage(AppText.text(R.string.text_last_service_hours_cannot_exceed_current_tractor_hours))
            else -> action(AppText.text(R.string.text_service_reminder_settings_saved)) { settingsRepository.saveServiceSettings(normalized) }
        }
    }

    fun updateCurrentHours(hours: Double) {
        val current = settings.value
        if (hours < current.currentTractorHours) { postMessage(AppText.text(R.string.text_current_hours_cannot_be_lower_than_the_previous_reading)); return }
        action(AppText.text(R.string.text_current_tractor_hours_updated)) {
            settingsRepository.saveServiceSettings(current.copy(currentTractorHours = BusinessCalculator.money(hours), lastHoursUpdatedAt = System.currentTimeMillis()))
            settingsRepository.clearHoursReminderDeferral()
            _showHoursUpdate.value = false
        }
    }
    fun dismissHoursUpdate() {
        _showHoursUpdate.value = false
        action(null) { settingsRepository.deferHoursReminder(System.currentTimeMillis()) }
    }
    fun markServiceCompleted(hours: Double) {
        val current = settings.value
        if (hours < current.lastServiceHours || hours > current.currentTractorHours) { postMessage(AppText.text(R.string.text_service_hours_must_be_between_the_previous_service_and)); return }
        val record = "${BusinessCalculator.money(hours)},${System.currentTimeMillis()}"
        val history = listOf(current.serviceHistory, record).filter(String::isNotBlank).joinToString(";")
        action(AppText.text(R.string.text_service_marked_completed)) { settingsRepository.saveServiceSettings(current.copy(lastServiceHours = BusinessCalculator.money(hours), serviceHistory = history)) }
    }

    fun backupJson(): String = BackupCodec.encode(
        BackupBundle(
            DatabaseSnapshot(
                customers = uiState.value.customers,
                fields = uiState.value.fields,
                workEntries = uiState.value.workEntries,
                payments = uiState.value.payments,
                reminders = reminders.value,
                workTypes = uiState.value.workTypes,
                dieselEntries = uiState.value.dieselEntries,
                yearArchives = uiState.value.yearArchives,
                customerYearArchives = uiState.value.customerYearArchives,
            ),
            settings.value,
        ),
    )

    fun customersCsv(): String = BackupCodec.customersCsv(uiState.value.customers)

    fun paymentsCsv(): String = BackupCodec.paymentsCsv(uiState.value.payments, uiState.value.customers)

    fun dismissArchivePrompt() {
        _pendingArchiveYear.value = null
    }

    fun backupAndArchivePendingYear() {
        val year = _pendingArchiveYear.value ?: return
        viewModelScope.launch {
            val state = uiState.value
            val archivedAt = System.currentTimeMillis()
            runCatching {
                val backupDirectory = File(getApplication<Application>().filesDir, "yearly_backups").apply { mkdirs() }
                File(backupDirectory, "HarvestPay-before-$year-archive-$archivedAt.json").writeText(backupJson())
                val rows = withContext(Dispatchers.Default) {
                    AccountArchiveCalculator.buildRows(
                        year,
                        archivedAt,
                        state.customers,
                        state.workEntries,
                        state.payments,
                        state.customerYearArchives,
                    )
                }
                repository.archiveYear(year, archivedAt, rows)
            }.onSuccess { archived ->
                _pendingArchiveYear.value = null
                messagesChannel.send(if (archived) AppText.text(R.string.text_1_s_account_archived_safely, year) else AppText.text(R.string.text_1_s_was_already_archived, year))
            }.onFailure {
                messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_could_not_archive_1_s_no_account_changes_were_made, year)))
            }
        }
    }

    fun restoreBackup(text: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching { BackupCodec.decode(text) }
                .onSuccess { bundle ->
                    runCatching {
                        repository.restore(bundle.snapshot)
                        settingsRepository.restoreSettings(bundle.settings)
                        repository.ensureDefaultWorkTypes(bundle.settings.defaultRate)
                    }.onSuccess {
                        messagesChannel.send(AppText.text(R.string.text_backup_restored_successfully))
                        onDone()
                    }.onFailure { messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_could_not_restore_backup))) }
                }
                .onFailure { messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_invalid_backup_file))) }
        }
    }

    fun retryLedger() { ledgerRefresh.value += 1 }

    private fun <T> saveWithFeedback(
        feedback: SuccessFeedback,
        failureMessage: String,
        onSaved: (T) -> Unit = {},
        save: suspend () -> T,
    ) {
        viewModelScope.launch {
            reportCompletedSave(
                save = save,
                onFailure = { error ->
                    messagesChannel.send(if (error is IllegalArgumentException) error.message ?: failureMessage else failureMessage)
                },
                onSaved = { result ->
                    // Room's write (including the work/payment transaction) has completed.
                    successChannel.send(feedback)
                    onSaved(result)
                },
            )
        }
    }

    private fun action(success: String?, block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess { success?.let { messagesChannel.send(it) } }
                .onFailure { messagesChannel.send(AppText.label(it.message ?: AppText.text(R.string.text_something_went_wrong))) }
        }
    }

    private fun postMessage(message: String) {
        viewModelScope.launch { messagesChannel.send(message) }
    }

    private fun checkHoursUpdatePrompt(value: AppSettings) {
        val deferredAt = value.lastHoursReminderDeferredAt
        val cooldownExpired = deferredAt <= 0L || System.currentTimeMillis() - deferredAt >= 12 * 60 * 60 * 1000L
        if (!hoursPromptHandledThisSession && cooldownExpired && ServiceCalculator.hoursUpdateDue(value)) {
            hoursPromptHandledThisSession = true
            _showHoursUpdate.value = true
        }
    }

    class Factory(
        private val application: HarvestPayApplication,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HarvestViewModel(
            application,
            application.repository,
            application.settingsRepository,
        ) as T
    }
}
