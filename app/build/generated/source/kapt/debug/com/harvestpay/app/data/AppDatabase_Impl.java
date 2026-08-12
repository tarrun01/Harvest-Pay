package com.harvestpay.app.data;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile HarvestDao _harvestDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(1, "816501a775c10b1f78985d81fc46b885", "a9cbbfa740d56c0d02decac0a3a57757") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `customers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `mobile` TEXT NOT NULL, `address` TEXT NOT NULL, `village` TEXT NOT NULL, `notes` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_customers_mobile` ON `customers` (`mobile`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_customers_name` ON `customers` (`name`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_customers_village` ON `customers` (`village`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `fields` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `fieldName` TEXT NOT NULL, `fieldNumber` TEXT NOT NULL, `location` TEXT NOT NULL, `sizeBigha` REAL NOT NULL, `notes` TEXT NOT NULL, FOREIGN KEY(`customerId`) REFERENCES `customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_fields_customerId` ON `fields` (`customerId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_fields_fieldName` ON `fields` (`fieldName`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `work_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `fieldId` INTEGER, `workDate` INTEGER NOT NULL, `sizeBigha` REAL NOT NULL, `ratePerBigha` REAL NOT NULL, `rounds` INTEGER NOT NULL, `workType` TEXT NOT NULL, `subtotal` REAL NOT NULL, `discount` REAL NOT NULL, `extraCharges` REAL NOT NULL, `totalAmount` REAL NOT NULL, `notes` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`customerId`) REFERENCES `customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`fieldId`) REFERENCES `fields`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_work_entries_customerId` ON `work_entries` (`customerId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_work_entries_fieldId` ON `work_entries` (`fieldId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_work_entries_workDate` ON `work_entries` (`workDate`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `workEntryId` INTEGER, `paymentDate` INTEGER NOT NULL, `amount` REAL NOT NULL, `paymentMethod` TEXT NOT NULL, `notes` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`customerId`) REFERENCES `customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`workEntryId`) REFERENCES `work_entries`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_payments_customerId` ON `payments` (`customerId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_payments_workEntryId` ON `payments` (`workEntryId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_payments_paymentDate` ON `payments` (`paymentDate`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `reminders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `reminderAt` INTEGER NOT NULL, `message` TEXT NOT NULL, `completed` INTEGER NOT NULL, FOREIGN KEY(`customerId`) REFERENCES `customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_reminders_customerId` ON `reminders` (`customerId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_reminders_reminderAt` ON `reminders` (`reminderAt`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '816501a775c10b1f78985d81fc46b885')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `customers`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `fields`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `work_entries`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `payments`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `reminders`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsCustomers = new HashMap<String, TableInfo.Column>(7);
        _columnsCustomers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("mobile", new TableInfo.Column("mobile", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("village", new TableInfo.Column("village", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysCustomers = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesCustomers = new HashSet<TableInfo.Index>(3);
        _indicesCustomers.add(new TableInfo.Index("index_customers_mobile", true, Arrays.asList("mobile"), Arrays.asList("ASC")));
        _indicesCustomers.add(new TableInfo.Index("index_customers_name", false, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesCustomers.add(new TableInfo.Index("index_customers_village", false, Arrays.asList("village"), Arrays.asList("ASC")));
        final TableInfo _infoCustomers = new TableInfo("customers", _columnsCustomers, _foreignKeysCustomers, _indicesCustomers);
        final TableInfo _existingCustomers = TableInfo.read(connection, "customers");
        if (!_infoCustomers.equals(_existingCustomers)) {
          return new RoomOpenDelegate.ValidationResult(false, "customers(com.harvestpay.app.data.CustomerEntity).\n"
                  + " Expected:\n" + _infoCustomers + "\n"
                  + " Found:\n" + _existingCustomers);
        }
        final Map<String, TableInfo.Column> _columnsFields = new HashMap<String, TableInfo.Column>(7);
        _columnsFields.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFields.put("customerId", new TableInfo.Column("customerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFields.put("fieldName", new TableInfo.Column("fieldName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFields.put("fieldNumber", new TableInfo.Column("fieldNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFields.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFields.put("sizeBigha", new TableInfo.Column("sizeBigha", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFields.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysFields = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFields.add(new TableInfo.ForeignKey("customers", "CASCADE", "NO ACTION", Arrays.asList("customerId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesFields = new HashSet<TableInfo.Index>(2);
        _indicesFields.add(new TableInfo.Index("index_fields_customerId", false, Arrays.asList("customerId"), Arrays.asList("ASC")));
        _indicesFields.add(new TableInfo.Index("index_fields_fieldName", false, Arrays.asList("fieldName"), Arrays.asList("ASC")));
        final TableInfo _infoFields = new TableInfo("fields", _columnsFields, _foreignKeysFields, _indicesFields);
        final TableInfo _existingFields = TableInfo.read(connection, "fields");
        if (!_infoFields.equals(_existingFields)) {
          return new RoomOpenDelegate.ValidationResult(false, "fields(com.harvestpay.app.data.FieldEntity).\n"
                  + " Expected:\n" + _infoFields + "\n"
                  + " Found:\n" + _existingFields);
        }
        final Map<String, TableInfo.Column> _columnsWorkEntries = new HashMap<String, TableInfo.Column>(14);
        _columnsWorkEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("customerId", new TableInfo.Column("customerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("fieldId", new TableInfo.Column("fieldId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("workDate", new TableInfo.Column("workDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("sizeBigha", new TableInfo.Column("sizeBigha", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("ratePerBigha", new TableInfo.Column("ratePerBigha", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("rounds", new TableInfo.Column("rounds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("workType", new TableInfo.Column("workType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("subtotal", new TableInfo.Column("subtotal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("discount", new TableInfo.Column("discount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("extraCharges", new TableInfo.Column("extraCharges", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("totalAmount", new TableInfo.Column("totalAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysWorkEntries = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysWorkEntries.add(new TableInfo.ForeignKey("customers", "CASCADE", "NO ACTION", Arrays.asList("customerId"), Arrays.asList("id")));
        _foreignKeysWorkEntries.add(new TableInfo.ForeignKey("fields", "SET NULL", "NO ACTION", Arrays.asList("fieldId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesWorkEntries = new HashSet<TableInfo.Index>(3);
        _indicesWorkEntries.add(new TableInfo.Index("index_work_entries_customerId", false, Arrays.asList("customerId"), Arrays.asList("ASC")));
        _indicesWorkEntries.add(new TableInfo.Index("index_work_entries_fieldId", false, Arrays.asList("fieldId"), Arrays.asList("ASC")));
        _indicesWorkEntries.add(new TableInfo.Index("index_work_entries_workDate", false, Arrays.asList("workDate"), Arrays.asList("ASC")));
        final TableInfo _infoWorkEntries = new TableInfo("work_entries", _columnsWorkEntries, _foreignKeysWorkEntries, _indicesWorkEntries);
        final TableInfo _existingWorkEntries = TableInfo.read(connection, "work_entries");
        if (!_infoWorkEntries.equals(_existingWorkEntries)) {
          return new RoomOpenDelegate.ValidationResult(false, "work_entries(com.harvestpay.app.data.WorkEntryEntity).\n"
                  + " Expected:\n" + _infoWorkEntries + "\n"
                  + " Found:\n" + _existingWorkEntries);
        }
        final Map<String, TableInfo.Column> _columnsPayments = new HashMap<String, TableInfo.Column>(8);
        _columnsPayments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("customerId", new TableInfo.Column("customerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("workEntryId", new TableInfo.Column("workEntryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("paymentDate", new TableInfo.Column("paymentDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("paymentMethod", new TableInfo.Column("paymentMethod", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayments.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysPayments = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPayments.add(new TableInfo.ForeignKey("customers", "CASCADE", "NO ACTION", Arrays.asList("customerId"), Arrays.asList("id")));
        _foreignKeysPayments.add(new TableInfo.ForeignKey("work_entries", "SET NULL", "NO ACTION", Arrays.asList("workEntryId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesPayments = new HashSet<TableInfo.Index>(3);
        _indicesPayments.add(new TableInfo.Index("index_payments_customerId", false, Arrays.asList("customerId"), Arrays.asList("ASC")));
        _indicesPayments.add(new TableInfo.Index("index_payments_workEntryId", false, Arrays.asList("workEntryId"), Arrays.asList("ASC")));
        _indicesPayments.add(new TableInfo.Index("index_payments_paymentDate", false, Arrays.asList("paymentDate"), Arrays.asList("ASC")));
        final TableInfo _infoPayments = new TableInfo("payments", _columnsPayments, _foreignKeysPayments, _indicesPayments);
        final TableInfo _existingPayments = TableInfo.read(connection, "payments");
        if (!_infoPayments.equals(_existingPayments)) {
          return new RoomOpenDelegate.ValidationResult(false, "payments(com.harvestpay.app.data.PaymentEntity).\n"
                  + " Expected:\n" + _infoPayments + "\n"
                  + " Found:\n" + _existingPayments);
        }
        final Map<String, TableInfo.Column> _columnsReminders = new HashMap<String, TableInfo.Column>(5);
        _columnsReminders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("customerId", new TableInfo.Column("customerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("reminderAt", new TableInfo.Column("reminderAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysReminders = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysReminders.add(new TableInfo.ForeignKey("customers", "CASCADE", "NO ACTION", Arrays.asList("customerId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesReminders = new HashSet<TableInfo.Index>(2);
        _indicesReminders.add(new TableInfo.Index("index_reminders_customerId", false, Arrays.asList("customerId"), Arrays.asList("ASC")));
        _indicesReminders.add(new TableInfo.Index("index_reminders_reminderAt", false, Arrays.asList("reminderAt"), Arrays.asList("ASC")));
        final TableInfo _infoReminders = new TableInfo("reminders", _columnsReminders, _foreignKeysReminders, _indicesReminders);
        final TableInfo _existingReminders = TableInfo.read(connection, "reminders");
        if (!_infoReminders.equals(_existingReminders)) {
          return new RoomOpenDelegate.ValidationResult(false, "reminders(com.harvestpay.app.data.ReminderEntity).\n"
                  + " Expected:\n" + _infoReminders + "\n"
                  + " Found:\n" + _existingReminders);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "customers", "fields", "work_entries", "payments", "reminders");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "customers", "fields", "work_entries", "payments", "reminders");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(HarvestDao.class, HarvestDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public HarvestDao harvestDao() {
    if (_harvestDao != null) {
      return _harvestDao;
    } else {
      synchronized(this) {
        if(_harvestDao == null) {
          _harvestDao = new HarvestDao_Impl(this);
        }
        return _harvestDao;
      }
    }
  }
}
