package com.app.homeSpace.homeSpace.data

import com.google.gson.annotations.SerializedName

data class HomeSpaceTableListResponse(

    @field:SerializedName("data")
    val homeSpaceTable: List<HomeSpaceTable>? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("statusCode")
    val statusCode: Int? = null
)