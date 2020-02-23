package com.app.stority.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.app.stority.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import static android.content.Context.WINDOW_SERVICE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.app.stority.helper.CameraUtils.DeleteLastImageId;
import static com.app.stority.helper.CameraUtils.checkCameraHardware;
import static com.app.stority.helper.CameraUtils.deleteTempFile;
import static com.app.stority.helper.CameraUtils.getUnixTimeStamp;
import static com.app.stority.helper.Constants.DEBUG_MODE;
import static com.app.stority.helper.Constants.MAX_IMAGE_DIMENSION;
import static com.app.stority.helper.Constants.QUALITY_FACTOR;
import static java.lang.Thread.currentThread;

/**
 * Created by sreekuttancj on 10/02/18.
 */

public class Commons {

    private String mCurrentPath;
    private final int REQUEST_PERMISSION_SETTING = 101;

    private CameraInterface cameraInterface;

    /**
     * 0: off
     * 1: on
     */
    private static int FLASH_VALUE;

    public Commons(Activity activity) {
        cameraInterface = (CameraInterface) activity;
        cameraInterface.setCommon(this);
    }

    //A safe way to get an instance of the Camera object.
    public Camera getCameraInstance(final Activity activity, Camera camera) {
        // check camera present or not
        if (checkCameraHardware(activity)) {
            //check permission status
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                showPermissionWarningDialog(activity);
            } else {
                try {
                    //first clear camera
                    camera = stopPreviewAndFreeCamera(camera);
                    // attempt to get a Camera instance
                    camera = Camera.open();
                    cameraInterface.setCamera(camera);
                    //set camera parameters
                    Logger.i("check_call_flow", "called get camera params");
                    setCameraParameters();
                    //start camera preview
                    cameraInterface.getCameraPreview().setCamera();
                } catch (Exception e) {
                    if (DEBUG_MODE)
                        e.printStackTrace();
                }
                return camera;
            }
            return null;
        } else {
            Toast.makeText(activity, R.string.no_camera_warning, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * set params: size,orientation,flash,focus,format and quality
     */
    void setCameraParameters() {
        // get Camera parameters

        if (cameraInterface == null) {
            return;
        } else if (cameraInterface.getCamera() == null) {
            return;
        }

        Logger.i("check_null_case", "cameraInterface: " + cameraInterface + " camera: " + cameraInterface.getCamera());

        Camera.Parameters params = cameraInterface.getCamera().getParameters();
        Display display = ((WindowManager) Objects.requireNonNull(cameraInterface.getActivity().getSystemService(WINDOW_SERVICE))).getDefaultDisplay();

        //set size
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width) {
                size = sizes.get(i);
            }
        }
        params.setPictureSize(size.width, size.height);

        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);
        int cameraRotationOffset = camInfo.orientation;

        int degrees=0;
        int rotation=display.getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }


        int rotate = (360 + cameraRotationOffset - degrees) % 360;
        params.setRotation(rotate);

        Logger.e("paramRotation-",""+rotate);

//
//
//        if (display.getRotation() == Surface.ROTATION_0) {
//            Logger.i("check_orientation", "0");
////            params.setPreviewSize(height, width);
//            cameraInterface.getCamera().setDisplayOrientation(90);
//            params.setRotation(90);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_90) {
//            Logger.i("check_orientation", "90");
////            params.setPreviewSize(width, height);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_180) {
//            Logger.i("check_orientation", "180");
////            params.setPreviewSize(height, width);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_270) {
////            params.setPreviewSize(width, height);
//            Logger.i("check_orientation", "270");
//            cameraInterface.getCamera().setDisplayOrientation(180);
//            params.setRotation(180);
//        }

        // set the focus mode
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        //set flash mode
        if (FLASH_VALUE == 0)
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        else
            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

        //set format and quality
        params.setPictureFormat(PixelFormat.JPEG);
        params.setJpegQuality(85);

        //set parameter
        cameraInterface.getCamera().setParameters(params);

        enableZoom();
    }

    /**
     * Enables zoom feature in native camera .  Called from listener of the view
     * used for zoom in  and zoom out.
     *
     * @param zoomInOrOut "false" for zoom in and "true" for zoom out
     */
    private void zoomCamera(boolean zoomInOrOut) {
        if (cameraInterface.getCamera() != null) {
            Camera.Parameters parameter = cameraInterface.getCamera().getParameters();

            if (parameter.isZoomSupported()) {
                int MAX_ZOOM = parameter.getMaxZoom();
                int currnetZoom = parameter.getZoom();
                if (zoomInOrOut && (currnetZoom < MAX_ZOOM && currnetZoom >= 0)) {
                    parameter.setZoom(++currnetZoom);
                } else if (!zoomInOrOut && (currnetZoom <= MAX_ZOOM && currnetZoom > 0)) {
                    parameter.setZoom(--currnetZoom);
                }
            } else
                Toast.makeText(cameraInterface.getActivity(), "Zoom Not Available", Toast.LENGTH_LONG).show();

            cameraInterface.getCamera().setParameters(parameter);
        }
    }

    private void enableZoom() {

        ZoomControls zoomControls = new ZoomControls(cameraInterface.getActivity());
        zoomControls.setIsZoomInEnabled(true);
        zoomControls.setIsZoomOutEnabled(true);
        zoomControls.setOnZoomInClickListener(v -> {
            // TODO Auto-generated method stub
            zoomCamera(false);
        });
        zoomControls.setOnZoomOutClickListener(v -> {
            // TODO Auto-generated method stub

            zoomCamera(true);
        });
        cameraInterface.getCameraPreview().addView(zoomControls);
    }

    //get output file uri
    public File getOutputMediaFileUri(Activity activity, int type) {

        File temp = null;

        try {
            temp = getOutputMediaFile(activity, type);
            if (temp != null)
                mCurrentPath = temp.getPath();
            else
                Logger.i(currentThread().toString(), "file null");
        } catch (Exception ex) {
            Logger.e(currentThread().toString(), "error occurred");
            Toast.makeText(activity, activity.getString(R.string.unexpectedError), Toast.LENGTH_SHORT).show();
        }

        // TODO: 13/02/18 check the access
        //            return FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", temp);
        return temp;
    }

    // Create a File for saving an image or video
    private File getOutputMediaFile(Activity activity, int type) {

        File mediaFile = null;


        if (isExternalStorageWritable()) {
            // yes memory is present for R/W operations..

            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state))
                return null;

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), activity.getString(R.string.app_name));

            // Create the storage directory if it does not exist
            Logger.i("storage directory ", "" + mediaStorageDir.exists());
            Logger.i("state", Environment.getExternalStorageState());

            Logger.i("directory ", "" + mediaStorageDir.getAbsolutePath());

            Logger.i("isExternal", "" + isExternalStorageAvailable());


            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdir()) {

                    // check private storage
                    mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), activity.getString(R.string.app_name));
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdir()) {
                            return null;
                        }
                    } else {
                        Logger.i(currentThread(), "folder already exists");
                    }
                } else {
                    Logger.i(currentThread(), "make dir unsuccessful");
                }
            } else {
                Logger.i(currentThread(), "external media doesn't exist");

                File mediaStorageInternal = new File(activity.getFilesDir(), activity.getString(R.string.app_name));
                if (!mediaStorageInternal.mkdir()) {

                    // check private storage
                    mediaStorageInternal = new File(activity.getCacheDir(), activity.getString(R.string.app_name));
                    if (!mediaStorageInternal.exists()) {
                        if (!mediaStorageInternal.mkdir()) {
                            return null;
                        }
                    } else {
                        Logger.i(currentThread(), "internal folder already exists");
                    }
                } else {
                    Logger.i(currentThread(), "make internal dir unsuccessful");
                }
            }

            Logger.i("directory ", "" + mediaStorageDir.getAbsolutePath());

            // Create a media file cropName
            if (type == MEDIA_TYPE_IMAGE) {

                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "MS_" + getUnixTimeStamp() + ".jpg");
            } else {
                return null;
            }
        } else {
            Toast.makeText(activity, activity.getString(R.string.noMemory), Toast.LENGTH_SHORT).show();
            Logger.e(currentThread(), "no memory present");
        }
        return mediaFile;
    }

    // Checks if the storage is available for read and write
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    //check f the storage is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

    //get original image path
    public String getCurrentPath() {
        return mCurrentPath;
    }

    //show camera permission dialog if necessary
    private void showPermissionWarningDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.need_camera_permissions));
        builder.setMessage(activity.getResources().getString(R.string.camera_permission_description));
        builder.setPositiveButton(activity.getResources().getString(R.string.grant), (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            Toast.makeText(activity, activity.getResources().getString(R.string.go_to_grant_permissions), Toast.LENGTH_LONG).show();
            Toast.makeText(activity, activity.getResources().getString(R.string.go_to_grant_permissions), Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    //compress image
    public static String getCompressedImageNSaveCamDetails(Activity activity, String mPath) {

        // Decoding path
        Bitmap mBitmap = BitmapFactory.decodeFile(mPath);
        String mCompressedPath = "";
        Logger.i("Commons", "Image: path: " + mPath);

        Bitmap mBitmap1;
        if (mBitmap != null) {
            int qualityFactor; //typically 100, but we cant degrade the quality till X (depending on quality of Camera).
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            // Figuring out whether the image is horizontal or portrait mode
            if (width > height) {
                // Scaling it to proper height so that it wont't be skewed.
                int MAX_IMAGE_HEIGHT = height * MAX_IMAGE_DIMENSION / width;
                mBitmap1 = Bitmap.createScaledBitmap(mBitmap, MAX_IMAGE_DIMENSION, MAX_IMAGE_HEIGHT, false);
                qualityFactor = QUALITY_FACTOR;
            } else {
                int MAX_IMAGE_WIDTH = width * MAX_IMAGE_DIMENSION / height;
                mBitmap1 = Bitmap.createScaledBitmap(mBitmap, MAX_IMAGE_WIDTH, MAX_IMAGE_DIMENSION, false);
                qualityFactor = QUALITY_FACTOR + 20;

            }
            // writing it in sd card again, with the cropName appended by the quality factor.
            mCompressedPath = mPath.substring(0, mPath.indexOf(".jpg")) + "_" + qualityFactor + ".jpg";
            File fileCompressed = new File(mCompressedPath);

            try {
                OutputStream fOutputStream = new BufferedOutputStream(new FileOutputStream(fileCompressed));

                mBitmap1.compress(Bitmap.CompressFormat.JPEG, qualityFactor, fOutputStream);
                fOutputStream.flush();
                fOutputStream.close();

                deleteTempFile(mPath);

                Logger.i("Commons", "path" + mCompressedPath + "\ncompressed size: " + fileCompressed.length());
            } catch (FileNotFoundException e) {
                Toast.makeText(activity, activity.getString(R.string.imageSaveFailed), Toast.LENGTH_SHORT).show();
                return null;
            } catch (IOException e) {
                Toast.makeText(activity, activity.getString(R.string.imageSaveFailed), Toast.LENGTH_SHORT).show();
                return null;
            } finally {
                mBitmap1.recycle();
                mBitmap.recycle();
                DeleteLastImageId(activity);
            }
        }
        return mCompressedPath;
    }



    //free camera resources
    private Camera stopPreviewAndFreeCamera(Camera camera) {

        if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
            camera.stopPreview();

            camera.release();
        }
        return null;
    }

    //flash control
    public int handleFlash(ImageButton imageButtonFlash) {
        if (FLASH_VALUE == 0) {
            imageButtonFlash.setImageDrawable(cameraInterface.getActivity().getResources().getDrawable(R.drawable.avd_flash_on_dark));
            FLASH_VALUE = 1;
            setCameraParameters();

        } else {
            imageButtonFlash.setImageDrawable(cameraInterface.getActivity().getResources().getDrawable(R.drawable.avd_flash_off_dark));
            FLASH_VALUE = 0;
            setCameraParameters();
        }
        return FLASH_VALUE;
    }

    //get camera details
    public CameraProvider getExifInterface(String path, int orientation, String accx, String accy, String accz) {
        // extracting information from actual image
        try {
            // extracting information from actual image
            ExifInterface exif = new ExifInterface(path);
            String focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            String exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            String whiteBalance = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            String imageWidth = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String imageHeight = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String apertureLength = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
            String exifOrientation = String.valueOf(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED));
            String flash;
            String finalOrientation;

            //set focal length manually
            focalLength = ((focalLength == null) ? "0" : focalLength);
            String stringFocalLength = focalLength;
            if (focalLength.length() > 1) {
                try {
                    float num = Float.valueOf(focalLength.substring(0, focalLength.indexOf("/")));
                    float den = Float.valueOf(focalLength.substring(focalLength.indexOf("/") + 1));
                    stringFocalLength = String.valueOf(num / den);
                } catch (StringIndexOutOfBoundsException e) {
                    if (DEBUG_MODE)
                            e.printStackTrace();
                }
            }
            //set others manually
            exposureTime = ((exposureTime == null) ? "0" : exposureTime);
            whiteBalance = ((whiteBalance == null) ? "0" : whiteBalance);
            imageWidth = ((imageWidth == null) ? "0" : imageWidth);
            imageHeight = ((imageHeight == null) ? "0" : imageHeight);
            //set apertureLength manually
            apertureLength = ((apertureLength == null) ? "0" : apertureLength);
            String stringAperture = apertureLength;
            if (apertureLength.length() > 1) {
                try {

                    float num = Float.valueOf(apertureLength.substring(0, apertureLength.indexOf("/")));
                    float den = Float.valueOf(apertureLength.substring(apertureLength.indexOf("/") + 1));
                    stringAperture = String.valueOf(num / den);
                } catch (StringIndexOutOfBoundsException e) {
                    if (DEBUG_MODE)
                        e.printStackTrace();
                }
            }
            //set flash manually
            if (FLASH_VALUE == 0) {
                flash = "No";
            } else {
                flash = "Yes";
            }
            //set orientation manually
            if (orientation == 1 || orientation == 3) {
                finalOrientation = "horizontal";
            } else {
                finalOrientation = "vertical";
            }

            Logger.i("check_exifInterface", "focalLength: " + stringFocalLength + " exposureTime: " + exposureTime + " whiteBalance: " + whiteBalance + " imageWidth: " + imageWidth +
                    " imageHeight: " + imageHeight + " flash: " + flash + " orientation: " + finalOrientation + " aperture: " + stringAperture + " accx: " + accx + " accy: " + accy + " accz: " + accz);
            return new CameraProvider(stringFocalLength, exposureTime, whiteBalance, imageWidth, imageHeight, flash, finalOrientation, stringAperture, accx, accy, accz, path,exifOrientation);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}