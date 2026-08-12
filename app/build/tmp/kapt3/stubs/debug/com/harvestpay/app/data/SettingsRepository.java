package com.harvestpay.app.data;

import android.content.Context;
import androidx.datastore.preferences.core.Preferences;
import java.io.IOException;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\"B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u001e\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u0017\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0011J\"\u0010\u001a\u001a\u00020\f2\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u001d\u0012\u0004\u0012\u00020\f0\u001cH\u0082@\u00a2\u0006\u0002\u0010\u001eJ\u0010\u0010\u001f\u001a\u00020\b2\u0006\u0010 \u001a\u00020!H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006#"}, d2 = {"Lcom/harvestpay/app/data/SettingsRepository;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "settings", "Lkotlinx/coroutines/flow/Flow;", "Lcom/harvestpay/app/data/AppSettings;", "getSettings", "()Lkotlinx/coroutines/flow/Flow;", "setTheme", "", "theme", "Lcom/harvestpay/app/data/ThemePreference;", "(Lcom/harvestpay/app/data/ThemePreference;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveBusinessSettings", "(Lcom/harvestpay/app/data/AppSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setAuthentication", "remember", "", "authenticated", "(ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearAuthentication", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "restoreSettings", "edit", "block", "Lkotlin/Function1;", "Landroidx/datastore/preferences/core/MutablePreferences;", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toSettings", "prefs", "Landroidx/datastore/preferences/core/Preferences;", "Keys", "app_debug"})
public final class SettingsRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.harvestpay.app.data.AppSettings> settings = null;
    
    public SettingsRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.harvestpay.app.data.AppSettings> getSettings() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setTheme(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ThemePreference theme, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveBusinessSettings(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.AppSettings settings, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setAuthentication(boolean remember, boolean authenticated, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearAuthentication(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object restoreSettings(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.AppSettings settings, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object edit(kotlin.jvm.functions.Function1<? super androidx.datastore.preferences.core.MutablePreferences, kotlin.Unit> block, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final com.harvestpay.app.data.AppSettings toSettings(androidx.datastore.preferences.core.Preferences prefs) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\b\u00c2\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\bR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\bR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\bR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\bR\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\bR\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\bR\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\bR\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\bR\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\bR\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\b\u00a8\u0006\""}, d2 = {"Lcom/harvestpay/app/data/SettingsRepository$Keys;", "", "<init>", "()V", "theme", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getTheme", "()Landroidx/datastore/preferences/core/Preferences$Key;", "defaultRate", "", "getDefaultRate", "ratePresets", "getRatePresets", "businessName", "getBusinessName", "ownerName", "getOwnerName", "ownerMobile", "getOwnerMobile", "tractorNumber", "getTractorNumber", "address", "getAddress", "village", "getVillage", "rememberLogin", "", "getRememberLogin", "rememberedAuthenticated", "getRememberedAuthenticated", "autoLockMinutes", "", "getAutoLockMinutes", "app_debug"})
    static final class Keys {
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> theme = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> defaultRate = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> ratePresets = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> businessName = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> ownerName = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> ownerMobile = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> tractorNumber = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> address = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> village = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> rememberLogin = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> rememberedAuthenticated = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> autoLockMinutes = null;
        @org.jetbrains.annotations.NotNull()
        public static final com.harvestpay.app.data.SettingsRepository.Keys INSTANCE = null;
        
        private Keys() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getTheme() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> getDefaultRate() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getRatePresets() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getBusinessName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getOwnerName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getOwnerMobile() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getTractorNumber() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getAddress() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getVillage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getRememberLogin() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getRememberedAuthenticated() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getAutoLockMinutes() {
            return null;
        }
    }
}