package com.app.homeSpace.helper;

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