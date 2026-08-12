package com.harvestpay.app;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.harvestpay.app.data.AppSettings;
import com.harvestpay.app.data.CustomerEntity;
import com.harvestpay.app.data.DatabaseSnapshot;
import com.harvestpay.app.data.FieldEntity;
import com.harvestpay.app.data.HarvestRepository;
import com.harvestpay.app.data.PaymentEntity;
import com.harvestpay.app.data.ReminderEntity;
import com.harvestpay.app.data.SettingsRepository;
import com.harvestpay.app.data.ThemePreference;
import com.harvestpay.app.data.WorkEntryEntity;
import com.harvestpay.app.domain.BusinessCalculator;
import com.harvestpay.app.domain.HarvestUiState;
import com.harvestpay.app.domain.WorkSummary;
import com.harvestpay.app.notification.ReminderWorker;
import com.harvestpay.app.security.LocalAuth;
import com.harvestpay.app.util.BackupBundle;
import com.harvestpay.app.util.BackupCodec;
import java.time.LocalDate;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u00c6\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001dB\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u001e\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020\u001d2\u0006\u0010(\u001a\u00020\u001d2\u0006\u0010)\u001a\u00020*J\u0006\u0010+\u001a\u00020&J\u0006\u0010,\u001a\u00020&J\u0006\u0010-\u001a\u00020&J\u0006\u0010.\u001a\u00020&J$\u0010/\u001a\u00020&2\u0006\u00100\u001a\u0002012\u0014\b\u0002\u00102\u001a\u000e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020&03J\u001e\u00104\u001a\u00020&2\u0006\u00100\u001a\u0002012\u000e\b\u0002\u00105\u001a\b\u0012\u0004\u0012\u00020&06J$\u00107\u001a\u00020&2\u0006\u00108\u001a\u0002092\u0014\b\u0002\u00102\u001a\u000e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020&03J\u000e\u0010:\u001a\u00020&2\u0006\u00108\u001a\u000209J4\u0010;\u001a\u00020&2\u0006\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020?2\u0006\u0010@\u001a\u00020\u001d2\u0014\b\u0002\u00102\u001a\u000e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020&03J\u000e\u0010A\u001a\u00020&2\u0006\u0010<\u001a\u00020=J>\u0010B\u001a\u00020&2\u0006\u0010<\u001a\u00020C2\u0006\u0010D\u001a\u00020?2\u0006\u0010E\u001a\u00020#2\u0006\u0010F\u001a\u00020\u001d2\u0006\u0010G\u001a\u00020\u001d2\u000e\b\u0002\u00102\u001a\b\u0012\u0004\u0012\u00020&06J\u0018\u0010H\u001a\u00020&2\u0006\u0010<\u001a\u00020C2\b\b\u0002\u0010F\u001a\u00020\u001dJ\u0018\u0010I\u001a\u00020&2\u0006\u0010J\u001a\u00020#2\b\b\u0002\u0010F\u001a\u00020\u001dJ\u000e\u0010K\u001a\u00020&2\u0006\u0010L\u001a\u00020MJ.\u0010N\u001a\u00020&2\u0006\u0010J\u001a\u00020#2\u0006\u0010O\u001a\u00020#2\u0006\u0010P\u001a\u00020\u001d2\u000e\b\u0002\u00102\u001a\b\u0012\u0004\u0012\u00020&06J\u000e\u0010Q\u001a\u00020&2\u0006\u0010R\u001a\u00020\u0019J\u000e\u0010S\u001a\u00020&2\u0006\u0010T\u001a\u00020UJ\u000e\u0010V\u001a\u00020&2\u0006\u0010W\u001a\u00020\u0012J\u0006\u0010X\u001a\u00020\u001dJ\u0006\u0010Y\u001a\u00020\u001dJ\u0006\u0010Z\u001a\u00020\u001dJ\u001e\u0010[\u001a\u00020&2\u0006\u0010\\\u001a\u00020\u001d2\u000e\b\u0002\u00105\u001a\b\u0012\u0004\u0012\u00020&06J5\u0010]\u001a\u00020&2\b\u0010^\u001a\u0004\u0018\u00010\u001d2\u001c\u0010_\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0`\u0012\u0006\u0012\u0004\u0018\u00010a03H\u0002\u00a2\u0006\u0002\u0010bJ\u0010\u0010c\u001a\u00020&2\u0006\u0010P\u001a\u00020\u001dH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u001d\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u00180\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0010R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001f\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0012\u0010\"\u001a\u0004\u0018\u00010#X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010$\u00a8\u0006e"}, d2 = {"Lcom/harvestpay/app/HarvestViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "repository", "Lcom/harvestpay/app/data/HarvestRepository;", "settingsRepository", "Lcom/harvestpay/app/data/SettingsRepository;", "<init>", "(Landroid/app/Application;Lcom/harvestpay/app/data/HarvestRepository;Lcom/harvestpay/app/data/SettingsRepository;)V", "_auth", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/harvestpay/app/AuthState;", "auth", "Lkotlinx/coroutines/flow/StateFlow;", "getAuth", "()Lkotlinx/coroutines/flow/StateFlow;", "settings", "Lcom/harvestpay/app/data/AppSettings;", "getSettings", "uiState", "Lcom/harvestpay/app/domain/HarvestUiState;", "getUiState", "reminders", "", "Lcom/harvestpay/app/data/ReminderEntity;", "getReminders", "messagesChannel", "Lkotlinx/coroutines/channels/Channel;", "", "messages", "Lkotlinx/coroutines/flow/Flow;", "getMessages", "()Lkotlinx/coroutines/flow/Flow;", "backgroundAt", "", "Ljava/lang/Long;", "login", "", "mobile", "password", "remember", "", "clearLoginError", "logout", "onBackground", "onForeground", "saveCustomer", "customer", "Lcom/harvestpay/app/data/CustomerEntity;", "onSaved", "Lkotlin/Function1;", "deleteCustomer", "onDone", "Lkotlin/Function0;", "saveField", "field", "Lcom/harvestpay/app/data/FieldEntity;", "deleteField", "saveWork", "work", "Lcom/harvestpay/app/data/WorkEntryEntity;", "initialPaid", "", "paymentMethod", "deleteWork", "addPayment", "Lcom/harvestpay/app/domain/WorkSummary;", "amount", "date", "method", "notes", "markWorkPaid", "markCustomerPaid", "customerId", "deletePayment", "payment", "Lcom/harvestpay/app/data/PaymentEntity;", "createReminder", "triggerAt", "message", "deleteReminder", "reminder", "setTheme", "theme", "Lcom/harvestpay/app/data/ThemePreference;", "saveSettings", "value", "backupJson", "customersCsv", "paymentsCsv", "restoreBackup", "text", "action", "success", "block", "Lkotlin/coroutines/Continuation;", "", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "postMessage", "Factory", "app_debug"})
public final class HarvestViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.harvestpay.app.data.HarvestRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.harvestpay.app.data.SettingsRepository settingsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.harvestpay.app.AuthState> _auth = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.harvestpay.app.AuthState> auth = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.harvestpay.app.data.AppSettings> settings = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.harvestpay.app.domain.HarvestUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.harvestpay.app.data.ReminderEntity>> reminders = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.channels.Channel<java.lang.String> messagesChannel = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> messages = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long backgroundAt;
    
    public HarvestViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.HarvestRepository repository, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.SettingsRepository settingsRepository) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.harvestpay.app.AuthState> getAuth() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.harvestpay.app.data.AppSettings> getSettings() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.harvestpay.app.domain.HarvestUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.harvestpay.app.data.ReminderEntity>> getReminders() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getMessages() {
        return null;
    }
    
    public final void login(@org.jetbrains.annotations.NotNull()
    java.lang.String mobile, @org.jetbrains.annotations.NotNull()
    java.lang.String password, boolean remember) {
    }
    
    public final void clearLoginError() {
    }
    
    public final void logout() {
    }
    
    public final void onBackground() {
    }
    
    public final void onForeground() {
    }
    
    public final void saveCustomer(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onSaved) {
    }
    
    public final void deleteCustomer(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.CustomerEntity customer, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void saveField(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.FieldEntity field, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onSaved) {
    }
    
    public final void deleteField(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.FieldEntity field) {
    }
    
    public final void saveWork(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.WorkEntryEntity work, double initialPaid, @org.jetbrains.annotations.NotNull()
    java.lang.String paymentMethod, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onSaved) {
    }
    
    public final void deleteWork(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.WorkEntryEntity work) {
    }
    
    public final void addPayment(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.WorkSummary work, double amount, long date, @org.jetbrains.annotations.NotNull()
    java.lang.String method, @org.jetbrains.annotations.NotNull()
    java.lang.String notes, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSaved) {
    }
    
    public final void markWorkPaid(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.WorkSummary work, @org.jetbrains.annotations.NotNull()
    java.lang.String method) {
    }
    
    public final void markCustomerPaid(long customerId, @org.jetbrains.annotations.NotNull()
    java.lang.String method) {
    }
    
    public final void deletePayment(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.PaymentEntity payment) {
    }
    
    public final void createReminder(long customerId, long triggerAt, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSaved) {
    }
    
    public final void deleteReminder(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ReminderEntity reminder) {
    }
    
    public final void setTheme(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.ThemePreference theme) {
    }
    
    public final void saveSettings(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.AppSettings value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String backupJson() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String customersCsv() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String paymentsCsv() {
        return null;
    }
    
    public final void restoreBackup(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    private final void action(java.lang.String success, kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> block) {
    }
    
    private final void postMessage(java.lang.String message) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J%\u0010\u0006\u001a\u0002H\u0007\"\b\b\u0000\u0010\u0007*\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00070\nH\u0016\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/harvestpay/app/HarvestViewModel$Factory;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "application", "Lcom/harvestpay/app/HarvestPayApplication;", "<init>", "(Lcom/harvestpay/app/HarvestPayApplication;)V", "create", "T", "Landroidx/lifecycle/ViewModel;", "modelClass", "Ljava/lang/Class;", "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;", "app_debug"})
    public static final class Factory implements androidx.lifecycle.ViewModelProvider.Factory {
        @org.jetbrains.annotations.NotNull()
        private final com.harvestpay.app.HarvestPayApplication application = null;
        
        public Factory(@org.jetbrains.annotations.NotNull()
        com.harvestpay.app.HarvestPayApplication application) {
            super();
        }
        
        @java.lang.Override()
        @kotlin.Suppress(names = {"UNCHECKED_CAST"})
        @org.jetbrains.annotations.NotNull()
        public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
        java.lang.Class<T> modelClass) {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
        java.lang.Class<T> modelClass, @org.jetbrains.annotations.NotNull()
        androidx.lifecycle.viewmodel.CreationExtras extras) {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
        kotlin.reflect.KClass<T> modelClass, @org.jetbrains.annotations.NotNull()
        androidx.lifecycle.viewmodel.CreationExtras extras) {
            return null;
        }
    }
}