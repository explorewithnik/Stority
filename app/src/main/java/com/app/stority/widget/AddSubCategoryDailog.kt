package com.app.stority.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.app.stority.R
import com.app.stority.databinding.DialogAddSubCategoryDataBinding
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_CANCEL
import com.app.stority.homeSpace.data.SubCategoryTable


class AddSubCategoryDailog(
    @get:JvmName("getContext_") val context: Context,
    private var data: SubCategoryTable = SubCategoryTable(),
    private val action: Int,
    private var dataBindingComponent: DataBindingComponent,
    private val onSaveCallback: ((SubCategoryTable, Int) -> Unit),
    private val onCancelCallback: ( (Int) -> Unit)
) : Dialog(context, R.style.EditTextDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.inflate<DialogAddSubCategoryDataBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_add_sub_category_data,
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