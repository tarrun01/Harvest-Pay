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

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000>\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0007\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003\u001a&\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a2\u0010\u000e\u001a\u00020\u00012\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0012\u001a\u00020\u000b2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u0014H\u0003\u001a\u0010\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u000bH\u0002\u00a8\u0006\u0017"}, d2 = {"HarvestPayRoot", "", "viewModel", "Lcom/harvestpay/app/HarvestViewModel;", "auth", "Lcom/harvestpay/app/AuthState;", "AuthenticatedApp", "QuickAction", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "title", "", "onClick", "Lkotlin/Function0;", "CustomerPickerScreen", "customers", "", "Lcom/harvestpay/app/data/CustomerEntity;", "action", "onPick", "Lkotlin/Function1;", "titleFor", "route", "app_debug"})
public final class HarvestPayAppKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HarvestPayRoot(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.HarvestViewModel viewModel, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.AuthState auth) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void AuthenticatedApp(com.harvestpay.app.HarvestViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QuickAction(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CustomerPickerScreen(java.util.List<com.harvestpay.app.data.CustomerEntity> customers, java.lang.String action, kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.CustomerEntity, kotlin.Unit> onPick) {
    }
    
    private static final java.lang.String titleFor(java.lang.String route) {
        return null;
    }
}