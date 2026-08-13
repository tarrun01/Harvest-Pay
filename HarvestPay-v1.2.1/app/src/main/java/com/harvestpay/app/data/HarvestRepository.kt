package com.harvestpay.app.data

import androidx.room.withTransaction

class HarvestRepository(private val database: AppDatabase) {
    private val dao = database.harvestDao()

    val customers = dao.observeCustomers()
    val fields = dao.observeFields()
    val workEntries = dao.observeWorkEntries()
    val payments = dao.observePayments()
    val reminders = dao.observeReminders()
    val workTypes = dao.observeWorkTypes()

    suspend fun mobileExists(mobile: String, excludingId: Long = 0) =
        dao.mobileExists(mobile, excludingId)

    suspend fun saveCustomer(customer: CustomerEntity): Long {
        if (customer.id == 0L) return dao.insertCustomer(customer)
        dao.updateCustomer(customer)
        return customer.id
    }

    suspend fun deleteCustomer(customer: CustomerEntity) = dao.deleteCustomer(customer)

    suspend fun saveField(field: FieldEntity): Long {
        if (field.id == 0L) return dao.insertField(field)
        dao.updateField(field)
        return field.id
    }

    suspend fun deleteField(field: FieldEntity) = dao.deleteField(field)

    suspend fun saveWorkEntry(workEntry: WorkEntryEntity): Long {
        if (workEntry.id == 0L) return dao.insertWorkEntry(workEntry)
        dao.updateWorkEntry(workEntry)
        return workEntry.id
    }

    suspend fun deleteWorkEntry(workEntry: WorkEntryEntity) = dao.deleteWorkEntry(workEntry)

    suspend fun workTypeNameExists(name: String, excludingId: Long = 0) =
        dao.workTypeNameExists(name, excludingId)

    suspend fun saveWorkType(workType: WorkTypeEntity): Long {
        if (workType.id == 0L) return dao.insertWorkType(workType)
        dao.updateWorkType(workType)
        return workType.id
    }

    suspend fun deleteWorkType(workType: WorkTypeEntity) = dao.deleteWorkType(workType)

    suspend fun ensureDefaultWorkTypes(defaultRate: Double) {
        if (dao.workTypeCount() != 0) return
        val safeRate = defaultRate.coerceAtLeast(0.0)
        dao.insertWorkTypes(
            listOf(
                WorkTypeEntity(name = "Ploughing", ratePerBigha = safeRate),
                WorkTypeEntity(name = "Cultivating", ratePerBigha = safeRate),
                WorkTypeEntity(name = "Rotavating", ratePerBigha = safeRate),
            ),
        )
    }

    suspend fun saveWorkWithPayment(
        workEntry: WorkEntryEntity,
        payment: PaymentEntity?,
    ): Long = database.withTransaction {
        val workId = if (workEntry.id == 0L) {
            dao.insertWorkEntry(workEntry)
        } else {
            dao.updateWorkEntry(workEntry)
            workEntry.id
        }
        payment?.let { dao.insertPayment(it.copy(workEntryId = workId)) }
        workId
    }

    suspend fun savePayment(payment: PaymentEntity): Long {
        if (payment.id == 0L) return dao.insertPayment(payment)
        dao.updatePayment(payment)
        return payment.id
    }

    suspend fun deletePayment(payment: PaymentEntity) = dao.deletePayment(payment)

    suspend fun addPayments(payments: List<PaymentEntity>) = database.withTransaction {
        if (payments.isNotEmpty()) dao.insertPayments(payments)
    }

    suspend fun saveReminder(reminder: ReminderEntity): Long {
        if (reminder.id == 0L) return dao.insertReminder(reminder)
        dao.updateReminder(reminder)
        return reminder.id
    }

    suspend fun deleteReminder(reminder: ReminderEntity) = dao.deleteReminder(reminder)

    suspend fun restore(snapshot: DatabaseSnapshot) = database.withTransaction {
        dao.clearReminders()
        dao.clearPayments()
        dao.clearWorkEntries()
        dao.clearFields()
        dao.clearCustomers()
        dao.clearWorkTypes()
        if (snapshot.customers.isNotEmpty()) dao.insertCustomers(snapshot.customers)
        if (snapshot.fields.isNotEmpty()) dao.insertFields(snapshot.fields)
        if (snapshot.workEntries.isNotEmpty()) dao.insertWorkEntries(snapshot.workEntries)
        if (snapshot.payments.isNotEmpty()) dao.insertPayments(snapshot.payments)
        if (snapshot.reminders.isNotEmpty()) dao.insertReminders(snapshot.reminders)
        if (snapshot.workTypes.isNotEmpty()) dao.insertWorkTypes(snapshot.workTypes)
    }
}

data class DatabaseSnapshot(
    val customers: List<CustomerEntity>,
    val fields: List<FieldEntity>,
    val workEntries: List<WorkEntryEntity>,
    val payments: List<PaymentEntity>,
    val reminders: List<ReminderEntity>,
    val workTypes: List<WorkTypeEntity>,
)
