package com.kotlin.final_project_kotlin.view

import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector

import android.view.MotionEvent
import android.view.View

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.kotlin.final_project_kotlin.controller.MainActivity
import com.kotlin.final_project_kotlin.controller.ScheduleAdapter
import com.kotlin.final_project_kotlin.databinding.ActivityScheduleBinding
import com.kotlin.final_project_kotlin.model.DataStore
import com.kotlin.final_project_kotlin.model.ScheduleServices

import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import kotlin.collections.HashMap

class Schedule : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleBinding
    private lateinit var servicesAdapter: ScheduleAdapter
    private lateinit var gestures: GestureDetector


    private val servicesList: MutableList<ScheduleServices> = mutableListOf()


    private val queue: RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)


        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (servicesList.size > 0) {
            binding.progressBar.visibility = View.GONE
            binding.servicesRecyclerView.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
            binding.noSchedule.visibility = View.VISIBLE
        }

        supportActionBar?.hide()

        val name = sharedPreferences.getString("user_name", "")
        getEvents()


        if (!isUserLoggedIn()) {
            redirectToLogin()
            return
        }

        binding.usernameText.text = "Bem-Vindo(a), $name"
        val servicesRecyclerView = binding.servicesRecyclerView
        servicesRecyclerView.layoutManager = GridLayoutManager(this, 1)
        servicesAdapter= ScheduleAdapter(this, servicesList)
        servicesRecyclerView.setHasFixedSize((true))
        servicesRecyclerView.adapter=servicesAdapter

        configureGesture()
        configureRecycleView()

        binding.btnSchedule.setOnClickListener {
            goSToHome(name.toString())
        }

        binding.userMenu.setOnClickListener {
            goMenu()
        }



    }

    private val eventsList: MutableList<ScheduleServices> = mutableListOf()


    private val sharedPreferences by lazy {
        getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }
    private fun getEvents() {
        binding.progressBar.visibility = View.VISIBLE
        binding.servicesRecyclerView.visibility = View.GONE
        binding.noSchedule.visibility = View.GONE

        val eventsUrl = "${DataStore.DATABASE_URL}get-events"
        val token = sharedPreferences.getString("user_jwt", "")

        val stringRequest = object : StringRequest(
            Method.POST, eventsUrl,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getInt("status")

                    if (status == 200) {
                        val jsonArray = jsonObject.getJSONArray("res")

                        eventsList.clear()

                        for (i in 0 until jsonArray.length()) {
                            val eventObject = jsonArray.getJSONObject(i)
                            val eventItem = ScheduleServices.fromJson(eventObject)
                            if (eventItem != null) {
                                eventsList.add(eventItem)
                            }
                        }


                        servicesList.clear()
                        servicesList.addAll(eventsList)

                        if (servicesList.size > 0) {
                            binding.progressBar.visibility = View.GONE
                            binding.servicesRecyclerView.visibility = View.VISIBLE
                        }else{
                            binding.progressBar.visibility = View.GONE
                            binding.noSchedule.visibility = View.VISIBLE
                        }


                        servicesAdapter.notifyDataSetChanged()
                    } else {
                        Log.e("EVENTS_ERROR", "Erro no status da resposta: $status")
                        if (servicesList.size > 0) {
                            binding.progressBar.visibility = View.GONE
                            binding.servicesRecyclerView.visibility = View.VISIBLE
                        }else{
                            binding.progressBar.visibility = View.GONE
                            binding.noSchedule.visibility = View.VISIBLE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("EVENTS_ERROR", "Erro ao fazer parsing do JSON: $e")
                    if (servicesList.size > 0) {
                        binding.progressBar.visibility = View.GONE
                        binding.servicesRecyclerView.visibility = View.VISIBLE
                    }else{
                        binding.progressBar.visibility = View.GONE
                        binding.noSchedule.visibility = View.VISIBLE
                    }
                }
            },
            Response.ErrorListener { error ->
                Log.e("EVENTS_ERROR", "Erro: $error")
                if (servicesList.size > 0) {
                    binding.progressBar.visibility = View.GONE
                    binding.servicesRecyclerView.visibility = View.VISIBLE
                }else{
                    binding.progressBar.visibility = View.GONE
                    binding.noSchedule.visibility = View.VISIBLE
                }

            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "$token"
                return headers
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(stringRequest)
    }

    private fun removeService(serviceId: Int?) {

        binding.progressBar.visibility = View.VISIBLE
        binding.servicesRecyclerView.visibility = View.GONE
        binding.noSchedule.visibility = View.GONE

        val servicesUrl = "${DataStore.DATABASE_URL}delete-schedule"
        val token = sharedPreferences.getString("user_jwt", "")

        val stringRequest = object : StringRequest(
            Method.POST, servicesUrl,
            Response.Listener { response ->

                servicesList.removeIf { it.id == serviceId }
                servicesAdapter.notifyDataSetChanged()

                Log.d("DEBUG DELETE", "ANTES DO IF: $servicesList")

                if (servicesList.size > 0) {
                    binding.progressBar.visibility = View.GONE
                    binding.servicesRecyclerView.visibility = View.VISIBLE
                }else{
                    binding.progressBar.visibility = View.GONE
                    binding.noSchedule.visibility = View.VISIBLE
                }

                Log.d("DEBUG DELETE", "DEPOIS DO IF: $servicesList")

                servicesAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Log.e("ADD_SERVICE_ERROR", "Error: $error")
                if (servicesList.size > 0) {
                    binding.progressBar.visibility = View.GONE
                    binding.servicesRecyclerView.visibility = View.VISIBLE
                }else{
                    binding.progressBar.visibility = View.GONE
                    binding.noSchedule.visibility = View.VISIBLE
                }

            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()
                headers["Authorization"] = token.toString() ?: ""
                headers["Content-Type"] = "application/json"
                Log.d("errooooor", "$token.toString()")
                return headers
            }

            override fun getBody(): ByteArray {
                val jsonParams = JSONObject().apply {
                    put("id", serviceId)
                }
                Log.d("errooooor", "$jsonParams")
                return jsonParams.toString().toByteArray(Charsets.UTF_8)
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(stringRequest)
    }

    private fun goSToHome(nome: String) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("nome", nome)
        startActivity(intent)
    }

    private fun goMenu() {
        val intent = Intent(this, userMenu::class.java)
        startActivity(intent)
    }

    private fun configureGesture() {
        gestures = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {

                binding.servicesRecyclerView.findChildViewUnder(e.x, e.y).run {
                    this?.let { view ->
                        binding.servicesRecyclerView.findChildViewUnder(e.x, e.y).run {
                            this?.let { view ->
                                binding.servicesRecyclerView.getChildAdapterPosition(view).apply {
                                    val serviceId = servicesList[this].id
                                    confirmScheduleModal(serviceId)
                                }
                            }
                        }
                    }
                }
                return super.onLongPress(e)
            }



            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                binding.servicesRecyclerView.findChildViewUnder(e.x, e.y).run {
                    this?.let { view ->
                        binding.servicesRecyclerView.findChildViewUnder(e.x, e.y).run {
                            this?.let { view ->
                                binding.servicesRecyclerView.getChildAdapterPosition(view).apply {
                                    val serviceId = servicesList[this].id
                                    val serviceName = servicesList[this].services?.name
                                    val serviceDesc = servicesList[this].services?.description
                                    val serviceDate = servicesList[this].scheduleList?.scheduleDate

                                    infoScheduleModal(serviceId, serviceName, serviceDesc, serviceDate)
                                }
                            }
                        }
                    }
                }

                return super.onSingleTapConfirmed(e)
            }
        })
    }

    private fun configureRecycleView() {
        binding.servicesRecyclerView.addOnItemTouchListener(object : OnItemTouchListener {

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

    private fun redirectToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("user_id", "")

        Log.d("TESTE PARAMS", "$userId")
        return !userId.isNullOrEmpty()
    }

    private fun confirmScheduleModal(serviceId: Int?) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Cancelar este Agendamento?")
        alertDialogBuilder.setMessage("***Atenção! Esta ação é irreversível, você deseja cancelar este agendamento de serviço?")

        alertDialogBuilder.setNegativeButton("VOLTAR") { dialog, which ->
            dialog.dismiss()
        }

        alertDialogBuilder.setPositiveButton("CONFIRMAR") { dialog, which ->
            Log.d("cancelando o agendamento", "$serviceId")
            removeService(serviceId)
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun infoScheduleModal(serviceId: Int?, serviceName: String?, serviceDesc: String?, serviceDate: String?) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        val title = "$serviceName"
        val parsedDate = parseDateString(serviceDate)
        val formattedDate = formatDateString(parsedDate)

        Log.d("1", "$parsedDate")
        Log.d("1", "$formattedDate")

        val message = "$serviceDesc\n\nData e Hora: $parsedDate"

        alertDialogBuilder.setTitle("Serviço: $title")
        alertDialogBuilder.setMessage("$message")

        alertDialogBuilder.setNegativeButton("VOLTAR") { dialog, which ->
            dialog.dismiss()
        }

        alertDialogBuilder.setPositiveButton("CANCELAR") { dialog, which ->

            confirmScheduleModal(serviceId)
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun parseDateString(dateString: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())

        try {
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    private fun formatDateString(date: String?): String {
        date?.let {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("GMT-3")

            try {
                Log.d("aaaaaaaaaaaaaaaaaaaa", "${inputFormat.parse(it)}")
                val parsedDate = inputFormat.parse(it)
                return outputFormat.format(parsedDate)
            } catch (e: ParseException) {
                e.printStackTrace()
                return "Data Indisponível"
            }
        }
        return "Data Indisponível"
    }
}