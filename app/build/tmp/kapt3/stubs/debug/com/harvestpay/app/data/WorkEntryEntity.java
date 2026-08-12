package com.harvestpay.app.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b-\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u0087\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\b\u0012\b\b\u0002\u0010\u000f\u001a\u00020\b\u0012\b\b\u0002\u0010\u0010\u001a\u00020\b\u0012\u0006\u0010\u0011\u001a\u00020\b\u0012\b\b\u0002\u0010\u0012\u001a\u00020\r\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0015J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010,\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u001aJ\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\bH\u00c6\u0003J\t\u0010/\u001a\u00020\bH\u00c6\u0003J\t\u00100\u001a\u00020\u000bH\u00c6\u0003J\t\u00101\u001a\u00020\rH\u00c6\u0003J\t\u00102\u001a\u00020\bH\u00c6\u0003J\t\u00103\u001a\u00020\bH\u00c6\u0003J\t\u00104\u001a\u00020\bH\u00c6\u0003J\t\u00105\u001a\u00020\bH\u00c6\u0003J\t\u00106\u001a\u00020\rH\u00c6\u0003J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\u009c\u0001\u00108\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\b2\b\b\u0002\u0010\u000f\u001a\u00020\b2\b\b\u0002\u0010\u0010\u001a\u00020\b2\b\b\u0002\u0010\u0011\u001a\u00020\b2\b\b\u0002\u0010\u0012\u001a\u00020\r2\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u00109J\u0013\u0010:\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010=\u001a\u00020\u000bH\u00d6\u0001J\t\u0010>\u001a\u00020\rH\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0017R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u000e\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001eR\u0011\u0010\u000f\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001eR\u0011\u0010\u0010\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001eR\u0011\u0010\u0011\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001eR\u0011\u0010\u0012\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010#R\u0011\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0017\u00a8\u0006?"}, d2 = {"Lcom/harvestpay/app/data/WorkEntryEntity;", "", "id", "", "customerId", "fieldId", "workDate", "sizeBigha", "", "ratePerBigha", "rounds", "", "workType", "", "subtotal", "discount", "extraCharges", "totalAmount", "notes", "createdAt", "<init>", "(JJLjava/lang/Long;JDDILjava/lang/String;DDDDLjava/lang/String;J)V", "getId", "()J", "getCustomerId", "getFieldId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getWorkDate", "getSizeBigha", "()D", "getRatePerBigha", "getRounds", "()I", "getWorkType", "()Ljava/lang/String;", "getSubtotal", "getDiscount", "getExtraCharges", "getTotalAmount", "getNotes", "getCreatedAt", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "copy", "(JJLjava/lang/Long;JDDILjava/lang/String;DDDDLjava/lang/String;J)Lcom/harvestpay/app/data/WorkEntryEntity;", "equals", "", "other", "hashCode", "toString", "app_debug"})
@androidx.room.Entity(tableName = "work_entries", foreignKeys = {@androidx.room.ForeignKey(entity = com.harvestpay.app.data.CustomerEntity.class, parentColumns = {"id"}, childColumns = {"customerId"}, onDelete = 5), @androidx.room.ForeignKey(entity = com.harvestpay.app.data.FieldEntity.class, parentColumns = {"id"}, childColumns = {"fieldId"}, onDelete = 3)}, indices = {@androidx.room.Index(value = {"customerId"}), @androidx.room.Index(value = {"fieldId"}), @androidx.room.Index(value = {"workDate"})})
public final class WorkEntryEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    private final long customerId = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long fieldId = null;
    
    /**
     * Local calendar date encoded as java.time.LocalDate.toEpochDay().
     */
    private final long workDate = 0L;
    private final double sizeBigha = 0.0;
    private final double ratePerBigha = 0.0;
    private final int rounds = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String workType = null;
    private final double subtotal = 0.0;
    private final double discount = 0.0;
    private final double extraCharges = 0.0;
    private final double totalAmount = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String notes = null;
    private final long createdAt = 0L;
    
    public WorkEntryEntity(long id, long customerId, @org.jetbrains.annotations.Nullable()
    java.lang.Long fieldId, long workDate, double sizeBigha, double ratePerBigha, int rounds, @org.jetbrains.annotations.NotNull()
    java.lang.String workType, double subtotal, double discount, double extraCharges, double totalAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String notes, long createdAt) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    public final long getCustomerId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getFieldId() {
        return null;
    }
    
    /**
     * Local calendar date encoded as java.time.LocalDate.toEpochDay().
     */
    public final long getWorkDate() {
        return 0L;
    }
    
    public final double getSizeBigha() {
        return 0.0;
    }
    
    public final double getRatePerBigha() {
        return 0.0;
    }
    
    public final int getRounds() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getWorkType() {
        return null;
    }
    
    public final double getSubtotal() {
        return 0.0;
    }
    
    public final double getDiscount() {
        return 0.0;
    }
    
    public final double getExtraCharges() {
        return 0.0;
    }
    
    public final double getTotalAmount() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNotes() {
        return null;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final double component10() {
        return 0.0;
    }
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component13() {
        return null;
    }
    
    public final long component14() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    public final int component7() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    public final double component9() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.WorkEntryEntity copy(long id, long customerId, @org.jetbrains.annotations.Nullable()
    java.lang.Long fieldId, long workDate, double sizeBigha, double ratePerBigha, int rounds, @org.jetbrains.annotations.NotNull()
    java.lang.String workType, double subtotal, double discount, double extraCharges, double totalAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String notes, long createdAt) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}