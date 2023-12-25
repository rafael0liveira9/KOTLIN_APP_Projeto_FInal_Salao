package com.kotlin.final_project_kotlin.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.util.*

data class ListAvaible(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("schedule_date")
    val scheduleDate: String? = null,

    @SerializedName("situation")
    val situation: Boolean? = false,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("deletedAt")
    val deletedAt: String? = null
) {
    companion object {
        fun fromJson(json: JSONObject): ListAvaible {
            return Gson().fromJson(json.toString(), ListAvaible::class.java)
        }
    }
}