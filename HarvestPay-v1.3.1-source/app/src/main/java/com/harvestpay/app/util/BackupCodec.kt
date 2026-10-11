package com.harvestpay.app.util

import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.DatabaseSnapshot
import com.harvestpay.app.data.DieselEntryEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.ReminderEntity
import com.harvestpay.app.data.ThemePreference
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.data.WorkTypeEntity
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.CustomerYearArchiveEntity
import com.harvestpay.app.data.HoursUpdateFrequency
import com.harvestpay.app.data.YearArchiveEntity
import com.harvestpay.app.data.billingMode
import java.time.Instant
import org.json.JSONArray
import org.json.JSONObject

data class BackupBundle(val snapshot: DatabaseSnapshot, val settings: AppSettings)

object BackupCodec {
    private const val SCHEMA_VERSION = 7

    fun encode(bundle: BackupBundle): String = JSONObject().apply {
        put("app", "Harvest Pay")
        put("schemaVersion", SCHEMA_VERSION)
        put("exportedAt", Instant.now().toString())
        put("customers", JSONArray().also { array -> bundle.snapshot.customers.forEach { array.put(it.json()) } })
        put("fields", JSONArray().also { array -> bundle.snapshot.fields.forEach { array.put(it.json()) } })
        put("workEntries", JSONArray().also { array -> bundle.snapshot.workEntries.forEach { array.put(it.json()) } })
        put("payments", JSONArray().also { array -> bundle.snapshot.payments.forEach { array.put(it.json()) } })
        put("reminders", JSONArray().also { array -> bundle.snapshot.reminders.forEach { array.put(it.json()) } })
        put("workTypes", JSONArray().also { array -> bundle.snapshot.workTypes.forEach { array.put(it.json()) } })
        put("dieselEntries", JSONArray().also { array -> bundle.snapshot.dieselEntries.forEach { array.put(it.json()) } })
        put("yearArchives", JSONArray().also { array -> bundle.snapshot.yearArchives.forEach { array.put(it.json()) } })
        put("customerYearArchives", JSONArray().also { array -> bundle.snapshot.customerYearArchives.forEach { array.put(it.json()) } })
        put("settings", bundle.settings.json())
    }.toString(2)

    fun decode(text: String): BackupBundle {
        val root = JSONObject(text)
        require(root.optString("app") == "Harvest Pay") { "This is not a Harvest Pay backup." }
        val schemaVersion = root.optInt("schemaVersion")
        require(schemaVersion in 1..SCHEMA_VERSION) { "Unsupported backup version." }

        val customers = root.requiredArray("customers").mapObjects { it.customer() }
        val fields = root.requiredArray("fields").mapObjects { it.field() }
        val work = root.requiredArray("workEntries").mapObjects { it.workEntry() }
        val payments = root.requiredArray("payments").mapObjects { it.payment() }
        val reminders = root.optJSONArray("reminders")?.mapObjects { it.reminder() }.orEmpty()
        val workTypes = root.optJSONArray("workTypes")?.mapObjects { it.workType() }.orEmpty()
        val dieselEntries = root.optJSONArray("dieselEntries")?.mapObjects { it.dieselEntry() }.orEmpty()
        val yearArchives = root.optJSONArray("yearArchives")?.mapObjects { it.yearArchive() }.orEmpty()
        val customerYearArchives = root.optJSONArray("customerYearArchives")?.mapObjects { it.customerYearArchive() }.orEmpty()
        val settings = root.optJSONObject("settings")?.settings() ?: AppSettings()

        require(customers.all { it.id > 0 && it.name.isNotBlank() && it.previousDue >= 0 }) {
            "Backup contains invalid customers."
        }
        require(fields.all { it.id > 0 && it.customerId > 0 && it.sizeBigha >= 0 }) { "Backup contains invalid fields." }
        require(fields.all { field ->
            field.measurementMethod in setOf("MANUAL", "GPS", "SATELLITE") &&
                (field.measurementMethod == "MANUAL" ||
                    (field.measuredAreaSquareMeters?.let { it.isFinite() && it > 0 } == true &&
                     field.measuredAreaAcres?.let { it.isFinite() && it > 0 } == true &&
                     field.measurementBighaPerAcre?.let { it.isFinite() && it > 0 } == true &&
                     field.measuredAt != null &&
                     com.harvestpay.app.measurement.FieldGeometry.validationError(com.harvestpay.app.measurement.decodeBoundary(field.boundaryCoordinates)) == null))
        }) { "Backup contains invalid field measurements." }
        require(settings.bighaPerAcre?.let { it.isFinite() && it > 0 } != false && settings.gpsMaximumAccuracyMeters.isFinite() && settings.gpsMaximumAccuracyMeters in 3.0..50.0) {
            "Backup contains invalid land measurement settings."
        }
        require(work.all { it.id > 0 && it.totalAmount >= 0 && it.sizeBigha >= 0 && (it.billingMode == BillingType.PER_BIGHA || it.durationMinutes > 0) }) { "Backup contains invalid work entries." }
        require(payments.all { it.id > 0 && it.amount >= 0 }) { "Backup contains invalid payments." }
        require(workTypes.all { it.id > 0 && it.name.isNotBlank() && it.ratePerBigha >= 0 }) {
            "Backup contains invalid work types."
        }
        require(
            dieselEntries.all {
                it.id > 0 && it.litres > 0 && it.totalAmount > 0 &&
                    it.broughtBy.isNotBlank() && it.paymentStatus in setOf("Paid", "Unpaid")
            },
        ) { "Backup contains invalid diesel entries." }
        require(yearArchives.distinctBy { it.year }.size == yearArchives.size && yearArchives.all { it.year in 1900..9999 && it.carryForwardCompleted }) {
            "Backup contains invalid yearly archives."
        }
        require(
            customerYearArchives.distinctBy { it.customerId to it.year }.size == customerYearArchives.size &&
                customerYearArchives.all {
                    it.customerId > 0 && it.year in 1900..9999 && it.openingOutstanding >= 0 && it.openingAdvance >= 0 &&
                        it.workTotal >= 0 && it.paymentsTotal >= 0 && it.closingOutstanding >= 0 && it.closingAdvance >= 0 &&
                        !(it.closingOutstanding > 0.005 && it.closingAdvance > 0.005) && it.carryForwardCompleted
                },
        ) { "Backup contains invalid customer archive balances." }

        return BackupBundle(
            snapshot = DatabaseSnapshot(
                customers,
                fields,
                work,
                payments,
                reminders,
                workTypes,
                dieselEntries,
                yearArchives,
                customerYearArchives,
            ),
            settings = settings,
        )
    }

    fun customersCsv(customers: List<CustomerEntity>): String = buildString {
        appendLine("Name,Mobile,Village,Address,Previous Due,Notes,Date Added")
        customers.forEach {
            appendLine(
                listOf(
                    it.name,
                    it.mobile,
                    it.village,
                    it.address,
                    it.previousDue.toString(),
                    it.notes,
                    it.createdAt.toString(),
                ).csvRow(),
            )
        }
    }

    fun paymentsCsv(
        payments: List<PaymentEntity>,
        customers: List<CustomerEntity>,
    ): String {
        val names = customers.associate { it.id to it.name }
        return buildString {
            appendLine("Date,Customer,Work ID,Amount,Method,Notes")
            payments.forEach {
                appendLine(
                    listOf(
                        formatDate(it.paymentDate),
                        names[it.customerId].orEmpty(),
                        it.workEntryId?.toString().orEmpty(),
                        it.amount.toString(),
                        it.paymentMethod,
                        it.notes,
                    ).csvRow(),
                )
            }
        }
    }

    private fun CustomerEntity.json() = JSONObject().apply {
        put("id", id); put("name", name); put("nickname", nickname); put("mobile", mobile); put("address", address)
        put("village", village); put("notes", notes); put("previousDue", previousDue)
        put("createdAt", createdAt)
    }

    private fun FieldEntity.json() = JSONObject().apply {
        put("id", id); put("customerId", customerId); put("fieldName", fieldName)
        put("fieldNumber", fieldNumber); put("location", location); put("sizeBigha", sizeBigha)
        put("measurementMethod", measurementMethod); put("measuredAreaSquareMeters", measuredAreaSquareMeters ?: JSONObject.NULL)
        put("measuredAreaAcres", measuredAreaAcres ?: JSONObject.NULL); put("measurementBighaPerAcre", measurementBighaPerAcre ?: JSONObject.NULL)
        put("measuredAt", measuredAt ?: JSONObject.NULL); put("boundaryCoordinates", boundaryCoordinates); put("originalGpsTrack", originalGpsTrack)
        put("notes", notes)
    }

    private fun WorkEntryEntity.json() = JSONObject().apply {
        put("id", id); put("customerId", customerId); put("fieldId", fieldId ?: JSONObject.NULL)
        put("workDate", workDate); put("sizeBigha", sizeBigha); put("ratePerBigha", ratePerBigha)
        put("rounds", rounds); put("roundMultiplier", roundMultiplier)
        put("billingType", billingType); put("durationMinutes", durationMinutes)
        put("workType", workType); put("workTypeHindi", workTypeHindi); put("subtotal", subtotal)
        put("discount", discount); put("extraCharges", extraCharges); put("totalAmount", totalAmount)
        put("notes", notes); put("createdAt", createdAt)
    }

    private fun PaymentEntity.json() = JSONObject().apply {
        put("id", id); put("customerId", customerId); put("workEntryId", workEntryId ?: JSONObject.NULL)
        put("paymentDate", paymentDate); put("amount", amount); put("paymentMethod", paymentMethod)
        put("notes", notes); put("createdAt", createdAt)
    }

    private fun ReminderEntity.json() = JSONObject().apply {
        put("id", id); put("customerId", customerId); put("reminderAt", reminderAt)
        put("message", message); put("completed", completed)
    }

    private fun WorkTypeEntity.json() = JSONObject().apply {
        put("id", id); put("name", name); put("hindiName", hindiName); put("ratePerBigha", ratePerBigha); put("billingType", billingType); put("createdAt", createdAt)
    }

    private fun DieselEntryEntity.json() = JSONObject().apply {
        put("id", id); put("litres", litres); put("totalAmount", totalAmount)
        put("entryDate", entryDate); put("paymentStatus", paymentStatus)
        put("broughtBy", broughtBy); put("createdAt", createdAt)
    }

    private fun YearArchiveEntity.json() = JSONObject().apply {
        put("year", year); put("archivedAt", archivedAt); put("carryForwardCompleted", carryForwardCompleted)
    }

    private fun CustomerYearArchiveEntity.json() = JSONObject().apply {
        put("customerId", customerId); put("year", year)
        put("openingOutstanding", openingOutstanding); put("openingAdvance", openingAdvance)
        put("workTotal", workTotal); put("paymentsTotal", paymentsTotal)
        put("closingOutstanding", closingOutstanding); put("closingAdvance", closingAdvance)
        put("archivedAt", archivedAt); put("carryForwardCompleted", carryForwardCompleted)
    }

    private fun AppSettings.json() = JSONObject().apply {
        put("theme", theme.name); put("defaultRate", defaultRate); put("ratePresets", ratePresets)
        put("businessName", businessName); put("ownerName", ownerName); put("ownerMobile", ownerMobile)
        put("tractorNumber", tractorNumber); put("address", address); put("village", village)
        put("autoLockMinutes", autoLockMinutes)
        put("bighaPerAcre", bighaPerAcre ?: JSONObject.NULL); put("gpsMaximumAccuracyMeters", gpsMaximumAccuracyMeters)
        put("serviceReminderEnabled", serviceReminderEnabled); put("currentTractorHours", currentTractorHours); put("serviceIntervalHours", serviceIntervalHours); put("lastServiceHours", lastServiceHours); put("hoursUpdateFrequency", hoursUpdateFrequency.name); put("lastHoursUpdatedAt", lastHoursUpdatedAt); put("serviceHistory", serviceHistory)
    }

    private fun JSONObject.customer() = CustomerEntity(
        id = getLong("id"), name = getString("name"), mobile = getString("mobile"),
        nickname = optString("nickname"),
        address = optString("address"), village = optString("village"), notes = optString("notes"),
        previousDue = optDouble("previousDue", 0.0),
        createdAt = optLong("createdAt", System.currentTimeMillis()),
    )

    private fun JSONObject.field() = FieldEntity(
        id = getLong("id"), customerId = getLong("customerId"), fieldName = getString("fieldName"),
        fieldNumber = optString("fieldNumber"), location = optString("location"),
        sizeBigha = getDouble("sizeBigha"), notes = optString("notes"),
        measurementMethod = optString("measurementMethod", "MANUAL"),
        measuredAreaSquareMeters = if(isNull("measuredAreaSquareMeters")) null else getDouble("measuredAreaSquareMeters"),
        measuredAreaAcres = if(isNull("measuredAreaAcres")) null else getDouble("measuredAreaAcres"),
        measurementBighaPerAcre = if(isNull("measurementBighaPerAcre")) null else getDouble("measurementBighaPerAcre"),
        measuredAt = nullableLong("measuredAt"), boundaryCoordinates = optString("boundaryCoordinates", "[]"), originalGpsTrack = optString("originalGpsTrack", "[]"),
    )

    private fun JSONObject.workEntry() = WorkEntryEntity(
        id = getLong("id"), customerId = getLong("customerId"),
        fieldId = nullableLong("fieldId"), workDate = getLong("workDate"),
        sizeBigha = getDouble("sizeBigha"), ratePerBigha = getDouble("ratePerBigha"),
        rounds = optInt("rounds", 1),
        roundMultiplier = optDouble("roundMultiplier", optDouble("rounds", 1.0)),
        billingType = optString("billingType", BillingType.PER_BIGHA.name), durationMinutes = optInt("durationMinutes", 0),
        workType = optString("workType", "Ploughing"),
        workTypeHindi = optString("workTypeHindi"),
        subtotal = getDouble("subtotal"), discount = optDouble("discount", 0.0),
        extraCharges = optDouble("extraCharges", 0.0), totalAmount = getDouble("totalAmount"),
        notes = optString("notes"), createdAt = optLong("createdAt", System.currentTimeMillis()),
    )

    private fun JSONObject.payment() = PaymentEntity(
        id = getLong("id"), customerId = getLong("customerId"),
        workEntryId = nullableLong("workEntryId"), paymentDate = getLong("paymentDate"),
        amount = getDouble("amount"), paymentMethod = getString("paymentMethod"),
        notes = optString("notes"), createdAt = optLong("createdAt", System.currentTimeMillis()),
    )

    private fun JSONObject.reminder() = ReminderEntity(
        id = getLong("id"), customerId = getLong("customerId"), reminderAt = getLong("reminderAt"),
        message = getString("message"), completed = optBoolean("completed", false),
    )

    private fun JSONObject.workType() = WorkTypeEntity(
        id = getLong("id"), name = getString("name"), ratePerBigha = getDouble("ratePerBigha"), hindiName = optString("hindiName"), billingType = optString("billingType", BillingType.PER_BIGHA.name),
        createdAt = optLong("createdAt", System.currentTimeMillis()),
    )

    private fun JSONObject.dieselEntry() = DieselEntryEntity(
        id = getLong("id"), litres = getDouble("litres"), totalAmount = getDouble("totalAmount"),
        entryDate = getLong("entryDate"), paymentStatus = optString("paymentStatus", "Paid"),
        broughtBy = getString("broughtBy"), createdAt = optLong("createdAt", System.currentTimeMillis()),
    )

    private fun JSONObject.yearArchive() = YearArchiveEntity(
        year = getInt("year"), archivedAt = getLong("archivedAt"),
        carryForwardCompleted = optBoolean("carryForwardCompleted", true),
    )

    private fun JSONObject.customerYearArchive() = CustomerYearArchiveEntity(
        customerId = getLong("customerId"), year = getInt("year"),
        openingOutstanding = optDouble("openingOutstanding", 0.0),
        openingAdvance = optDouble("openingAdvance", 0.0),
        workTotal = optDouble("workTotal", 0.0), paymentsTotal = optDouble("paymentsTotal", 0.0),
        closingOutstanding = optDouble("closingOutstanding", 0.0),
        closingAdvance = optDouble("closingAdvance", 0.0),
        archivedAt = getLong("archivedAt"), carryForwardCompleted = optBoolean("carryForwardCompleted", true),
    )

    private fun JSONObject.settings() = AppSettings(
        bighaPerAcre = if(isNull("bighaPerAcre")) null else getDouble("bighaPerAcre"),
        gpsMaximumAccuracyMeters = optDouble("gpsMaximumAccuracyMeters",15.0),
        theme = runCatching { ThemePreference.valueOf(optString("theme", "SYSTEM")) }
            .getOrDefault(ThemePreference.SYSTEM),
        defaultRate = optDouble("defaultRate", 800.0),
        ratePresets = optString("ratePresets", "700,800,900,1000"),
        businessName = optString("businessName", "Harvest Pay"),
        ownerName = optString("ownerName"), ownerMobile = optString("ownerMobile"),
        tractorNumber = optString("tractorNumber"), address = optString("address"),
        village = optString("village"), autoLockMinutes = optInt("autoLockMinutes", 15),
        serviceReminderEnabled = optBoolean("serviceReminderEnabled", false), currentTractorHours = optDouble("currentTractorHours", 0.0), serviceIntervalHours = optDouble("serviceIntervalHours", 500.0), lastServiceHours = optDouble("lastServiceHours", 0.0),
        hoursUpdateFrequency = runCatching { HoursUpdateFrequency.valueOf(optString("hoursUpdateFrequency", "WEEKLY")) }.getOrDefault(HoursUpdateFrequency.WEEKLY), lastHoursUpdatedAt = optLong("lastHoursUpdatedAt", 0L), serviceHistory = optString("serviceHistory"),
    )

    private fun JSONObject.requiredArray(key: String): JSONArray =
        requireNotNull(optJSONArray(key)) { "Backup is missing $key." }

    private fun JSONObject.nullableLong(key: String): Long? = if (isNull(key)) null else getLong(key)

    private inline fun <T> JSONArray.mapObjects(block: (JSONObject) -> T): List<T> =
        (0 until length()).map { block(getJSONObject(it)) }

    private fun List<String>.csvRow(): String = joinToString(",") { value ->
        "\"${value.replace("\"", "\"\"")}\""
    }
}
