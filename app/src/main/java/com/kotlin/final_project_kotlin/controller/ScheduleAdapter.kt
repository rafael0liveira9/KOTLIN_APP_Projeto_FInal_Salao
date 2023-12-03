package com.kotlin.final_project_kotlin.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.final_project_kotlin.databinding.ScheduleCardBinding
import com.kotlin.final_project_kotlin.model.ScheduleServices

class ScheduleAdapter(private val context: Context, private val servicesList: MutableList<ScheduleServices>):
    RecyclerView.Adapter<ScheduleAdapter.ScheduleServicesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleServicesViewHolder {
        val serviceItem = ScheduleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleServicesViewHolder(serviceItem)
    }

    override fun getItemCount() = servicesList.size

    override fun onBindViewHolder(holder: ScheduleServicesViewHolder, position: Int) {
        servicesList[position].img?.let { resourceId ->
            holder.imgService.setImageResource(resourceId)
        }
        holder.titleService.text=servicesList[position].title
    }

    inner class ScheduleServicesViewHolder(binding: ScheduleCardBinding): RecyclerView.ViewHolder(binding.root) {
        val imgService = binding.serviceImage
        val titleService = binding.serviceTitle
        val descriptionService = binding.serviceDescription
        val dateService = binding.serviceDate
    }
}