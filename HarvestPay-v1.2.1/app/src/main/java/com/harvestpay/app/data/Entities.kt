package com.harvestpay.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "customers",
    indices = [Index(value = ["mobile"], unique = true), Index("name"), Index("village")],
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobile: String,
    val address: String = "",
    val village: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "fields",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("customerId"), Index("fieldName")],
)
data class FieldEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val fieldName: String,
    val fieldNumber: String = "",
    val location: String = "",
    val sizeBigha: Double,
    val notes: String = "",
)

@Entity(
    tableName = "work_entries",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FieldEntity::class,
            parentColumns = ["id"],
            childColumns = ["fieldId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("customerId"), Index("fieldId"), Index("workDate")],
)
data class WorkEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val fieldId: Long?,
    /** Local calendar date encoded as java.time.LocalDate.toEpochDay(). */
    val workDate: Long,
    val sizeBigha: Double,
    val ratePerBigha: Double,
    /** Legacy integer value retained so existing databases upgrade without rebuilding payment links. */
    val rounds: Int = 1,
    /** Decimal multiplier used for billing, for example 0.5, 1 or 1.5 rounds. */
    @ColumnInfo(defaultValue = "1.0") val roundMultiplier: Double = 1.0,
    val workType: String = "Ploughing",
    val subtotal: Double,
    val discount: Double = 0.0,
    val extraCharges: Double = 0.0,
    val totalAmount: Double,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "work_types",
    indices = [Index(value = ["name"], unique = true)],
)
data class WorkTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val ratePerBigha: Double,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = WorkEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["workEntryId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("customerId"), Index("workEntryId"), Index("paymentDate")],
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val workEntryId: Long?,
    /** Local calendar date encoded as java.time.LocalDate.toEpochDay(). */
    val paymentDate: Long,
    val amount: Double,
    val paymentMethod: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("customerId"), Index("reminderAt")],
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val reminderAt: Long,
    val message: String,
    val completed: Boolean = false,
)
