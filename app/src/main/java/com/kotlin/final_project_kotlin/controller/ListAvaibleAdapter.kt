package com.kotlin.final_project_kotlin.controller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.kotlin.final_project_kotlin.databinding.ListAvaibleBinding
import com.kotlin.final_project_kotlin.model.DataStore
import com.kotlin.final_project_kotlin.model.ListAvaible
import com.kotlin.final_project_kotlin.model.Services
import com.kotlin.final_project_kotlin.model.User
import com.kotlin.final_project_kotlin.view.Schedule
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.RequestQueue
import com.google.android.material.snackbar.Snackbar
import com.kotlin.final_project_kotlin.R
import com.kotlin.final_project_kotlin.view.toSchedule

import java.text.SimpleDateFormat
import java.util.*

class ListAvaibleAdapter(
    private val context: Context,
    private var avaibleList: MutableList<ListAvaible>,
    private val servicesList: MutableList<Services>,
    private val rootView: View,
    private val queue: RequestQueue) :
    RecyclerView.Adapter<ListAvaibleAdapter.ListAvaibleViewHolder>() {


    private var selectedView: View? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAvaibleViewHolder {
        val itemBinding = ListAvaibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListAvaibleViewHolder(itemBinding)
    }

    override fun getItemCount() = avaibleList.size

    override fun onBindViewHolder(holder: ListAvaibleViewHolder, position: Int) {
        val avaible = avaibleList[position]
        val service = servicesList.getOrNull(0)

        val date = avaible.scheduleDate?.let { parseIso8601Date(it) } ?: ""
        val hour = avaible.scheduleDate?.let { parseIso8601Hour(it) } ?: ""
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.dateText.text = outputDateFormat.format(date)

        val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.hourText.text = outputTimeFormat.format(hour)


        holder.btnGetHour.setOnClickListener {
            Log.d("TESTEEEEEE aaaaaa", "$service")
            val selectedDateId = avaible?.id
            val selectedServiceId = service?.id
            val dateString = "${outputDateFormat.format(date)}"

            confirmScheduleModal(selectedServiceId, selectedDateId, dateString)
        }
    }

    private fun parseIso8601Date(iso8601Date: String): Date {
        val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.parse(iso8601Date) ?: Date()
    }

    private fun parseIso8601Hour(iso8601Date: String): Date {
        val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.parse(iso8601Date) ?: Date()
    }

    fun updateAvaibleList(newAvaibleList: List<ListAvaible>) {
        notifyDataSetChanged()
    }

    inner class ListAvaibleViewHolder(binding: ListAvaibleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val dateText = binding.dateText
        val hourText = binding.hourText
        val btnGetHour = binding.btnGetHour
        init {
            itemView.setOnClickListener {
                selectedView = it
            }
        }
    }

    private fun confirmScheduleModal(serviceId: Int?, scheduleDateId: Int?, dateString: String?) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)

        alertDialogBuilder.setTitle("Confirmação de Agendamento")
        alertDialogBuilder.setMessage("Você confirma o agendamento para o dia $dateString ?")

        alertDialogBuilder.setNegativeButton("DESISTIR") { dialog, which ->
            dialog.dismiss()
        }

        alertDialogBuilder.setPositiveButton("CONFIRMAR") { dialog, which ->
            addService(serviceId, scheduleDateId)
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun addService(serviceId: Int?, scheduleDateId: Int?) {

        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.servicesGetRecyclerView)
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE


        val servicesUrl = "${DataStore.DATABASE_URL}create-schedule"

        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("user_jwt", "")

        val stringRequest = object : StringRequest(
            Method.POST, servicesUrl,
            Response.Listener { response ->
                Log.d("RESPONSE_BODY", "Response body: $response")

                selectedView?.let { messageDone(it, "Horário Agendado com Sucesso!") }
                val intent = Intent(context, Schedule::class.java)
                context.startActivity(intent)
            },
            Response.ErrorListener { error ->

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                Log.e("ADD_SERVICE_ERROR", "Error: $error")
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token.toString() ?: ""
                headers["Content-Type"] = "application/json"

                return headers
            }

            override fun getBody(): ByteArray {
                val jsonParams = JSONObject().apply {
                    put("serviceId", serviceId)
                    put("scheduleDateId", scheduleDateId)
                }

                return jsonParams.toString().toByteArray(Charsets.UTF_8)
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            5000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(stringRequest)
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
}