package dev.shorthouse.remindme.ui.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    class StringResource(
        @StringRes val stringId: Int,
        vararg val stringArgs: Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is StringResource -> context.getString(stringId, stringArgs)
        }
    }
}
