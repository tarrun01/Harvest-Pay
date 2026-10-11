package com.harvestpay.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

enum class BillingType { PER_BIGHA, PER_MINUTE }

@Entity(
    tableName = "customers",
    indices = [Index("mobile"), Index("name"), Index("village")],
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** Internal-only label used to distinguish customers with similar names. */
    @ColumnInfo(defaultValue = "''") val nickname: String = "",
    val mobile: String,
    val address: String = "",
    val village: String = "",
    val notes: String = "",
    /** Balance brought forward when the customer is first added. */
    @ColumnInfo(defaultValue = "0.0") val previousDue: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
)

val CustomerEntity.appDisplayName: String
    get() = nickname.trim().takeIf(String::isNotBlank)?.let { "$name ($it)" } ?: name

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
    @ColumnInfo(defaultValue = "'MANUAL'") val measurementMethod: String = "MANUAL",
    val measuredAreaSquareMeters: Double? = null,
    val measuredAreaAcres: Double? = null,
    val measurementBighaPerAcre: Double? = null,
    val measuredAt: Long? = null,
    @ColumnInfo(defaultValue = "'[]'") val boundaryCoordinates: String = "[]",
    @ColumnInfo(defaultValue = "'[]'") val originalGpsTrack: String = "[]",
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
    @ColumnInfo(defaultValue = "'PER_BIGHA'") val billingType: String = BillingType.PER_BIGHA.name,
    @ColumnInfo(defaultValue = "0") val durationMinutes: Int = 0,
    /** Legacy integer value retained so existing databases upgrade without rebuilding payment links. */
    val rounds: Int = 1,
    /** Decimal multiplier used for billing, for example 0.5, 1 or 1.5 rounds. */
    @ColumnInfo(defaultValue = "1.0") val roundMultiplier: Double = 1.0,
    val workType: String = "Ploughing",
    @ColumnInfo(defaultValue = "''") val workTypeHindi: String = "",
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
    @ColumnInfo(defaultValue = "''") val hindiName: String = "",
    @ColumnInfo(defaultValue = "'PER_BIGHA'") val billingType: String = BillingType.PER_BIGHA.name,
    val createdAt: Long = System.currentTimeMillis(),
)

val WorkEntryEntity.billingMode: BillingType
    get() = runCatching { BillingType.valueOf(billingType) }.getOrDefault(BillingType.PER_BIGHA)

val WorkTypeEntity.billingMode: BillingType
    get() = runCatching { BillingType.valueOf(billingType) }.getOrDefault(BillingType.PER_BIGHA)

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

@Entity(
    tableName = "diesel_entries",
    indices = [Index("entryDate"), Index("paymentStatus")],
)
data class DieselEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val litres: Double,
    val totalAmount: Double,
    /** Local calendar date encoded as java.time.LocalDate.toEpochDay(). */
    val entryDate: Long,
    val paymentStatus: String = "Paid",
    val broughtBy: String,
    val createdAt: Long = System.currentTimeMillis(),
)

/** One completed account year. The row is written last in the archive transaction. */
@Entity(tableName = "year_archives")
data class YearArchiveEntity(
    @PrimaryKey val year: Int,
    val archivedAt: Long,
    val carryForwardCompleted: Boolean = true,
)

/** Immutable yearly balance snapshot. Transactions remain in their original tables. */
@Entity(
    tableName = "customer_year_archives",
    primaryKeys = ["customerId", "year"],
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("customerId"), Index("year")],
)
data class CustomerYearArchiveEntity(
    val customerId: Long,
    val year: Int,
    val openingOutstanding: Double = 0.0,
    val openingAdvance: Double = 0.0,
    val workTotal: Double = 0.0,
    val paymentsTotal: Double = 0.0,
    val closingOutstanding: Double = 0.0,
    val closingAdvance: Double = 0.0,
    val archivedAt: Long,
    val carryForwardCompleted: Boolean = true,
)
