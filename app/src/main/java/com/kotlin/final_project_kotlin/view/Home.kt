package com.kotlin.final_project_kotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.kotlin.final_project_kotlin.controller.ServicesAdapter
import com.kotlin.final_project_kotlin.databinding.ActivityHomeBinding
import com.kotlin.final_project_kotlin.model.DataStore
import com.kotlin.final_project_kotlin.model.Services

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var gestures: GestureDetector
    private val servicesList: MutableList<Services> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureGesture()
        configureRecycleView()

        supportActionBar?.hide()
        val name = intent.extras?.getString("nome")

        binding.usernameText.text = "Salão da Nice"
        val servicesRecyclerView = binding.servicesRecyclerView
        servicesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        servicesAdapter= ServicesAdapter(this, DataStore.ourServicesList)
        servicesRecyclerView.setHasFixedSize((true))
        servicesRecyclerView.adapter=servicesAdapter

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun goToSchedule() {
        val intent = Intent(this, toSchedule::class.java)
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
                        val clickedPosition = binding.servicesRecyclerView.getChildAdapterPosition(view)
                        val intent = Intent(this@Home, toSchedule::class.java).apply {
                            putExtra("position", clickedPosition)
                        }
                        startActivity(intent)
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }

    private fun configureRecycleView(){
        binding.servicesRecyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.findChildViewUnder(e.x, e.y).apply {
                    return (this != null && gestures.onTouchEvent(e))
                }
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                TODO("Not yet implemented")
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                TODO("Not yet implemented")
            }

        })
    }

}