package com.harvestpay.app.domain;

import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.WorkEntryEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J \u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\nJ\u001e\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u0005J\u0016\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0005J\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0005J>\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u00162\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u00162\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00162\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0016J\u000e\u0010\u001e\u001a\u00020\u00052\u0006\u0010\u001f\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/harvestpay/app/domain/BusinessCalculator;", "", "<init>", "()V", "EPSILON", "", "subtotal", "sizeBigha", "ratePerBigha", "rounds", "", "finalAmount", "discount", "extraCharges", "pending", "total", "paid", "status", "Lcom/harvestpay/app/domain/PaymentStatus;", "buildUiState", "Lcom/harvestpay/app/domain/HarvestUiState;", "customers", "", "Lcom/harvestpay/app/data/CustomerEntity;", "fields", "Lcom/harvestpay/app/data/FieldEntity;", "workEntries", "Lcom/harvestpay/app/data/WorkEntryEntity;", "payments", "Lcom/harvestpay/app/data/PaymentEntity;", "money", "value", "app_debug"})
public final class BusinessCalculator {
    private static final double EPSILON = 0.005;
    @org.jetbrains.annotations.NotNull()
    public static final com.harvestpay.app.domain.BusinessCalculator INSTANCE = null;
    
    private BusinessCalculator() {
        super();
    }
    
    public final double subtotal(double sizeBigha, double ratePerBigha, int rounds) {
        return 0.0;
    }
    
    public final double finalAmount(double subtotal, double discount, double extraCharges) {
        return 0.0;
    }
    
    public final double pending(double total, double paid) {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.PaymentStatus status(double total, double paid) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.domain.HarvestUiState buildUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.CustomerEntity> customers, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.FieldEntity> fields, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.WorkEntryEntity> workEntries, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.PaymentEntity> payments) {
        return null;
    }
    
    public final double money(double value) {
        return 0.0;
    }
}