package com.app.stority.helper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.widget.Toast
import androidx.core.content.FileProvider
import com.app.stority.R
import com.app.stority.displayImage.CameraPath
import com.app.stority.helper.CameraUtils.getOutputMediaFile
import com.app.stority.helper.Constants.Companion.MAX_IMAGE_DIMENSION
import com.app.stority.helper.Constants.Companion.QUALITY_FACTOR

import java.io.*

class Utils {

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        var thePath: String? = null
        val uri = contentURI.toString()
        Logger.e("uri", "" + uri)
        if (uri.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].contains("content")) {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentURI, filePathColumn, null, null, null)
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                thePath = cursor.getString(columnIndex)
            }
            cursor.close()
            if (thePath == null) {
                Logger.e(Thread.currentThread(), "no path found")
                thePath = "no-path-found"
                var pictureBitmap: Bitmap? = null
                var mBitmap1: Bitmap? = null
                var inputStream: InputStream? = null
                try {
                    inputStream = context.contentResolver.openInputStream(contentURI)
                    if (inputStream != null) {
                        pictureBitmap = BitmapFactory.decodeStream(inputStream)
                        val qualityFactor: Int //typically 100, but we cant degrade the quality till X (depending on quality of Camera).
                        val width = pictureBitmap!!.width
                        val height = pictureBitmap.height

                        // Figuring out whether the image is horizontal or portrait mode
                        if (width > height) {
                            // Scaling it to proper height so that it wont't be skewed.
                            val MAX_IMAGE_HEIGHT = height * MAX_IMAGE_DIMENSION / width
                            mBitmap1 = Bitmap.createScaledBitmap(
                                pictureBitmap,
                                MAX_IMAGE_DIMENSION,
                                MAX_IMAGE_HEIGHT,
                                false
                            )
                            qualityFactor = QUALITY_FACTOR
                        } else {
                            val MAX_IMAGE_WIDTH = width * MAX_IMAGE_DIMENSION / height
                            mBitmap1 = Bitmap.createScaledBitmap(
                                pictureBitmap,
                                MAX_IMAGE_WIDTH,
                                MAX_IMAGE_DIMENSION,
                                false
                            )
                            qualityFactor = QUALITY_FACTOR + 20

                        }
                        val fileCompressed =
                            getOutputMediaFile(context as Activity, MEDIA_TYPE_IMAGE)
                        val fOutputStream =
                            BufferedOutputStream(FileOutputStream(fileCompressed) as OutputStream?)
                        mBitmap1!!.compress(
                            Bitmap.CompressFormat.JPEG,
                            qualityFactor,
                            fOutputStream
                        )
                        fOutputStream.flush()
                        fOutputStream.close()
                        thePath = fileCompressed.getAbsolutePath()
                    }
                } catch (e: Exception) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    if (pictureBitmap != null) {
                        try {
                            pictureBitmap.recycle()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    if (mBitmap1 != null) {
                        try {
                            mBitmap1.recycle()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        } else if (uri.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].contains(
                "file"
            )
        ) {
            thePath = uri.replace("file://", "")
        }
        return thePath ?: "no-path-found"
    }

    fun getOutputMediaFileUri(type: Int, context: Context): CameraPath? {
        var temp: File? = null
        val cameraPath: CameraPath? = CameraPath()

        try {
            temp = getOutputMediaFile(context as Activity?, type)
            if (temp != null)
                cameraPath?.currentPath = temp.path
            else
                Logger.e(Thread.currentThread(), "file null")
        } catch (ex: Exception) {
            Logger.e(Thread.currentThread(), "error occurred")
            Toast.makeText(context, context.getString(R.string.unexpectedError), Toast.LENGTH_LONG)
                .show()
        }

        Logger.e(Thread.currentThread(), "path: " + cameraPath?.currentPath + " " + temp?.path)
        return when (temp) {
            null -> null
            else -> {
                cameraPath?.uri = context.let {
                    FileProvider.getUriForFile(
                        it,
                        context.applicationContext?.packageName + ".provider", temp
                    )
                }
                cameraPath
            }
        }
    }

}