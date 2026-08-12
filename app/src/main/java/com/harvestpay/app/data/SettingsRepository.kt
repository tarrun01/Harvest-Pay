package com.harvestpay.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.harvestDataStore by preferencesDataStore(name = "harvest_pay_settings")

enum class ThemePreference { SYSTEM, LIGHT, DARK }

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
)

class SettingsRepository(private val context: Context) {
    private object Keys {
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
    }

    val settings: Flow<AppSettings> = context.harvestDataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map(::toSettings)

    suspend fun setTheme(theme: ThemePreference) = edit { it[Keys.theme] = theme.name }

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

    suspend fun setAuthentication(remember: Boolean, authenticated: Boolean) = edit {
        it[Keys.rememberLogin] = remember
        it[Keys.rememberedAuthenticated] = remember && authenticated
    }

    suspend fun clearAuthentication() = edit {
        it[Keys.rememberedAuthenticated] = false
        it[Keys.rememberLogin] = false
    }

    suspend fun restoreSettings(settings: AppSettings) = edit {
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
    }

    private suspend fun edit(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.harvestDataStore.edit { preferences -> block(preferences) }
    }

    private fun toSettings(prefs: Preferences) = AppSettings(
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
    )
}
