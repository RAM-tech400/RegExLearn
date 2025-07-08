package com.ramapps.regexlearn.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.content.edit
import androidx.core.text.toSpanned
import androidx.core.view.updateMarginsRelative
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
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

    private var lessonId = 0
    private var initialFlags = ""
    private var flags = ""
    private var answerRegex = ""
    private var answersArrayList = ArrayList<String>()
    private var contentText = ""
    private val lessonSelectionBottomSheet = LessonSelectionBottomSheet(object : Listeners.LessonSelection {
        override fun onSelect(lessonId: Int) {
            Log.v(TAG, "Lesson selection listener, Selected Lesson Id: $lessonId")
            updateCurrentLessonId(lessonId)
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
                Log.v(TAG, "Loading previous lesson. Id:${lessonId - 1}")
                updateCurrentLessonId(lessonId - 1)
                loadLesson()
            }
        }

        nextLessonButton.setOnClickListener{
            if (lessonId < (Utils().getJSONArrayFromRaw(resources, R.raw.lessons_data).length() - 1)) {
                Log.v(TAG, "Loading previous lesson. Id:${lessonId + 1}")
                updateCurrentLessonId(lessonId + 1)
                loadLesson()
            }
        }

        regexTextInput.editText!!.addTextChangedListener(object : TextWatcher{
            val handler = Handler()
            val codeAfterTextEditedRunnable = Runnable {
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacks(codeAfterTextEditedRunnable)
                handler.postDelayed(codeAfterTextEditedRunnable, 500)
            }
        })
    }

    private fun updateCurrentLessonId(lessonId : Int) {
        Log.v(TAG, "updateCurrentLessonId($lessonId)")
        requireActivity().getSharedPreferences(
            GlobalVariables.PREFERENCES_NAME_USER_DATA,
            Activity.MODE_PRIVATE).edit {
            putInt(
                GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON,
                lessonId
            )
        }
    }

    private fun loadLesson() {
        clearPreviousLessonData()

        lessonId = requireActivity().getSharedPreferences(GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE).getInt(
            GlobalVariables.PREFERENCES_USER_DATA_SELECTED_LESSON, 0)
        val lessonDataJSON = Utils().getJSONArrayFromRaw(resources, R.raw.lessons_data).getJSONObject(lessonId)
        val localizationLessonData = Utils().getJSONObjectFromRaw(resources,
            R.raw.lessons_localization_data
        )

        Log.d(TAG, "Selected lesson data that we should load: $lessonDataJSON")

        val lastOpenedLessonId = requireActivity().getSharedPreferences(
            GlobalVariables.PREFERENCES_NAME_USER_DATA, Activity.MODE_PRIVATE)
            .getInt(GlobalVariables.PREFERENCES_USER_DATA_LAST_LESSON, 0)

        nextLessonButton.isEnabled = (lessonId + 1) <= lastOpenedLessonId

        if (lessonId > lastOpenedLessonId) {
            updateCurrentLessonId(lessonId)
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

        Log.d(TAG, "Detected initialFlags: $initialFlags")
        Log.d(TAG, "Detected flags: $flags")
        Log.d(TAG, "Detected answerRegex: $answerRegex")
        Log.d(TAG, "Detected title: $title")
        Log.d(TAG, "Detected description: $description")
        Log.d(TAG, "Detected content: $content")
        Log.d(TAG, "Detected initialValue: $initialValue")
        Log.d(TAG, "Detected cursorPosition: $cursorPosition")
        Log.d(TAG, "Detected interactive: $interactive")
        Log.d(TAG, "Detected readOnly: $readOnly")

        toolbar.title = RegexUtils().formatText(title, android.R.attr.colorActivatedHighlight, formatMarker = "`")
        descriptionTextView.text = RegexUtils().formatText(description, android.R.attr.colorActivatedHighlight, formatMarker = "`")

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
            contentText = content

            for (i in 0..<(lessonDataJSON.optJSONArray(GlobalVariables.LESSON_JSON_KEY_ANSWER)
                ?.length() ?: 0)) {
                lessonDataJSON.optJSONArray(GlobalVariables.LESSON_JSON_KEY_ANSWER)?.let {
                    Log.d(TAG, "Detected answers: $it")
                    answersArrayList.add(
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

    private fun clearPreviousLessonData() {
        Log.v(TAG, "clearPreviousLessonData()")
        lessonId = 0
        initialFlags = ""
        flags = ""
        answerRegex = ""
        answersArrayList = ArrayList<String>()
        contentText = ""
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