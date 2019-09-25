package com.app.stority.helper;

import android.app.Activity;
import android.hardware.Camera;



public interface CameraInterface {

    void setCamera(Camera camera);

    Camera getCamera();

    CameraPreview getCameraPreview();

    Activity getActivity();

    void setCommon(Commons common);

    Commons getCommon();
}