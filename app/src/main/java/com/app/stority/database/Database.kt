package com.app.stority.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.stority.BuildConfig
import com.app.stority.homeSpace.data.HomeSpaceDao
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.data.SubCategoryTable


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

