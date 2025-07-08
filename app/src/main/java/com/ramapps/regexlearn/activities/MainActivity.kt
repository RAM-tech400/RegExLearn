package com.ramapps.regexlearn.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.ramapps.regexlearn.GlobalVariables
import com.ramapps.regexlearn.R
import com.ramapps.regexlearn.fragments.HomeFragment
import com.ramapps.regexlearn.fragments.LearningFragment
import com.ramapps.regexlearn.fragments.PlaygroundFragment
import com.ramapps.regexlearn.fragments.SettingsFragment
import java.util.Locale
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainerView: FragmentContainerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { loadAppLanguage() }
        setContentView(R.layout.activity_main)
        initViews()
        loadViewsDefaultState()
        addListeners()
    }

    private fun loadViewsDefaultState() {
        val prefs = getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, MODE_PRIVATE)
        val defaultFragmentId = prefs.getInt(
            GlobalVariables.PREFERENCES_SETTINGS_DEFAULT_FRAGMENT,
            R.id.menu_item_home
        )
        if (supportFragmentManager.fragments.isEmpty()) {
            bottomNavigationView.selectedItemId = defaultFragmentId
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.main_fragment_container_view,
                    getBottomNavigationFragmentFromId(defaultFragmentId)
                ).commit()
        }
    }

    private fun getBottomNavigationFragmentFromId(fragmentId: Int): Fragment {
        return when(fragmentId) {
            R.id.menu_item_learning -> LearningFragment.newInstance()
            R.id.menu_item_playground -> PlaygroundFragment.newInstance()
            R.id.menu_item_settings -> SettingsFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
    }

    private fun loadAppLanguage() {
        // Getting language settings and set it for android 12 and below. In the Android 13+ this setting automatically (App language feature).
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val langCode =
                getSharedPreferences(GlobalVariables.PREFERENCES_NAME_SETTINGS, MODE_PRIVATE).getString(
                    GlobalVariables.PREFERENCES_SETTINGS_LANGUAGE,
                    ""
                )!!
            val configuration = resources.configuration
            val displayMetrics = resources.displayMetrics
            if (langCode.isEmpty()) {
                configuration.setLocale(Locale.getDefault())
            } else {
                configuration.setLocale(Locale(langCode))
            }
            resources.updateConfiguration(configuration, displayMetrics)
        }
    }

    private fun addListeners() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutouts = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

            val top = max(systemBars.top, displayCutouts.top)
            val right = max(systemBars.right, displayCutouts.right)
            val bottom = max(systemBars.bottom, displayCutouts.bottom)
            val left = max(systemBars.left, displayCutouts.left)

            v.setPadding(left, 0, right, 0)
            insets
        }

        bottomNavigationView.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_fragment_container_view,
                    getBottomNavigationFragmentFromId(item.itemId)
                ).commit()
                return true
            }
        })
    }

    private fun initViews() {
        fragmentContainerView = findViewById(R.id.main_fragment_container_view)
        bottomNavigationView = findViewById(R.id.main_bottom_navigation_view)
    }
}