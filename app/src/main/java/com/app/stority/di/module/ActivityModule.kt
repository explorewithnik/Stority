package com.app.stority.di.module

import com.app.stority.displayImage.DisplayImageActivity
import com.app.stority.homeSpace.owner.activity.HomeSpaceActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [(FragmentModule::class)])
    abstract fun contributeDisplayImageActivity(): DisplayImageActivity

    @ContributesAndroidInjector(modules = [(FragmentModule::class)])
    abstract fun contributeHomeSpaceActivity(): HomeSpaceActivity

}