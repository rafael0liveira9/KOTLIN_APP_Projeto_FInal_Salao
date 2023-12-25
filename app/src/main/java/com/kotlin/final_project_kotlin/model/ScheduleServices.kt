package com.kotlin.final_project_kotlin.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.util.*

data class ScheduleServices(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("services")
    val services: ServiceInfo? = null,

    @SerializedName("schedule_list")
    val scheduleList: ScheduleList? = null
) {
    data class ServiceInfo(
        @SerializedName("name")
        val name: String? = null,

        @SerializedName("description")
        val description: String? = null,

        @SerializedName("image")
        val image: String? = null
    )

    data class ScheduleList(
        @SerializedName("schedule_date")
        val scheduleDate: String? = null
    )


    companion object {
        fun fromJson(json: JSONObject): ScheduleServices? {
            return try {
                val jsonString = json.toString()
                Log.d("DEBUG", "JSON String: $jsonString")

                val scheduleServices = Gson().fromJson(jsonString, ScheduleServices::class.java)
                Log.d("DEBUG", "ScheduleServices: $scheduleServices")

                scheduleServices
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null
            }
        }
    }

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", id)

        services?.let {
            val serviceObject = JSONObject()
            serviceObject.put("name", it.name)
            serviceObject.put("description", it.description)
            serviceObject.put("image", it.image)

            jsonObject.put("services", serviceObject)
        }


        scheduleList?.let {
            val scheduleListObject = JSONObject()
            scheduleListObject.put("schedule_date", it.scheduleDate)

            jsonObject.put("schedule_list", scheduleListObject)
        }

        return jsonObject
    }
}