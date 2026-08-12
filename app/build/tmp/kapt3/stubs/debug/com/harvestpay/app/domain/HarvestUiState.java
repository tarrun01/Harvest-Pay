package com.harvestpay.app.domain;

import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.WorkEntryEntity;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0019\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B{\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\b0\u0003H\u00c6\u0003J\u000f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0\u0003H\u00c6\u0003J\u000f\u0010$\u001a\b\u0012\u0004\u0012\u00020\f0\u0003H\u00c6\u0003J\u000f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0010H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0012H\u00c6\u0003J}\u0010(\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00032\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u00c6\u0001J\u0013\u0010)\u001a\u00020\u00122\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010+\u001a\u00020,H\u00d6\u0001J\t\u0010-\u001a\u00020.H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0016R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0016R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001f\u00a8\u0006/"}, d2 = {"Lcom/harvestpay/app/domain/HarvestUiState;", "", "customers", "", "Lcom/harvestpay/app/data/CustomerEntity;", "fields", "Lcom/harvestpay/app/data/FieldEntity;", "workEntries", "Lcom/harvestpay/app/data/WorkEntryEntity;", "payments", "Lcom/harvestpay/app/data/PaymentEntity;", "workSummaries", "Lcom/harvestpay/app/domain/WorkSummary;", "customerSummaries", "Lcom/harvestpay/app/domain/CustomerSummary;", "stats", "Lcom/harvestpay/app/domain/DashboardStats;", "loading", "", "<init>", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/harvestpay/app/domain/DashboardStats;Z)V", "getCustomers", "()Ljava/util/List;", "getFields", "getWorkEntries", "getPayments", "getWorkSummaries", "getCustomerSummaries", "getStats", "()Lcom/harvestpay/app/domain/DashboardStats;", "getLoading", "()Z", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
public final class HarvestUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.data.CustomerEntity> customers = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.data.FieldEntity> fields = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.data.WorkEntryEntity> workEntries = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.data.PaymentEntity> payments = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.domain.WorkSummary> workSummaries = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.harvestpay.app.domain.CustomerSummary> customerSummaries = null;
    @org.jetbrains.annotations.NotNull()
    private final com.harvestpay.app.domain.DashboardStats stats = null;
    private final boolean loading = false;
    
    public HarvestUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.CustomerEntity> customers, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.FieldEntity> fields, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.WorkEntryEntity> workEntries, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.PaymentEntity> payments, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.domain.WorkSummary> workSummaries, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.domain.CustomerSummary> customerSummaries, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.DashboardStats stats, boolean loading) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.CustomerEntity> getCustomers() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.FieldEntity> getFields() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.WorkEntryEntity> getWorkEntries() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.PaymentEntity> getPayments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.domain.WorkSummary> getWorkSummaries() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.domain.CustomerSummary> getCustomerSummaries() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.DashboardStats getStats() {
        return null;
    }
    
    public final boolean getLoading() {
        return false;
    }
    
    public HarvestUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.CustomerEntity> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.FieldEntity> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.WorkEntryEntity> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.data.PaymentEntity> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.domain.WorkSummary> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.harvestpay.app.domain.CustomerSummary> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.DashboardStats component7() {
        return null;
    }
    
    public final boolean component8() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.HarvestUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.CustomerEntity> customers, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.FieldEntity> fields, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.WorkEntryEntity> workEntries, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.PaymentEntity> payments, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.domain.WorkSummary> workSummaries, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.domain.CustomerSummary> customerSummaries, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.DashboardStats stats, boolean loading) {
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