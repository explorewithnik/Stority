package com.app.stority.di.module


import com.app.stority.helper.AppExecutors
import com.app.stority.homeSpace.data.HomeSpaceDao
import com.app.stority.homeSpace.repo.HomeSpaceRepository
import com.app.stority.remoteUtils.WebService
import com.app.stority.homeSpace.repo.SubCategoryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeSpaceRepository(webService: WebService, executors: AppExecutors, dao : HomeSpaceDao): HomeSpaceRepository{
        return HomeSpaceRepository(executor = executors, webService = webService, dao = dao)
    }

    @Provides
    @Singleton
    fun provideSubCategoryRepository(webService: WebService, executors: AppExecutors, dao : HomeSpaceDao): SubCategoryRepository {
        return SubCategoryRepository(
            executor = executors,
            webService = webService,
            dao = dao
        )
    }

}