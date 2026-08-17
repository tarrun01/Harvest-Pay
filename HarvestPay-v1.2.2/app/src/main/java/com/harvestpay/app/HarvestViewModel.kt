package com.harvestpay.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.harvestpay.app.data.AppSettings
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
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.WorkSummary
import com.harvestpay.app.notification.ReminderWorker
import com.harvestpay.app.security.LocalAuth
import com.harvestpay.app.util.BackupBundle
import com.harvestpay.app.util.BackupCodec
import com.harvestpay.app.util.formatMoney
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AuthState(
    val resolved: Boolean = false,
    val authenticated: Boolean = false,
    val error: String? = null,
)

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

    val uiState = combine(ledgerSource, repository.dieselEntries) { source, dieselEntries ->
        BusinessCalculator.buildUiState(
            source.customers,
            source.fields,
            source.workEntries,
            source.payments,
            dieselEntries,
        ).copy(workTypes = source.workTypes)
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
    private var backgroundAt: Long? = null

    init {
        viewModelScope.launch {
            val initial = settingsRepository.settings.first()
            _auth.value = AuthState(
                resolved = true,
                authenticated = initial.rememberedAuthenticated,
            )
            repository.ensureDefaultWorkTypes(initial.defaultRate)
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
            } else {
                _auth.value = AuthState(
                    resolved = true,
                    authenticated = false,
                    error = "Invalid mobile number or password.",
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
            postMessage(it)
            return
        }
        viewModelScope.launch {
            val currentCredential = settingsRepository.passwordCredential()
            val currentIsValid = withContext(Dispatchers.Default) {
                LocalAuth.verifyPassword(currentPassword.toCharArray(), currentCredential)
            }
            if (!currentIsValid) {
                messagesChannel.send("Current password is incorrect.")
                return@launch
            }
            runCatching {
                val newCredential = withContext(Dispatchers.Default) {
                    LocalAuth.createCredential(newPassword.toCharArray())
                }
                settingsRepository.setPasswordCredential(newCredential)
            }.onSuccess {
                messagesChannel.send("Password changed successfully.")
                onChanged()
            }.onFailure {
                messagesChannel.send(it.message ?: "Could not change password.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            settingsRepository.clearAuthentication()
            _auth.value = AuthState(resolved = true, authenticated = false)
        }
    }

    fun onBackground() {
        if (_auth.value.authenticated) backgroundAt = System.currentTimeMillis()
    }

    fun onForeground() {
        val since = backgroundAt ?: return
        backgroundAt = null
        val lockMinutes = settings.value.autoLockMinutes
        if (lockMinutes <= 0) return
        val timeout = lockMinutes * 60_000L
        if (_auth.value.authenticated && System.currentTimeMillis() - since >= timeout) {
            viewModelScope.launch {
                settingsRepository.setAuthentication(settings.value.rememberLogin, authenticated = false)
                _auth.value = AuthState(resolved = true, authenticated = false)
                messagesChannel.send("Harvest Pay locked after inactivity.")
            }
        }
    }

    fun saveCustomer(customer: CustomerEntity, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val mobile = LocalAuth.normalizeMobile(customer.mobile)
            when {
                customer.name.isBlank() -> messagesChannel.send("Customer name is required.")
                customer.previousDue < 0 -> messagesChannel.send("Previous due cannot be negative.")
                !LocalAuth.isValidIndianMobile(mobile) -> messagesChannel.send("Enter a valid Indian mobile number.")
                repository.mobileExists(mobile, customer.id) -> messagesChannel.send("A customer with this mobile number already exists.")
                else -> runCatching {
                    repository.saveCustomer(
                        customer.copy(
                            name = customer.name.trim(),
                            mobile = mobile,
                            previousDue = BusinessCalculator.money(customer.previousDue),
                        ),
                    )
                }
                    .onSuccess { id -> messagesChannel.send("Customer saved."); onSaved(id) }
                    .onFailure { messagesChannel.send(it.message ?: "Could not save customer.") }
            }
        }
    }

    fun deleteCustomer(customer: CustomerEntity, onDone: () -> Unit = {}) = action("Customer deleted.") {
        repository.deleteCustomer(customer)
        onDone()
    }

    fun saveField(field: FieldEntity, onSaved: (Long) -> Unit = {}) {
        if (field.fieldName.isBlank() || field.sizeBigha <= 0) {
            postMessage("Enter a field name and a size greater than zero.")
            return
        }
        action("Field saved.") { onSaved(repository.saveField(field.copy(fieldName = field.fieldName.trim()))) }
    }

    fun deleteField(field: FieldEntity) = action("Field deleted.") { repository.deleteField(field) }

    fun saveWork(
        work: WorkEntryEntity,
        initialPaid: Double,
        paymentMethod: String,
        onSaved: (Long) -> Unit = {},
    ) {
        if (
            work.sizeBigha <= 0 || work.ratePerBigha < 0 || work.roundMultiplier <= 0 ||
            work.totalAmount < 0 || initialPaid < 0
        ) {
            postMessage("Work and payment values cannot be negative; size and rounds must be greater than zero.")
            return
        }
        viewModelScope.launch {
            runCatching {
                val payment = initialPaid.takeIf { it > 0 }?.let {
                    PaymentEntity(
                        customerId = work.customerId,
                        workEntryId = null,
                        paymentDate = work.workDate,
                        amount = it,
                        paymentMethod = paymentMethod,
                        notes = "Payment recorded with work entry",
                    )
                }
                repository.saveWorkWithPayment(work, payment)
            }.onSuccess { id ->
                messagesChannel.send("Work entry saved.")
                onSaved(id)
            }.onFailure { messagesChannel.send(it.message ?: "Could not save work.") }
        }
    }

    fun deleteWork(work: WorkEntryEntity) = action("Work entry deleted.") { repository.deleteWorkEntry(work) }

    fun addPayment(
        work: WorkSummary,
        amount: Double,
        date: Long,
        method: String,
        notes: String,
        onSaved: () -> Unit = {},
    ) {
        if (amount <= 0) {
            postMessage("Payment amount must be greater than zero.")
            return
        }
        action("Payment recorded.") {
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
            onSaved()
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
            postMessage("Payment amount must be greater than zero.")
            return
        }
        val customer = uiState.value.customerSummaries.firstOrNull { it.customer.id == customerId }
        if (customer == null) {
            postMessage("Choose a customer first.")
            return
        }
        val projectedAdvance = BusinessCalculator.money(
            (customer.totalPaid + amount - customer.totalBill).coerceAtLeast(0.0),
        )
        action(
            if (projectedAdvance > 0.005) {
                "Payment recorded. ${formatMoney(projectedAdvance)} is now advance credit."
            } else {
                "Payment recorded."
            },
        ) {
            repository.savePayment(
                PaymentEntity(
                    customerId = customerId,
                    workEntryId = null,
                    paymentDate = date,
                    amount = BusinessCalculator.money(amount),
                    paymentMethod = method,
                    notes = notes.trim().ifBlank { "Customer account payment" },
                ),
            )
            onSaved()
        }
    }

    fun markWorkPaid(work: WorkSummary, method: String = "Cash") {
        if (work.pending <= 0.005) return
        addPayment(work, work.pending, LocalDate.now().toEpochDay(), method, "Balance marked paid")
    }

    fun markCustomerPaid(customerId: Long, method: String = "Cash") {
        val summary = uiState.value.customerSummaries.firstOrNull { it.customer.id == customerId } ?: return
        if (summary.pending <= 0.005) return
        action("Complete balance marked as paid.") {
            repository.savePayment(
                PaymentEntity(
                    customerId = customerId,
                    workEntryId = null,
                    paymentDate = LocalDate.now().toEpochDay(),
                    amount = summary.pending,
                    paymentMethod = method,
                    notes = "Customer balance marked paid",
                ),
            )
        }
    }

    fun saveWorkType(workType: WorkTypeEntity) {
        val name = workType.name.trim()
        if (name.isBlank() || workType.ratePerBigha < 0) {
            postMessage("Enter a work type name and a non-negative rate.")
            return
        }
        viewModelScope.launch {
            when {
                repository.workTypeNameExists(name, workType.id) ->
                    messagesChannel.send("A work type with this name already exists.")
                else -> runCatching {
                    repository.saveWorkType(workType.copy(name = name))
                }.onSuccess {
                    messagesChannel.send("Work type saved.")
                }.onFailure {
                    messagesChannel.send(it.message ?: "Could not save work type.")
                }
            }
        }
    }

    fun deleteWorkType(workType: WorkTypeEntity) {
        if (uiState.value.workTypes.size <= 1) {
            postMessage("Keep at least one work type for new work entries.")
            return
        }
        action("Work type deleted.") { repository.deleteWorkType(workType) }
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
            cleanEntry.litres <= 0 -> postMessage("Diesel quantity must be greater than zero.")
            cleanEntry.totalAmount <= 0 -> postMessage("Diesel amount must be greater than zero.")
            cleanEntry.broughtBy.isBlank() -> postMessage("Enter who brought the diesel.")
            cleanStatus !in setOf("Paid", "Unpaid") -> postMessage("Choose Paid or Unpaid status.")
            else -> viewModelScope.launch {
                when {
                    repository.dieselEntryExists(cleanEntry) ->
                        messagesChannel.send("This diesel entry already exists.")
                    else -> runCatching { repository.saveDieselEntry(cleanEntry) }
                        .onSuccess {
                            messagesChannel.send("Diesel entry saved.")
                            onSaved()
                        }
                        .onFailure { messagesChannel.send(it.message ?: "Could not save diesel entry.") }
                }
            }
        }
    }

    fun deleteDieselEntry(entry: DieselEntryEntity) =
        action("Diesel entry deleted.") { repository.deleteDieselEntry(entry) }

    fun deletePayment(payment: PaymentEntity) = action("Payment deleted.") { repository.deletePayment(payment) }

    fun createReminder(customerId: Long, triggerAt: Long, message: String, onSaved: () -> Unit = {}) {
        if (triggerAt <= System.currentTimeMillis()) {
            postMessage("Choose a reminder time in the future.")
            return
        }
        val customer = uiState.value.customers.firstOrNull { it.id == customerId } ?: return
        viewModelScope.launch {
            runCatching {
                repository.saveReminder(ReminderEntity(customerId = customerId, reminderAt = triggerAt, message = message))
            }.onSuccess { id ->
                ReminderWorker.schedule(getApplication(), id, customer.name, message, triggerAt)
                messagesChannel.send("Payment reminder scheduled.")
                onSaved()
            }.onFailure { messagesChannel.send(it.message ?: "Could not schedule reminder.") }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) = action("Reminder deleted.") {
        ReminderWorker.cancel(getApplication(), reminder.id)
        repository.deleteReminder(reminder)
    }

    fun setTheme(theme: ThemePreference) = action(null) { settingsRepository.setTheme(theme) }

    fun saveSettings(value: AppSettings) = action("Settings saved.") {
        require(value.defaultRate >= 0) { "Default rate cannot be negative." }
        require(value.autoLockMinutes >= 0) { "Auto-lock value is invalid." }
        settingsRepository.saveBusinessSettings(value)
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
            ),
            settings.value,
        ),
    )

    fun customersCsv(): String = BackupCodec.customersCsv(uiState.value.customers)

    fun paymentsCsv(): String = BackupCodec.paymentsCsv(uiState.value.payments, uiState.value.customers)

    fun restoreBackup(text: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching { BackupCodec.decode(text) }
                .onSuccess { bundle ->
                    runCatching {
                        repository.restore(bundle.snapshot)
                        settingsRepository.restoreSettings(bundle.settings)
                        repository.ensureDefaultWorkTypes(bundle.settings.defaultRate)
                    }.onSuccess {
                        messagesChannel.send("Backup restored successfully.")
                        onDone()
                    }.onFailure { messagesChannel.send(it.message ?: "Could not restore backup.") }
                }
                .onFailure { messagesChannel.send(it.message ?: "Invalid backup file.") }
        }
    }

    private fun action(success: String?, block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess { success?.let { messagesChannel.send(it) } }
                .onFailure { messagesChannel.send(it.message ?: "Something went wrong.") }
        }
    }

    private fun postMessage(message: String) {
        viewModelScope.launch { messagesChannel.send(message) }
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
