package com.app.homeSpace.di.module

import com.app.homeSpace.displayImage.DisplayFragment
import com.app.homeSpace.homeSpace.owner.fragment.HomeSpaceFragment
import com.app.homeSpace.homeSpace.owner.fragment.SubCategoryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeDisplayImageFragment(): DisplayFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeSpaceFragment(): HomeSpaceFragment

    @ContributesAndroidInjector
    abstract fun contributeSubCategoryFragment(): SubCategoryFragment

}