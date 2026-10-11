package com.harvestpay.app.measurement

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import android.content.Context
import android.util.AtomicFile
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MeasurementMethod { GPS, SATELLITE }
enum class MeasurementStage { MEASURING, REVIEW, ACCEPTED }
data class MeasurementDraft(
    val method: MeasurementMethod = MeasurementMethod.GPS,
    val points: List<GeoPoint> = emptyList(),
    val originalTrack: List<GeoPoint> = emptyList(),
    val stage: MeasurementStage = MeasurementStage.MEASURING,
    val recording: Boolean = false,
    val measuredAt: Long = 0,
    val factorUsed: Double? = null,
    val assignedCustomerId: Long? = null,
    val interrupted: Boolean = false,
    val undo: List<List<GeoPoint>> = emptyList(),
) {
    val areaSquareMeters get() = FieldGeometry.area(points)
    val acres get() = FieldGeometry.acres(areaSquareMeters)
}
fun encodeBoundary(points: List<GeoPoint>): String = JSONArray().apply {
    points.forEach { put(JSONArray().put(it.latitude).put(it.longitude)) }
}.toString()
fun decodeBoundary(json: String): List<GeoPoint> = runCatching {
    val a = JSONArray(json)
    List(a.length()) { i -> a.getJSONArray(i).let { GeoPoint(it.getDouble(0),it.getDouble(1)) } }
}.getOrDefault(emptyList())

/** One private local draft. Serialized writes cannot overwrite newer state. */
class MeasurementStore(context: Context) {
    private val file = AtomicFile(java.io.File(context.filesDir, "field_measurement_draft.json"))
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val writes = Channel<MeasurementDraft?>(Channel.CONFLATED)
    private val state = MutableStateFlow(read())
    val draft = state.asStateFlow()
    val fix = MutableStateFlow<GpsSample?>(null)
    val gpsMessage = MutableStateFlow(AppText.text(R.string.text_waiting_for_an_accurate_gps_fix))
    val persistenceError = MutableStateFlow<String?>(null)
    var serviceRunning = false
    init {
        scope.launch {
            for (value in writes) {
                try {
                    if (value == null) file.delete() else {
                        val json = JSONObject().put("method",value.method.name).put("points",encodeBoundary(value.points))
                            .put("track",encodeBoundary(value.originalTrack)).put("stage",value.stage.name)
                            .put("recording",value.recording).put("measuredAt",value.measuredAt)
                            .put("factor",value.factorUsed).put("customerId",value.assignedCustomerId)
                            .put("undo",JSONArray().apply { value.undo.forEach { put(encodeBoundary(it)) } })
                        val stream = file.startWrite()
                        try { stream.write(json.toString().toByteArray()); file.finishWrite(stream) }
                        catch (e: Exception) { file.failWrite(stream); throw e }
                    }
                    persistenceError.value = null
                } catch (_: Exception) { persistenceError.value = AppText.text(R.string.text_could_not_preserve_this_draft_keep_the_app_open_until_t) }
            }
        }
    }
    private fun read(): MeasurementDraft? = runCatching {
        val json = JSONObject(file.openRead().bufferedReader().use { it.readText() })
        MeasurementDraft(
            method = MeasurementMethod.valueOf(json.getString("method")),
            points = decodeBoundary(json.getString("points")), originalTrack = decodeBoundary(json.optString("track","[]")),
            stage = MeasurementStage.valueOf(json.getString("stage")), recording = false,
            measuredAt = json.optLong("measuredAt"), factorUsed = if (json.isNull("factor")) null else json.getDouble("factor"),
            assignedCustomerId = if (json.isNull("customerId")) null else json.getLong("customerId"),
            interrupted = json.optBoolean("recording"),
            undo = json.optJSONArray("undo")?.let { a -> List(a.length()) { decodeBoundary(a.getString(it)) } }.orEmpty(),
        )
    }.getOrNull()
    fun begin(method: MeasurementMethod) = set(MeasurementDraft(method=method))
    fun set(value: MeasurementDraft?) { state.value=value; writes.trySend(value) }
    fun update(block: (MeasurementDraft) -> MeasurementDraft) { state.value?.let { set(block(it)) } }
    private var lastEditAt = 0L
    private var lastEditIndex: Int? = null
    fun edit(points: List<GeoPoint>) = update { draft ->
        val changed = if (draft.points.size == points.size) points.indices.filter { points[it] != draft.points[it] } else emptyList()
        val index = changed.singleOrNull()
        val now = android.os.SystemClock.elapsedRealtime()
        val coalesce = index != null && index == lastEditIndex && now - lastEditAt < 250
        lastEditAt = now; lastEditIndex = index
        draft.copy(points=points, undo=if(coalesce) draft.undo else (draft.undo+listOf(draft.points)).takeLast(30))
    }
    fun undo() { lastEditIndex=null; update { if (it.undo.isEmpty()) it else it.copy(points=it.undo.last(),undo=it.undo.dropLast(1)) } }
    fun clear() = set(null)
}
