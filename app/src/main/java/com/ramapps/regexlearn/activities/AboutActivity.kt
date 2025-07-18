package com.ramapps.regexlearn.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ramapps.regexlearn.R
import kotlin.math.max
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {
    private lateinit var goToAppGithubButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        initViews()
        addListeners()
    }

    private fun initViews() {
        goToAppGithubButton = findViewById(R.id.about_button_view_github)
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

        goToAppGithubButton.setOnClickListener {
            val repoUri = "https://github.com/RAM-tech400/RegExLearn.git".toUri()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = repoUri
            startActivity(intent)
        }
    }
}