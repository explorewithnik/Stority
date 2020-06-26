package com.app.stority.database

import androidx.room.TypeConverter
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.gson.Gson

class TypeConverter {

    @androidx.room.TypeConverter
    fun listToJson(value: MutableList<String?>?): String? {
        return Gson().toJson(value)
    }

    @androidx.room.TypeConverter
    fun jsonToList(value: String?): MutableList<String?>? {
        val objects = Gson().fromJson(value, Array<String?>::class.java)
        return objects?.toMutableList()
    }

    @TypeConverter
    fun cardViewToView(cv: CircularRevealCardView?): CircularRevealCardView? {
        return cv
    }
}