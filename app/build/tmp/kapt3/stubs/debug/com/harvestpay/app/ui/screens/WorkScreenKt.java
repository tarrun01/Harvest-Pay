package com.harvestpay.app.ui.screens;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.KeyboardType;
import com.harvestpay.app.data.AppSettings;
import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.WorkEntryEntity;
import com.harvestpay.app.domain.BusinessCalculator;
import com.harvestpay.app.domain.HarvestUiState;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\u001aY\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u001e\u0010\b\u001a\u001a\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\t2\u0010\b\u0002\u0010\r\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000eH\u0007\u00a2\u0006\u0002\u0010\u000f\u001a\"\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\f2\b\b\u0002\u0010\u0013\u001a\u00020\u0014H\u0003\u00a8\u0006\u0015"}, d2 = {"WorkFormScreen", "", "uiState", "Lcom/harvestpay/app/domain/HarvestUiState;", "settings", "Lcom/harvestpay/app/data/AppSettings;", "preselectedCustomerId", "", "onSave", "Lkotlin/Function3;", "Lcom/harvestpay/app/data/WorkEntryEntity;", "", "", "onCancel", "Lkotlin/Function0;", "(Lcom/harvestpay/app/domain/HarvestUiState;Lcom/harvestpay/app/data/AppSettings;Ljava/lang/Long;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function0;)V", "SummaryRow", "label", "value", "emphasized", "", "app_debug"})
public final class WorkScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void WorkFormScreen(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.HarvestUiState uiState, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.AppSettings settings, @org.jetbrains.annotations.Nullable()
    java.lang.Long preselectedCustomerId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super com.harvestpay.app.data.WorkEntryEntity, ? super java.lang.Double, ? super java.lang.String, kotlin.Unit> onSave, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SummaryRow(java.lang.String label, java.lang.String value, boolean emphasized) {
    }
}