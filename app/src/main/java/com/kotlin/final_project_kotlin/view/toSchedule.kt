package com.kotlin.final_project_kotlin.view

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.kotlin.final_project_kotlin.databinding.ActivityToScheduleBinding
import com.kotlin.final_project_kotlin.model.DataStore
import java.util.Calendar

class toSchedule : AppCompatActivity() {

    private lateinit var binding: ActivityToScheduleBinding
    private var calendar : Calendar = Calendar.getInstance()
    private var data: String = ""
    private var time: String = ""
    private var position = -1


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityToScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        intent.run {
            position = this.getIntExtra("position", -1)
            val service = DataStore.getService(position)
            if (position != -1 && service != null) {
                Log.d("Tag", (position.toString()))
                binding.scheduleTitleName.text = service.name
            } else {
                Log.d("Tag", "Erro ao obter serviço")
            }
        }

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener { _, yearPick, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR,yearPick)
            calendar.set(Calendar.MONTH,monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            var day = dayOfMonth.toString()
            var month = monthOfYear.toString()
            val year = yearPick.toString()

            if (dayOfMonth<10) {
                day = "0$dayOfMonth"
            }

            if (monthOfYear<10) {
                month = "0$monthOfYear"
            }else{
                month = (monthOfYear +1).toString()
            }

            data = "$day/$month/$year"
        }
        val hourPicker = binding.hourPicker
        hourPicker.setOnTimeChangedListener { _, hourPick, minutesPick ->
            var hour:String
            var minutes:String

            if (hourPick<10) {
                hour = "0$hourPick"
            }else{
                hour = hourPick.toString()
            }

            if (minutesPick<10) {
                minutes = "0$minutesPick"
            }else{
                minutes = minutesPick.toString()
            }

            time = "$hour:$minutes"

        }

        binding.hourPicker.setIs24HourView(true)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            when {
                time.isEmpty() -> {
                    message(it, "Por Favor, selecione um Horário...")
                }
                time < "08:00" && time > "19:00" -> {
                    message(it, "O Horário de Atendimento: 08:00 as 19:00")
                }
                data.isEmpty() -> {
                    message(it, "Por Favor, selecione uma Data...")
                }
                else -> {
                    if(position == -1) {
                        addService()
                        messageDone(it, "Serviço Adicionado com Sucesso!")
                    }else {
                        editService()
                        messageDone(it, "Serviço Atualizado com Sucesso!")
                    }
                    finish()
                }
            }

        }
    }

    private fun message (view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }
    private fun messageDone (view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#008000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun addService() {

    }
    private fun editService() {

    }
}