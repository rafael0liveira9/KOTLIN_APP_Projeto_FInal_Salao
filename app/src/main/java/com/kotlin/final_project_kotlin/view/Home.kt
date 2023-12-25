package com.kotlin.final_project_kotlin.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kotlin.final_project_kotlin.controller.ServicesAdapter
import com.kotlin.final_project_kotlin.databinding.ActivityHomeBinding
import com.kotlin.final_project_kotlin.model.DataStore
import com.kotlin.final_project_kotlin.model.Services
import org.json.JSONArray
import org.json.JSONException

class Home : AppCompatActivity() {
    lateinit var queue: RequestQueue

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var gestures: GestureDetector

    private val servicesList: MutableList<Services> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureGesture()
        configureRecycleView()

        supportActionBar?.hide()
        val name = intent.extras?.getString("nome")

        binding.usernameText.text = "Salão da Nice"
        val servicesRecyclerView = binding.servicesRecyclerView
        servicesRecyclerView.layoutManager = GridLayoutManager(this, 2)


        servicesAdapter = ServicesAdapter(this, mutableListOf())
        servicesRecyclerView.setHasFixedSize(true)
        servicesRecyclerView.adapter = servicesAdapter

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.userMenu.setOnClickListener {
            goMenu()
        }

        getServices()
    }

    private fun goToSchedule() {
        val intent = Intent(this, toSchedule::class.java)
        startActivity(intent)
    }

    private fun goMenu() {
        val intent = Intent(this, userMenu::class.java)
        startActivity(intent)
    }

    private fun configureGesture() {
        gestures = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                binding.servicesRecyclerView.findChildViewUnder(e.x, e.y).apply {
                    this?.let { view ->
                        val clickedPosition =
                            binding.servicesRecyclerView.getChildAdapterPosition(view)
                        val selectedServiceId = servicesList[clickedPosition].id
                        val selectedServiceName = servicesList[clickedPosition].name

                        val intent = Intent(this@Home, toSchedule::class.java).apply {
                            putExtra("id", selectedServiceId)
                            putExtra("name", selectedServiceName)
                        }
                        startActivity(intent)
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }

    private fun configureRecycleView() {
        binding.servicesRecyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.findChildViewUnder(e.x, e.y).apply {
                    return (this != null && gestures.onTouchEvent(e))
                }
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                // TODO: Implement if needed
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                // TODO: Implement if needed
            }
        })
    }

    private fun getServices() {
        val servicesUrl = "${DataStore.DATABASE_URL}get-services"

        val stringRequest = object : StringRequest(
            Method.GET, servicesUrl,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)

                    servicesList.clear()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val serviceItem = Services.fromJson(jsonObject)
                        if (serviceItem != null) {
                            servicesList.add(serviceItem)
                        }
                    }

                    Log.d("SERVICES_RESPONSE", servicesList.toString())

                    servicesAdapter.updateServicesList(servicesList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("SERVICES_ERROR", "Erro ao fazer parsing do JSON: $e")
                }
            },
            Response.ErrorListener { error ->
                Log.e("SERVICES_ERROR", "Erro: $error")
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            5000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(stringRequest)
    }
}
