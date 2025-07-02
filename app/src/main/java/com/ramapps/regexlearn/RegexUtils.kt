package com.ramapps.regexlearn

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import com.google.android.material.color.MaterialColors
import java.util.regex.Pattern

class RegexUtils {
    companion object {
        const val TAG = "RegexUtils"
    }


    fun applyStyleToString(regex: Regex, flags: String = "", text: String) : CharSequence {
        Log.v(TAG, "applyStyleToString($regex, $flags, ${text})")
        val pattern = Pattern.compile(regex.pattern, getFlagsInt(flags))
        val matcher = pattern.matcher(text)
        val styledString = SpannableStringBuilder(text)
        val highlightColor = MaterialColors.harmonize(0xff00ff00.toInt(), android.R.attr.colorBackground)

        while (matcher.find()) {
            styledString.setSpan(RoundedBackgroundSpan(highlightColor), matcher.start(), matcher.end(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
        }

        return styledString
    }

    fun getFlagsInt(flagsLetters: String) : Int {
        var flags = 0
        if (flagsLetters.contains('i', true)) {
            flags = flags or Pattern.CASE_INSENSITIVE
        }
        if (flagsLetters.contains('m', true)) {
            flags = flags or Pattern.MULTILINE
        }
        return flags
    }
}
