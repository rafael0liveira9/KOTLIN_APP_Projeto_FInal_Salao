package com.kotlin.final_project_kotlin.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.kotlin.final_project_kotlin.databinding.ActivityMainBinding
import com.kotlin.final_project_kotlin.view.Register


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var queue: RequestQueue
    val url = "https://www.google.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(this)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
                when {
                    email.isEmpty() -> {
                        message(binding.root, "Por Favor, digite o seu Email...")
                    }
                    password.isEmpty() -> {
                        message(binding.root, "Por Favor, digite a sua Senha...")
                    }
                    password.length < 6 -> {
                        message(binding.root, "A Senha deve conter 6 ou mais caracteres.")
                    }
                    else -> {
                        goHome(email, password)
                    }
                }
        }

        binding.btnRegister.setOnClickListener {
            goRegister()
        }
    }

    private fun message(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }


    private fun goHome(email: String, password: String) {
        val loginUrl = "http://192.168.1.2:3030/signin-user"

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val stringRequest = object : StringRequest(
            Method.POST, loginUrl,
            Response.Listener { response ->
                Log.d("LOGIN_RESPONSE", response)
                Toast.makeText(this, "Requisição OK", Toast.LENGTH_LONG)
            },
            Response.ErrorListener { error ->
                Log.e("LOGIN_ERROR", "Error: $error")
                Toast.makeText(this, "Erro na Requisição", Toast.LENGTH_LONG)
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            5000, // 5 seconds
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(stringRequest)
    }

    private fun goRegister() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}
