package com.harvestpay.app.localization

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/** Android resources are the single source of translated application text. */
object AppText {
    internal var resolver: ((Int, Array<out Any?>) -> String)? = null
    private var quantityResolver: ((Int, Int, Array<out Any?>) -> String)? = null
    fun quantity(id: Int, count: Int, vararg arguments: Any?): String =
        requireNotNull(quantityResolver)(id, count, arguments)
    /** Choose the owner-provided name without changing the canonical stored identifier. */
    fun bilingualName(english: String, hindi: String): String =
        if (java.util.Locale.getDefault().language == "hi" && hindi.isNotBlank()) hindi else english
    fun label(value: String): String = StableLabels.display(value)
    fun initialize(context: Context) {
        quantityResolver = { id, count, arguments ->
            ContextCompat.getContextForLanguage(context).resources.getQuantityString(id, count, *arguments)
        }
        resolver = { id, arguments ->
            ContextCompat.getContextForLanguage(context).getString(id, *arguments)
        }
    }
    fun text(@StringRes id: Int, vararg arguments: Any?): String =
        requireNotNull(resolver) { "Application text resources have not been initialized" }(id, arguments)
}
