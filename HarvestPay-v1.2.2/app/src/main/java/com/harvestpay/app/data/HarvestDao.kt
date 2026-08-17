package com.harvestpay.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HarvestDao {
    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun observeCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM fields ORDER BY fieldName COLLATE NOCASE")
    fun observeFields(): Flow<List<FieldEntity>>

    @Query("SELECT * FROM work_entries ORDER BY workDate DESC, createdAt DESC")
    fun observeWorkEntries(): Flow<List<WorkEntryEntity>>

    @Query("SELECT * FROM payments ORDER BY paymentDate DESC, createdAt DESC")
    fun observePayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM reminders WHERE completed = 0 ORDER BY reminderAt")
    fun observeReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM work_types ORDER BY name COLLATE NOCASE")
    fun observeWorkTypes(): Flow<List<WorkTypeEntity>>

    @Query("SELECT * FROM diesel_entries ORDER BY entryDate DESC, createdAt DESC")
    fun observeDieselEntries(): Flow<List<DieselEntryEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM customers WHERE mobile = :mobile AND id != :excludingId)")
    suspend fun mobileExists(mobile: String, excludingId: Long = 0): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM work_types WHERE LOWER(name) = LOWER(:name) AND id != :excludingId)")
    suspend fun workTypeNameExists(name: String, excludingId: Long = 0): Boolean

    @Query("SELECT COUNT(*) FROM work_types")
    suspend fun workTypeCount(): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM diesel_entries
            WHERE litres = :litres
              AND totalAmount = :totalAmount
              AND entryDate = :entryDate
              AND paymentStatus = :paymentStatus
              AND LOWER(TRIM(broughtBy)) = LOWER(TRIM(:broughtBy))
              AND id != :excludingId
        )
        """,
    )
    suspend fun dieselEntryExists(
        litres: Double,
        totalAmount: Double,
        entryDate: Long,
        paymentStatus: String,
        broughtBy: String,
        excludingId: Long = 0,
    ): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertField(field: FieldEntity): Long

    @Update
    suspend fun updateField(field: FieldEntity)

    @Delete
    suspend fun deleteField(field: FieldEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFields(fields: List<FieldEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorkEntry(workEntry: WorkEntryEntity): Long

    @Update
    suspend fun updateWorkEntry(workEntry: WorkEntryEntity)

    @Delete
    suspend fun deleteWorkEntry(workEntry: WorkEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkEntries(workEntries: List<WorkEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorkType(workType: WorkTypeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkTypes(workTypes: List<WorkTypeEntity>)

    @Update
    suspend fun updateWorkType(workType: WorkTypeEntity)

    @Delete
    suspend fun deleteWorkType(workType: WorkTypeEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDieselEntry(entry: DieselEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDieselEntries(entries: List<DieselEntryEntity>)

    @Update
    suspend fun updateDieselEntry(entry: DieselEntryEntity)

    @Delete
    suspend fun deleteDieselEntry(entry: DieselEntryEntity)

    @Query("DELETE FROM reminders")
    suspend fun clearReminders()

    @Query("DELETE FROM payments")
    suspend fun clearPayments()

    @Query("DELETE FROM work_entries")
    suspend fun clearWorkEntries()

    @Query("DELETE FROM fields")
    suspend fun clearFields()

    @Query("DELETE FROM customers")
    suspend fun clearCustomers()

    @Query("DELETE FROM work_types")
    suspend fun clearWorkTypes()

    @Query("DELETE FROM diesel_entries")
    suspend fun clearDieselEntries()
}
