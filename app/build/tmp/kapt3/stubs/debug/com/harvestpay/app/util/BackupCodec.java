package com.harvestpay.app.util;

import com.harvestpay.app.data.AppSettings;
import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.DatabaseSnapshot;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.ReminderEntity;
import com.harvestpay.app.data.ThemePreference;
import com.harvestpay.app.data.WorkEntryEntity;
import java.time.Instant;
import org.json.JSONArray;
import org.json.JSONObject;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\u0007J\u0014\u0010\f\u001a\u00020\u00072\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eJ\"\u0010\u0010\u001a\u00020\u00072\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000e2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eJ\f\u0010\u0013\u001a\u00020\u0014*\u00020\u000fH\u0002J\f\u0010\u0013\u001a\u00020\u0014*\u00020\u0015H\u0002J\f\u0010\u0013\u001a\u00020\u0014*\u00020\u0016H\u0002J\f\u0010\u0013\u001a\u00020\u0014*\u00020\u0012H\u0002J\f\u0010\u0013\u001a\u00020\u0014*\u00020\u0017H\u0002J\f\u0010\u0013\u001a\u00020\u0014*\u00020\u0018H\u0002J\f\u0010\u0019\u001a\u00020\u000f*\u00020\u0014H\u0002J\f\u0010\u001a\u001a\u00020\u0015*\u00020\u0014H\u0002J\f\u0010\u001b\u001a\u00020\u0016*\u00020\u0014H\u0002J\f\u0010\u001c\u001a\u00020\u0012*\u00020\u0014H\u0002J\f\u0010\u001d\u001a\u00020\u0017*\u00020\u0014H\u0002J\f\u0010\u001e\u001a\u00020\u0018*\u00020\u0014H\u0002J\u0014\u0010\u001f\u001a\u00020 *\u00020\u00142\u0006\u0010!\u001a\u00020\u0007H\u0002J\u001b\u0010\"\u001a\u0004\u0018\u00010#*\u00020\u00142\u0006\u0010!\u001a\u00020\u0007H\u0002\u00a2\u0006\u0002\u0010$J-\u0010%\u001a\b\u0012\u0004\u0012\u0002H&0\u000e\"\u0004\b\u0000\u0010&*\u00020 2\u0012\u0010\'\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u0002H&0(H\u0082\bJ\u0012\u0010)\u001a\u00020\u0007*\b\u0012\u0004\u0012\u00020\u00070\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/harvestpay/app/util/BackupCodec;", "", "<init>", "()V", "SCHEMA_VERSION", "", "encode", "", "bundle", "Lcom/harvestpay/app/util/BackupBundle;", "decode", "text", "customersCsv", "customers", "", "Lcom/harvestpay/app/data/CustomerEntity;", "paymentsCsv", "payments", "Lcom/harvestpay/app/data/PaymentEntity;", "json", "Lorg/json/JSONObject;", "Lcom/harvestpay/app/data/FieldEntity;", "Lcom/harvestpay/app/data/WorkEntryEntity;", "Lcom/harvestpay/app/data/ReminderEntity;", "Lcom/harvestpay/app/data/AppSettings;", "customer", "field", "workEntry", "payment", "reminder", "settings", "requiredArray", "Lorg/json/JSONArray;", "key", "nullableLong", "", "(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/Long;", "mapObjects", "T", "block", "Lkotlin/Function1;", "csvRow", "app_debug"})
public final class BackupCodec {
    private static final int SCHEMA_VERSION = 1;
    @org.jetbrains.annotations.NotNull()
    public static final com.harvestpay.app.util.BackupCodec INSTANCE = null;
    
    private BackupCodec() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encode(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.util.BackupBundle bundle) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.util.BackupBundle decode(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String customersCsv(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.CustomerEntity> customers) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String paymentsCsv(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.PaymentEntity> payments, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.CustomerEntity> customers) {
        return null;
    }
    
    private final org.json.JSONObject json(com.harvestpay.app.data.CustomerEntity $this$json) {
        return null;
    }
    
    private final org.json.JSONObject json(com.harvestpay.app.data.FieldEntity $this$json) {
        return null;
    }
    
    private final org.json.JSONObject json(com.harvestpay.app.data.WorkEntryEntity $this$json) {
        return null;
    }
    
    private final org.json.JSONObject json(com.harvestpay.app.data.PaymentEntity $this$json) {
        return null;
    }
    
    private final org.json.JSONObject json(com.harvestpay.app.data.ReminderEntity $this$json) {
        return null;
    }
    
    private final org.json.JSONObject json(com.harvestpay.app.data.AppSettings $this$json) {
        return null;
    }
    
    private final com.harvestpay.app.data.CustomerEntity customer(org.json.JSONObject $this$customer) {
        return null;
    }
    
    private final com.harvestpay.app.data.FieldEntity field(org.json.JSONObject $this$field) {
        return null;
    }
    
    private final com.harvestpay.app.data.WorkEntryEntity workEntry(org.json.JSONObject $this$workEntry) {
        return null;
    }
    
    private final com.harvestpay.app.data.PaymentEntity payment(org.json.JSONObject $this$payment) {
        return null;
    }
    
    private final com.harvestpay.app.data.ReminderEntity reminder(org.json.JSONObject $this$reminder) {
        return null;
    }
    
    private final com.harvestpay.app.data.AppSettings settings(org.json.JSONObject $this$settings) {
        return null;
    }
    
    private final org.json.JSONArray requiredArray(org.json.JSONObject $this$requiredArray, java.lang.String key) {
        return null;
    }
    
    private final java.lang.Long nullableLong(org.json.JSONObject $this$nullableLong, java.lang.String key) {
        return null;
    }
    
    private final <T extends java.lang.Object>java.util.List<T> mapObjects(org.json.JSONArray $this$mapObjects, kotlin.jvm.functions.Function1<? super org.json.JSONObject, ? extends T> block) {
        return null;
    }
    
    private final java.lang.String csvRow(java.util.List<java.lang.String> $this$csvRow) {
        return null;
    }
}