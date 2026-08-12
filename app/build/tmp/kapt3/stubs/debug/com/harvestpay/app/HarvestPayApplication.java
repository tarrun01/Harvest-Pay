package com.harvestpay.app;

import android.app.Application;
import com.harvestpay.app.data.AppDatabase;
import com.harvestpay.app.data.HarvestRepository;
import com.harvestpay.app.data.SettingsRepository;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\n\u001a\u00020\u000b8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\t\u001a\u0004\b\f\u0010\rR\u001b\u0010\u000f\u001a\u00020\u00108FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\t\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0014"}, d2 = {"Lcom/harvestpay/app/HarvestPayApplication;", "Landroid/app/Application;", "<init>", "()V", "database", "Lcom/harvestpay/app/data/AppDatabase;", "getDatabase", "()Lcom/harvestpay/app/data/AppDatabase;", "database$delegate", "Lkotlin/Lazy;", "repository", "Lcom/harvestpay/app/data/HarvestRepository;", "getRepository", "()Lcom/harvestpay/app/data/HarvestRepository;", "repository$delegate", "settingsRepository", "Lcom/harvestpay/app/data/SettingsRepository;", "getSettingsRepository", "()Lcom/harvestpay/app/data/SettingsRepository;", "settingsRepository$delegate", "app_debug"})
public final class HarvestPayApplication extends android.app.Application {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy database$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy repository$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy settingsRepository$delegate = null;
    
    public HarvestPayApplication() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.AppDatabase getDatabase() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.HarvestRepository getRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.data.SettingsRepository getSettingsRepository() {
        return null;
    }
}