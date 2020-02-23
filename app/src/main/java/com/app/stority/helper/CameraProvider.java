package com.app.stority.helper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Details container of camera.
 */

public class CameraProvider implements Parcelable {

    private String focalLength, exposureTime, whiteBalance,
            imageWidth, imageHeight, flash, orientation, aperture,
            accX, accY, accZ,exifOrientation;
    private String imageUrl;

    public CameraProvider(String focalLength, String exposureTime, String whiteBalance,
                          String imageWidth, String imageHeight, String flash, String orientation,
                          String aperture, String accX, String accY, String accZ, String imageUrl, String exifOrientation) {

        this.focalLength = focalLength;
        this.exposureTime = exposureTime;
        this.whiteBalance = whiteBalance;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.flash = flash;
        this.orientation = orientation;
        this.aperture = aperture;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.imageUrl=imageUrl;
        this.exifOrientation=exifOrientation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFocalLength() {
        return this.focalLength;
    }

    public String getExposureTime() {
        return this.exposureTime;
    }

    public String getWhiteBalance() {
        return this.whiteBalance;
    }

    public String getImageWidth() {
        return this.imageWidth;
    }

    public String getImageHeight() {
        return this.imageHeight;
    }

    public String getFlash() {
        return this.flash;
    }

    public String getOrientation() {
        return this.orientation;
    }

    public String getAperture() {
        return this.aperture;
    }

    public String getAccX() {
        return this.accX;
    }

    public String getAccY() {
        return this.accY;
    }

    public String getAccZ() {
        return this.accZ;
    }

    public String getExifOrientation() {
        return exifOrientation;
    }

    // Parcelling part
    public CameraProvider(Parcel in){
        String[] data = new String[13];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.focalLength = data[0];
        this.exposureTime = data[1];
        this.whiteBalance = data[2];
        this.imageWidth=data[3];
        this.imageHeight=data[4];
        this.flash=data[5];
        this.orientation=data[6];
        this.aperture=data[7];
        this.accX=data[8];
        this.accY=data[9];
        this.accZ=data[10];
        this.imageUrl=data[11];
        this.exifOrientation=data[12];
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {
                this.focalLength,
                this.exposureTime,
                this.whiteBalance
                ,this.imageWidth,
                this.imageHeight,
                this.flash,
                this.orientation,
                this.aperture,
                this.accX,
                this.accY,
                this.accZ,
                this.imageUrl,
                this.exifOrientation});
    }
    public static final Creator CREATOR = new Creator() {
        public CameraProvider createFromParcel(Parcel in) {
            return new CameraProvider(in);
        }

        public CameraProvider[] newArray(int size) {
            return new CameraProvider[size];
        }
    };
}