package com.harvestpay.app.domain

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SuccessFeedbackTest {
    @Test
    fun `confirmation waits for save to complete`() = runTest {
        val committed = CompletableDeferred<Long>()
        val events = mutableListOf<String>()
        val job = launch {
            reportCompletedSave(
                save = { committed.await() },
                onFailure = { events += "error" },
                onSaved = { events += "saved:$it" },
            )
        }
        runCurrent()
        assertTrue(events.isEmpty())
        committed.complete(42L)
        job.join()
        assertEquals(listOf("saved:42"), events)
    }

    @Test
    fun `failed save reports error without success`() = runTest {
        val events = mutableListOf<String>()
        reportCompletedSave(
            save = { throw IllegalStateException("Database unavailable") },
            onFailure = { events += "error:${it.message}" },
            onSaved = { events += "saved" },
        )
        assertEquals(listOf("error:Database unavailable"), events)
    }

    @Test
    fun `cancelled save produces no confirmation`() = runTest {
        val events = mutableListOf<String>()
        try {
            reportCompletedSave(
                save = { throw CancellationException("Cancelled") },
                onFailure = { events += "error" },
                onSaved = { events += "saved" },
            )
            fail("Cancellation must propagate")
        } catch (_: CancellationException) {
            assertTrue(events.isEmpty())
        }
    }
}
