package com.harvestpay.app.ui.screens;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.core.content.ContextCompat;
import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.ReminderEntity;
import com.harvestpay.app.domain.HarvestUiState;
import com.harvestpay.app.domain.WorkSummary;
import java.time.LocalDate;
import java.time.ZoneId;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000R\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0098\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\b2\u0014\u0010\n\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\t\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001aY\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0011\u001a\u0004\u0018\u00010\t2*\u0010\u0012\u001a&\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u0018H\u0007\u00a2\u0006\u0002\u0010\u0019\u001aB\u0010\u001a\u001a\u00020\u00012\b\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u001d\u001a\u00020\u00152\u0018\u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00010\u001e2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u0018H\u0007\u00a8\u0006\u001f"}, d2 = {"PaymentsScreen", "", "uiState", "Lcom/harvestpay/app/domain/HarvestUiState;", "reminders", "", "Lcom/harvestpay/app/data/ReminderEntity;", "onOpenCustomer", "Lkotlin/Function1;", "", "onAddPayment", "onMarkPaid", "onAddReminder", "onDeletePayment", "Lcom/harvestpay/app/data/PaymentEntity;", "onDeleteReminder", "PaymentFormScreen", "preselectedCustomerId", "onSave", "Lkotlin/Function5;", "Lcom/harvestpay/app/domain/WorkSummary;", "", "", "onCancel", "Lkotlin/Function0;", "(Lcom/harvestpay/app/domain/HarvestUiState;Ljava/lang/Long;Lkotlin/jvm/functions/Function5;Lkotlin/jvm/functions/Function0;)V", "ReminderFormScreen", "customer", "Lcom/harvestpay/app/data/CustomerEntity;", "pendingAmount", "Lkotlin/Function2;", "app_debug"})
public final class PaymentsScreensKt {
    
    @androidx.compose.runtime.Composable()
    public static final void PaymentsScreen(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.HarvestUiState uiState, @org.jetbrains.annotations.NotNull()
    java.util.List<com.harvestpay.app.data.ReminderEntity> reminders, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onOpenCustomer, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onAddPayment, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onMarkPaid, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onAddReminder, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.PaymentEntity, kotlin.Unit> onDeletePayment, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.ReminderEntity, kotlin.Unit> onDeleteReminder) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PaymentFormScreen(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.HarvestUiState uiState, @org.jetbrains.annotations.Nullable()
    java.lang.Long preselectedCustomerId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function5<? super com.harvestpay.app.domain.WorkSummary, ? super java.lang.Double, ? super java.lang.Long, ? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ReminderFormScreen(@org.jetbrains.annotations.Nullable()
    com.harvestpay.app.data.CustomerEntity customer, double pendingAmount, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.String, kotlin.Unit> onSave, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancel) {
    }
}