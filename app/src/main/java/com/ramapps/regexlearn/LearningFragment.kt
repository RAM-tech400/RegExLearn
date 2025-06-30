package com.ramapps.regexlearn

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.view.updateMarginsRelative
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.edit

class LearningFragment : Fragment() {

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
                Utils().getJSONObjectFromRaw(requireContext().resources, R.raw.lessons_localization_data),
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

                lessonTitleTextView.text = Utils().stylingFormattedText(t)

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

    private lateinit var toolbar: Toolbar
    private lateinit var descriptionTextView : TextView
    private lateinit var contentTextView : TextView
    private lateinit var regexTextInput : TextInputLayout
    private lateinit var previousLessonButton : Button
    private lateinit var nextLessonButton: Button

    private var initialFlags = ""
    private var flags = ""
    private var answerRegex = ""
    private var answer = ArrayList<String>()

    private val lessonSelectionBottomSheet = LessonSelectionBottomSheet(object : Listeners.LessonSelection{
        override fun onSelect(lessonId: Int) {
            Log.v(TAG, "Lesson selection listener, Selected Lesson Id: $lessonId")

            requireActivity()
                .getSharedPreferences(
                    GlobalVariables.PREFERENCES_NAME_USER_DATA,
                    Activity.MODE_PRIVATE)
                .edit {
                    putInt(
                        GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON,
                        lessonId
                    )
                }

            loadLesson()
        }
    })

    private fun loadLesson() {
        val lessonId = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE).getInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, 0)
        val lessonDataJSON = Utils().getJSONArrayFromRaw(resources, R.raw.lessons_data).getJSONObject(lessonId)
        val localizationLessonData = Utils().getJSONObjectFromRaw(resources, R.raw.lessons_localization_data)

        Log.d(TAG, "Selected lesson data that we should load: $lessonDataJSON")

        val lastOpenedLessonId = requireActivity().getSharedPreferences(
            GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE)
            .getInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, 0)

        nextLessonButton.isEnabled = (lessonId + 1) <= lastOpenedLessonId

        if (lessonId > lastOpenedLessonId) {
            requireActivity().getSharedPreferences(
                GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE)
                .edit {
                    putInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, lessonId)
                }
        }

        initialFlags = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_INITIAL_FLAGS)
        flags = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_FLAGS)
        answerRegex = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_REGEX)

        val title = localizationLessonData.optString(lessonDataJSON.getString(GlobalVariables.LESSON_JSON_KEY_TITLE))
        val description = localizationLessonData.optString(lessonDataJSON.getString(GlobalVariables.LESSON_JSON_KEY_DESCRIPTION))
        val content = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_CONTENT)
        val initialValue = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_INITIAL_VALUE)
        val cursorPosition = lessonDataJSON.optInt(GlobalVariables.LESSON_JSON_KEY_CURSOR_POSITION)
        val interactive = lessonDataJSON.optBoolean(GlobalVariables.LESSON_JSON_KEY_INTERACTIVE, true)
        val readOnly = lessonDataJSON.optBoolean(GlobalVariables.LESSON_JSON_KEY_INITIAL_READ_ONLY)

        toolbar.title = title
        descriptionTextView.text = description
        regexTextInput.editText!!.setText(initialValue)

        if (readOnly) {
            regexTextInput.isEnabled = false
            nextLessonButton.isEnabled = true
        } else {
            regexTextInput.isEnabled = true
        }

        if (interactive) {
            regexTextInput.isEnabled = true
            regexTextInput.editText!!.setSelection(cursorPosition)
            regexTextInput.editText!!.requestFocus()

            contentTextView.visibility = View.VISIBLE
            contentTextView.text = content

            for (i in 0..<(lessonDataJSON.optJSONArray(GlobalVariables.LESSON_JSON_KEY_ANSWER)
                ?.length() ?: 0)) {
                lessonDataJSON.optJSONArray(GlobalVariables.LESSON_JSON_KEY_ANSWER)?.let {
                    answer.add(
                        it.getString(i)
                    )
                }
            }
        } else {
            regexTextInput.isEnabled = false
            nextLessonButton.isEnabled = true

            contentTextView.visibility = View.INVISIBLE
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_learning, container, false)
        initViews(view)
        addListeners()
        loadLesson()
        return view
    }

    private fun addListeners() {
        Log.v(TAG, "addListeners()")
        toolbar.setOnMenuItemClickListener(object : OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.menu_item_select_lesson -> {
                        lessonSelectionBottomSheet.show(requireActivity().supportFragmentManager, LessonSelectionBottomSheet.TAG)
                        return true
                    }
                    else -> return false
                }
            }
        })

        previousLessonButton.setOnClickListener{
            val selectedLessonId = requireActivity().getSharedPreferences(
                GlobalVariables.PREFERENCES_NAME_USER_DATA,
                Activity.MODE_PRIVATE)
                .getInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, 0)

            if (selectedLessonId > 0) {
                Log.v(TAG, "Loading previous lesson. Id:${selectedLessonId - 1}")
                requireActivity()
                    .getSharedPreferences(
                        GlobalVariables.PREFERENCES_NAME_USER_DATA,
                        Activity.MODE_PRIVATE)
                    .edit {
                        putInt(
                            GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON,
                            selectedLessonId - 1
                        )
                    }

                loadLesson()
            }
        }

        nextLessonButton.setOnClickListener{
            val selectedLessonId = requireActivity().getSharedPreferences(
                GlobalVariables.PREFERENCES_NAME_USER_DATA,
                Activity.MODE_PRIVATE)
                .getInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, 0)

            if (selectedLessonId < (Utils().getJSONArrayFromRaw(resources, R.raw.lessons_data).length() - 1)) {
                Log.v(TAG, "Loading previous lesson. Id:${selectedLessonId + 1}")
                requireActivity()
                    .getSharedPreferences(
                        GlobalVariables.PREFERENCES_NAME_USER_DATA,
                        Activity.MODE_PRIVATE)
                    .edit {
                        putInt(
                            GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON,
                            selectedLessonId + 1
                        )
                    }

                loadLesson()
            }
        }
    }

    private fun initViews(view: View) {
        toolbar = view.findViewById(R.id.learning_fragment_toolbar)
        descriptionTextView = view.findViewById(R.id.learning_fragment_text_view_lesson_description)
        contentTextView = view.findViewById(R.id.learning_fragment_text_view_lesson_content)
        regexTextInput = view.findViewById(R.id.learning_fragment_text_input_layout_regex)
        previousLessonButton = view.findViewById(R.id.learning_fragment_button_previous_lesson)
        nextLessonButton = view.findViewById(R.id.learning_fragment_button_next_lesson)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LearningFragment()

        const val TAG = "LearningFragment"
    }
}