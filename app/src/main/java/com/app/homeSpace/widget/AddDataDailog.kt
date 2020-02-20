package com.app.homeSpace.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.app.homeSpace.R
import com.app.homeSpace.databinding.DialogAddDataBinding
import com.app.homeSpace.homeSpace.data.HomeSpaceTable
import com.app.homeSpace.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_CANCEL


class AddDataDailog(
    @get:JvmName("getContext_") val context: Context,
    private var data: HomeSpaceTable? = HomeSpaceTable(),
    private val action: Int,
    private var dataBindingComponent: DataBindingComponent,
    private val onSaveCallback: ((HomeSpaceTable?, Int) -> Unit),
    private val onCancelCallback: ((Int) -> Unit)
) : Dialog(context, R.style.EditTextDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.inflate<DialogAddDataBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_add_data,
            null,
            false,
            dataBindingComponent
        ).also { bind ->
            setContentView(bind.root)
            bind.data = data

            bind.saveButton.setOnClickListener {
                bind?.data?.let {

                    onSaveCallback.invoke(data, action)
                    this.dismiss()
                }
            }
            bind.cancelButton.setOnClickListener {
                this.dismiss()
                onCancelCallback.invoke(ACTION_CANCEL)
            }


            this.setCanceledOnTouchOutside(false)
        }
    }


}