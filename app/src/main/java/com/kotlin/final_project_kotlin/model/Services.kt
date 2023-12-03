package com.kotlin.final_project_kotlin.model


class Services (
    val id : Int? = null,
    val img: Int? = null,
    val name: String? = null,
    val description: String? = null
        ) {
    constructor(id: Int, name: String): this(id,null, name, "")
    constructor(id: Int, name: String, description: String?): this(id,null, name, "")
}