package com.app.homeSpace.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.homeSpace.BuildConfig
import com.app.homeSpace.homeSpace.data.HomeSpaceDao
import com.app.homeSpace.homeSpace.data.HomeSpaceTable
import com.app.homeSpace.homeSpace.data.SubCategoryTable


@Database(
    entities = [
        (HomeSpaceTable::class),
        (SubCategoryTable::class)
    ], version = BuildConfig.VERSION_CODE, exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun homeSpaceDao(): HomeSpaceDao
}

