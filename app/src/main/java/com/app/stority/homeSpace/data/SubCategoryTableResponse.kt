package com.app.stority.homeSpace.data

import com.google.gson.annotations.SerializedName

data class SubCategoryTableResponse(

    @field:SerializedName("data")
    val subCategoryTable: List<SubCategoryTable>? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("statusCode")
    val statusCode: Int? = null
)