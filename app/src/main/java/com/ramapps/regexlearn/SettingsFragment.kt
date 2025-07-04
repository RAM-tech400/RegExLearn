package com.ramapps.regexlearn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.card.MaterialCardView

class SettingsFragment : Fragment() {

    private lateinit var languageSettingsItemCardView : MaterialCardView
    private lateinit var darkModeSettingsItemCardView : MaterialCardView
    private lateinit var contactMeSettingsItemCardView : MaterialCardView
    private lateinit var aboutAppSettingsItemCardView : MaterialCardView

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
        // Todo: Implement later.
    }

    companion object {
        const val TAG = "SettingsFragment"

        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}