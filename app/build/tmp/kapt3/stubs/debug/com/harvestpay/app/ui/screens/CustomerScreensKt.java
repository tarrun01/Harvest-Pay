package com.harvestpay.app.ui.screens;

import android.content.Context;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.KeyboardType;
import com.harvestpay.app.data.AppSettings;
import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.domain.CustomerSummary;
import com.harvestpay.app.domain.HarvestUiState;
import com.harvestpay.app.domain.WorkSummary;
import com.harvestpay.app.util.ReceiptData;
import com.harvestpay.app.util.ReceiptGenerator;
import com.harvestpay.app.util.ShareUtils;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000\\\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\u001aZ\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001a:\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a4\u0010\u0012\u001a\u00020\u00012\b\u0010\u0013\u001a\u0004\u0018\u00010\u000b2\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a<\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\b2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00182\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u00a2\u0001\u0010\u0019\u001a\u00020\u00012\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u001a\u001a\u00020\u001b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001aV\u0010%\u001a\u00020\u00012\u0006\u0010&\u001a\u00020\'2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020$0)2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u00a8\u0006-"}, d2 = {"CustomersScreen", "", "uiState", "Lcom/harvestpay/app/domain/HarvestUiState;", "onAddCustomer", "Lkotlin/Function0;", "onOpenCustomer", "Lkotlin/Function1;", "", "onEditCustomer", "onDeleteCustomer", "Lcom/harvestpay/app/data/CustomerEntity;", "CustomerListCard", "summary", "Lcom/harvestpay/app/domain/CustomerSummary;", "onOpen", "onEdit", "onDelete", "CustomerFormScreen", "existing", "onSave", "onCancel", "FieldFormScreen", "customerId", "Lcom/harvestpay/app/data/FieldEntity;", "CustomerProfileScreen", "settings", "Lcom/harvestpay/app/data/AppSettings;", "onAddField", "onEditField", "onDeleteField", "onAddWork", "onAddPayment", "onDeleteWork", "Lcom/harvestpay/app/data/WorkEntryEntity;", "onDeletePayment", "Lcom/harvestpay/app/data/PaymentEntity;", "WorkHistoryCard", "work", "Lcom/harvestpay/app/domain/WorkSummary;", "payments", "", "onWhatsApp", "onShareReceipt", "onSavePdf", "app_debug"})
public final class CustomerScreensKt {
    
    @androidx.compose.runtime.Composable()
    public static final void CustomersScreen(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.HarvestUiState uiState, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddCustomer, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onOpenCustomer, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onEditCustomer, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.CustomerEntity, kotlin.Unit> onDeleteCustomer) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CustomerListCard(com.harvestpay.app.domain.CustomerSummary summary, kotlin.jvm.functions.Function0<kotlin.Unit> onOpen, kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void CustomerFormScreen(@org.jetbrains.annotations.Nullable()
    com.harvestpay.app.data.CustomerEntity existing, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.CustomerEntity, kotlin.Unit> onSave, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void FieldFormScreen(long customerId, @org.jetbrains.annotations.Nullable()
    com.harvestpay.app.data.FieldEntity existing, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.FieldEntity, kotlin.Unit> onSave, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void CustomerProfileScreen(@org.jetbrains.annotations.Nullable()
    com.harvestpay.app.domain.CustomerSummary summary, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.AppSettings settings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddField, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onEditField, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.FieldEntity, kotlin.Unit> onDeleteField, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddWork, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddPayment, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.WorkEntryEntity, kotlin.Unit> onDeleteWork, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.PaymentEntity, kotlin.Unit> onDeletePayment) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void WorkHistoryCard(com.harvestpay.app.domain.WorkSummary work, java.util.List<com.harvestpay.app.data.PaymentEntity> payments, kotlin.jvm.functions.Function0<kotlin.Unit> onWhatsApp, kotlin.jvm.functions.Function0<kotlin.Unit> onShareReceipt, kotlin.jvm.functions.Function0<kotlin.Unit> onSavePdf, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete) {
    }
}