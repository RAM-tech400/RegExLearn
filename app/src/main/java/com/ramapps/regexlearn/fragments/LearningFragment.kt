package com.ramapps.regexlearn.fragments

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.content.edit
import androidx.core.text.toSpanned
import androidx.fragment.app.Fragment
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.ramapps.regexlearn.GlobalVariables
import com.ramapps.regexlearn.Listeners
import com.ramapps.regexlearn.R
import com.ramapps.regexlearn.RegexUtils
import com.ramapps.regexlearn.RoundedBackgroundSpan
import com.ramapps.regexlearn.Utils
import java.util.regex.PatternSyntaxException

class LearningFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var descriptionTextView : TextView
    private lateinit var contentTextView : TextView
    private lateinit var regexTextInput : TextInputLayout
    private lateinit var previousLessonButton : Button
    private lateinit var nextLessonButton: Button
    private lateinit var flagsChipGroup: ChipGroup

    private var lessonId = 0
    private var lastUnlockedLessonId = 0
    private var initialFlags = ""
    private var initialValue = ""
    private var flags = ""
    private var answerRegex = ""
    private var answersArrayList = ArrayList<String>()
    private var descriptionText = ""
    private var contentText = ""
    private var isReadOnly = false
    private var isInteractiveLesson = true
    private var regexTextInputCursorPosition = 0
    private val lessonSelectionBottomSheet = LessonSelectionBottomSheet(object : Listeners.LessonSelection {
        override fun onSelect(lessonId: Int) {
            Log.v(TAG, "Lesson selection listener, Selected Lesson Id: $lessonId")
            this@LearningFragment.lessonId = lessonId
            updateLessonIds()
            loadLesson()
        }
    })

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

    private fun initViews(view: View) {
        toolbar = view.findViewById(R.id.learning_fragment_toolbar)
        descriptionTextView = view.findViewById(R.id.learning_fragment_text_view_lesson_description)
        contentTextView = view.findViewById(R.id.learning_fragment_text_view_lesson_content)
        regexTextInput = view.findViewById(R.id.learning_fragment_text_input_layout_regex)
        previousLessonButton = view.findViewById(R.id.learning_fragment_button_previous_lesson)
        nextLessonButton = view.findViewById(R.id.learning_fragment_button_next_lesson)
        flagsChipGroup = view.findViewById(R.id.learning_fragment_chip_group)
    }

    private fun addListeners() {
        Log.v(TAG, "addListeners()")

        toolbar.setOnMenuItemClickListener(object : OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem): Boolean {
                // We have just one toolbar options menu item, So don't need to check items id.
                // If more items added in future, then we check that items id to run every items action.
                lessonSelectionBottomSheet.show(requireActivity().supportFragmentManager,
                    LessonSelectionBottomSheet.TAG
                )
                return true
            }
        })

        previousLessonButton.setOnClickListener{
            if (lessonId > 0) {
                lessonId -= 1
                updateLessonIds()
                Log.v(TAG, "Loading previous lesson. Id:${lessonId}")
                loadLesson()
            }
        }

        nextLessonButton.setOnClickListener{
            if (lessonId < (Utils().getJSONArrayFromRaw(resources, R.raw.lessons_data).length() - 1)) {
                lessonId += 1
                updateLessonIds()
                Log.v(TAG, "Loading next lesson. Id:${lessonId}")
                loadLesson()
            }
        }

        regexTextInput.editText!!.addTextChangedListener(object : TextWatcher{
            val handler = Handler()
            val codeAfterTextEditedRunnable = Runnable {
                refreshRegex()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacks(codeAfterTextEditedRunnable)
                handler.postDelayed(codeAfterTextEditedRunnable, 500)
            }
        })

        flagsChipGroup.setOnCheckedStateChangeListener{ _: ChipGroup, checkedIds: MutableList<Int> ->
            Log.v(TAG, "Regex flags changed...")
            flags = ""
            checkedIds.forEach{id ->
                when (id) {
                    R.id.learning_fragment_chip_global -> flags += "g"
                    R.id.learning_fragment_chip_multiline -> flags += "m"
                    R.id.learning_fragment_chip_ignore_case -> flags += "i"
                }
            }
            refreshRegex()
            Log.d(TAG, "Enabled flags: $flags")
        }
    }

    private fun loadLesson() {
        val prefs = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE)!!

        clearPreviousLessonData()

        lessonId = prefs.getInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, 0)
        lastUnlockedLessonId = prefs.getInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, 0)

        val lessonDataJSON = Utils().getJSONArrayFromRaw(resources, R.raw.lessons_data).getJSONObject(lessonId)!!
        val localizationLessonDataJSON = Utils().getJSONObjectFromRaw(resources, R.raw.lessons_localization_data)

        initialFlags = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_INITIAL_FLAGS)
        flags = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_FLAGS)
        answerRegex = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_REGEX)
        descriptionText = localizationLessonDataJSON.optString(lessonDataJSON.getString(GlobalVariables.LESSON_JSON_KEY_DESCRIPTION))
        contentText = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_CONTENT)
        initialValue = lessonDataJSON.optString(GlobalVariables.LESSON_JSON_KEY_INITIAL_VALUE)
        regexTextInputCursorPosition = lessonDataJSON.optInt(GlobalVariables.LESSON_JSON_KEY_CURSOR_POSITION)
        isInteractiveLesson = lessonDataJSON.optBoolean(GlobalVariables.LESSON_JSON_KEY_INTERACTIVE, true)
        isReadOnly = lessonDataJSON.optBoolean(GlobalVariables.LESSON_JSON_KEY_INITIAL_READ_ONLY)

        var answers = lessonDataJSON.optJSONArray(GlobalVariables.LESSON_JSON_KEY_ANSWER)
        for (i in 0 until answers.length()) {
            answersArrayList.add(answers.getString(i))
        }

        toolbar.title = RegexUtils().formatText(
            localizationLessonDataJSON.optString(lessonDataJSON.getString(GlobalVariables.LESSON_JSON_KEY_TITLE)),
            android.R.attr.colorActivatedHighlight,
            formatMarker = "`")

        Log.d(TAG, "Selected lesson data that we should load: $lessonDataJSON")
        Log.d(TAG, "Selected lesson id: $lessonId")
        Log.d(TAG, "Selected last opened lesson id: $lastUnlockedLessonId")
        Log.d(TAG, "Detected initialFlags: $initialFlags")
        Log.d(TAG, "Detected flags: $flags")
        Log.d(TAG, "Detected answerRegex: $answerRegex")
        Log.d(TAG, "Detected title: ${toolbar.title}")
        Log.d(TAG, "Detected description: $descriptionText")
        Log.d(TAG, "Detected content: $contentText")
        Log.d(TAG, "Detected initialValue: $initialValue")
        Log.d(TAG, "Detected cursorPosition: $regexTextInputCursorPosition")
        Log.d(TAG, "Detected interactive: $isInteractiveLesson")
        Log.d(TAG, "Detected readOnly: $isReadOnly")
        Log.d(TAG, "Detected answers: ${answersArrayList.size} items, ${answersArrayList.joinToString(",")}")

        setNextAndPrevButtonState()
        setRegexTextInputState()
        setFlagsChipsState()
        setTextViewsState()
    }

    private fun refreshRegex() {
        regexTextInput.error = ""
        val regexInputText = regexTextInput.editText!!.text.toString()
        Log.d(TAG, "Regex input text changed to $regexInputText")
        try{
            contentTextView.text = RegexUtils().applyStyleToString(Regex(regexInputText), flags, contentText)
            Log.v(TAG, "ContentTextView text has been set to: ${contentTextView.text}")
            if (checkAnswer()) {
                Log.v(TAG, "Checking answer return true. The next lesson button will enabled.")
                nextLessonButton.isEnabled = true
            } else {
                Log.v(TAG, "Checking answer return false. So regex pattern is incorrect.")
            }
        } catch (e : PatternSyntaxException) {
            Log.e(TAG, "Wrong regex pattern! Error details: $e")
            regexTextInput.error = getString(R.string.wrong_regex_pattern)
        }
    }

    private fun clearPreviousLessonData() {
        Log.v(TAG, "clearPreviousLessonData()")
        initialFlags = ""
        flags = ""
        answerRegex = ""
        answersArrayList = ArrayList()
        contentText = ""
        flagsChipGroup.clearCheck()
    }

    private fun updateLessonIds() {
        Log.v(TAG, "updateLessonIds()")
        Log.d(TAG, "Updating Lesson Ids; selected: $lessonId, lastUnlocked: $lastUnlockedLessonId")
        val prefs = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE)
        prefs.edit { putInt(GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, lessonId) }
        if (lessonId > lastUnlockedLessonId) {
            lastUnlockedLessonId = lessonId
            prefs.edit { putInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, lastUnlockedLessonId) }
        }
    }

    private fun setNextAndPrevButtonState() {
        Log.v(TAG, "setNextAndPrevButtonState()")
        val lessonsCount = Utils().getJSONArrayFromRaw(resources, R.raw.lessons_data).length()
        val lastUnlockedLessonId = requireActivity().getSharedPreferences(
            GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE).getInt(
            GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, 0)

        previousLessonButton.visibility = View.VISIBLE
        nextLessonButton.visibility = View.VISIBLE
        nextLessonButton.isEnabled = true

        when (lessonId) {
            0 -> previousLessonButton.visibility = View.GONE
            lessonsCount - 1 -> nextLessonButton.visibility = View.GONE
            lastUnlockedLessonId -> nextLessonButton.isEnabled = false
        }

        if (isReadOnly || !isInteractiveLesson) nextLessonButton.isEnabled = true
    }

    private fun setRegexTextInputState() {
        Log.v(TAG, "setRegexTextInputState()")
        regexTextInput.editText!!.setText(initialValue)
        regexTextInput.isEnabled = !isReadOnly
        regexTextInput.visibility = if (isInteractiveLesson) { View.VISIBLE } else { View.GONE }
        regexTextInput.editText!!.setSelection(regexTextInputCursorPosition)
        regexTextInput.requestFocus()
    }

    private fun setFlagsChipsState() {
        Log.v(TAG, "setFlagsChipsState()")
        initialFlags.split("").forEach { flag ->
            when(flag) {
                "g" -> flagsChipGroup.check(R.id.learning_fragment_chip_global)
                "m" -> flagsChipGroup.check(R.id.learning_fragment_chip_multiline)
                "i" -> flagsChipGroup.check(R.id.learning_fragment_chip_ignore_case)
            }
        }

        flagsChipGroup.isEnabled = !isReadOnly
        flagsChipGroup.visibility = if (isInteractiveLesson) { View.VISIBLE } else { View.GONE }
    }

    private fun setTextViewsState() {
        Log.v(TAG, "setTextViewsState()")

        descriptionTextView.text = RegexUtils().formatText(descriptionText, android.R.attr.colorActivatedHighlight, formatMarker = "`")

        contentTextView.visibility = if (isInteractiveLesson) { View.VISIBLE } else { View.GONE }
        contentTextView.text = contentText
    }

    private fun checkAnswer(): Boolean {
        Log.v(TAG, "checkAnswer()")

        val spannedContentText = contentTextView.text.toSpanned()
        val contentTextSpans = spannedContentText.toSpanned().getSpans(
            0, spannedContentText.length, RoundedBackgroundSpan::class.java)
        var answers = ""
        answersArrayList.forEach{ a -> answers += "$a, "}

        Log.d(TAG, "contentTextView has ${contentTextSpans.size} RoundedBackgroundSpan spans.")

        Log.d(TAG, "Checking the count of spans with answers...")
        if (contentTextSpans.size != answersArrayList.size) return false

        contentTextSpans.forEach{ s ->
            val spanText = spannedContentText.substring(spannedContentText.getSpanStart(s), spannedContentText.getSpanEnd(s))
            Log.d(TAG, "Checking if span($spanText) exist in answer($answers)...")
            if (!answersArrayList.contains(spanText)) return false
        }

        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = LearningFragment()

        const val TAG = "LearningFragment"
    }

}