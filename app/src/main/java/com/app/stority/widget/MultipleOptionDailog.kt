package com.app.stority.widget

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.app.stority.R
import com.app.stority.databinding.DialogMultipleOptionBinding
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_COPY
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_DELETE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_SHARE
import com.google.android.material.bottomsheet.BottomSheetDialog


class MultipleOptionDailog(
    @get:JvmName("getContext_") val context: Context,
    private var data: HomeSpaceTable = HomeSpaceTable(),
    private val onMoreActionCalback: ((HomeSpaceTable, String) -> Unit)
) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.inflate<DialogMultipleOptionBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_multiple_option,
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

            bind.delete.setOnClickListener {
                bind?.data?.let {
                    onMoreActionCalback.invoke(data, ACTION_DELETE)
                    this.dismiss()
                }
            }

        }
    }


}