package com.harvestpay.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.harvestpay.app.security.LocalAuth
import com.harvestpay.app.security.PasswordCredential
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.harvestDataStore by preferencesDataStore(name = "harvest_pay_settings")

enum class ThemePreference { SYSTEM, LIGHT, DARK }
enum class HoursUpdateFrequency { WEEKLY, MONTHLY }
enum class BiometricLockTiming(val millis: Long, val label: String) {
    IMMEDIATE(0L, "Immediately"),
    SECONDS_15(15_000L, "After 15 seconds"),
    SECONDS_30(30_000L, "After 30 seconds"),
    MINUTE_1(60_000L, "After 1 minute"),
    MINUTES_2(120_000L, "After 2 minutes"),
    MINUTES_5(300_000L, "After 5 minutes"),
    MINUTES_10(600_000L, "After 10 minutes"),
    MINUTES_30(1_800_000L, "After 30 minutes"),
}

data class AppSettings(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val defaultRate: Double = 800.0,
    val ratePresets: String = "700,800,900,1000",
    val businessName: String = "Harvest Pay",
    val ownerName: String = "",
    val ownerMobile: String = "",
    val tractorNumber: String = "",
    val address: String = "",
    val village: String = "",
    val rememberLogin: Boolean = false,
    val rememberedAuthenticated: Boolean = false,
    val autoLockMinutes: Int = 15,
    val serviceReminderEnabled: Boolean = false,
    val currentTractorHours: Double = 0.0,
    val serviceIntervalHours: Double = 500.0,
    val lastServiceHours: Double = 0.0,
    val hoursUpdateFrequency: HoursUpdateFrequency = HoursUpdateFrequency.WEEKLY,
    val lastHoursUpdatedAt: Long = 0L,
    val serviceHistory: String = "",
    val biometricLockEnabled: Boolean = false,
    val biometricLockTiming: BiometricLockTiming = BiometricLockTiming.MINUTES_2,
    val lastHoursReminderDeferredAt: Long = 0L,
    val bighaPerAcre: Double? = null,
    val gpsMaximumAccuracyMeters: Double = 15.0,
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val bighaPerAcre = doublePreferencesKey("bigha_per_acre")
        val gpsAccuracy = doublePreferencesKey("gps_maximum_accuracy_meters")
        val theme = stringPreferencesKey("theme")
        val defaultRate = doublePreferencesKey("default_rate")
        val ratePresets = stringPreferencesKey("rate_presets")
        val businessName = stringPreferencesKey("business_name")
        val ownerName = stringPreferencesKey("owner_name")
        val ownerMobile = stringPreferencesKey("owner_mobile")
        val tractorNumber = stringPreferencesKey("tractor_number")
        val address = stringPreferencesKey("owner_address")
        val village = stringPreferencesKey("owner_village")
        val rememberLogin = booleanPreferencesKey("remember_login")
        val rememberedAuthenticated = booleanPreferencesKey("remembered_authenticated")
        val autoLockMinutes = intPreferencesKey("auto_lock_minutes")
        val passwordSalt = stringPreferencesKey("owner_password_salt")
        val passwordHash = stringPreferencesKey("owner_password_hash")
        val passwordAlgorithm = stringPreferencesKey("owner_password_algorithm")
        val serviceReminderEnabled = booleanPreferencesKey("service_reminder_enabled")
        val currentTractorHours = doublePreferencesKey("current_tractor_hours")
        val serviceIntervalHours = doublePreferencesKey("service_interval_hours")
        val lastServiceHours = doublePreferencesKey("last_service_hours")
        val hoursUpdateFrequency = stringPreferencesKey("hours_update_frequency")
        val lastHoursUpdatedAt = longPreferencesKey("last_hours_updated_at")
        val serviceHistory = stringPreferencesKey("service_history")
        val biometricLockEnabled = booleanPreferencesKey("biometric_lock_enabled")
        val biometricLockTiming = stringPreferencesKey("biometric_lock_timing")
        val lastHoursReminderDeferredAt = longPreferencesKey("last_hours_reminder_deferred_at")
    }

    val settings: Flow<AppSettings> = context.harvestDataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map(::toSettings)

    suspend fun saveLandSettings(factor: Double, accuracy: Double) {
        require(factor.isFinite() && factor > 0 && accuracy.isFinite() && accuracy in 3.0..50.0)
        edit { it[Keys.bighaPerAcre] = factor; it[Keys.gpsAccuracy] = accuracy }
    }

    suspend fun setTheme(theme: ThemePreference) = edit { it[Keys.theme] = theme.name }
    suspend fun setBiometricLockEnabled(enabled: Boolean) = edit { it[Keys.biometricLockEnabled] = enabled }
    suspend fun setBiometricLockTiming(timing: BiometricLockTiming) = edit { it[Keys.biometricLockTiming] = timing.name }
    suspend fun deferHoursReminder(at: Long) = edit { it[Keys.lastHoursReminderDeferredAt] = at }
    suspend fun clearHoursReminderDeferral() = edit { it.remove(Keys.lastHoursReminderDeferredAt) }

    suspend fun saveBusinessSettings(settings: AppSettings) = edit {
        it[Keys.defaultRate] = settings.defaultRate
        it[Keys.ratePresets] = settings.ratePresets
        it[Keys.businessName] = settings.businessName
        it[Keys.ownerName] = settings.ownerName
        it[Keys.ownerMobile] = settings.ownerMobile
        it[Keys.tractorNumber] = settings.tractorNumber
        it[Keys.address] = settings.address
        it[Keys.village] = settings.village
        it[Keys.autoLockMinutes] = settings.autoLockMinutes
    }

    suspend fun saveServiceSettings(settings: AppSettings) = edit {
        it[Keys.serviceReminderEnabled] = settings.serviceReminderEnabled
        it[Keys.currentTractorHours] = settings.currentTractorHours
        it[Keys.serviceIntervalHours] = settings.serviceIntervalHours
        it[Keys.lastServiceHours] = settings.lastServiceHours
        it[Keys.hoursUpdateFrequency] = settings.hoursUpdateFrequency.name
        it[Keys.lastHoursUpdatedAt] = settings.lastHoursUpdatedAt
        it[Keys.serviceHistory] = settings.serviceHistory
    }

    suspend fun setAuthentication(remember: Boolean, authenticated: Boolean) = edit {
        it[Keys.rememberLogin] = remember
        it[Keys.rememberedAuthenticated] = remember && authenticated
    }

    suspend fun clearAuthentication() = edit {
        it[Keys.rememberedAuthenticated] = false
        it[Keys.rememberLogin] = false
    }

    suspend fun passwordCredential(): PasswordCredential? {
        val preferences = context.harvestDataStore.data.first()
        val salt = preferences[Keys.passwordSalt]
        val hash = preferences[Keys.passwordHash]
        return if (salt.isNullOrBlank() || hash.isNullOrBlank()) {
            null
        } else {
            PasswordCredential(
                saltBase64 = salt,
                hashBase64 = hash,
                algorithm = preferences[Keys.passwordAlgorithm] ?: LocalAuth.ALGORITHM_PBKDF2,
            )
        }
    }

    suspend fun setPasswordCredential(credential: PasswordCredential) = edit {
        it[Keys.passwordSalt] = credential.saltBase64
        it[Keys.passwordHash] = credential.hashBase64
        it[Keys.passwordAlgorithm] = credential.algorithm
    }

    suspend fun restoreSettings(settings: AppSettings) = edit {
        val factor = settings.bighaPerAcre
        if (factor != null && factor.isFinite() && factor > 0) it[Keys.bighaPerAcre] = factor else it.remove(Keys.bighaPerAcre)
        it[Keys.gpsAccuracy] = settings.gpsMaximumAccuracyMeters
        it[Keys.theme] = settings.theme.name
        it[Keys.defaultRate] = settings.defaultRate
        it[Keys.ratePresets] = settings.ratePresets
        it[Keys.businessName] = settings.businessName
        it[Keys.ownerName] = settings.ownerName
        it[Keys.ownerMobile] = settings.ownerMobile
        it[Keys.tractorNumber] = settings.tractorNumber
        it[Keys.address] = settings.address
        it[Keys.village] = settings.village
        it[Keys.autoLockMinutes] = settings.autoLockMinutes
        it[Keys.serviceReminderEnabled] = settings.serviceReminderEnabled
        it[Keys.currentTractorHours] = settings.currentTractorHours
        it[Keys.serviceIntervalHours] = settings.serviceIntervalHours
        it[Keys.lastServiceHours] = settings.lastServiceHours
        it[Keys.hoursUpdateFrequency] = settings.hoursUpdateFrequency.name
        it[Keys.lastHoursUpdatedAt] = settings.lastHoursUpdatedAt
        it[Keys.serviceHistory] = settings.serviceHistory
    }

    private suspend fun edit(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.harvestDataStore.edit { preferences -> block(preferences) }
    }

    private fun toSettings(prefs: Preferences) = AppSettings(
        bighaPerAcre = prefs[Keys.bighaPerAcre]?.takeIf { it.isFinite() && it > 0 },
        gpsMaximumAccuracyMeters = prefs[Keys.gpsAccuracy]?.takeIf { it.isFinite() && it in 3.0..50.0 } ?: 15.0,
        theme = runCatching { ThemePreference.valueOf(prefs[Keys.theme] ?: "SYSTEM") }
            .getOrDefault(ThemePreference.SYSTEM),
        defaultRate = prefs[Keys.defaultRate] ?: 800.0,
        ratePresets = prefs[Keys.ratePresets] ?: "700,800,900,1000",
        businessName = prefs[Keys.businessName] ?: "Harvest Pay",
        ownerName = prefs[Keys.ownerName] ?: "",
        ownerMobile = prefs[Keys.ownerMobile] ?: "",
        tractorNumber = prefs[Keys.tractorNumber] ?: "",
        address = prefs[Keys.address] ?: "",
        village = prefs[Keys.village] ?: "",
        rememberLogin = prefs[Keys.rememberLogin] ?: false,
        rememberedAuthenticated = prefs[Keys.rememberedAuthenticated] ?: false,
        autoLockMinutes = prefs[Keys.autoLockMinutes] ?: 15,
        serviceReminderEnabled = prefs[Keys.serviceReminderEnabled] ?: false,
        currentTractorHours = prefs[Keys.currentTractorHours] ?: 0.0,
        serviceIntervalHours = prefs[Keys.serviceIntervalHours] ?: 500.0,
        lastServiceHours = prefs[Keys.lastServiceHours] ?: 0.0,
        hoursUpdateFrequency = runCatching { HoursUpdateFrequency.valueOf(prefs[Keys.hoursUpdateFrequency] ?: "WEEKLY") }.getOrDefault(HoursUpdateFrequency.WEEKLY),
        lastHoursUpdatedAt = prefs[Keys.lastHoursUpdatedAt] ?: 0L,
        serviceHistory = prefs[Keys.serviceHistory] ?: "",
        biometricLockEnabled = prefs[Keys.biometricLockEnabled] ?: false,
        biometricLockTiming = runCatching { BiometricLockTiming.valueOf(prefs[Keys.biometricLockTiming] ?: "MINUTES_2") }.getOrDefault(BiometricLockTiming.MINUTES_2),
        lastHoursReminderDeferredAt = prefs[Keys.lastHoursReminderDeferredAt] ?: 0L,
    )
}
