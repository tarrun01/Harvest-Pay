package com.harvestpay.app.ui.navigation;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.material3.SnackbarHostState;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.text.font.FontWeight;
import androidx.navigation.NavType;
import com.harvestpay.app.AuthState;
import com.harvestpay.app.HarvestViewModel;
import com.harvestpay.app.data.CustomerEntity;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0011\b\u00c2\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/harvestpay/app/ui/navigation/Routes;", "", "<init>", "()V", "DASHBOARD", "", "CUSTOMERS", "NEW_WORK", "PAYMENTS", "MORE", "SEARCH", "REPORTS", "BACKUP", "SETTINGS", "ABOUT", "CUSTOMER", "CUSTOMER_FORM", "FIELD_FORM", "WORK_FORM", "PAYMENT_FORM", "REMINDER_FORM", "PICKER", "app_debug"})
final class Routes {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DASHBOARD = "dashboard";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CUSTOMERS = "customers";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String NEW_WORK = "new_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PAYMENTS = "payments";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String MORE = "more";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SEARCH = "search";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String REPORTS = "reports";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BACKUP = "backup";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SETTINGS = "settings";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ABOUT = "about";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CUSTOMER = "customer/{customerId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CUSTOMER_FORM = "customer_form/{customerId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FIELD_FORM = "field_form/{customerId}/{fieldId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WORK_FORM = "work_form/{customerId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PAYMENT_FORM = "payment_form/{customerId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String REMINDER_FORM = "reminder_form/{customerId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PICKER = "picker/{action}";
    @org.jetbrains.annotations.NotNull()
    public static final com.harvestpay.app.ui.navigation.Routes INSTANCE = null;
    
    private Routes() {
        super();
    }
}