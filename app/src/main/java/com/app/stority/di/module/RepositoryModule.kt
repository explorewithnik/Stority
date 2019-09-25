package com.app.stority.di.module


import com.app.stority.helper.AppExecutors
import com.app.stority.homeSpace.data.HomeSpaceDao
import com.app.stority.homeSpace.repo.HomeSpaceRepository
import com.app.stority.remoteUtils.WebService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideCompatibilityRepository(webService: WebService, executors: AppExecutors, dao : HomeSpaceDao): HomeSpaceRepository{
        return HomeSpaceRepository(executor = executors, webService = webService, dao = dao)
    }

}