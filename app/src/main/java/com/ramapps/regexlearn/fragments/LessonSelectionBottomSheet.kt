package com.ramapps.regexlearn.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.updateMarginsRelative
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.ramapps.regexlearn.GlobalVariables
import com.ramapps.regexlearn.Listeners
import com.ramapps.regexlearn.R
import com.ramapps.regexlearn.RegexUtils
import com.ramapps.regexlearn.Utils

class LessonSelectionBottomSheet(private val lessonSelectionListener: Listeners.LessonSelection) : BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(makeLessonSelectionView())
        dialog.behavior.maxHeight = (requireContext().resources.displayMetrics.heightPixels * 0.7).toInt()
        return dialog
    }

    @SuppressLint("InflateParams")
    fun makeLessonSelectionView() : View {
        val lessonsTitle = Utils().getLessonsTitleFromDataJson(
            Utils().getJSONArrayFromRaw(requireContext().resources, R.raw.lessons_data),
            Utils().getJSONObjectFromRaw(requireContext().resources,
                R.raw.lessons_localization_data
            ),
        )

        val scrollView = ScrollView(requireContext())
        scrollView.scrollBarSize = 0

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        scrollView.removeAllViews()
        layout.removeAllViews()
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            (dialog as BottomSheetDialog).behavior.isDraggable = (scrollY == 0)
        }

        val titleTextView = TextView(requireContext())
        titleTextView.text = requireContext().getString(R.string.select_lesson)
        titleTextView.textSize = 22f
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.updateMarginsRelative(24, 24, 0, 16)
        titleTextView.layoutParams = layoutParams
        layout.addView(titleTextView)

        lessonsTitle.forEachIndexed { i, t ->
            val v = LayoutInflater.from(requireContext()).inflate(R.layout.item_view_lesson, null)
            val lessonTitleTextView = v.findViewById<TextView>(R.id.item_view_lesson_text_view_title)
            val stateImageView = v.findViewById<ImageView>(R.id.item_view_lesson_image_view_state)
            val parentLayout = v.findViewById<LinearLayout>(R.id.item_view_lesson_parent)

            lessonTitleTextView.text = RegexUtils().formatText(t, android.R.attr.colorActivatedHighlight, formatMarker = "`")

            val lastOpenedLessonId = requireActivity().getSharedPreferences(
                GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE)
                .getInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, 0)

            if (i < lastOpenedLessonId) {
                val harmonizedColorForDone = MaterialColors.harmonize((0xff00ff00).toInt(), android.R.attr.textColorPrimary)
                stateImageView.setImageResource(R.drawable.outline_done_outline_24)
                stateImageView.imageTintList = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_enabled)),
                    intArrayOf(harmonizedColorForDone))
                lessonTitleTextView.setTextColor(harmonizedColorForDone)
            }

            if (i > lastOpenedLessonId) {
                parentLayout.isEnabled = false
                parentLayout.alpha = 0.5f
                stateImageView.setImageResource(R.drawable.outline_lock_24)
            }


            parentLayout.setOnClickListener{ _ ->
                lessonSelectionListener.onSelect(i)
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            }

            v.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layout.addView(v)
        }

        scrollView.addView(layout)

        return scrollView
    }

    companion object {
        const val TAG = "LessonSelectionBottomSheet"
    }
}
