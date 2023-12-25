package com.kotlin.final_project_kotlin.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kotlin.final_project_kotlin.controller.MainActivity
import com.kotlin.final_project_kotlin.databinding.ActivityUserMenuBinding

private lateinit var binding: ActivityUserMenuBinding

class userMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserMenuBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.logOutBtn.setOnClickListener {
            goToLogin()
        }

        supportActionBar?.hide()

    }

    private fun goToLogin() {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}