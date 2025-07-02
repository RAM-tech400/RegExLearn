package com.ramapps.regexlearn

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class PlaygroundFragment : Fragment() {

    private lateinit var previewTextInput : TextInputLayout
    private lateinit var runFloatingActionButton : FloatingActionButton
    private lateinit var flagsChipGroup: ChipGroup
    private lateinit var regexPatternInput : TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playground, container, false)
        initializeViews(view)
        addListeners()
        return view
    }

    fun addListeners() {
        // TODO: Adding necessary component listeners here.
    }

    private fun initializeViews(view: View) {
        previewTextInput = view.findViewById(R.id.playground_fragment_text_input_preview)
        runFloatingActionButton = view.findViewById(R.id.playground_fragment_fab_run)
        flagsChipGroup = view.findViewById(R.id.playground_fragment_chip_group)
        regexPatternInput = view.findViewById(R.id.playground_fragment_text_input_layout_regex)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PlaygroundFragment()

        const val TAG = "PlaygroundFragment"
    }
}