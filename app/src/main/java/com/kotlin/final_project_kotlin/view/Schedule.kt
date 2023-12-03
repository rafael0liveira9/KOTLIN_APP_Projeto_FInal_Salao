package com.kotlin.final_project_kotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.kotlin.final_project_kotlin.R
import com.kotlin.final_project_kotlin.controller.ScheduleAdapter

import com.kotlin.final_project_kotlin.databinding.ActivityScheduleBinding
import com.kotlin.final_project_kotlin.model.ScheduleServices

class Schedule : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding
    private lateinit var servicesAdapter: ScheduleAdapter
    private lateinit var gestures: GestureDetector

    private val servicesList: MutableList<ScheduleServices> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val name = intent.extras?.getString("nome")

        binding.usernameText.text = "Bem-Vindo(a), $name"
        val servicesRecyclerView = binding.servicesRecyclerView
        servicesRecyclerView.layoutManager = GridLayoutManager(this, 1)
        servicesAdapter= ScheduleAdapter(this, servicesList)
        servicesRecyclerView.setHasFixedSize((true))
        servicesRecyclerView.adapter=servicesAdapter
        getServices()
        configureGesture()
        configureRecycleView()

        binding.btnSchedule.setOnClickListener {
            goSToHome(name.toString())
        }
    }

    private fun getServices() {
        val service1 = ScheduleServices(1, R.drawable.cabelos, "sabd uhsbadias uhdsaiud", "")
        servicesList.add(service1)

        val service2 = ScheduleServices(2,R.drawable.cabelos,  "sabd uhsbadias uhdsaiud", "")
        servicesList.add(service2)

        val service3 = ScheduleServices(3,R.drawable.cabelos, "sabd uhsbadias uhdsaiud", "")
        servicesList.add(service3)
    }

    private fun goSToHome(nome: String) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("nome", nome)
        startActivity(intent)
    }


    private fun configureGesture() {
        gestures = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                binding.servicesRecyclerView.findChildViewUnder(e.x, e.y).run {
                    this?.let {view ->
                        binding.servicesRecyclerView.getChildAdapterPosition(view).apply {
                            Intent(this@Schedule, toSchedule::class.java).run {
                                intent.putExtra("position", this@apply)
                                startActivity(this)
                            }
                        }
                    }
                }

                return super.onSingleTapConfirmed(e)
            }
        })
    }

    private fun configureRecycleView(){
        binding.servicesRecyclerView.addOnItemTouchListener(object : OnItemTouchListener {

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