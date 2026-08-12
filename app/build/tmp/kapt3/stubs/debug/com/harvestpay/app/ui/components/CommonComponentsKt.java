package com.harvestpay.app.ui.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.material3.ExposedDropdownMenuAnchorType;
import androidx.compose.material3.ExposedDropdownMenuDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.KeyboardType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0005\u001a.\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0006H\u0007\u001a\"\u0010\u0007\u001a\u00020\u00012\u0018\u0010\b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\n0\tH\u0007\u001a\u0018\u0010\u000b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u0003H\u0007\u001aF\u0010\r\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u001ao\u0010\u0014\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00152\u0006\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u0001H\u00152\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00150\t2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u0002H\u0015\u0012\u0004\u0012\u00020\u00030\u001a2\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u0002H\u0015\u0012\u0004\u0012\u00020\u00010\u001a2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\b\b\u0002\u0010\u001e\u001a\u00020\u0011H\u0007\u00a2\u0006\u0002\u0010\u001f\u001a6\u0010 \u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u00032\u0006\u0010!\u001a\u00020\"2\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\u00010\u001a2\b\b\u0002\u0010\u001c\u001a\u00020\u001dH\u0007\u001aB\u0010$\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00032\u0012\u0010%\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u001a2\u0006\u0010\u0016\u001a\u00020\u00032\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010\u0003H\u0007\u00a8\u0006\'"}, d2 = {"SectionHeader", "", "title", "", "action", "onAction", "Lkotlin/Function0;", "MetricGrid", "metrics", "", "Lkotlin/Pair;", "EmptyState", "body", "ConfirmDialog", "message", "confirmLabel", "destructive", "", "onConfirm", "onDismiss", "DropdownField", "T", "label", "value", "options", "optionLabel", "Lkotlin/Function1;", "onSelected", "modifier", "Landroidx/compose/ui/Modifier;", "enabled", "(Ljava/lang/String;Ljava/lang/Object;Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Z)V", "DateField", "epochDay", "", "onDateChanged", "DecimalField", "onValueChange", "prefix", "app_debug"})
public final class CommonComponentsKt {
    
    @androidx.compose.runtime.Composable()
    public static final void SectionHeader(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String action, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAction) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void MetricGrid(@org.jetbrains.annotations.NotNull()
    java.util.List<kotlin.Pair<java.lang.String, java.lang.String>> metrics) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EmptyState(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String body) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ConfirmDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.NotNull()
    java.lang.String confirmLabel, boolean destructive, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onConfirm, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final <T extends java.lang.Object>void DropdownField(@org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.Nullable()
    T value, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends T> options, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super T, java.lang.String> optionLabel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super T, kotlin.Unit> onSelected, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, boolean enabled) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DateField(@org.jetbrains.annotations.NotNull()
    java.lang.String label, long epochDay, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onDateChanged, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DecimalField(@org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onValueChange, @org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.Nullable()
    java.lang.String prefix) {
    }
}