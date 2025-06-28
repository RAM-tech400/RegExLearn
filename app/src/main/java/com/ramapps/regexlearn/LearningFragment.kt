package com.ramapps.regexlearn

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.view.updateMarginsRelative
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LearningFragment : Fragment() {

    class LessonSelectionBottomSheet : BottomSheetDialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = BottomSheetDialog(requireContext())

            dialog.setContentView(getLessonSelectionView())
            dialog.behavior.isShouldRemoveExpandedCorners = true

            return dialog
        }

        @SuppressLint("InflateParams")
        fun getLessonSelectionView() : View {
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
            scrollView.setOnScrollChangeListener(object : View.OnScrollChangeListener {
                override fun onScrollChange(
                    v: View?,
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {
                    (dialog as BottomSheetDialog).behavior.isDraggable = (scrollY == 0)
                }
            })

            val titleTextView = TextView(requireContext())
            titleTextView.text = requireContext().getString(R.string.select_lesson)
            titleTextView.textSize = 22f
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.updateMarginsRelative(24, 24, 0, 16)
            titleTextView.layoutParams = layoutParams
            layout.addView(titleTextView)

            lessonsTitle.forEach { t ->
                val v = LayoutInflater.from(requireContext()).inflate(R.layout.item_view_lesson, null)
                val lessonTitleTextView = v.findViewById<TextView>(R.id.item_view_lesson_text_view_title)
                v.findViewById<ImageView>(R.id.item_view_lesson_image_view_state)
                val parentLayout = v.findViewById<LinearLayout>(R.id.item_view_lesson_parent)

                lessonTitleTextView.text = Utils().stylingFormattedText(t)

                parentLayout.setOnClickListener{ _ ->
                    Toast.makeText(requireContext(), "Lesson <${t}> selected", Toast.LENGTH_SHORT).show()
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
    private val lessonSelectionBottomSheet = LessonSelectionBottomSheet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_learning, container, false)
        initViews(view)
        addListeners()
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
    }

    private fun initViews(view: View) {
        toolbar = view.findViewById(R.id.learning_fragment_toolbar)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LearningFragment()

        const val TAG = "LearningFragment"
    }
}