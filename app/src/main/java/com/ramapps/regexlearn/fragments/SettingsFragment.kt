package com.ramapps.regexlearn.fragments

import android.app.Activity
import android.app.LocaleManager
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ramapps.regexlearn.GlobalVariables
import com.ramapps.regexlearn.R
import com.ramapps.regexlearn.activities.AboutActivity
import com.ramapps.regexlearn.activities.MainActivity
import java.util.Locale
import java.util.Objects

class SettingsFragment : Fragment() {
    private lateinit var settingsPreferences : SharedPreferences

    private lateinit var languageSettingsItemCardView : MaterialCardView
    private lateinit var defaultFragmentSettingsItemCardView : MaterialCardView
    private lateinit var darkModeSettingsItemCardView : MaterialCardView
    private lateinit var resetLearningProgressSettingsItemCardView : MaterialCardView
    private lateinit var contactMeSettingsItemCardView : MaterialCardView
    private lateinit var aboutAppSettingsItemCardView : MaterialCardView
    private lateinit var parentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        initVariables()
        initViews(view)
        addListeners()
        loadSettingsToShow()
        return view
    }

    private fun initVariables() {
        settingsPreferences = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, Activity.MODE_PRIVATE)
    }

    private fun initViews(parentView: View) {
        this.parentView = parentView
        languageSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_language)
        defaultFragmentSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_default_fragment)
        darkModeSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_night_mode)
        resetLearningProgressSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_reset_learning_progress)
        contactMeSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_contact_me)
        aboutAppSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_about_app)
    }

    private fun addListeners() {
        languageSettingsItemCardView.setOnClickListener{
            val languagesLabelList = resources.getStringArray(R.array.languages_label).asList().toMutableList()
            languagesLabelList.add(0, getString(R.string.system_default))
            Log.d(TAG, "Languages label list: ${languagesLabelList.joinToString(", ")}")

            val currentLocale = getCurrentLocale()
            val defaultSelection = if (languagesLabelList.indexOf(localeToLanguageLabel(currentLocale)) < 0) {
                0
            } else {
                languagesLabelList.indexOf(localeToLanguageLabel(currentLocale))
            }

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.language))
                .setSingleChoiceItems(languagesLabelList.toTypedArray(), defaultSelection) { dialog, which ->
                    updateAppLanguage(languagesLabelList[which])
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        val relaunchIntent = Intent(requireContext(), MainActivity::class.java)
                        relaunchIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(relaunchIntent)
                    }
                    dialog.dismiss()
                    loadAndShowLanguage()
                }
                .create()
            dialog.window!!.attributes.gravity = Gravity.BOTTOM
            dialog.show()
        }

        defaultFragmentSettingsItemCardView.setOnClickListener{
            val bottomNavigationItemsIdList = arrayOf(
                getString(R.string.home),
                getString(R.string.learning),
                getString(R.string.playground))
            val currentDefaultFragment = settingsPreferences.getInt(
                GlobalVariables.PREFERENCES_SETTINGS_DEFAULT_FRAGMENT,
                R.id.menu_item_home
            )
            val defSelection = when (currentDefaultFragment) {
                R.id.menu_item_learning -> 1
                R.id.menu_item_playground -> 2
                else -> 0
            }
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.default_launch_screen))
                .setSingleChoiceItems(bottomNavigationItemsIdList, defSelection) {dialog, which ->
                    val selectedBottomNavigationItemId = when(which) {
                        1 -> R.id.menu_item_learning
                        2 -> R.id.menu_item_playground
                        else -> R.id.menu_item_home
                    }
                    settingsPreferences.edit{
                        putInt(
                            GlobalVariables.PREFERENCES_SETTINGS_DEFAULT_FRAGMENT,
                            selectedBottomNavigationItemId)
                    }
                    dialog.dismiss()
                    loadAndShowDefaultFragment()
                }
                .create()
            dialog.window!!.attributes.gravity = Gravity.BOTTOM
            dialog.show()
        }


        darkModeSettingsItemCardView.setOnClickListener{
            val darkModeLabels = arrayOf(getString(R.string.off), getString(R.string.on), getString(
                R.string.system_default
            ))
            val darkModeCurrentState = when (settingsPreferences.getInt(
                GlobalVariables.PREFERENCES_SETTINGS_DARK_MODE,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
                    AppCompatDelegate.MODE_NIGHT_NO -> 0
                    AppCompatDelegate.MODE_NIGHT_YES -> 1
                    else -> 2
                }
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dark_mode))
                .setSingleChoiceItems(darkModeLabels, darkModeCurrentState) {dia, which ->
                    val selectedDarkMode = when(which) {
                        0 -> AppCompatDelegate.MODE_NIGHT_NO
                        1 -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                    settingsPreferences.edit {
                        putInt(
                            GlobalVariables.PREFERENCES_SETTINGS_DARK_MODE,
                            selectedDarkMode
                        )
                    }
                    AppCompatDelegate.setDefaultNightMode(selectedDarkMode)
                    loadAndShowDarkMode()
                    dia.dismiss()
                }
                .create()
            dialog.window!!.attributes.gravity = Gravity.BOTTOM
            dialog.show()
        }

        resetLearningProgressSettingsItemCardView.setOnClickListener{
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.replay_24)
                .setTitle(getString(R.string.reset_learning_progress))
                .setMessage(getString(R.string.message_reset_learning_progress_alert))
                .setPositiveButton(getString(R.string.reset)) { dia, _ ->
                    Log.d(TAG, "Resetting learning progress...")
                    val prefs = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE)!!
                    val lastUnlockedLessonId = prefs.getInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, 0)
                    val lessonId = prefs.getInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, 0)
                    prefs.edit {
                        putInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, 0)
                        putInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, 0)
                    }
                    Snackbar.make(parentView, R.string.learning_progress_reseted_successfully, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, {
                            prefs.edit {
                                putInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, lastUnlockedLessonId)
                                putInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, lessonId)
                            }
                        }).show()
                    dia.dismiss()
                }
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create()
            dialog.show()
        }

        contactMeSettingsItemCardView.setOnClickListener{
            val uri = Uri.fromParts("mailto", "deputy-wolf-roster@duck.com", null)
            val prepareEmailCompose = Intent(Intent.ACTION_VIEW)
            prepareEmailCompose.data = uri
            startActivity(prepareEmailCompose)
        }

        aboutAppSettingsItemCardView.setOnClickListener{
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
    }

    private fun loadSettingsToShow() {
        loadAndShowLanguage()
        loadAndShowDefaultFragment()
        loadAndShowDarkMode()
    }

    private fun getCurrentLocale(): Locale {
        val currentLocale: Locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the system integrated locale manager to manage languages and locales.
            // This give the user the ability to change app language from system settings.
            Log.d(TAG, "Using android system LocaleManager to manage locales.")
            val locales = AppCompatDelegate.getApplicationLocales()
            Log.d(TAG, "Application locales: $locales")
            currentLocale = Objects.requireNonNullElse(locales.get(0), Locale(""))
        } else {
            // In old android versions we should manage locales by ourself.
            // Using shared preferences to save and restore locales.
            Log.d(TAG, "Using SharedPreferences to manage locales.")
            val prefs = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, Activity.MODE_PRIVATE)
            currentLocale = Locale(prefs.getString(GlobalVariables.PREFERENCES_SETTINGS_LANGUAGE, "")!!)
        }
        Log.d(TAG, "Detected locale that used by app currently: $currentLocale")
        return currentLocale
    }

    private fun localeToLanguageLabel(locale: Locale): String {
        Log.d(TAG, "localeToLanguage($locale)")
        return when(locale.language) {
            "ar" -> getString(R.string.arabic)
            "en" -> getString(R.string.english)
            "fr" -> getString(R.string.french)
            "de" -> getString(R.string.german)
            "it" -> getString(R.string.italian)
            "ko" -> getString(R.string.korean)
            "fa" -> getString(R.string.persian)
            "ru" -> getString(R.string.russia)
            "es" -> getString(R.string.spanish)
            else -> getString(R.string.system_default)
        }
    }

    private fun updateAppLanguage(languageLabel: String) {
        val locale = languageLabelToLocale(languageLabel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the system integrated locale manager to manage languages and locales.
            // This give the user the ability to change app language from system settings.
            Log.d(TAG, "Using android system LocaleManager to manage locales.")
            val locales: LocaleListCompat =
                if(Objects.equals(locale.language, "")) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.create(languageLabelToLocale(languageLabel))
                }
            AppCompatDelegate.setApplicationLocales(locales)
        } else {
            // In old android versions we should manage locales by ourself.
            // Using shared preferences to save and restore locales.
            Log.d(TAG, "Using SharedPreferences to manage locales.")
            settingsPreferences.edit { putString(GlobalVariables.PREFERENCES_SETTINGS_LANGUAGE, locale.language) }
        }
    }

    private fun loadAndShowLanguage() {
        val currentLocale: Locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the system integrated locale manager to manage languages and locales.
            // This give the user the ability to change app language from system settings.
            Log.d(TAG, "Using android system LocaleManager to manage locales.")
            val localeManager = requireActivity().getSystemService(LocaleManager::class.java)
            val locales = localeManager.getApplicationLocales(requireContext().packageName)
            Log.d(TAG, "Application locales: $locales")
            currentLocale = Objects.requireNonNullElse(locales.get(0), Locale(""))
        } else {
            // In old android versions we should manage locales by ourself.
            // Using shared preferences to save and restore locales.
            Log.d(TAG, "Using SharedPreferences to manage locales.")
            val prefs = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, Activity.MODE_PRIVATE)
            currentLocale = Locale(prefs.getString(GlobalVariables.PREFERENCES_SETTINGS_LANGUAGE, "")!!)
        }
        val languageDescriptionTextView = parentView.findViewById<TextView>(R.id.settings_fragment_text_view_description_language)
        languageDescriptionTextView.text = localeToLanguageLabel(currentLocale)
    }

    private fun loadAndShowDefaultFragment() {
        val currentBottomNavigationDefaultItemId = settingsPreferences.getInt(
            GlobalVariables.PREFERENCES_SETTINGS_DEFAULT_FRAGMENT,
            R.id.menu_item_home
        )
        val defaultBottomNavigationFragmentStateIcon = when(currentBottomNavigationDefaultItemId) {
            R.id.menu_item_learning -> R.drawable.outline_history_edu_24
            R.id.menu_item_playground -> R.drawable.outline_playground_24px
            else -> R.drawable.outline_home_24
        }
        val defaultBottomNavigationFragmentStateImageView = parentView.findViewById<ImageView>(R.id.settings_fragment_image_view_icon_default_fragment)
        defaultBottomNavigationFragmentStateImageView.setImageResource(defaultBottomNavigationFragmentStateIcon)
    }

    private fun loadAndShowDarkMode() {
        val darkModeDescriptionTextView = parentView.findViewById<TextView>(R.id.settings_fragment_text_view_description_night_mode)
        val darkModeState = settingsPreferences.getInt(GlobalVariables.PREFERENCES_SETTINGS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        when (darkModeState) {
            AppCompatDelegate.MODE_NIGHT_NO -> darkModeDescriptionTextView.text = getString(R.string.off)
            AppCompatDelegate.MODE_NIGHT_YES -> darkModeDescriptionTextView.text = getString(R.string.on)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> darkModeDescriptionTextView.text = getString(
                R.string.system_default
            )
        }
    }

    private fun languageLabelToLocale(languageLabel: String): Locale {
        Log.d(TAG, "languageLabelToLocale($languageLabel)")
        return when(languageLabel) {
            getString(R.string.arabic) -> Locale("ar")
            getString(R.string.english) -> Locale("en")
            getString(R.string.french) -> Locale("fr")
            getString(R.string.german) -> Locale("de")
            getString(R.string.italian) -> Locale("it")
            getString(R.string.korean) -> Locale("ko")
            getString(R.string.persian) -> Locale("fa")
            getString(R.string.russia) -> Locale("ru")
            getString(R.string.spanish) -> Locale("es")
            else -> Locale("")
        }
    }

    companion object {
        const val TAG = "SettingsFragment"

        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}