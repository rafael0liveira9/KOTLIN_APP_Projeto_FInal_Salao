package com.kotlin.final_project_kotlin.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.final_project_kotlin.R
import com.kotlin.final_project_kotlin.controller.MainActivity

class SplashScreen : AppCompatActivity() {
    private val splashTimeOut: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        supportActionBar?.hide()
        window.statusBarColor = Color.TRANSPARENT

        Handler().postDelayed({

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)

    }
}