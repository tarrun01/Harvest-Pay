package com.harvestpay.app.data

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    private val databaseName = "harvest-migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

    @Test
    fun allPublishedMigrationsReachCurrentSchema() {
        (1..7).forEach { version ->
            helper.createDatabase("$databaseName-$version", version).close()
            helper.runMigrationsAndValidate(
                "$databaseName-$version",
                8,
                true,
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6,
                AppDatabase.MIGRATION_6_7,
                AppDatabase.MIGRATION_7_8,
            ).close()
        }
    }

    @Test
    fun measurementMigrationPreservesFieldSizeAndDefaultsToManual() {
        val name = "$databaseName-7-8"
        val db = helper.createDatabase(name, 7)
        db.execSQL("INSERT INTO customers (id,name,nickname,mobile,address,village,notes,previousDue,createdAt) VALUES (1,'Existing','','','','','',125.0,1)")
        db.execSQL("INSERT INTO fields (id,customerId,fieldName,fieldNumber,location,sizeBigha,notes) VALUES (1,1,'Existing field','','',4.75,'keep')")
        db.close()
        helper.runMigrationsAndValidate(name,8,true,AppDatabase.MIGRATION_7_8).use { migrated ->
            migrated.query("SELECT sizeBigha,measurementMethod,measuredAreaSquareMeters,boundaryCoordinates,notes FROM fields WHERE id=1").use {
                assertTrue(it.moveToFirst()); assertEquals(4.75,it.getDouble(0),0.0)
                assertEquals("MANUAL",it.getString(1)); assertTrue(it.isNull(2))
                assertEquals("[]",it.getString(3)); assertEquals("keep",it.getString(4))
            }
        }
    }

    @Test
    fun migrationSixToSevenPreservesLedgerAndAddsEmptyArchiveTables() {
        val name = "$databaseName-6-7"
        val db = helper.createDatabase(name, 6)
        db.execSQL("INSERT INTO customers (id, name, nickname, mobile, address, village, notes, previousDue, createdAt) VALUES (1, 'Ramesh', '', '', '', '', '', 8000.0, 1)")
        db.close()

        val migrated = helper.runMigrationsAndValidate(name, 7, true, AppDatabase.MIGRATION_6_7)
        migrated.query("SELECT name, previousDue FROM customers WHERE id = 1").use {
            assertTrue(it.moveToFirst())
            assertEquals("Ramesh", it.getString(0))
            assertEquals(8_000.0, it.getDouble(1), 0.001)
        }
        migrated.query("SELECT COUNT(*) FROM year_archives").use { assertTrue(it.moveToFirst()); assertEquals(0, it.getInt(0)) }
        migrated.query("SELECT COUNT(*) FROM customer_year_archives").use { assertTrue(it.moveToFirst()); assertEquals(0, it.getInt(0)) }
        migrated.close()
    }

    @Test
    fun migrationFiveToSixPreservesLedgerAndAddsSafeDefaults() {
        val db = helper.createDatabase(databaseName, 5)
        seedVersionFive(db)
        db.close()

        val migrated = helper.runMigrationsAndValidate(databaseName, 6, true, AppDatabase.MIGRATION_5_6)
        migrated.query("SELECT previousDue FROM customers WHERE id = 1").use {
            assertTrue(it.moveToFirst())
            assertEquals(275.0, it.getDouble(0), 0.001)
        }
        migrated.query("SELECT billingType, durationMinutes, workTypeHindi, totalAmount, roundMultiplier FROM work_entries WHERE id = 1").use {
            assertTrue(it.moveToFirst())
            assertEquals("PER_BIGHA", it.getString(0))
            assertEquals(0, it.getInt(1))
            assertEquals("जुताई", it.getString(2))
            assertEquals(1_200.0, it.getDouble(3), 0.001)
            assertEquals(1.5, it.getDouble(4), 0.001)
        }
        migrated.close()
    }

    private fun seedVersionFive(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT INTO customers (id, name, nickname, mobile, address, village, notes, previousDue, createdAt) VALUES (1, 'Ramesh', '', '', '', '', '', 275.0, 1)")
        db.execSQL("INSERT INTO work_types (id, name, ratePerBigha, createdAt) VALUES (1, 'Ploughing', 800.0, 1)")
        db.execSQL("INSERT INTO work_entries (id, customerId, fieldId, workDate, sizeBigha, ratePerBigha, rounds, roundMultiplier, workType, subtotal, discount, extraCharges, totalAmount, notes, createdAt) VALUES (1, 1, NULL, 1, 1.0, 800.0, 1, 1.5, 'Ploughing', 1200.0, 0.0, 0.0, 1200.0, '', 1)")
    }
}
