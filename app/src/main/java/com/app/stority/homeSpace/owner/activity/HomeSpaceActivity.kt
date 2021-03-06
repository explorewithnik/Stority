package com.app.stority.homeSpace.owner.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.stority.R
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment
import com.app.stority.homeSpace.owner.fragment.SubCategoryViewFragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_home_space.*
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

        if (isFirstRun == true) {
            sharedPref?.edit()?.putBoolean(FIRST_TIME_LAUNCH, false)?.apply()
        }

    }


    fun resetFirstTimeRunValue(value: Boolean) {
        isFirstRun = value
    }


    companion object {
        const val FIRST_TIME_LAUNCH = "firstTimeLaunch"
        const val FIRST_TIME_LAUNCH_2 = "firstTimeLaunch2"
        const val PRIVATE_MODE = 0
        const val PREF_NAME = "HomeSpacePref"
    }

    override fun onBackPressed() {
        val fragment = container.childFragmentManager.fragments[0]
        if (fragment != null && fragment is SubCategoryViewFragment) {
            fragment.onBackPress()
        } /*else if (fragment != null && fragment is SubCategoryFragment) {
            fragment.onBackPress()
        } */ else {
            super.onBackPressed()
        }

//        if (fragment != null && fragment is HomeSpaceFragment) {
//            fragment.onBackPress()
//        } else {
//            super.onBackPressed()
//        }
    }
}
