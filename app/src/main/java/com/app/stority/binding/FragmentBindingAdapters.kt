package com.app.stority.binding

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.app.stority.R
import com.app.stority.displayImage.ImageViewerActivity
import com.app.stority.helper.CameraUtils.checkCameraHardware
import com.app.stority.helper.CameraUtils.getInternalStorageSize
import com.app.stority.helper.Logger
import com.app.stority.widget.InputFilterMinMax
import com.app.stority.widget.TakePictureDialog
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {

    /** Attribute "app:imageUrl" will help you to load image (local or image URL) on ImageView */
    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        Glide.with(fragment).load(url).into(imageView)
    }


    /** Attribute "app:visibility" will help you to change the view visibility if condition is true then it will be visible else gone. */
    @BindingAdapter("visibility")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }


    //time stamp


    @BindingAdapter(value = ["timeStamp", "format"], requireAll = false)
    fun bindDateTime(textView: TextView, timeStamp: String?, format: String?) {
        if (timeStamp == null) return
        bindDateTime(textView, Date(timeStamp.toLong()), format, "--/--")
    }

    @BindingAdapter(value = ["timeStamp", "format", "emptyTxt"], requireAll = true)
    fun bindDateTime(textView: TextView, timeStamp: String?, format: String?, emptyTxt: String?) {
        if (timeStamp == null || timeStamp.equals("null", true)) {
            textView.text = emptyTxt
        } else
            bindDateTime(textView, Date(timeStamp.toLong()), format, emptyTxt)
    }

    @BindingAdapter(value = ["timeStampLong", "format", "emptyTxt"], requireAll = true)
    fun bindDateTime(textView: TextView, timeStamp: Long?, format: String?, emptyTxt: String?) {
        if (timeStamp == null) return
        bindDateTime(textView, Date(timeStamp), format, emptyTxt)
    }

    @BindingAdapter(value = ["date", "format", "emptyTxt"], requireAll = true)
    fun bindDateTime(textView: TextView, date: Date?, format: String?, emptyTxt: String?) {
        try {
            if (date != null) {
                val dateFormat = SimpleDateFormat(format, Locale.ENGLISH)
                textView.text = dateFormat.format(date)
            } else {
                textView.text = emptyTxt
            }
        } catch (e: Exception) {
            textView.text = emptyTxt
            //if (AppConfig.DEBUG_MODE) e.printStackTrace()
        }
    }


    //min max value
    @BindingAdapter(value = ["maxValue", "minValue"], requireAll = true)
    fun bindMaxMinValue(editText: EditText, maxValue: String?, minValue: String?) {
        Logger.e(Thread.currentThread(), "BindingAdapter MaxValue ${maxValue}  MinValue ${minValue}")
        editText.filters = arrayOf<InputFilter>(InputFilterMinMax(min = minValue, max = maxValue))
    }

    @BindingAdapter(value = ["maxDigitsBeforeDecimal", "maxDigitsAfterDecimal"], requireAll = true)
    fun bindInputFilter(editText: EditText, maxDigitsBeforeDecimal: Int?, maxDigitsAfterDecimal: Int?) {
        val filter = InputFilter { charSequence, start, end, spanned, dStart, dEnd ->
            val builder = StringBuilder(spanned)
            builder.replace(dStart, dEnd, charSequence.subSequence(start, end).toString())

            if (!builder.toString().matches(("(([0-9]{01})([0-9]{00," + (maxDigitsBeforeDecimal?.minus(1)) + "})?)?(\\.[0-9]{0," + maxDigitsAfterDecimal + "})?").toRegex())) {
                return@InputFilter if (charSequence.isEmpty()) spanned.subSequence(dStart, dEnd) else ""
            }
            null
        }

        editText.filters = arrayOf(filter)
    }

}

