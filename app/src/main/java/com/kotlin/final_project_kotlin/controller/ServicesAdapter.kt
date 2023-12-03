package com.kotlin.final_project_kotlin.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.final_project_kotlin.databinding.ServiceCardBinding
import com.kotlin.final_project_kotlin.model.Services

class ServicesAdapter(private val context: Context, private val servicesList: MutableList<Services>):
    RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val serviceItem = ServiceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServicesViewHolder(serviceItem)
    }

    override fun getItemCount() = servicesList.size

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        servicesList[position].img?.let { resourceId ->
            holder.imgService.setImageResource(resourceId)
        }
        holder.titleService.text=servicesList[position].name
    }

    inner class ServicesViewHolder(binding: ServiceCardBinding): RecyclerView.ViewHolder(binding.root) {
        val imgService = binding.serviceImage
        val titleService = binding.titleText
    }
}