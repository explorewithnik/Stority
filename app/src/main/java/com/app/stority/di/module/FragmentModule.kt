package com.app.stority.di.module

import com.app.stority.displayImage.DisplayFragment
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment
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