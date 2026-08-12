package com.harvestpay.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CustomerEntity::class,
        FieldEntity::class,
        WorkEntryEntity::class,
        PaymentEntity::class,
        ReminderEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun harvestDao(): HarvestDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "harvest_pay.db",
            ).build().also { instance = it }
        }
    }
}
