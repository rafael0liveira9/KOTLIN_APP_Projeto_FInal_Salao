package com.kotlin.final_project_kotlin.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class Services(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("value")
    val value: Double? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("deletedAt")
    val deletedAt: String? = null
) {
    companion object {
        fun fromJson(json: JSONObject): Services? {
            return try {
                Gson().fromJson(json.toString(), Services::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null
            }
        }
    }
}
