package com.ssafy.notification.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan

fun String.toSpanString(): SpannableString {
    val spannableString = SpannableString(this.replace("*", ""))
    val pattern = "\\*(.*?)\\*".toRegex()

    var offset = 0
    pattern.findAll(this).forEach { matchResult ->
        val boldText = matchResult.groupValues[1]
        val start = matchResult.range.first - offset
        val end = start + boldText.length

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        offset += 2
    }

    return spannableString
}
