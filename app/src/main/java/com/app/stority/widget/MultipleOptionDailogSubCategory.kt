package com.app.stority.widget

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.app.stority.R
import com.app.stority.databinding.DialogMultipleOptionBinding
import com.app.stority.databinding.DialogMultipleOptionSubCategoryBinding
import com.app.stority.homeSpace.data.SubCategoryTable
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_CANCEL
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_COPY
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_EDIT
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_SHARE
import com.google.android.material.bottomsheet.BottomSheetDialog


class MultipleOptionDailogSubCategory(
    @get:JvmName("getContext_") val context: Context,
    private var data: SubCategoryTable? = SubCategoryTable(),
    private val onMoreActionCalback: ((SubCategoryTable?, String) -> Unit),
    private val onCancelCallback: ((Int) -> Unit)
) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.inflate<DialogMultipleOptionSubCategoryBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_multiple_option_sub_category,
            null,
            false,
            null
        ).also { bind ->
            setContentView(bind.root)
            bind.data = data
            bind.copy.setOnClickListener {
                bind?.data?.let {
                    onMoreActionCalback.invoke(data, ACTION_COPY)
                    this.dismiss()
                }
            }

            bind.share.setOnClickListener {
                bind?.data?.let {
                    onMoreActionCalback.invoke(data, ACTION_SHARE)
                    this.dismiss()
                }
            }

            bind.edit.setOnClickListener {
                bind?.data?.let {
                    onMoreActionCalback.invoke(data, ACTION_EDIT)
                    this.dismiss()
                }
            }
        }

        this.setOnCancelListener {
            it.dismiss()
            onCancelCallback.invoke(ACTION_CANCEL)
        }
    }


}