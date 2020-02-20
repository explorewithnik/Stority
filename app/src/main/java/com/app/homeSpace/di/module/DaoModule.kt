package com.app.homeSpace.di.module

import android.app.Application
import androidx.room.Room
import com.app.homeSpace.database.Database
import com.app.homeSpace.homeSpace.data.HomeSpaceDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DaoModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): Database {
        return Room.databaseBuilder(application,
            Database::class.java, "stority.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideHomeSpaceDao(db: Database): HomeSpaceDao {
        return db.homeSpaceDao()
    }
}