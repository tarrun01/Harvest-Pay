package com.harvestpay.app.domain

import kotlinx.coroutines.CancellationException

data class SuccessFeedback(val message: String, val detail: String? = null)

/** Only a completed save can reach onSaved; cancellation is never presented as success. */
internal suspend fun <T> reportCompletedSave(
    save: suspend () -> T,
    onFailure: suspend (Exception) -> Unit,
    onSaved: suspend (T) -> Unit,
) {
    val result = try {
        save()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        onFailure(error)
        return
    }
    onSaved(result)
}
