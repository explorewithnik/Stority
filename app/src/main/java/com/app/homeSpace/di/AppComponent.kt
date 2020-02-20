package com.app.homeSpace.di

import android.app.Application
import com.app.homeSpace.di.module.ActivityModule
import com.app.homeSpace.di.module.AppModule
import com.app.homeSpace.helper.AppController

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        (AndroidSupportInjectionModule::class),
        (AndroidInjectionModule::class),
        (AppModule::class),
        (ActivityModule::class)
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: AppController)
}