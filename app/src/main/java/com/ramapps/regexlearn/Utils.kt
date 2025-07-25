package com.ramapps.regexlearn

import android.content.res.Resources
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringWriter

class Utils {
    companion object {
        const val TAG = "Utils"
    }

    fun getJSONArrayFromRaw(rawResources: Resources, rawJsonId: Int): JSONArray {
        Log.v(TAG, "getJSONArrayFromRaw()")
        val inputStream = rawResources.openRawResource(rawJsonId)
        val stringJSON = inputStream.bufferedReader().use { it.readText() }
        Log.v(TAG, "Closing open streams for making free-up resources...")
        inputStream.close()
        return JSONArray(stringJSON)
    }

    fun getJSONObjectFromRaw(rawResources: Resources, rawJsonId: Int): JSONObject {
        Log.v(TAG, "getJSONObjectFromRaw()")
        val inputStream = rawResources.openRawResource(rawJsonId)
        val stringJSON = inputStream.bufferedReader().use { it.readText() }
        Log.v(TAG, "Closing open streams for making free-up resources...")
        inputStream.close()
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
}