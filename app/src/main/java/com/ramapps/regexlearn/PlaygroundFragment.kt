package com.ramapps.regexlearn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlaygroundFragment : Fragment() {

    private lateinit var previewTextInput : TextView
    private lateinit var runFloatingActionButton : FloatingActionButton
    private lateinit var flagsChipGroup: ChipGroup
    private lateinit var regexPatternInputsLinearLayout : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playground, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PlaygroundFragment()

        const val TAG = "PlaygroundFragment"
    }
}