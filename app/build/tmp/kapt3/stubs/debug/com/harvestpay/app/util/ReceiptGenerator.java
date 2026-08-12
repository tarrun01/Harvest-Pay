package com.harvestpay.app.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import androidx.core.content.FileProvider;
import com.harvestpay.app.data.AppSettings;
import com.harvestpay.app.domain.WorkSummary;
import java.io.File;
import java.io.OutputStream;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eJ\u001e\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0013\u00a8\u0006\u0014"}, d2 = {"Lcom/harvestpay/app/util/ReceiptGenerator;", "", "<init>", "()V", "from", "Lcom/harvestpay/app/util/ReceiptData;", "summary", "Lcom/harvestpay/app/domain/WorkSummary;", "settings", "Lcom/harvestpay/app/data/AppSettings;", "writePdf", "", "data", "output", "Ljava/io/OutputStream;", "share", "context", "Landroid/content/Context;", "whatsapp", "", "app_debug"})
public final class ReceiptGenerator {
    @org.jetbrains.annotations.NotNull()
    public static final com.harvestpay.app.util.ReceiptGenerator INSTANCE = null;
    
    private ReceiptGenerator() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.harvestpay.app.util.ReceiptData from(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.domain.WorkSummary summary, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.data.AppSettings settings) {
        return null;
    }
    
    public final void writePdf(@org.jetbrains.annotations.NotNull()
    com.harvestpay.app.util.ReceiptData data, @org.jetbrains.annotations.NotNull()
    java.io.OutputStream output) {
    }
    
    public final void share(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.harvestpay.app.util.ReceiptData data, boolean whatsapp) {
    }
}