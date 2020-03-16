package com.app.stority.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.app.stority.R
import com.app.stority.databinding.ConfirmationDialogBinding
import com.app.stority.helper.Logger
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.data.SubCategoryTable
import com.google.gson.Gson


class ConfirmationDailog(
    @get:JvmName("getContext_") val context: Context,
    private var data: SubCategoryTable? = SubCategoryTable(),
    private val homeSpaceData: List<HomeSpaceTable?> = ArrayList(),
    private val subCategoryData: List<SubCategoryTable?> = ArrayList(),
    private val action: String,
    private val title: String,
    private val typeOfDataToDelete: String,
    private var dataBindingComponent: DataBindingComponent,
    private val onDeleteCallback: ((SubCategoryTable?, String) -> Unit),
    private val onHomeSpaceDeleteCallback: ((List<HomeSpaceTable?>, String) -> Unit),
    private val onSubCategoryListDeleteCallback: ((List<SubCategoryTable?>, String) -> Unit),
    private val onCancelCallback: ((String) -> Unit)
) : Dialog(context, R.style.EditTextDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.inflate<ConfirmationDialogBinding>(
            LayoutInflater.from(context),
            R.layout.confirmation_dialog,
            null,
            false,
            dataBindingComponent
        )

        setContentView(binding.root)

        when (typeOfDataToDelete) {
            HOME_SPACE_DATA -> {
                binding.title = title
                binding.deleteButton.setOnClickListener {
                    homeSpaceData.let {
                        onHomeSpaceDeleteCallback.invoke(
                            it,
                            action
                        )
                        this.dismiss()
                    }
                }
            }

            SUB_CATEGORY_LIST_DATA -> {
                binding.title = title
                binding.deleteButton.setOnClickListener {
                    subCategoryData.let {
                        onSubCategoryListDeleteCallback.invoke(
                            it,
                            action
                        )
                        this.dismiss()
                    }
                }
            }

            SUB_CATEGORY_DATA -> {
                binding.data = data
                binding.title = title
                binding.deleteButton.setOnClickListener {
                    binding?.data?.let {
                        onDeleteCallback.invoke(it, action)
                        this.dismiss()
                    }
                }
            }

        }

        binding.cancelButton.setOnClickListener {
            this.dismiss()
            onCancelCallback.invoke("cancel")
        }

        this.setCanceledOnTouchOutside(false)


    }

    companion object {
        const val ACTION_CANCEL = -1
        const val HOME_SPACE_DATA = "homeSpaceData"
        const val SUB_CATEGORY_DATA = "subCategoryData"
        const val SUB_CATEGORY_LIST_DATA = "subCategoryListData"

    }

}