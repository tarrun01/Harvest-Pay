package com.harvestpay.app.util

import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.DatabaseSnapshot
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.ReminderEntity
import com.harvestpay.app.data.ThemePreference
import com.harvestpay.app.data.WorkEntryEntity
import java.time.Instant
import org.json.JSONArray
import org.json.JSONObject

data class BackupBundle(val snapshot: DatabaseSnapshot, val settings: AppSettings)

object BackupCodec {
    private const val SCHEMA_VERSION = 1

    fun encode(bundle: BackupBundle): String = JSONObject().apply {
        put("app", "Harvest Pay")
        put("schemaVersion", SCHEMA_VERSION)
        put("exportedAt", Instant.now().toString())
        put("customers", JSONArray().also { array -> bundle.snapshot.customers.forEach { array.put(it.json()) } })
        put("fields", JSONArray().also { array -> bundle.snapshot.fields.forEach { array.put(it.json()) } })
        put("workEntries", JSONArray().also { array -> bundle.snapshot.workEntries.forEach { array.put(it.json()) } })
        put("payments", JSONArray().also { array -> bundle.snapshot.payments.forEach { array.put(it.json()) } })
        put("reminders", JSONArray().also { array -> bundle.snapshot.reminders.forEach { array.put(it.json()) } })
        put("settings", bundle.settings.json())
    }.toString(2)

    fun decode(text: String): BackupBundle {
        val root = JSONObject(text)
        require(root.optString("app") == "Harvest Pay") { "This is not a Harvest Pay backup." }
        require(root.optInt("schemaVersion") == SCHEMA_VERSION) { "Unsupported backup version." }

        val customers = root.requiredArray("customers").mapObjects { it.customer() }
        val fields = root.requiredArray("fields").mapObjects { it.field() }
        val work = root.requiredArray("workEntries").mapObjects { it.workEntry() }
        val payments = root.requiredArray("payments").mapObjects { it.payment() }
        val reminders = root.optJSONArray("reminders")?.mapObjects { it.reminder() }.orEmpty()
        val settings = root.optJSONObject("settings")?.settings() ?: AppSettings()

        require(customers.all { it.id > 0 && it.name.isNotBlank() }) { "Backup contains invalid customers." }
        require(fields.all { it.id > 0 && it.customerId > 0 && it.sizeBigha >= 0 }) { "Backup contains invalid fields." }
        require(work.all { it.id > 0 && it.totalAmount >= 0 && it.sizeBigha >= 0 }) { "Backup contains invalid work entries." }
        require(payments.all { it.id > 0 && it.amount >= 0 }) { "Backup contains invalid payments." }

        return BackupBundle(
            snapshot = DatabaseSnapshot(customers, fields, work, payments, reminders),
            settings = settings,
        )
    }

    fun customersCsv(customers: List<CustomerEntity>): String = buildString {
        appendLine("Name,Mobile,Village,Address,Notes,Date Added")
        customers.forEach {
            appendLine(listOf(it.name, it.mobile, it.village, it.address, it.notes, it.createdAt.toString()).csvRow())
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
        put("id", id); put("name", name); put("mobile", mobile); put("address", address)
        put("village", village); put("notes", notes); put("createdAt", createdAt)
    }

    private fun FieldEntity.json() = JSONObject().apply {
        put("id", id); put("customerId", customerId); put("fieldName", fieldName)
        put("fieldNumber", fieldNumber); put("location", location); put("sizeBigha", sizeBigha)
        put("notes", notes)
    }

    private fun WorkEntryEntity.json() = JSONObject().apply {
        put("id", id); put("customerId", customerId); put("fieldId", fieldId ?: JSONObject.NULL)
        put("workDate", workDate); put("sizeBigha", sizeBigha); put("ratePerBigha", ratePerBigha)
        put("rounds", rounds); put("workType", workType); put("subtotal", subtotal)
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

    private fun AppSettings.json() = JSONObject().apply {
        put("theme", theme.name); put("defaultRate", defaultRate); put("ratePresets", ratePresets)
        put("businessName", businessName); put("ownerName", ownerName); put("ownerMobile", ownerMobile)
        put("tractorNumber", tractorNumber); put("address", address); put("village", village)
        put("autoLockMinutes", autoLockMinutes)
    }

    private fun JSONObject.customer() = CustomerEntity(
        id = getLong("id"), name = getString("name"), mobile = getString("mobile"),
        address = optString("address"), village = optString("village"), notes = optString("notes"),
        createdAt = optLong("createdAt", System.currentTimeMillis()),
    )

    private fun JSONObject.field() = FieldEntity(
        id = getLong("id"), customerId = getLong("customerId"), fieldName = getString("fieldName"),
        fieldNumber = optString("fieldNumber"), location = optString("location"),
        sizeBigha = getDouble("sizeBigha"), notes = optString("notes"),
    )

    private fun JSONObject.workEntry() = WorkEntryEntity(
        id = getLong("id"), customerId = getLong("customerId"),
        fieldId = nullableLong("fieldId"), workDate = getLong("workDate"),
        sizeBigha = getDouble("sizeBigha"), ratePerBigha = getDouble("ratePerBigha"),
        rounds = optInt("rounds", 1), workType = optString("workType", "Ploughing"),
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

    private fun JSONObject.settings() = AppSettings(
        theme = runCatching { ThemePreference.valueOf(optString("theme", "SYSTEM")) }
            .getOrDefault(ThemePreference.SYSTEM),
        defaultRate = optDouble("defaultRate", 800.0),
        ratePresets = optString("ratePresets", "700,800,900,1000"),
        businessName = optString("businessName", "Harvest Pay"),
        ownerName = optString("ownerName"), ownerMobile = optString("ownerMobile"),
        tractorNumber = optString("tractorNumber"), address = optString("address"),
        village = optString("village"), autoLockMinutes = optInt("autoLockMinutes", 15),
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
