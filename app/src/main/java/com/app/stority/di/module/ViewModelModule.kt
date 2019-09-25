package com.app.stority.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.stority.di.ViewModelKey
import com.app.stority.di.factory.AppModelFactory
import com.app.stority.homeSpace.observer.HomeSpaceViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeSpaceViewModel::class)
    abstract fun bindCompatibilityViewModel(viewModel: HomeSpaceViewModel): ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: AppModelFactory): ViewModelProvider.Factory
}