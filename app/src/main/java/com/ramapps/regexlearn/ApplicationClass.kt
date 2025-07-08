package com.ramapps.regexlearn

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors

class ApplicationClass : Application() {
    private lateinit var settingsPrefs : SharedPreferences

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        assignGlobalVariables()
        loadAndSetDarkMode()
    }

    private fun assignGlobalVariables() {
        settingsPrefs = getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, MODE_PRIVATE)
    }

    private fun loadAndSetDarkMode() {
        val darkModeState = settingsPrefs.getInt(
            GlobalVariables.PREFERENCES_SETTINGS_DARK_MODE,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(darkModeState)
    }

}