package com.app.homeSpace.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.homeSpace.di.ViewModelKey
import com.app.homeSpace.di.factory.AppModelFactory
import com.app.homeSpace.homeSpace.observer.HomeSpaceViewModel
import com.app.homeSpace.homeSpace.observer.SubCategoryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeSpaceViewModel::class)
    abstract fun bindHomeSpaceViewModel(viewModel: HomeSpaceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SubCategoryViewModel::class)
    abstract fun bindSubCategoryViewModel(viewModel: SubCategoryViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: AppModelFactory): ViewModelProvider.Factory
}