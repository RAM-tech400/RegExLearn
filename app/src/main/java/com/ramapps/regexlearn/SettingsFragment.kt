package com.ramapps.regexlearn

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.card.MaterialCardView

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
            // Todo: Implement later.
        }

        darkModeSettingsItemCardView.setOnClickListener{
            // Todo: Implement later.
        }

        contactMeSettingsItemCardView.setOnClickListener{
            // Todo: Implement later.
        }

        aboutAppSettingsItemCardView.setOnClickListener{
            // Todo: Implement later.
        }
    }

    private fun loadSettingsToShow() {
        loadAndShowDarkMode()
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