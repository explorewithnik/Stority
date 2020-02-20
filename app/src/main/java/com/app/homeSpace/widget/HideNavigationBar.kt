package com.app.homeSpace.widget

import android.app.Activity
import android.view.View

class HideNavigationBar(activity: Activity) {
    init {

       activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

}