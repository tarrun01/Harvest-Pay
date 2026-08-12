package com.harvestpay.app.data;

import android.content.Context;
import androidx.datastore.preferences.core.Preferences;
import java.io.IOException;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b%\b\u0086\b\u0018\u00002\u00020\u0001B\u007f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\u0007\u0012\b\b\u0002\u0010\n\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\f\u001a\u00020\u0007\u0012\b\b\u0002\u0010\r\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0013\u0010\u0014J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0005H\u00c6\u0003J\t\u0010(\u001a\u00020\u0007H\u00c6\u0003J\t\u0010)\u001a\u00020\u0007H\u00c6\u0003J\t\u0010*\u001a\u00020\u0007H\u00c6\u0003J\t\u0010+\u001a\u00020\u0007H\u00c6\u0003J\t\u0010,\u001a\u00020\u0007H\u00c6\u0003J\t\u0010-\u001a\u00020\u0007H\u00c6\u0003J\t\u0010.\u001a\u00020\u0007H\u00c6\u0003J\t\u0010/\u001a\u00020\u000fH\u00c6\u0003J\t\u00100\u001a\u00020\u000fH\u00c6\u0003J\t\u00101\u001a\u00020\u0012H\u00c6\u0003J\u0081\u0001\u00102\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\u00072\b\b\u0002\u0010\r\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u00c6\u0001J\u0013\u00103\u001a\u00020\u000f2\b\u00104\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00105\u001a\u00020\u0012H\u00d6\u0001J\t\u00106\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001aR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001aR\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001aR\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001aR\u0011\u0010\f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001aR\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001aR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\"R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%\u00a8\u00067"}, d2 = {"Lcom/harvestpay/app/data/AppSettings;", "", "theme", "Lcom/harvestpay/app/data/ThemePreference;", "defaultRate", "", "ratePresets", "", "businessName", "ownerName", "ownerMobile", "tractorNumber", "address", "village", "rememberLogin", "", "rememberedAuthenticated", "autoLockMinutes", "", "<init>", "(Lcom/harvestpay/app/data/ThemePreference;DLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZI)V", "getTheme", "()Lcom/harvestpay/app/data/ThemePreference;", "getDefaultRate", "()D", "getRatePresets", "()Ljava/lang/String;", "getBusinessName", "getOwnerName", "getOwnerMobile", "getTractorNumber", "getAddress", "getVillage", "getRememberLogin", "()Z", "getRememberedAuthenticated", "getAutoLockMinutes", "()I", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class AppSettings {
    @org.jetbrains.annotations.NotNull()
    private final com.harvestpay.app.data.ThemePreference theme = null;
    private final double defaultRate = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ratePresets = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String businessName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ownerName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ownerMobile = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String tractorNumber = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String address = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String village = null;
    private final boolean rememberLogin = false;
    private final boolean rememberedAuthenticated = false;
    private final int autoLockMinutes = 0;
    
    public AppSettings(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ThemePreference theme, double defaultRate, @org.jetbrains.annotations.NotNull()
    java.lang.String ratePresets, @org.jetbrains.annotations.NotNull()
    java.lang.String businessName, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerName, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerMobile, @org.jetbrains.annotations.NotNull()
    java.lang.String tractorNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    java.lang.String village, boolean rememberLogin, boolean rememberedAuthenticated, int autoLockMinutes) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.ThemePreference getTheme() {
        return null;
    }
    
    public final double getDefaultRate() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRatePresets() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBusinessName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOwnerName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOwnerMobile() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTractorNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVillage() {
        return null;
    }
    
    public final boolean getRememberLogin() {
        return false;
    }
    
    public final boolean getRememberedAuthenticated() {
        return false;
    }
    
    public final int getAutoLockMinutes() {
        return 0;
    }
    
    public AppSettings() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.ThemePreference component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final int component12() {
        return 0;
    }
    
    public final double component2() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.AppSettings copy(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ThemePreference theme, double defaultRate, @org.jetbrains.annotations.NotNull()
    java.lang.String ratePresets, @org.jetbrains.annotations.NotNull()
    java.lang.String businessName, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerName, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerMobile, @org.jetbrains.annotations.NotNull()
    java.lang.String tractorNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    java.lang.String village, boolean rememberLogin, boolean rememberedAuthenticated, int autoLockMinutes) {
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