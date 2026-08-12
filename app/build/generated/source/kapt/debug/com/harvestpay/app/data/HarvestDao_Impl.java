package com.harvestpay.app.data;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class HarvestDao_Impl implements HarvestDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<CustomerEntity> __insertAdapterOfCustomerEntity;

  private final EntityInsertAdapter<CustomerEntity> __insertAdapterOfCustomerEntity_1;

  private final EntityInsertAdapter<FieldEntity> __insertAdapterOfFieldEntity;

  private final EntityInsertAdapter<FieldEntity> __insertAdapterOfFieldEntity_1;

  private final EntityInsertAdapter<WorkEntryEntity> __insertAdapterOfWorkEntryEntity;

  private final EntityInsertAdapter<WorkEntryEntity> __insertAdapterOfWorkEntryEntity_1;

  private final EntityInsertAdapter<PaymentEntity> __insertAdapterOfPaymentEntity;

  private final EntityInsertAdapter<PaymentEntity> __insertAdapterOfPaymentEntity_1;

  private final EntityInsertAdapter<ReminderEntity> __insertAdapterOfReminderEntity;

  private final EntityInsertAdapter<ReminderEntity> __insertAdapterOfReminderEntity_1;

  private final EntityDeleteOrUpdateAdapter<CustomerEntity> __deleteAdapterOfCustomerEntity;

  private final EntityDeleteOrUpdateAdapter<FieldEntity> __deleteAdapterOfFieldEntity;

  private final EntityDeleteOrUpdateAdapter<WorkEntryEntity> __deleteAdapterOfWorkEntryEntity;

  private final EntityDeleteOrUpdateAdapter<PaymentEntity> __deleteAdapterOfPaymentEntity;

  private final EntityDeleteOrUpdateAdapter<ReminderEntity> __deleteAdapterOfReminderEntity;

  private final EntityDeleteOrUpdateAdapter<CustomerEntity> __updateAdapterOfCustomerEntity;

  private final EntityDeleteOrUpdateAdapter<FieldEntity> __updateAdapterOfFieldEntity;

  private final EntityDeleteOrUpdateAdapter<WorkEntryEntity> __updateAdapterOfWorkEntryEntity;

  private final EntityDeleteOrUpdateAdapter<PaymentEntity> __updateAdapterOfPaymentEntity;

  private final EntityDeleteOrUpdateAdapter<ReminderEntity> __updateAdapterOfReminderEntity;

  public HarvestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCustomerEntity = new EntityInsertAdapter<CustomerEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `customers` (`id`,`name`,`mobile`,`address`,`village`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final CustomerEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getMobile() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMobile());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAddress());
        }
        if (entity.getVillage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getVillage());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getNotes());
        }
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__insertAdapterOfCustomerEntity_1 = new EntityInsertAdapter<CustomerEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `customers` (`id`,`name`,`mobile`,`address`,`village`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final CustomerEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getMobile() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMobile());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAddress());
        }
        if (entity.getVillage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getVillage());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getNotes());
        }
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__insertAdapterOfFieldEntity = new EntityInsertAdapter<FieldEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `fields` (`id`,`customerId`,`fieldName`,`fieldNumber`,`location`,`sizeBigha`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final FieldEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getFieldName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getFieldName());
        }
        if (entity.getFieldNumber() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getFieldNumber());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getLocation());
        }
        statement.bindDouble(6, entity.getSizeBigha());
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
      }
    };
    this.__insertAdapterOfFieldEntity_1 = new EntityInsertAdapter<FieldEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `fields` (`id`,`customerId`,`fieldName`,`fieldNumber`,`location`,`sizeBigha`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final FieldEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getFieldName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getFieldName());
        }
        if (entity.getFieldNumber() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getFieldNumber());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getLocation());
        }
        statement.bindDouble(6, entity.getSizeBigha());
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
      }
    };
    this.__insertAdapterOfWorkEntryEntity = new EntityInsertAdapter<WorkEntryEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `work_entries` (`id`,`customerId`,`fieldId`,`workDate`,`sizeBigha`,`ratePerBigha`,`rounds`,`workType`,`subtotal`,`discount`,`extraCharges`,`totalAmount`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final WorkEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getFieldId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getFieldId());
        }
        statement.bindLong(4, entity.getWorkDate());
        statement.bindDouble(5, entity.getSizeBigha());
        statement.bindDouble(6, entity.getRatePerBigha());
        statement.bindLong(7, entity.getRounds());
        if (entity.getWorkType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getWorkType());
        }
        statement.bindDouble(9, entity.getSubtotal());
        statement.bindDouble(10, entity.getDiscount());
        statement.bindDouble(11, entity.getExtraCharges());
        statement.bindDouble(12, entity.getTotalAmount());
        if (entity.getNotes() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getNotes());
        }
        statement.bindLong(14, entity.getCreatedAt());
      }
    };
    this.__insertAdapterOfWorkEntryEntity_1 = new EntityInsertAdapter<WorkEntryEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `work_entries` (`id`,`customerId`,`fieldId`,`workDate`,`sizeBigha`,`ratePerBigha`,`rounds`,`workType`,`subtotal`,`discount`,`extraCharges`,`totalAmount`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final WorkEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getFieldId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getFieldId());
        }
        statement.bindLong(4, entity.getWorkDate());
        statement.bindDouble(5, entity.getSizeBigha());
        statement.bindDouble(6, entity.getRatePerBigha());
        statement.bindLong(7, entity.getRounds());
        if (entity.getWorkType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getWorkType());
        }
        statement.bindDouble(9, entity.getSubtotal());
        statement.bindDouble(10, entity.getDiscount());
        statement.bindDouble(11, entity.getExtraCharges());
        statement.bindDouble(12, entity.getTotalAmount());
        if (entity.getNotes() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getNotes());
        }
        statement.bindLong(14, entity.getCreatedAt());
      }
    };
    this.__insertAdapterOfPaymentEntity = new EntityInsertAdapter<PaymentEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `payments` (`id`,`customerId`,`workEntryId`,`paymentDate`,`amount`,`paymentMethod`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PaymentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getWorkEntryId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getWorkEntryId());
        }
        statement.bindLong(4, entity.getPaymentDate());
        statement.bindDouble(5, entity.getAmount());
        if (entity.getPaymentMethod() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getPaymentMethod());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__insertAdapterOfPaymentEntity_1 = new EntityInsertAdapter<PaymentEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payments` (`id`,`customerId`,`workEntryId`,`paymentDate`,`amount`,`paymentMethod`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PaymentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getWorkEntryId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getWorkEntryId());
        }
        statement.bindLong(4, entity.getPaymentDate());
        statement.bindDouble(5, entity.getAmount());
        if (entity.getPaymentMethod() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getPaymentMethod());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__insertAdapterOfReminderEntity = new EntityInsertAdapter<ReminderEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reminders` (`id`,`customerId`,`reminderAt`,`message`,`completed`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ReminderEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        statement.bindLong(3, entity.getReminderAt());
        if (entity.getMessage() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getMessage());
        }
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__insertAdapterOfReminderEntity_1 = new EntityInsertAdapter<ReminderEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reminders` (`id`,`customerId`,`reminderAt`,`message`,`completed`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ReminderEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        statement.bindLong(3, entity.getReminderAt());
        if (entity.getMessage() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getMessage());
        }
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__deleteAdapterOfCustomerEntity = new EntityDeleteOrUpdateAdapter<CustomerEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `customers` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final CustomerEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deleteAdapterOfFieldEntity = new EntityDeleteOrUpdateAdapter<FieldEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `fields` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final FieldEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deleteAdapterOfWorkEntryEntity = new EntityDeleteOrUpdateAdapter<WorkEntryEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `work_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final WorkEntryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deleteAdapterOfPaymentEntity = new EntityDeleteOrUpdateAdapter<PaymentEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `payments` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PaymentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deleteAdapterOfReminderEntity = new EntityDeleteOrUpdateAdapter<ReminderEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reminders` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ReminderEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCustomerEntity = new EntityDeleteOrUpdateAdapter<CustomerEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `customers` SET `id` = ?,`name` = ?,`mobile` = ?,`address` = ?,`village` = ?,`notes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final CustomerEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getMobile() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMobile());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getAddress());
        }
        if (entity.getVillage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getVillage());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getNotes());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__updateAdapterOfFieldEntity = new EntityDeleteOrUpdateAdapter<FieldEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `fields` SET `id` = ?,`customerId` = ?,`fieldName` = ?,`fieldNumber` = ?,`location` = ?,`sizeBigha` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final FieldEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getFieldName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getFieldName());
        }
        if (entity.getFieldNumber() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getFieldNumber());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getLocation());
        }
        statement.bindDouble(6, entity.getSizeBigha());
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
        statement.bindLong(8, entity.getId());
      }
    };
    this.__updateAdapterOfWorkEntryEntity = new EntityDeleteOrUpdateAdapter<WorkEntryEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `work_entries` SET `id` = ?,`customerId` = ?,`fieldId` = ?,`workDate` = ?,`sizeBigha` = ?,`ratePerBigha` = ?,`rounds` = ?,`workType` = ?,`subtotal` = ?,`discount` = ?,`extraCharges` = ?,`totalAmount` = ?,`notes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final WorkEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getFieldId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getFieldId());
        }
        statement.bindLong(4, entity.getWorkDate());
        statement.bindDouble(5, entity.getSizeBigha());
        statement.bindDouble(6, entity.getRatePerBigha());
        statement.bindLong(7, entity.getRounds());
        if (entity.getWorkType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getWorkType());
        }
        statement.bindDouble(9, entity.getSubtotal());
        statement.bindDouble(10, entity.getDiscount());
        statement.bindDouble(11, entity.getExtraCharges());
        statement.bindDouble(12, entity.getTotalAmount());
        if (entity.getNotes() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getNotes());
        }
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getId());
      }
    };
    this.__updateAdapterOfPaymentEntity = new EntityDeleteOrUpdateAdapter<PaymentEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `payments` SET `id` = ?,`customerId` = ?,`workEntryId` = ?,`paymentDate` = ?,`amount` = ?,`paymentMethod` = ?,`notes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PaymentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getWorkEntryId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getWorkEntryId());
        }
        statement.bindLong(4, entity.getPaymentDate());
        statement.bindDouble(5, entity.getAmount());
        if (entity.getPaymentMethod() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getPaymentMethod());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getNotes());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__updateAdapterOfReminderEntity = new EntityDeleteOrUpdateAdapter<ReminderEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reminders` SET `id` = ?,`customerId` = ?,`reminderAt` = ?,`message` = ?,`completed` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ReminderEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        statement.bindLong(3, entity.getReminderAt());
        if (entity.getMessage() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getMessage());
        }
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getId());
      }
    };
  }

  @Override
  public Object insertCustomer(final CustomerEntity customer,
      final Continuation<? super Long> $completion) {
    if (customer == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfCustomerEntity.insertAndReturnId(_connection, customer);
    }, $completion);
  }

  @Override
  public Object insertCustomers(final List<CustomerEntity> customers,
      final Continuation<? super Unit> $completion) {
    if (customers == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfCustomerEntity_1.insert(_connection, customers);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertField(final FieldEntity field, final Continuation<? super Long> $completion) {
    if (field == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfFieldEntity.insertAndReturnId(_connection, field);
    }, $completion);
  }

  @Override
  public Object insertFields(final List<FieldEntity> fields,
      final Continuation<? super Unit> $completion) {
    if (fields == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfFieldEntity_1.insert(_connection, fields);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertWorkEntry(final WorkEntryEntity workEntry,
      final Continuation<? super Long> $completion) {
    if (workEntry == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfWorkEntryEntity.insertAndReturnId(_connection, workEntry);
    }, $completion);
  }

  @Override
  public Object insertWorkEntries(final List<WorkEntryEntity> workEntries,
      final Continuation<? super Unit> $completion) {
    if (workEntries == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfWorkEntryEntity_1.insert(_connection, workEntries);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertPayment(final PaymentEntity payment,
      final Continuation<? super Long> $completion) {
    if (payment == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfPaymentEntity.insertAndReturnId(_connection, payment);
    }, $completion);
  }

  @Override
  public Object insertPayments(final List<PaymentEntity> payments,
      final Continuation<? super Unit> $completion) {
    if (payments == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfPaymentEntity_1.insert(_connection, payments);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertReminder(final ReminderEntity reminder,
      final Continuation<? super Long> $completion) {
    if (reminder == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfReminderEntity.insertAndReturnId(_connection, reminder);
    }, $completion);
  }

  @Override
  public Object insertReminders(final List<ReminderEntity> reminders,
      final Continuation<? super Unit> $completion) {
    if (reminders == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfReminderEntity_1.insert(_connection, reminders);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object deleteCustomer(final CustomerEntity customer,
      final Continuation<? super Unit> $completion) {
    if (customer == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfCustomerEntity.handle(_connection, customer);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object deleteField(final FieldEntity field, final Continuation<? super Unit> $completion) {
    if (field == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfFieldEntity.handle(_connection, field);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object deleteWorkEntry(final WorkEntryEntity workEntry,
      final Continuation<? super Unit> $completion) {
    if (workEntry == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfWorkEntryEntity.handle(_connection, workEntry);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object deletePayment(final PaymentEntity payment,
      final Continuation<? super Unit> $completion) {
    if (payment == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfPaymentEntity.handle(_connection, payment);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object deleteReminder(final ReminderEntity reminder,
      final Continuation<? super Unit> $completion) {
    if (reminder == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfReminderEntity.handle(_connection, reminder);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateCustomer(final CustomerEntity customer,
      final Continuation<? super Unit> $completion) {
    if (customer == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfCustomerEntity.handle(_connection, customer);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateField(final FieldEntity field, final Continuation<? super Unit> $completion) {
    if (field == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfFieldEntity.handle(_connection, field);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateWorkEntry(final WorkEntryEntity workEntry,
      final Continuation<? super Unit> $completion) {
    if (workEntry == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfWorkEntryEntity.handle(_connection, workEntry);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updatePayment(final PaymentEntity payment,
      final Continuation<? super Unit> $completion) {
    if (payment == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfPaymentEntity.handle(_connection, payment);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateReminder(final ReminderEntity reminder,
      final Continuation<? super Unit> $completion) {
    if (reminder == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfReminderEntity.handle(_connection, reminder);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<CustomerEntity>> observeCustomers() {
    final String _sql = "SELECT * FROM customers ORDER BY createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"customers"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfMobile = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mobile");
        final int _columnIndexOfAddress = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "address");
        final int _columnIndexOfVillage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "village");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<CustomerEntity> _result = new ArrayList<CustomerEntity>();
        while (_stmt.step()) {
          final CustomerEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpMobile;
          if (_stmt.isNull(_columnIndexOfMobile)) {
            _tmpMobile = null;
          } else {
            _tmpMobile = _stmt.getText(_columnIndexOfMobile);
          }
          final String _tmpAddress;
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null;
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress);
          }
          final String _tmpVillage;
          if (_stmt.isNull(_columnIndexOfVillage)) {
            _tmpVillage = null;
          } else {
            _tmpVillage = _stmt.getText(_columnIndexOfVillage);
          }
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new CustomerEntity(_tmpId,_tmpName,_tmpMobile,_tmpAddress,_tmpVillage,_tmpNotes,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<FieldEntity>> observeFields() {
    final String _sql = "SELECT * FROM fields ORDER BY fieldName COLLATE NOCASE";
    return FlowUtil.createFlow(__db, false, new String[] {"fields"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerId");
        final int _columnIndexOfFieldName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fieldName");
        final int _columnIndexOfFieldNumber = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fieldNumber");
        final int _columnIndexOfLocation = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "location");
        final int _columnIndexOfSizeBigha = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sizeBigha");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final List<FieldEntity> _result = new ArrayList<FieldEntity>();
        while (_stmt.step()) {
          final FieldEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCustomerId;
          _tmpCustomerId = _stmt.getLong(_columnIndexOfCustomerId);
          final String _tmpFieldName;
          if (_stmt.isNull(_columnIndexOfFieldName)) {
            _tmpFieldName = null;
          } else {
            _tmpFieldName = _stmt.getText(_columnIndexOfFieldName);
          }
          final String _tmpFieldNumber;
          if (_stmt.isNull(_columnIndexOfFieldNumber)) {
            _tmpFieldNumber = null;
          } else {
            _tmpFieldNumber = _stmt.getText(_columnIndexOfFieldNumber);
          }
          final String _tmpLocation;
          if (_stmt.isNull(_columnIndexOfLocation)) {
            _tmpLocation = null;
          } else {
            _tmpLocation = _stmt.getText(_columnIndexOfLocation);
          }
          final double _tmpSizeBigha;
          _tmpSizeBigha = _stmt.getDouble(_columnIndexOfSizeBigha);
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          _item = new FieldEntity(_tmpId,_tmpCustomerId,_tmpFieldName,_tmpFieldNumber,_tmpLocation,_tmpSizeBigha,_tmpNotes);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<WorkEntryEntity>> observeWorkEntries() {
    final String _sql = "SELECT * FROM work_entries ORDER BY workDate DESC, createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"work_entries"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerId");
        final int _columnIndexOfFieldId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fieldId");
        final int _columnIndexOfWorkDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workDate");
        final int _columnIndexOfSizeBigha = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sizeBigha");
        final int _columnIndexOfRatePerBigha = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ratePerBigha");
        final int _columnIndexOfRounds = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "rounds");
        final int _columnIndexOfWorkType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workType");
        final int _columnIndexOfSubtotal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subtotal");
        final int _columnIndexOfDiscount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "discount");
        final int _columnIndexOfExtraCharges = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extraCharges");
        final int _columnIndexOfTotalAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalAmount");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<WorkEntryEntity> _result = new ArrayList<WorkEntryEntity>();
        while (_stmt.step()) {
          final WorkEntryEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCustomerId;
          _tmpCustomerId = _stmt.getLong(_columnIndexOfCustomerId);
          final Long _tmpFieldId;
          if (_stmt.isNull(_columnIndexOfFieldId)) {
            _tmpFieldId = null;
          } else {
            _tmpFieldId = _stmt.getLong(_columnIndexOfFieldId);
          }
          final long _tmpWorkDate;
          _tmpWorkDate = _stmt.getLong(_columnIndexOfWorkDate);
          final double _tmpSizeBigha;
          _tmpSizeBigha = _stmt.getDouble(_columnIndexOfSizeBigha);
          final double _tmpRatePerBigha;
          _tmpRatePerBigha = _stmt.getDouble(_columnIndexOfRatePerBigha);
          final int _tmpRounds;
          _tmpRounds = (int) (_stmt.getLong(_columnIndexOfRounds));
          final String _tmpWorkType;
          if (_stmt.isNull(_columnIndexOfWorkType)) {
            _tmpWorkType = null;
          } else {
            _tmpWorkType = _stmt.getText(_columnIndexOfWorkType);
          }
          final double _tmpSubtotal;
          _tmpSubtotal = _stmt.getDouble(_columnIndexOfSubtotal);
          final double _tmpDiscount;
          _tmpDiscount = _stmt.getDouble(_columnIndexOfDiscount);
          final double _tmpExtraCharges;
          _tmpExtraCharges = _stmt.getDouble(_columnIndexOfExtraCharges);
          final double _tmpTotalAmount;
          _tmpTotalAmount = _stmt.getDouble(_columnIndexOfTotalAmount);
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new WorkEntryEntity(_tmpId,_tmpCustomerId,_tmpFieldId,_tmpWorkDate,_tmpSizeBigha,_tmpRatePerBigha,_tmpRounds,_tmpWorkType,_tmpSubtotal,_tmpDiscount,_tmpExtraCharges,_tmpTotalAmount,_tmpNotes,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<PaymentEntity>> observePayments() {
    final String _sql = "SELECT * FROM payments ORDER BY paymentDate DESC, createdAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"payments"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerId");
        final int _columnIndexOfWorkEntryId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workEntryId");
        final int _columnIndexOfPaymentDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paymentDate");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfPaymentMethod = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "paymentMethod");
        final int _columnIndexOfNotes = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notes");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<PaymentEntity> _result = new ArrayList<PaymentEntity>();
        while (_stmt.step()) {
          final PaymentEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCustomerId;
          _tmpCustomerId = _stmt.getLong(_columnIndexOfCustomerId);
          final Long _tmpWorkEntryId;
          if (_stmt.isNull(_columnIndexOfWorkEntryId)) {
            _tmpWorkEntryId = null;
          } else {
            _tmpWorkEntryId = _stmt.getLong(_columnIndexOfWorkEntryId);
          }
          final long _tmpPaymentDate;
          _tmpPaymentDate = _stmt.getLong(_columnIndexOfPaymentDate);
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpPaymentMethod;
          if (_stmt.isNull(_columnIndexOfPaymentMethod)) {
            _tmpPaymentMethod = null;
          } else {
            _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod);
          }
          final String _tmpNotes;
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null;
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new PaymentEntity(_tmpId,_tmpCustomerId,_tmpWorkEntryId,_tmpPaymentDate,_tmpAmount,_tmpPaymentMethod,_tmpNotes,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<ReminderEntity>> observeReminders() {
    final String _sql = "SELECT * FROM reminders WHERE completed = 0 ORDER BY reminderAt";
    return FlowUtil.createFlow(__db, false, new String[] {"reminders"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCustomerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "customerId");
        final int _columnIndexOfReminderAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "reminderAt");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final List<ReminderEntity> _result = new ArrayList<ReminderEntity>();
        while (_stmt.step()) {
          final ReminderEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCustomerId;
          _tmpCustomerId = _stmt.getLong(_columnIndexOfCustomerId);
          final long _tmpReminderAt;
          _tmpReminderAt = _stmt.getLong(_columnIndexOfReminderAt);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          final boolean _tmpCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp != 0;
          _item = new ReminderEntity(_tmpId,_tmpCustomerId,_tmpReminderAt,_tmpMessage,_tmpCompleted);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object mobileExists(final String mobile, final long excludingId,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM customers WHERE mobile = ? AND id != ?)";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (mobile == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, mobile);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, excludingId);
        final Boolean _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp == null ? null : _tmp != 0;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearReminders(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM reminders";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearPayments(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM payments";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearWorkEntries(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM work_entries";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearFields(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM fields";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearCustomers(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM customers";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
