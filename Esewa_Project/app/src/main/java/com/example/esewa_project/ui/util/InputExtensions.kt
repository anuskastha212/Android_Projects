package com.example.esewa_project.ui.util

import android.text.InputFilter
import android.widget.EditText

fun EditText.disableEmojis() {
    val emojiFilter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            val type = Character.getType(source[i])
            if (type == Character.SURROGATE.toInt() ||
                type == Character.OTHER_SYMBOL.toInt() ||
                type == Character.ENCLOSING_MARK.toInt()
            ) {
                return@InputFilter ""
            }
        }
        null
    }

    // Add the emoji filter without removing existing filters (like maxLength)
    this.filters = this.filters.plus(emojiFilter)
}