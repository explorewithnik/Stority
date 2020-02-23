package com.app.stority.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.app.stority.R
import com.app.stority.databinding.DialogCaptureImageBinding


class TakePictureDialog(
    context: Context,
    private val listener: DialogItemClickListener
) : Dialog(context, R.style.BaseDialogTheme) {

    private lateinit var binding: DialogCaptureImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_capture_image, null,false)
        setContentView(binding.root)

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        binding.let {
            it.cameraContainer.setOnClickListener {
                listener.onCameraClick()
                dismiss()
            }

            it.galleryContainer.setOnClickListener {
                listener.onGalleryClick()
                dismiss()
            }
        }
    }

    interface DialogItemClickListener {
        fun onCameraClick()
        fun onGalleryClick()
    }
}