package com.app.homeSpace.helper

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.app.homeSpace.R
import com.app.homeSpace.databinding.DialogConfirmationLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ConfirmationDialog<T>(
    @get:JvmName("getContext_") val context: Context,
    val title: String? = "Alert",
    val message: String? = "Are You Sure",
    vararg val args: T,
    private val positiveActionTitle: String? = "OK",
    private val actionPositive: ((T) -> Unit)? = null,
    private val actionNegative: (() -> Unit)? = null
) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.inflate<DialogConfirmationLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_confirmation_layout,
            null,
            false)
        setContentView(binding.root)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val newParam = args[0] as Array<*>

        binding.let {
            it.title = title
            it.message = message
            it.positiveButtonText = positiveActionTitle
            it.positiveBtn.setOnClickListener {
                actionPositive?.invoke(newParam as T)
                this.dismiss()
            }
            it.negativeBtn.setOnClickListener {
                actionNegative?.invoke()
                this.dismiss()
            }
        }
    }
}