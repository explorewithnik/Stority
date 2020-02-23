package com.app.stority.widget

import android.text.Spanned
import android.text.InputFilter
import com.app.stority.helper.Logger


class InputFilterMinMax : InputFilter {

    private var min: Double = 0.0
    private var max: Double = 0.0

    constructor(min: Double, max: Double) {
        this.min = min
        this.max = max
    }

    constructor(min: String?, max: String?) {
        if (!min.isNullOrEmpty())
            this.min = min.toDouble()
        if (!max.isNullOrEmpty())
            this.max = max.toDouble()

    }

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            Logger.e(Thread.currentThread(),"Input Value: $min -- $max -- $input -- ${isInRange(min, max, input)} ")
            if (isInRange(min, max, input))
                return null
        } catch (nfe: NumberFormatException) {
        }

        return ""
    }

    private fun isInRange(a: Double, b: Double, c: Double): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }
}