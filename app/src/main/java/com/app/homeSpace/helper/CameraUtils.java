package com.app.homeSpace.helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.app.homeSpace.R;
import com.crashlytics.android.Crashlytics;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.app.homeSpace.helper.Constants.MAX_IMAGE_DIMENSION;
import static com.app.homeSpace.helper.Constants.QUALITY_FACTOR;
import static java.lang.Thread.currentThread;

public class CameraUtils {

    public static String getUnixTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }

    public static long getInternalStorageSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        long mb = (availableBlocks * blockSize) / (1024 * 1024);
        Logger.e(currentThread(), "size - " + mb + " MB");
        return mb;
    }

    public static boolean checkCameraHardware(Activity activity) {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    public static String getCompressedImageNSaveCamDetails(Activity activity, String mOlderPath,
                                                           String accx, String accy, String accz) {

        // Decoding path
        Bitmap mBitmap = BitmapFactory.decodeFile(mOlderPath);
        String mCompressedPath = "";
        Logger.e(currentThread(), "Image: path: " + mOlderPath);
//        Crashlytics.logException(new Exception(Thread.currentThread() + " Image: path: " + mOlderPath));

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
            mCompressedPath = mOlderPath.substring(0, mOlderPath.indexOf(".jpg")) + "_" + String.valueOf(qualityFactor) + ".jpg";
            File fileCompressed = new File(mCompressedPath);

//            Crashlytics.logException(new Exception(Thread.currentThread() + " mCompressedPath_is: " + mCompressedPath + " and File exist" + fileCompressed.exists()));

            Logger.e(Thread.currentThread(), "compressesedPath: " + mCompressedPath + " and  File Exist:" + fileCompressed.exists());

            try {
                OutputStream fOutputStream = new BufferedOutputStream(new FileOutputStream(fileCompressed));

                mBitmap1.compress(Bitmap.CompressFormat.JPEG, qualityFactor, fOutputStream);
                fOutputStream.flush();
                fOutputStream.close();

                if (!accx.equals("0") || !accy.equals("0") || !accz.equals("0")) {
                    // extracting information from actual image
                    ExifInterface exif = new ExifInterface(mOlderPath);


                    String focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                    String exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
                    String whiteBalance = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                    String imageWidth = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                    String imageHeight = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                    String flash = exif.getAttribute(ExifInterface.TAG_FLASH);
                    String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    String aperture = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE);

//                    PrefManager.setCameraDetails(new CameraProvider(focalLength, exposureTime,
//                            whiteBalance, imageWidth, imageHeight, flash, orientation, aperture, accx, accy, accz));
                }

                deleteTempFile(mOlderPath);
                Logger.e(currentThread(), "path" + mCompressedPath + "\ncompressed size: " + String.valueOf(fileCompressed.length()));
            } catch (FileNotFoundException e) {
                if (Constants.DEBUG_MODE)
                    e.printStackTrace();
                Crashlytics.logException(e);
                Logger.e(Thread.currentThread(), "1. catch exception mCompressedPath_is: " + e.toString());
                Toast.makeText(activity, activity.getString(R.string.imageSaveFailed), Toast.LENGTH_SHORT).show();
                return null;
            } catch (IOException e) {
                if (Constants.DEBUG_MODE)
                    e.printStackTrace();
                Crashlytics.logException(e);
                Toast.makeText(activity, activity.getString(R.string.imageSaveFailed), Toast.LENGTH_SHORT).show();
                Logger.e(Thread.currentThread(), "2. catch exception mCompressedPath_is: " + e.toString());
                return null;
            } finally {
                mBitmap.recycle();
                mBitmap1.recycle();
                DeleteLastImageId(activity);
            }
        }
        return mCompressedPath;
    }

    public static void deleteTempFile(String mPath) {

        if (mPath != null && !mPath.equalsIgnoreCase("") && mPath.contains("Buyer DTM")) {

            File fdelete = new File(mPath);
            // Beauty of "a && b" operation is it will only do 'b' when 'a' is true.
            // So, don't worry about the deletion of non existing file.
            if (fdelete.exists() && fdelete.delete()) {
                Logger.e("TaskActivity", "file Deleted :" + mPath);
            } else {
                Logger.e("TaskActivity", "file not Deleted :" + mPath);
            }
        }
    }

    public static void DeleteLastImageId(Activity activity) {
        Logger.e("last image method ", "called");
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.DATE_MODIFIED};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        Logger.e("cursor count ", "" + imageCursor.getCount());
        if (imageCursor.moveToFirst()) {
            int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String date = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
            String dateAdded = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            String dateModified = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            if (dateModified != null) {

                long diff = (new Date().getTime() / 1000) - Long.parseLong(dateModified);
                Logger.e("diff in seconds ", "" + diff);
                Logger.e("full path", fullPath);

                if (diff < 5) {

                    removeImage(activity, id);
                }
            }

            imageCursor.close();
        }
    }

    private static void removeImage(Activity activity, int id) {
        ContentResolver cr = activity.getContentResolver();
        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{Long.toString(id)});
    }

    public static File getOutputMediaFile(Activity activity, int type) {

        File mediaFile = null;

        if (isExternalStorageWritable()) {
            // yes memory is present for R/W operations..

            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state))
                return null;

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), activity.getString(R.string.app_name));

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
                        Logger.e(currentThread(), "folder already exists");
                    }
                } else {
                    Logger.e(currentThread(), "make dir unsuccessful");
                }
            } else {
                Logger.e(currentThread(), "external media doesn't exist");

                File mediaStorageInternal = new File(activity.getFilesDir(), "KisanNetwork");
                if (!mediaStorageInternal.mkdir()) {

                    // check private storage
                    mediaStorageInternal = new File(activity.getCacheDir(), "KisanNetwork");
                    if (!mediaStorageInternal.exists()) {
                        if (!mediaStorageInternal.mkdir()) {
                            return null;
                        }
                    } else {
                        Logger.e(currentThread(), "internal folder already exists");
                    }
                } else {
                    Logger.e(currentThread(), "make internal dir unsuccessful");
                }
            }

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

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }



}
