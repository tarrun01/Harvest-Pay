package com.harvestpay.app

import android.app.Application
import com.harvestpay.app.data.AppDatabase
import com.harvestpay.app.data.HarvestRepository
import com.harvestpay.app.data.SettingsRepository

class HarvestPayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        com.harvestpay.app.localization.AppText.initialize(this)
    }
    val measurementStore by lazy { com.harvestpay.app.measurement.MeasurementStore(this) }
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { HarvestRepository(database) }
    val settingsRepository by lazy { SettingsRepository(this) }
}
