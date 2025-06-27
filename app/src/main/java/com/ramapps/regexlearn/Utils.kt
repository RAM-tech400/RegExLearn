package com.ramapps.regexlearn

import androidx.annotation.NonNull
import org.json.JSONArray
import org.json.JSONObject

class Utils {
    fun getLessonsTitleFromDataJson(@NonNull jsonArrayLessons: JSONArray) : List<String> {
        var titlesArrayList : ArrayList<String>

        for(i in 0..<jsonArrayLessons.length()){
            val jsonLesson = jsonArrayLessons.getJSONObject(i)
            titlesArrayList.add()
        }
    }
}