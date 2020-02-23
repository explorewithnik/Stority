package com.app.stority.homeSpace.owner.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.stority.R
import com.app.stority.helper.Logger
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class HomeSpaceActivity : AppCompatActivity(), HasSupportFragmentInjector {


    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
     var isFirstRun: Boolean? = null
    override fun supportFragmentInjector() = dispatchingAndroidInjector

    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_space)

        sharedPref = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        isFirstRun = sharedPref?.getBoolean(FIRST_TIME_LAUNCH, true) ?: true
        Logger.e(Thread.currentThread(), "firstTimeLaunch  $isFirstRun")

        if (isFirstRun==true) {
            sharedPref?.edit()?.putBoolean(FIRST_TIME_LAUNCH, false)?.apply()
        }

    }


    companion object {
        const val FIRST_TIME_LAUNCH = "firstTimeLaunch"
        const val PRIVATE_MODE = 0
        const val PREF_NAME = "HomeSpacePref"
    }
}