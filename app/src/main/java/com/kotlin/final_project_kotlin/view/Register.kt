package com.kotlin.final_project_kotlin.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.RequestQueue
import android.view.View
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.snackbar.Snackbar
import com.kotlin.final_project_kotlin.databinding.ActivityRegisterBinding
import com.kotlin.final_project_kotlin.model.DataStore
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kotlin.final_project_kotlin.model.User
import org.json.JSONException
import org.json.JSONObject


class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        queue = Volley.newRequestQueue(this)

        binding.btnRegister.setOnClickListener{
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            when {
                email.isEmpty() -> {
                    message(it, "Por Favor, digite o seu Nome")
                }password.isEmpty() -> {
                message(it, "Por Favor, digite a sua Senha")
            }password.length < 6 -> {
                message(it, "A Senha deve conter 6 ou mais caracteres.")
            }else -> {
                createUser(name, email, password)
            }
            }
        }
        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun message (view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun goHome(nome: String) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("nome", nome)
        startActivity(intent)
    }

    private fun createUser(name: String, email: String, password: String) {
        val loginUrl = "${DataStore.DATABASE_URL}new-user"

        val params = HashMap<String, String>()
        params["name"] = name
        params["email"] = email
        params["password"] = password

        val stringRequest = object : StringRequest(
            Method.POST, loginUrl,
            Response.Listener { response ->

                try {
                    val jsonObject = JSONObject(response)

                    val jwt = jsonObject.getString("jwt")
                    val userJsonObject = jsonObject.getJSONObject("user")

                    val user = User(
                        id = userJsonObject.getInt("id"),
                        name = userJsonObject.getString("name"),
                        email = userJsonObject.getString("email"),
                        jwt = jwt
                    )

                    val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_id", user.id.toString())
                    editor.putString("user_name", user.name)
                    editor.putString("user_email", user.email)
                    editor.putString("user_jwt", user.jwt)
                    editor.apply()

                    Log.d("REGISTER_SHARED", "RES: $user")

                    val intent = Intent(this, Schedule::class.java)
                    startActivity(intent)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener { error ->
                Log.e("REGISTER_ERROR", "Error: $error")
                message(binding.root, "Não foi possivel se cadastrar com estes dados.")
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
}