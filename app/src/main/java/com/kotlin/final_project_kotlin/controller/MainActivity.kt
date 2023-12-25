package com.kotlin.final_project_kotlin.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.kotlin.final_project_kotlin.databinding.ActivityMainBinding
import com.kotlin.final_project_kotlin.model.DataStore
import com.kotlin.final_project_kotlin.model.User
import com.kotlin.final_project_kotlin.view.Register
import com.kotlin.final_project_kotlin.view.Schedule
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var queue: RequestQueue


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(this)

        supportActionBar?.hide()

        if (!!isUserLoggedIn()) {
            redirectToHome()
            return
        }

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
        val loginUrl = "${DataStore.DATABASE_URL}signin-user"

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val stringRequest = object : StringRequest(
            Method.POST, loginUrl,
            Response.Listener { response ->
                Log.d("LOGIN_RESPONSE", "$response")

                try {
                    val jsonObject = JSONObject(response)
                    val userJsonObject = jsonObject.getJSONObject("user")

                    val gson = Gson()
                    val user = gson.fromJson(userJsonObject.toString(), User::class.java)

                    val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_id", user.id.toString())
                    editor.putString("user_name", user.name)
                    editor.putString("user_email", user.email)
                    editor.putString("user_jwt", user.jwt)
                    editor.apply()

                    Log.d("LOGIN_SHARED", "RES: $user")

                    val intent = Intent(this, Schedule::class.java)
                    startActivity(intent)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("LOGIN_ERROR", "Error: $error")
                message(binding.root, "Não foi possível fazer o login, tente novamente!")
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
    private fun redirectToHome() {

        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
        finish()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("user_id", "")

        Log.d("TESTE PARAMS", "$userId")
        return !userId.isNullOrEmpty()
    }
}
