package com.app.stority.displayImage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.stority.R
import com.app.stority.databinding.ActivityImageViewerBinding
import com.app.stority.helper.CameraProvider
import com.app.stority.helper.CameraUtils
import com.app.stority.helper.CameraUtils.getCompressedImageNSaveCamDetails
import com.app.stority.helper.Logger
import com.app.stority.helper.Utils
import com.bumptech.glide.Glide


class ImageViewerActivity : AppCompatActivity() {



    private var cameraPath: CameraPath? = CameraPath()
    private var cameraProvider: CameraProvider? = null

    private var imageUrl = ""
    private var code: String? = ""

    private val utils = Utils()

    lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_viewer)

        code = intent?.getStringExtra(ARG_IMAGE_CODE)
        launchIntent()

        if (intent.hasExtra(ARG_REMARK_FLAG))
            binding.remark.visibility = View.INVISIBLE

        binding.saveFab.setOnClickListener {
            exit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.e(Thread.currentThread(), "Fragment: RequestCode: $requestCode resultCode: $resultCode")
        var compressedPath : String? = ""

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> compressedPath = getCompressedImageNSaveCamDetails(this, cameraPath?.currentPath, "0", "0", "0")

                REQUEST_GALLERY -> compressedPath = try {
                    val uri = data!!.data
                    utils.getRealPathFromURI(this, uri!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            }

            Logger.i(Thread.currentThread(), "Uri: $compressedPath")

            if (!compressedPath.isNullOrEmpty() && !compressedPath.equals("", ignoreCase = true) ) {
                imageUrl = compressedPath
                Glide.with(this)
                    .load(compressedPath)
                    .into(binding.imageview)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
    }

    private fun launchIntent() {
        when (code) {
            CODE_CAMERA -> {
                showCamera(REQUEST_CAMERA)
            }

            CODE_GALLERY -> {
                launchGallery(REQUEST_GALLERY)
            }
        }
    }

    private fun showCamera(code: Int) {
        if (CameraUtils.checkCameraHardware(this)) {
            if (CameraUtils.getInternalStorageSize() > 5) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    cameraPath = Utils().getOutputMediaFileUri(1, this)

                    Logger.e(Thread.currentThread(), "Path: " + cameraPath?.uri + " " + cameraPath?.currentPath)

                    if (cameraPath?.uri == null) {
                        Toast.makeText(this, getString(R.string.generalError), Toast.LENGTH_LONG).show()
                    } else {
                        Logger.e(Thread.currentThread(), "storage uri: " + cameraPath?.uri.toString())
                        // set the image file name
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPath?.uri)
                        startActivityForResult(takePictureIntent, code)
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.noMemory), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchGallery(code: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, code)
    }



    private fun exit() {
        Logger.i(Thread.currentThread(), "Url: $imageUrl Remarks: ${binding.remark.text}")
        val output = Intent()
        output.putExtra(ARG_IMAGE_URL, imageUrl)
        output.putExtra(ARG_REMARK, binding.remark.text.toString())
        output.putExtra(ARG_CAMERA_PROVIDER, cameraProvider)
        setResult(Activity.RESULT_OK, output)
        finish()
    }

    companion object {
        // arguments used in sending and receiving intents
        const val ARG_IMAGE_CODE = "code"
        const val ARG_REMARK = "remarks"
        const val ARG_IMAGE_URL = "image_url"
        const val ARG_REMARK_FLAG = "showRemarks"
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
        private const val REQUEST_SECOND_CAMERA = 102
        private const val ARG_CAMERA_PROVIDER = "cameraProvider"

        // code corresponding to which option to be used out of gallery, device camera or app's camera
        const val CODE_GALLERY = "gallery"
        const val CODE_CAMERA = "camera"
        const val CODE_SECOND_CAMERA = "camera_2"
    }
}

