package com.ramapps.regexlearn

import android.app.Activity
import android.app.LocaleManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale
import java.util.Objects
import androidx.core.content.edit

class SettingsFragment : Fragment() {

    private lateinit var languageSettingsItemCardView : MaterialCardView
    private lateinit var darkModeSettingsItemCardView : MaterialCardView
    private lateinit var contactMeSettingsItemCardView : MaterialCardView
    private lateinit var aboutAppSettingsItemCardView : MaterialCardView

    private lateinit var parentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        initViews(view)
        addListeners()
        loadSettingsToShow()
        return view
    }

    private fun initViews(parentView: View) {
        this.parentView = parentView
        languageSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_language)
        darkModeSettingsItemCardView = parentView.findViewById(R.id.settings_fragment_card_view_night_mode)
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
                .setSingleChoiceItems(languagesLabelList.toTypedArray(), defaultSelection, DialogInterface.OnClickListener{dialog, which ->
                    updateAppLanguage(languagesLabelList[which])
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        val relauanchIntent = Intent(requireContext(), MainActivity::class.java)
                        relauanchIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(relauanchIntent)
                    }
                })
                .create()
            dialog.window!!.attributes.gravity = Gravity.BOTTOM
            dialog.show()
        }

        darkModeSettingsItemCardView.setOnClickListener{
            val prefs = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, Activity.MODE_PRIVATE)
            val darkModeLabels = arrayOf(getString(R.string.off), getString(R.string.on), getString(R.string.system_default))
            val darkModeCurrentState = when (prefs.getInt(GlobalVariables.PREFERENCES_SETTINGS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
                AppCompatDelegate.MODE_NIGHT_NO -> 0
                AppCompatDelegate.MODE_NIGHT_YES -> 1
                else -> 2
            }
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dark_mode))
                .setSingleChoiceItems(darkModeLabels, darkModeCurrentState, DialogInterface.OnClickListener{dia, which ->
                    val selectedDarkMode = when(which) {
                        0 -> AppCompatDelegate.MODE_NIGHT_NO
                        1 -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                    prefs.edit {
                        putInt(
                            GlobalVariables.PREFERENCES_SETTINGS_DARK_MODE,
                            selectedDarkMode
                        )
                    }
                    AppCompatDelegate.setDefaultNightMode(selectedDarkMode)
                    loadAndShowDarkMode()
                    dia.dismiss()
                })
                .create()
            dialog.window!!.attributes.gravity = Gravity.BOTTOM
            dialog.show()
        }

        contactMeSettingsItemCardView.setOnClickListener{
            val uri = Uri.fromParts("mailto", "deputy-wolf-roster@duck.com", null)
            val prepareEmailCompose = Intent(Intent.ACTION_VIEW)
            prepareEmailCompose.data = uri
            startActivity(prepareEmailCompose)
        }

        aboutAppSettingsItemCardView.setOnClickListener{
            // Todo: Implement later.
        }
    }

    private fun updateAppLanguage(languageLabel: String) {
        val locale = languageLabelToLocale(languageLabel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the system integrated locale manager to manage languages and locales.
            // This give the user the ability to change app language from system settings.
            Log.d(TAG, "Using android system LocaleManager to manage locales.")
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
        } else {
            // In old android versions we should manage locales by ourself.
            // Using shared preferences to save and restore locales.
            Log.d(TAG, "Using SharedPreferences to manage locales.")
            val prefs = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, Activity.MODE_PRIVATE)
            prefs.edit { putString(GlobalVariables.PREFERENCES_SETTINGS_LANGUAGE, locale.language) }
        }
    }

    private fun languageLabelToLocale(languageLabel: String): Locale {
        Log.d(TAG, "languageLabelToLocale($languageLabel)")
        return when(languageLabel) {
            getString(R.string.arabic) -> Locale("ar")
            getString(R.string.german) -> Locale("de")
            getString(R.string.spanish) -> Locale("es")
            getString(R.string.persian) -> Locale("fa")
            getString(R.string.french) -> Locale("fr")
            getString(R.string.italian) -> Locale("it")
            getString(R.string.korean) -> Locale("ko")
            getString(R.string.russia) -> Locale("ru")
            else -> Locale("")
        }
    }

    private fun getCurrentLocale(): Locale {
        var currentLocale = Locale("")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the system integrated locale manager to manage languages and locales.
            // This give the user the ability to change app language from system settings.
            Log.d(TAG, "Using android system LocaleManager to manage locales.")
            val localeManager = requireActivity().getSystemService(LocaleManager::class.java)
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
            "de" -> getString(R.string.german)
            "es" -> getString(R.string.spanish)
            "fa" -> getString(R.string.persian)
            "fr" -> getString(R.string.french)
            "it" -> getString(R.string.italian)
            "ko" -> getString(R.string.korean)
            "ru" -> getString(R.string.russia)
            else -> getString(R.string.system_default)
        }
    }

    private fun loadSettingsToShow() {
        loadAndShowDarkMode()
        loadAndShowLanguage()
    }

    private fun loadAndShowLanguage() {
        var currentLocale = Locale("")
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

    private fun loadAndShowDarkMode() {
        val darkModeDescriptionTextView = parentView.findViewById<TextView>(R.id.settings_fragment_text_view_description_night_mode)
        val darkModeState = requireActivity()
            .getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, Activity.MODE_PRIVATE)
            .getInt(GlobalVariables.PREFERENCES_SETTINGS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        when (darkModeState) {
            AppCompatDelegate.MODE_NIGHT_NO -> darkModeDescriptionTextView.text = getString(R.string.off)
            AppCompatDelegate.MODE_NIGHT_YES -> darkModeDescriptionTextView.text = getString(R.string.on)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> darkModeDescriptionTextView.text = getString(R.string.system_default)
        }
    }

    companion object {
        const val TAG = "SettingsFragment"

        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}