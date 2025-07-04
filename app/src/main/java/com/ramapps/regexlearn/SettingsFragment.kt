package com.ramapps.regexlearn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SettingsFragment : Fragment() {
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
        // Todo: Implement later.
    }

    private fun addListeners() {
        // Todo: Implement later.
    }

    private fun loadSettingsToShow() {
        // Todo: Implement later.
    }

    companion object {
        const val TAG = "SettingsFragment"
    }
}