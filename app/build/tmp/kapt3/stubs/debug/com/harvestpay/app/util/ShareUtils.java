package com.harvestpay.app.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.harvestpay.app.domain.CustomerSummary;
import com.harvestpay.app.domain.WorkSummary;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tJ\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005J\u0016\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005\u00a8\u0006\u0011"}, d2 = {"Lcom/harvestpay/app/util/ShareUtils;", "", "<init>", "()V", "paymentMessage", "", "summary", "Lcom/harvestpay/app/domain/CustomerSummary;", "work", "Lcom/harvestpay/app/domain/WorkSummary;", "openWhatsApp", "", "context", "Landroid/content/Context;", "mobile", "message", "dial", "app_debug"})
public final class ShareUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.harvestpay.app.util.ShareUtils INSTANCE = null;
    
    private ShareUtils() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String paymentMessage(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.CustomerSummary summary, @org.jetbrains.annotations.Nullable()
    com.harvestpay.app.domain.WorkSummary work) {
        return null;
    }
    
    public final void openWhatsApp(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String mobile, @org.jetbrains.annotations.NotNull()
    java.lang.String message) {
    }
    
    public final void dial(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String mobile) {
    }
}