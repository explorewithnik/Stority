package com.app.stority.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.app.stority.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Application File for request queues of volley.
 * Additional cases are made to remove duplicate calls to volley.
 */

class AppController : Application(), HasActivityInjector {
    lateinit var context: Context

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        context = applicationContext
        instance = this
        appInitialization()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * Returns an [AndroidInjector] of [Activity]s.
     */
    override fun activityInjector() = dispatchingAndroidInjector

    private fun appInitialization() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler)
    }


    private var defaultUEH: Thread.UncaughtExceptionHandler? = null

    // handler listener
    private val _unCaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, ex ->
        defaultUEH!!.uncaughtException(thread, ex)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        lateinit var instance: AppController
    }
}