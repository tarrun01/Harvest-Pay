package com.harvestpay.app.domain;

import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.WorkEntryEntity;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\u0006\u0010\u000e\u001a\u00020\f\u0012\u0006\u0010\u000f\u001a\u00020\f\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\u0004\b\u0012\u0010\u0013J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000f\u0010$\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H\u00c6\u0003J\u000f\u0010%\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\fH\u00c6\u0003J\t\u0010\'\u001a\u00020\fH\u00c6\u0003J\t\u0010(\u001a\u00020\fH\u00c6\u0003J\t\u0010)\u001a\u00020\fH\u00c6\u0003J\u0010\u0010*\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u0010 J|\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00052\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\f2\b\b\u0002\u0010\u000f\u001a\u00020\f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00c6\u0001\u00a2\u0006\u0002\u0010,J\u0013\u0010-\u001a\u00020.2\b\u0010/\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00100\u001a\u000201H\u00d6\u0001J\t\u00102\u001a\u000203H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\u000e\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001bR\u0011\u0010\u000f\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001bR\u0015\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u0010!\u001a\u0004\b\u001f\u0010 \u00a8\u00064"}, d2 = {"Lcom/harvestpay/app/domain/CustomerSummary;", "", "customer", "Lcom/harvestpay/app/data/CustomerEntity;", "fields", "", "Lcom/harvestpay/app/data/FieldEntity;", "work", "Lcom/harvestpay/app/domain/WorkSummary;", "payments", "Lcom/harvestpay/app/data/PaymentEntity;", "totalBigha", "", "totalBill", "totalPaid", "pending", "lastWorkDate", "", "<init>", "(Lcom/harvestpay/app/data/CustomerEntity;Ljava/util/List;Ljava/util/List;Ljava/util/List;DDDDLjava/lang/Long;)V", "getCustomer", "()Lcom/harvestpay/app/data/CustomerEntity;", "getFields", "()Ljava/util/List;", "getWork", "getPayments", "getTotalBigha", "()D", "getTotalBill", "getTotalPaid", "getPending", "getLastWorkDate", "()Ljava/lang/Long;", "Ljava/lang/Long;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Lcom/harvestpay/app/data/CustomerEntity;Ljava/util/List;Ljava/util/List;Ljava/util/List;DDDDLjava/lang/Long;)Lcom/harvestpay/app/domain/CustomerSummary;", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class CustomerSummary {
    @org.jetbrains.annotations.NotNull()
    private final com.harvestpay.app.data.CustomerEntity customer = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.data.FieldEntity> fields = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.domain.WorkSummary> work = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.data.PaymentEntity> payments = null;
    private final double totalBigha = 0.0;
    private final double totalBill = 0.0;
    private final double totalPaid = 0.0;
    private final double pending = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long lastWorkDate = null;
    
    public CustomerSummary(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.FieldEntity> fields, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.domain.WorkSummary> work, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.PaymentEntity> payments, double totalBigha, double totalBill, double totalPaid, double pending, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastWorkDate) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.CustomerEntity getCustomer() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.FieldEntity> getFields() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.domain.WorkSummary> getWork() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.PaymentEntity> getPayments() {
        return null;
    }
    
    public final double getTotalBigha() {
        return 0.0;
    }
    
    public final double getTotalBill() {
        return 0.0;
    }
    
    public final double getTotalPaid() {
        return 0.0;
    }
    
    public final double getPending() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastWorkDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.CustomerEntity component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.FieldEntity> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.domain.WorkSummary> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.PaymentEntity> component4() {
        return null;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    public final double component7() {
        return 0.0;
    }
    
    public final double component8() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.CustomerSummary copy(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.FieldEntity> fields, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.domain.WorkSummary> work, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.PaymentEntity> payments, double totalBigha, double totalBill, double totalPaid, double pending, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastWorkDate) {
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