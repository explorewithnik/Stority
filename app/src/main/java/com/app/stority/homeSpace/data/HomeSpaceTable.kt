package com.app.stority.homeSpace.data

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
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
    var timeStamp: String? = System.currentTimeMillis().toString(),

    @field: SerializedName("backGroundColor")
    var backGroundColor: String? = "-1"
)


