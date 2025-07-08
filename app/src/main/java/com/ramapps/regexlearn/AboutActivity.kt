package com.ramapps.regexlearn

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        addListeners()
    }

    private fun addListeners() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutouts = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

            val right = max(systemBars.right, displayCutouts.right)
            val left = max(systemBars.left, displayCutouts.left)

            v.setPadding(left, 0, right, 0)

            insets
        }
    }
}