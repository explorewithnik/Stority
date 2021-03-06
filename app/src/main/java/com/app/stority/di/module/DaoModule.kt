package com.app.stority.di.module

import android.app.Application
import androidx.room.Room
import com.app.stority.database.Database
import com.app.stority.homeSpace.data.HomeSpaceDao
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