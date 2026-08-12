package com.harvestpay.app.ui.screens;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import com.harvestpay.app.data.AppSettings;
import com.harvestpay.app.data.ThemePreference;
import java.time.LocalDate;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\u001aN\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a.\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a8\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00132\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u0013H\u0007\u001aF\u0010\u0016\u001a\u00020\u00012\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u0013H\u0007\u001a.\u0010\u001b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\b\u0010\u001c\u001a\u00020\u0001H\u0007\u00a8\u0006\u001d"}, d2 = {"MoreScreen", "", "onReports", "Lkotlin/Function0;", "onBackup", "onSettings", "onAbout", "onLogout", "MoreRow", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "title", "", "subtitle", "onClick", "SettingsScreen", "current", "Lcom/harvestpay/app/data/AppSettings;", "onTheme", "Lkotlin/Function1;", "Lcom/harvestpay/app/data/ThemePreference;", "onSave", "BackupScreen", "backupText", "customersCsv", "paymentsCsv", "onRestore", "BackupAction", "AboutScreen", "app_debug"})
public final class MoreScreensKt {
    
    @androidx.compose.runtime.Composable()
    public static final void MoreScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onReports, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBackup, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSettings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAbout, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLogout) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MoreRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, java.lang.String subtitle, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SettingsScreen(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.AppSettings current, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.ThemePreference, kotlin.Unit> onTheme, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.harvestpay.app.data.AppSettings, kotlin.Unit> onSave) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void BackupScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<java.lang.String> backupText, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<java.lang.String> customersCsv, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<java.lang.String> paymentsCsv, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onRestore) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BackupAction(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, java.lang.String subtitle, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void AboutScreen() {
    }
}