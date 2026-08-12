package com.harvestpay.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.DatabaseSnapshot
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.HarvestRepository
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.ReminderEntity
import com.harvestpay.app.data.SettingsRepository
import com.harvestpay.app.data.ThemePreference
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.WorkSummary
import com.harvestpay.app.notification.ReminderWorker
import com.harvestpay.app.security.LocalAuth
import com.harvestpay.app.util.BackupBundle
import com.harvestpay.app.util.BackupCodec
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthState(
    val resolved: Boolean = false,
    val authenticated: Boolean = false,
    val error: String? = null,
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
        SharingStarted.WhileSubscribed(5_000),
        AppSettings(),
    )

    val uiState = combine(
        repository.customers,
        repository.fields,
        repository.workEntries,
        repository.payments,
        BusinessCalculator::buildUiState,
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HarvestUiState(),
    )

    val reminders = repository.reminders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
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
        }
    }

    fun login(mobile: String, password: String, remember: Boolean) {
        viewModelScope.launch {
            val valid = LocalAuth.verify(mobile, password.toCharArray())
            if (valid) {
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
        val timeout = settings.value.autoLockMinutes.coerceAtLeast(1) * 60_000L
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
                !LocalAuth.isValidIndianMobile(mobile) -> messagesChannel.send("Enter a valid Indian mobile number.")
                repository.mobileExists(mobile, customer.id) -> messagesChannel.send("A customer with this mobile number already exists.")
                else -> runCatching { repository.saveCustomer(customer.copy(name = customer.name.trim(), mobile = mobile)) }
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
        if (work.sizeBigha <= 0 || work.ratePerBigha < 0 || work.totalAmount < 0 || initialPaid < 0) {
            postMessage("Work and payment values cannot be negative; field size must be greater than zero.")
            return
        }
        if (initialPaid > work.totalAmount + 0.005) {
            postMessage("Amount paid cannot exceed the final amount.")
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
        if (amount > work.pending + 0.005) {
            postMessage("Payment cannot exceed the pending amount.")
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

    fun markWorkPaid(work: WorkSummary, method: String = "Cash") {
        if (work.pending <= 0.005) return
        addPayment(work, work.pending, LocalDate.now().toEpochDay(), method, "Balance marked paid")
    }

    fun markCustomerPaid(customerId: Long, method: String = "Cash") {
        val pendingWork = uiState.value.workSummaries.filter {
            it.work.customerId == customerId && it.pending > 0.005
        }
        if (pendingWork.isEmpty()) return
        action("Complete balance marked as paid.") {
            repository.addPayments(
                pendingWork.map {
                    PaymentEntity(
                        customerId = customerId,
                        workEntryId = it.work.id,
                        paymentDate = LocalDate.now().toEpochDay(),
                        amount = it.pending,
                        paymentMethod = method,
                        notes = "Customer balance marked paid",
                    )
                },
            )
        }
    }

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
        settingsRepository.saveBusinessSettings(value)
    }

    fun backupJson(): String = BackupCodec.encode(
        BackupBundle(
            DatabaseSnapshot(
                uiState.value.customers,
                uiState.value.fields,
                uiState.value.workEntries,
                uiState.value.payments,
                reminders.value,
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
