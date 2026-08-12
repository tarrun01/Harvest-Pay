package com.harvestpay.app.domain;

import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.WorkEntryEntity;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\fH\u00c6\u0003JI\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\'H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019\u00a8\u0006("}, d2 = {"Lcom/harvestpay/app/domain/WorkSummary;", "", "work", "Lcom/harvestpay/app/data/WorkEntryEntity;", "customer", "Lcom/harvestpay/app/data/CustomerEntity;", "field", "Lcom/harvestpay/app/data/FieldEntity;", "paid", "", "pending", "status", "Lcom/harvestpay/app/domain/PaymentStatus;", "<init>", "(Lcom/harvestpay/app/data/WorkEntryEntity;Lcom/harvestpay/app/data/CustomerEntity;Lcom/harvestpay/app/data/FieldEntity;DDLcom/harvestpay/app/domain/PaymentStatus;)V", "getWork", "()Lcom/harvestpay/app/data/WorkEntryEntity;", "getCustomer", "()Lcom/harvestpay/app/data/CustomerEntity;", "getField", "()Lcom/harvestpay/app/data/FieldEntity;", "getPaid", "()D", "getPending", "getStatus", "()Lcom/harvestpay/app/domain/PaymentStatus;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class WorkSummary {
    @org.jetbrains.annotations.NotNull()
    private final com.harvestpay.app.data.WorkEntryEntity work = null;
    @org.jetbrains.annotations.Nullable()
    private final com.harvestpay.app.data.CustomerEntity customer = null;
    @org.jetbrains.annotations.Nullable()
    private final com.harvestpay.app.data.FieldEntity field = null;
    private final double paid = 0.0;
    private final double pending = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final com.harvestpay.app.domain.PaymentStatus status = null;
    
    public WorkSummary(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.WorkEntryEntity work, @org.jetbrains.annotations.Nullable()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.Nullable()
    com.harvestpay.app.data.FieldEntity field, double paid, double pending, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.PaymentStatus status) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.WorkEntryEntity getWork() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.harvestpay.app.data.CustomerEntity getCustomer() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.harvestpay.app.data.FieldEntity getField() {
        return null;
    }
    
    public final double getPaid() {
        return 0.0;
    }
    
    public final double getPending() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.PaymentStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.WorkEntryEntity component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.harvestpay.app.data.CustomerEntity component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.harvestpay.app.data.FieldEntity component3() {
        return null;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.PaymentStatus component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.WorkSummary copy(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.WorkEntryEntity work, @org.jetbrains.annotations.Nullable()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.Nullable()
    com.harvestpay.app.data.FieldEntity field, double paid, double pending, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.PaymentStatus status) {
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