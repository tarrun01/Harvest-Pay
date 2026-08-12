package com.harvestpay.app.security

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Verifies the initial owner credential without storing the password in readable form. */
object LocalAuth {
    private const val OWNER_MOBILE = "9639048837"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BASE64 = "SGFydmVzdFBheS0yMDI2LUxvY2FsLXYx"
    private const val PASSWORD_HASH_BASE64 = "6kN0W+8t8WfOXKZdEi9GR+juYdlEw/XW0OaWmET24EY="

    fun verify(mobile: String, password: CharArray): Boolean {
        if (normalizeMobile(mobile) != OWNER_MOBILE) {
            password.fill('\u0000')
            return false
        }
        return try {
            val salt = Base64.decode(SALT_BASE64, Base64.NO_WRAP)
            val expected = Base64.decode(PASSWORD_HASH_BASE64, Base64.NO_WRAP)
            val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS)
            val actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
            spec.clearPassword()
            MessageDigest.isEqual(expected, actual)
        } finally {
            password.fill('\u0000')
        }
    }

    fun normalizeMobile(value: String): String {
        val digits = value.filter(Char::isDigit)
        return when {
            digits.length == 12 && digits.startsWith("91") -> digits.drop(2)
            digits.length == 11 && digits.startsWith("0") -> digits.drop(1)
            else -> digits
        }
    }

    fun isValidIndianMobile(value: String): Boolean =
        normalizeMobile(value).matches(Regex("^[6-9][0-9]{9}$"))
}
