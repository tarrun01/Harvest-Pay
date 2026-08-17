package com.harvestpay.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CustomerEntity::class,
        FieldEntity::class,
        WorkEntryEntity::class,
        PaymentEntity::class,
        ReminderEntity::class,
        WorkTypeEntity::class,
        DieselEntryEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun harvestDao(): HarvestDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `work_entries` ADD COLUMN `roundMultiplier` REAL NOT NULL DEFAULT 1.0",
                )
                db.execSQL("UPDATE `work_entries` SET `roundMultiplier` = CAST(`rounds` AS REAL)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `work_types` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `ratePerBigha` REAL NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_work_types_name` ON `work_types` (`name`)",
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `customers` ADD COLUMN `previousDue` REAL NOT NULL DEFAULT 0.0",
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `diesel_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `litres` REAL NOT NULL,
                        `totalAmount` REAL NOT NULL,
                        `entryDate` INTEGER NOT NULL,
                        `paymentStatus` TEXT NOT NULL,
                        `broughtBy` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_diesel_entries_entryDate` ON `diesel_entries` (`entryDate`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_diesel_entries_paymentStatus` ON `diesel_entries` (`paymentStatus`)",
                )
            }
        }

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "harvest_pay.db",
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .build()
                .also { instance = it }
        }
    }
}
