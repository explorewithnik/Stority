package com.app.homeSpace.database

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
}