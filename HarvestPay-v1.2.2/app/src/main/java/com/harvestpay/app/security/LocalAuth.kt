package com.harvestpay.app.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class PasswordCredential(
    val saltBase64: String,
    val hashBase64: String,
    val algorithm: String = LocalAuth.ALGORITHM_PBKDF2,
)

/** Verifies the owner credential and migrates legacy hashes without storing readable passwords. */
object LocalAuth {
    const val ALGORITHM_SHA256 = "SHA-256"
    const val ALGORITHM_PBKDF2 = "PBKDF2"
    private const val OWNER_MOBILE = "9639048837"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BASE64 = "SGFydmVzdFBheS0yMDI2LUxvY2FsLXYx"
    private const val PASSWORD_HASH_BASE64 = "QjYJh+gdhkH2QCL1IHbPXTI0hrWm4xjGrzeLp0j88vk="

    fun verify(
        mobile: String,
        password: CharArray,
        credential: PasswordCredential? = null,
    ): Boolean {
        if (normalizeMobile(mobile) != OWNER_MOBILE) {
            password.fill('\u0000')
            return false
        }
        return verifyPassword(password, credential)
    }

    fun verifyPassword(password: CharArray, credential: PasswordCredential? = null): Boolean {
        return try {
            val salt = Base64.decode(credential?.saltBase64 ?: SALT_BASE64, Base64.NO_WRAP)
            val expected = Base64.decode(credential?.hashBase64 ?: PASSWORD_HASH_BASE64, Base64.NO_WRAP)
            val actual = when (credential?.algorithm ?: ALGORITHM_SHA256) {
                ALGORITHM_SHA256 -> deriveSha256(password, salt)
                else -> derivePbkdf2(password, salt)
            }
            MessageDigest.isEqual(expected, actual)
        } catch (_: IllegalArgumentException) {
            false
        } finally {
            password.fill('\u0000')
        }
    }

    fun createCredential(password: CharArray): PasswordCredential {
        return try {
            val salt = ByteArray(24).also(SecureRandom()::nextBytes)
            val hash = deriveSha256(password, salt)
            PasswordCredential(
                saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP),
                hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP),
                algorithm = ALGORITHM_SHA256,
            )
        } finally {
            password.fill('\u0000')
        }
    }

    fun passwordChangeError(current: String, new: String, confirmation: String): String? = when {
        current.isBlank() -> "Enter your current password."
        new.length < 6 -> "New password must contain at least 6 characters."
        new != confirmation -> "New password and confirmation do not match."
        new == current -> "Choose a new password different from the current password."
        else -> null
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

    fun needsFastHashMigration(credential: PasswordCredential?): Boolean =
        credential != null && credential.algorithm != ALGORITHM_SHA256

    private fun deriveSha256(password: CharArray, salt: ByteArray): ByteArray {
        val passwordBytes = password.concatToString().toByteArray(Charsets.UTF_8)
        return try {
            MessageDigest.getInstance("SHA-256").apply {
                update(salt)
                update(passwordBytes)
            }.digest()
        } finally {
            passwordBytes.fill(0)
        }
    }

    private fun derivePbkdf2(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
        } finally {
            spec.clearPassword()
        }
    }
}
