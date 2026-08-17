package com.harvestpay.app.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAuthTest {
    @Test
    fun passwordChangeRequiresCurrentPassword() {
        assertEquals(
            "Enter your current password.",
            LocalAuth.passwordChangeError("", "newpass", "newpass"),
        )
    }

    @Test
    fun passwordChangeRequiresSixCharacters() {
        assertEquals(
            "New password must contain at least 6 characters.",
            LocalAuth.passwordChangeError("oldpass", "12345", "12345"),
        )
    }

    @Test
    fun passwordChangeRequiresMatchingConfirmation() {
        assertEquals(
            "New password and confirmation do not match.",
            LocalAuth.passwordChangeError("oldpass", "newpass", "different"),
        )
    }

    @Test
    fun validPasswordChangeHasNoError() {
        assertNull(LocalAuth.passwordChangeError("oldpass", "newpass", "newpass"))
    }

    @Test
    fun legacyCredentialsRequireFastHashMigration() {
        assertFalse(LocalAuth.needsFastHashMigration(null))
        assertTrue(LocalAuth.needsFastHashMigration(PasswordCredential("salt", "hash")))
        assertFalse(
            LocalAuth.needsFastHashMigration(
                PasswordCredential("salt", "hash", LocalAuth.ALGORITHM_SHA256),
            ),
        )
    }
}
