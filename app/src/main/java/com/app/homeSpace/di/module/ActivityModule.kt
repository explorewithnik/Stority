package com.app.homeSpace.di.module

import com.app.homeSpace.displayImage.DisplayImageActivity
import com.app.homeSpace.homeSpace.owner.activity.HomeSpaceActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [(FragmentModule::class)])
    abstract fun contributeDisplayImageActivity(): DisplayImageActivity

    @ContributesAndroidInjector(modules = [(FragmentModule::class)])
    abstract fun contributeHomeSpaceActivity(): HomeSpaceActivity

}