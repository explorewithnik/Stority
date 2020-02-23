package com.app.stority.widget

import androidx.annotation.IntDef

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(AutoResetMode.NEVER, AutoResetMode.UNDER, AutoResetMode.OVER, AutoResetMode.ALWAYS)
annotation class AutoResetMode {

    companion object Parser {
        const val UNDER = 0
        const val OVER = 1
        const val ALWAYS = 2
        const val NEVER = 3

        @AutoResetMode
        fun fromInt(value: Int): Int {
            return when (value) {
                OVER -> OVER
                ALWAYS -> ALWAYS
                NEVER -> NEVER
                else -> UNDER
            }
        }
    }


}