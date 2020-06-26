package com.app.stority.homeSpace.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "SubCategoryTable",
    foreignKeys = [ForeignKey(
        entity = HomeSpaceTable::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = CASCADE
    )]
)
data class SubCategoryTable(

    @PrimaryKey(autoGenerate = true)
    @field:SerializedName("subCategoryId")
    var subCategoryId: Int = 0,

    @field:SerializedName("id")
    var id: Int = 0,

    @field : SerializedName("subImageUrl")
    var subImageUrl: String? = null,

    @field : SerializedName("text")
    var text: String? = null,

    @field : SerializedName("timeStamp")
    var timeStamp: String? = System.currentTimeMillis().toString(),

    @field: SerializedName("backGroundColor")
    var backGroundColor: String? = "-1"
)
