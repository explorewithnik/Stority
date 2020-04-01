package com.app.stority.homeSpace.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HomeSpaceTable")
data class HomeSpaceTable(
    @PrimaryKey(autoGenerate = true)
    @field:SerializedName("id")
    var id: Int = 0,

    @field : SerializedName("imageUrl")
    var imageUrl: String? = null,

    @field : SerializedName("text")
    var text: String? = null,

    @field : SerializedName("timeStamp")
    var timeStamp: String? = System.currentTimeMillis().toString()

)


