package com.harvestpay.app.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\'\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00040\u0003H\'J\u0014\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00040\u0003H\'J\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\u00040\u0003H\'J\u0014\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00040\u0003H\'J \u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u0016\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0016\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0016\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u001c\u0010\u001b\u001a\u00020\u00192\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010 J\u0016\u0010!\u001a\u00020\u00192\u0006\u0010\u001f\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010 J\u0016\u0010\"\u001a\u00020\u00192\u0006\u0010\u001f\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010 J\u001c\u0010#\u001a\u00020\u00192\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010%\u001a\u00020\u00132\u0006\u0010&\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\'J\u0016\u0010(\u001a\u00020\u00192\u0006\u0010&\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\'J\u0016\u0010)\u001a\u00020\u00192\u0006\u0010&\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\'J\u001c\u0010*\u001a\u00020\u00192\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\t0\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010,\u001a\u00020\u00132\u0006\u0010-\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010.J\u0016\u0010/\u001a\u00020\u00192\u0006\u0010-\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010.J\u0016\u00100\u001a\u00020\u00192\u0006\u0010-\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010.J\u001c\u00101\u001a\u00020\u00192\f\u00102\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u00103\u001a\u00020\u00132\u0006\u00104\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u00105J\u0016\u00106\u001a\u00020\u00192\u0006\u00104\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u00105J\u0016\u00107\u001a\u00020\u00192\u0006\u00104\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u00105J\u001c\u00108\u001a\u00020\u00192\f\u00109\u001a\b\u0012\u0004\u0012\u00020\r0\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ\u000e\u0010:\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010;J\u000e\u0010<\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010;J\u000e\u0010=\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010;J\u000e\u0010>\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010;J\u000e\u0010?\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010;\u00a8\u0006@\u00c0\u0006\u0003"}, d2 = {"Lcom/harvestpay/app/data/HarvestDao;", "", "observeCustomers", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/harvestpay/app/data/CustomerEntity;", "observeFields", "Lcom/harvestpay/app/data/FieldEntity;", "observeWorkEntries", "Lcom/harvestpay/app/data/WorkEntryEntity;", "observePayments", "Lcom/harvestpay/app/data/PaymentEntity;", "observeReminders", "Lcom/harvestpay/app/data/ReminderEntity;", "mobileExists", "", "mobile", "", "excludingId", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertCustomer", "customer", "(Lcom/harvestpay/app/data/CustomerEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateCustomer", "", "deleteCustomer", "insertCustomers", "customers", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertField", "field", "(Lcom/harvestpay/app/data/FieldEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateField", "deleteField", "insertFields", "fields", "insertWorkEntry", "workEntry", "(Lcom/harvestpay/app/data/WorkEntryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateWorkEntry", "deleteWorkEntry", "insertWorkEntries", "workEntries", "insertPayment", "payment", "(Lcom/harvestpay/app/data/PaymentEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updatePayment", "deletePayment", "insertPayments", "payments", "insertReminder", "reminder", "(Lcom/harvestpay/app/data/ReminderEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateReminder", "deleteReminder", "insertReminders", "reminders", "clearReminders", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearPayments", "clearWorkEntries", "clearFields", "clearCustomers", "app_debug"})
@androidx.room.Dao()
public abstract interface HarvestDao {
    
    @androidx.room.Query(value = "SELECT * FROM customers ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.harvestpay.app.data.CustomerEntity>> observeCustomers();
    
    @androidx.room.Query(value = "SELECT * FROM fields ORDER BY fieldName COLLATE NOCASE")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.harvestpay.app.data.FieldEntity>> observeFields();
    
    @androidx.room.Query(value = "SELECT * FROM work_entries ORDER BY workDate DESC, createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.harvestpay.app.data.WorkEntryEntity>> observeWorkEntries();
    
    @androidx.room.Query(value = "SELECT * FROM payments ORDER BY paymentDate DESC, createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.harvestpay.app.data.PaymentEntity>> observePayments();
    
    @androidx.room.Query(value = "SELECT * FROM reminders WHERE completed = 0 ORDER BY reminderAt")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.harvestpay.app.data.ReminderEntity>> observeReminders();
    
    @androidx.room.Query(value = "SELECT EXISTS(SELECT 1 FROM customers WHERE mobile = :mobile AND id != :excludingId)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object mobileExists(@org.jetbrains.annotations.NotNull()
    java.lang.String mobile, long excludingId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @androidx.room.Insert(onConflict = 3)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCustomer(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateCustomer(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCustomer(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCustomers(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.CustomerEntity> customers, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 3)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertField(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.FieldEntity field, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateField(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.FieldEntity field, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteField(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.FieldEntity field, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertFields(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.FieldEntity> fields, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 3)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertWorkEntry(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.WorkEntryEntity workEntry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateWorkEntry(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.WorkEntryEntity workEntry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteWorkEntry(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.WorkEntryEntity workEntry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertWorkEntries(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.WorkEntryEntity> workEntries, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 3)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPayment(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.PaymentEntity payment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updatePayment(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.PaymentEntity payment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deletePayment(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.PaymentEntity payment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPayments(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.PaymentEntity> payments, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 3)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertReminder(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ReminderEntity reminder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateReminder(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ReminderEntity reminder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteReminder(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ReminderEntity reminder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertReminders(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.ReminderEntity> reminders, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM reminders")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearReminders(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM payments")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearPayments(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM work_entries")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearWorkEntries(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM fields")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearFields(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM customers")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearCustomers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}