package com.kotlin.final_project_kotlin.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kotlin.final_project_kotlin.databinding.ActivityToScheduleBinding
import com.kotlin.final_project_kotlin.model.DataStore
import com.kotlin.final_project_kotlin.model.ListAvaible
import com.kotlin.final_project_kotlin.controller.ListAvaibleAdapter
import org.json.JSONArray
import org.json.JSONException
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kotlin.final_project_kotlin.model.Services

class toSchedule : AppCompatActivity() {

    private lateinit var binding: ActivityToScheduleBinding
    lateinit var queue: RequestQueue

    private val avaibleList = mutableListOf<ListAvaible>()
    private val servicesList = mutableListOf<Services>()
    private lateinit var adapter: ListAvaibleAdapter
    private lateinit var gestures: GestureDetector
    private lateinit var rootView: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityToScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        queue = Volley.newRequestQueue(this)

        supportActionBar?.hide()
        rootView = findViewById(android.R.id.content)


        adapter = ListAvaibleAdapter(this, avaibleList, servicesList, rootView, queue)
        binding.servicesGetRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.servicesGetRecyclerView.adapter = adapter

        getList()
        configureGesture()

        intent.run {
            val serviceId = this.getIntExtra("id", -1)
            val serviceName = this.getStringExtra("name")
            val serviceImage = this.getStringExtra("image")
            val servicePrice = this.getDoubleExtra("value", 0.0)




            if (serviceId != -1) {

                val service = Services(id = serviceId, name = serviceName )
                servicesList.add(service)

                if (serviceName != null) {
                    binding.scheduleTitleName.text = serviceName
                } else {
                    Log.d("Tag", "Erro ao obter serviço")
                }
            } else {
                Log.d("Tag", "Erro ao obter ID do serviço")
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    fun message(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    fun messageDone(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#008000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    fun getList() {
        val listUrl = "${DataStore.DATABASE_URL}get-list"

        val stringRequest = object : StringRequest(
            Method.GET, listUrl,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)

                    avaibleList.clear()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val listItem = ListAvaible.fromJson(jsonObject)
                        avaibleList.add(listItem)

                    }


                    adapter.updateAvaibleList(avaibleList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("LIST_ERROR", "Erro ao fazer parsing do JSON: $e")
                }
            },
            Response.ErrorListener { error ->
                Log.e("LIST_ERROR", "Erro: $error")
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            5000, // 5 segundos
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(stringRequest)
    }

    private fun configureGesture() {
        gestures = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                binding.servicesGetRecyclerView.findChildViewUnder(e.x, e.y).apply {
                    this?.let { view ->
                        val clickedPosition =
                            binding.servicesGetRecyclerView.getChildAdapterPosition(view)
                        val selectedServiceId = avaibleList[clickedPosition].id

                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }


}