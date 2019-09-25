package com.app.stority.di.module

import dagger.Module

@Module(includes = [
    (ViewModelModule::class),
    (NetworkModule::class),
    (RepositoryModule::class),
    (DaoModule::class)]
)
class AppModule