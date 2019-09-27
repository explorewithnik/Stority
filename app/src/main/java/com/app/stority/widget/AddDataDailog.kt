package com.app.stority.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.app.stority.R
import com.app.stority.databinding.DialogAddDataBinding


class AddDataDailog(
    context: Context,
    private val listener: DialogEventTriggerListner
) : Dialog(context, R.style.BaseDialogTheme) {

    private lateinit var binding: DialogAddDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_add_data,
            null,
            false
        )
        setContentView(binding.root)

        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        binding.let {

            it.saveButton.setOnClickListener { view ->
                listener.saveData(it.categoryEt.text.toString())
                dismiss()
            }

            it.cancelButton.setOnClickListener {
                listener.cancel()
                dismiss()
            }
        }
    }

    interface DialogEventTriggerListner {
        fun saveData(text: String)
        fun cancel()
    }
}