package com.harvestpay.app.domain;

import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.WorkEntryEntity;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/harvestpay/app/domain/PaymentStatus;", "", "<init>", "(Ljava/lang/String;I)V", "PAID", "PARTIALLY_PAID", "PENDING", "app_debug"})
public enum PaymentStatus {
    /*public static final*/ PAID /* = new PAID() */,
    /*public static final*/ PARTIALLY_PAID /* = new PARTIALLY_PAID() */,
    /*public static final*/ PENDING /* = new PENDING() */;
    
    PaymentStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.harvestpay.app.domain.PaymentStatus> getEntries() {
        return null;
    }
}