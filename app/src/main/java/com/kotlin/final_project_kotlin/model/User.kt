package com.kotlin.final_project_kotlin.model

import com.google.gson.annotations.SerializedName
import java.text.DateFormat

data class User(

    @SerializedName("id")
    var id : Int,

    @SerializedName("name")
    var name : String,

    @SerializedName("email")
    var email : String,

    @SerializedName("jwt")
    var jwt : String
) {

}