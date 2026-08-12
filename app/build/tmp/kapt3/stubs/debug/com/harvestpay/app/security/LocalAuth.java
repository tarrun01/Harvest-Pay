package com.harvestpay.app.security;

import android.util.Base64;
import java.security.MessageDigest;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Verifies the initial owner credential without storing the password in readable form.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0019\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0005J\u000e\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/harvestpay/app/security/LocalAuth;", "", "<init>", "()V", "OWNER_MOBILE", "", "ITERATIONS", "", "KEY_LENGTH_BITS", "SALT_BASE64", "PASSWORD_HASH_BASE64", "verify", "", "mobile", "password", "", "normalizeMobile", "value", "isValidIndianMobile", "app_debug"})
public final class LocalAuth {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String OWNER_MOBILE = "9639048837";
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH_BITS = 256;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SALT_BASE64 = "SGFydmVzdFBheS0yMDI2LUxvY2FsLXYx";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PASSWORD_HASH_BASE64 = "6kN0W+8t8WfOXKZdEi9GR+juYdlEw/XW0OaWmET24EY=";
    @org.jetbrains.annotations.NotNull()
    public static final com.harvestpay.app.security.LocalAuth INSTANCE = null;
    
    private LocalAuth() {
        super();
    }
    
    public final boolean verify(@org.jetbrains.annotations.NotNull()
    java.lang.String mobile, @org.jetbrains.annotations.NotNull()
    char[] password) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String normalizeMobile(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
    
    public final boolean isValidIndianMobile(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return false;
    }
}