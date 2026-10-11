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
        YearArchiveEntity::class,
        CustomerYearArchiveEntity::class,
    ],
    version = 8,
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP INDEX IF EXISTS `index_customers_mobile`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_customers_mobile` ON `customers` (`mobile`)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `customers` ADD COLUMN `nickname` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `work_types` ADD COLUMN `hindiName` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `work_types` ADD COLUMN `billingType` TEXT NOT NULL DEFAULT 'PER_BIGHA'")
                db.execSQL("ALTER TABLE `work_entries` ADD COLUMN `billingType` TEXT NOT NULL DEFAULT 'PER_BIGHA'")
                db.execSQL("ALTER TABLE `work_entries` ADD COLUMN `durationMinutes` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `work_entries` ADD COLUMN `workTypeHindi` TEXT NOT NULL DEFAULT ''")
                db.execSQL("""UPDATE `work_types` SET `hindiName` = CASE LOWER(`name`) WHEN 'cultivating' THEN 'कल्टीवेटर' WHEN 'rotavator' THEN 'रोटावेटर' WHEN 'rotavating' THEN 'रोटावेटर' WHEN 'ploughing' THEN 'जुताई' WHEN 'sowing' THEN 'बुवाई' ELSE '' END""")
                db.execSQL("""UPDATE `work_entries` SET `workTypeHindi` = CASE LOWER(`workType`) WHEN 'cultivating' THEN 'कल्टीवेटर' WHEN 'rotavator' THEN 'रोटावेटर' WHEN 'rotavating' THEN 'रोटावेटर' WHEN 'ploughing' THEN 'जुताई' WHEN 'sowing' THEN 'बुवाई' ELSE '' END""")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `year_archives` (
                        `year` INTEGER NOT NULL,
                        `archivedAt` INTEGER NOT NULL,
                        `carryForwardCompleted` INTEGER NOT NULL,
                        PRIMARY KEY(`year`)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `customer_year_archives` (
                        `customerId` INTEGER NOT NULL,
                        `year` INTEGER NOT NULL,
                        `openingOutstanding` REAL NOT NULL,
                        `openingAdvance` REAL NOT NULL,
                        `workTotal` REAL NOT NULL,
                        `paymentsTotal` REAL NOT NULL,
                        `closingOutstanding` REAL NOT NULL,
                        `closingAdvance` REAL NOT NULL,
                        `archivedAt` INTEGER NOT NULL,
                        `carryForwardCompleted` INTEGER NOT NULL,
                        PRIMARY KEY(`customerId`, `year`),
                        FOREIGN KEY(`customerId`) REFERENCES `customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_year_archives_customerId` ON `customer_year_archives` (`customerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_year_archives_year` ON `customer_year_archives` (`year`)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `fields` ADD COLUMN `measurementMethod` TEXT NOT NULL DEFAULT 'MANUAL'")
                listOf("measuredAreaSquareMeters", "measuredAreaAcres", "measurementBighaPerAcre").forEach {
                    db.execSQL("ALTER TABLE `fields` ADD COLUMN `$it` REAL")
                }
                db.execSQL("ALTER TABLE `fields` ADD COLUMN `measuredAt` INTEGER")
                db.execSQL("ALTER TABLE `fields` ADD COLUMN `boundaryCoordinates` TEXT NOT NULL DEFAULT '[]'")
                db.execSQL("ALTER TABLE `fields` ADD COLUMN `originalGpsTrack` TEXT NOT NULL DEFAULT '[]'")
            }
        }

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "harvest_pay.db",
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .build()
                .also { instance = it }
        }
    }
}
