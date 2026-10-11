package com.harvestpay.app.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalAuthInstrumentedTest {
    @Test
    fun generatedCredentialRejectsEmptyAndWrongPasswordsAndAcceptsCorrectPassword() {
        val credential = LocalAuth.createCredential(charArrayOf('s', 'a', 'f', 'e', '-', '7', '2'))

        assertFalse(LocalAuth.verifyPassword(charArrayOf(), credential))
        assertFalse(LocalAuth.verifyPassword(charArrayOf('w', 'r', 'o', 'n', 'g'), credential))
        assertTrue(LocalAuth.verifyPassword(charArrayOf('s', 'a', 'f', 'e', '-', '7', '2'), credential))
    }
}
