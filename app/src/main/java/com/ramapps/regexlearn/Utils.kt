package com.ramapps.regexlearn

import android.content.res.Resources
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.provider.FontsContractCompat.TypefaceStyle
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringWriter
import java.util.regex.Matcher
import java.util.regex.Pattern

class Utils {
    companion object {
        const val TAG = "Utils"
    }

    fun getJSONArrayFromRaw(rawResources: Resources, rawJsonId: Int): JSONArray {
        Log.v(TAG, "getJSONArrayFromRaw()")

        val inputStream = rawResources.openRawResource(rawJsonId)
        val writer = StringWriter()
        while (true) {
            val n = inputStream.read()

            if (n < 0) break

            writer.write(n)
        }

        val stringJSON = writer.toString()

        Log.v(TAG, "Closing open streams for making free-up resources...")
        inputStream.close()
        writer.close()

        return JSONArray(stringJSON)
    }

    fun getJSONObjectFromRaw(rawResources: Resources, rawJsonId: Int): JSONObject {
        Log.v(TAG, "getJSONObjectFromRaw()")

        val inputStream = rawResources.openRawResource(rawJsonId)
        val writer = StringWriter()

        while (true) {
            val n = inputStream.read()

            if (n < 0) break

            writer.write(n)
        }

        val stringJSON = writer.toString()

        Log.v(TAG, "Closing open streams for making free-up resources...")
        inputStream.close()
        writer.close()

        return JSONObject(stringJSON)
    }

    fun getLessonsTitleFromDataJson(
        jsonArrayLessons: JSONArray,
        jsonLocalizationLessonValues: JSONObject
    ): List<String> {
        Log.v(TAG, "getLessonsTitleFromDataJson")
        val titlesArrayList = ArrayList<String>()

        for (i in 0..<jsonArrayLessons.length()) {
            val jsonLesson = jsonArrayLessons.getJSONObject(i)
            titlesArrayList.add(
                jsonLocalizationLessonValues.getString(
                    jsonLesson.getString(
                        GlobalVariables.LESSON_JSON_KEY_TITLE
                    )
                )
            )
        }

        return titlesArrayList
    }

    fun stylingFormattedText(text: String) : CharSequence {
        var styledText = SpannableStringBuilder(text)

        val highlightPattern = Pattern.compile("`\\S+`")
        val matcher = highlightPattern.matcher(text)
        while (matcher.find()) {
            styledText.setSpan(
                BackgroundColorSpan(
                    com.google.android.material.color.MaterialColors.harmonize(0x3300ff00.toInt(),
                        android.R.attr.colorPrimary)),
                matcher.start(),
                matcher.end(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            styledText.delete(matcher.start(), matcher.start() + 1)
            styledText.delete(matcher.end() - 2, matcher.end() - 1)
        }

        return styledText
    }
}