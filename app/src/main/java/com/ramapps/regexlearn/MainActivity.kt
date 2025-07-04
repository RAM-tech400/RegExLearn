package com.ramapps.regexlearn

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainerView: FragmentContainerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initViews()
        addListeners()
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_container_view, HomeFragment.newInstance()).commit()
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
                when(item.itemId) {
                    R.id.menu_item_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container_view, HomeFragment.newInstance())
                            .commit()
                        return true
                    }
                    R.id.menu_item_learning -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container_view, LearningFragment.newInstance())
                            .commit()
                        return true
                    }
                    R.id.menu_item_playground -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container_view, PlaygroundFragment.newInstance())
                            .commit()
                        return true
                    }
                    R.id.menu_item_settings -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container_view, SettingsFragment.newInstance())
                            .commit()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun initViews() {
        fragmentContainerView = findViewById(R.id.main_fragment_container_view)
        bottomNavigationView = findViewById(R.id.main_bottom_navigation_view)
    }
}