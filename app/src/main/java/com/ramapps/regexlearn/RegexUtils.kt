package com.ramapps.regexlearn

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import com.google.android.material.color.MaterialColors
import java.util.regex.Pattern

class RegexUtils {

    fun formatText(text: String, highlightColor: Int, highlightAlpha: Int = 16, formatMarker: String) : CharSequence {
        Log.v(TAG, "formatText($text, $highlightColor, $formatMarker)")
        val pattern = Pattern.compile("$formatMarker.*?$formatMarker", Pattern.MULTILINE)
        val matcher = pattern.matcher(text)
        val spanBuilder = SpannableStringBuilder()
        var pointer = 0
        while (matcher.find()) {
            spanBuilder.append(text.substring(pointer, matcher.start()))
            val formatText = text.substring(
                matcher.start() + formatMarker.length,
                matcher.end() - formatMarker.length
            )
            Log.d(TAG, "Format text: $formatText")
            val spannableString = SpannableString(formatText)
            spannableString.setSpan(
                RoundedBackgroundSpan(
                    highlightColor,
                    backgroundAlpha = highlightAlpha
                ), 0, formatText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spanBuilder.append(spannableString)
            pointer = matcher.end()
        }
        spanBuilder.append(text.substring(pointer, text.length))
        return spanBuilder
    }

    fun applyStyleToString(regex: Regex, flags: String = "", text: String) : CharSequence {
        Log.v(TAG, "applyStyleToString($regex, $flags, ${text})")
        val pattern = Pattern.compile(regex.pattern, getFlagsInt(flags))
        val matcher = pattern.matcher(text)
        val styledString = SpannableStringBuilder(text)
        val highlightColor = MaterialColors.harmonize(0xff00ff00.toInt(), android.R.attr.colorBackground)
        while (matcher.find()) {
            styledString.setSpan(
                RoundedBackgroundSpan(highlightColor, backgroundAlpha = 128),
                matcher.start(),
                matcher.end(),
                SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        return styledString
    }

    private fun getFlagsInt(flagsLetters: String) : Int {
        var flags = 0
        if (flagsLetters.contains('i', true)) {
            flags = flags or Pattern.CASE_INSENSITIVE
        }
        if (flagsLetters.contains('m', true)) {
            flags = flags or Pattern.MULTILINE
        }
        return flags
    }

    companion object {
        const val TAG = "RegexUtils"
    }
}
