package com.kotlin.final_project_kotlin.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.final_project_kotlin.databinding.ServiceCardBinding
import com.kotlin.final_project_kotlin.model.Services

class ServicesAdapter(private val context: Context, private var servicesList: MutableList<Services>):
    RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val serviceItem = ServiceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServicesViewHolder(serviceItem)
    }

    override fun getItemCount() = servicesList.size

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        val service = servicesList[position]

        holder.titleService.text = service.name

        // Referencie a imagem pelo nome no service.image
        val imageResourceId = holder.itemView.context.resources.getIdentifier(
            service.image, "drawable", holder.itemView.context.packageName
        )

        // Defina a imagem no ImageView
        holder.imgService.setImageResource(imageResourceId)
    }

    fun updateServicesList(newServicesList: List<Services>) {
        servicesList.clear()
        servicesList.addAll(newServicesList)
        notifyDataSetChanged()
    }

    inner class ServicesViewHolder(binding: ServiceCardBinding): RecyclerView.ViewHolder(binding.root) {
        val imgService = binding.serviceImage
        val titleService = binding.titleText
    }
}