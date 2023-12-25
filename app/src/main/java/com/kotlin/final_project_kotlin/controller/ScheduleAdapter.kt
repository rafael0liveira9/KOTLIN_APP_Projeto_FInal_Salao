package com.kotlin.final_project_kotlin.controller

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.final_project_kotlin.databinding.ScheduleCardBinding
import com.kotlin.final_project_kotlin.model.ScheduleServices
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(private val context: Context, private val servicesList: List<ScheduleServices>) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleServicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleServicesViewHolder {
        val binding =
            ScheduleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleServicesViewHolder(binding)
    }

    override fun getItemCount() = servicesList.size

    fun formatrDate(x: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy | HH:mm", Locale.getDefault())

        try {
            val utcDate = inputFormat.parse(x.toString())
            val formattedDate = outputFormat.format(utcDate)
            return formattedDate
        } catch (e: ParseException) {
            e.printStackTrace()

            return ""
        }
    }


    override fun onBindViewHolder(holder: ScheduleServicesViewHolder, position: Int) {
        val service = servicesList[position]


        val imageName = service.services?.image ?: "cabelos"
        val imageResourceId = context.resources.getIdentifier(imageName.toString(), "drawable", context.packageName)



        holder.imgService.setImageResource(imageResourceId)
        holder.titleService.text = service.services?.name
        holder.descriptionService.text = service.services?.description
        holder.dateService.text = "Data: ${formatrDate(service.scheduleList?.scheduleDate)}"

        holder.itemView.setOnClickListener {

        }
    }


    inner class ScheduleServicesViewHolder(binding: ScheduleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgService = binding.serviceImage
        val titleService = binding.serviceTitle
        val descriptionService = binding.serviceDescription
        val dateService = binding.serviceDate
    }
}