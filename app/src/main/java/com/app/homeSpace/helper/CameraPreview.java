package com.app.homeSpace.helper;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by sreekuttancj on 12/02/18.
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;
    private CameraInterface cameraInterface;
    private Activity activity;

    public CameraPreview(Context context, Activity activity, SurfaceView surfaceView) {
        super(context);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        this.activity=activity;
        cameraInterface = (CameraInterface) activity;
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //set camera preview
    public void setCamera() {
        if (cameraInterface.getCamera() != null) {

            try {
                cameraInterface.getCamera().setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            cameraInterface.getCamera().startPreview();
        }
    }

    //SURFACE PART
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logger.i("surface_check", "called surfaceCreated");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);
            int cameraRotationOffset = camInfo.orientation;
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees=0;
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
            int displayRotation = (cameraRotationOffset - degrees + 360) % 360;
            Logger.e("displayRotation-",""+displayRotation);
            cameraInterface.getCamera().setDisplayOrientation(displayRotation);
            cameraInterface.getCamera().setPreviewDisplay(holder);
            cameraInterface.getCamera().startPreview();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.i("surface_check", "called surfaceChanged");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            cameraInterface.getCamera().stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

//        Camera.Parameters parameters = camera.getParameters();
//        Display display = ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

//        if(display.getRotation() == Surface.ROTATION_0) {
//            parameters.setPreviewSize(height, width);
//            camera.setDisplayOrientation(90);
//            parameters.setRotation(90);
//        }
//
//        if(display.getRotation() == Surface.ROTATION_90) {
//            parameters.setPreviewSize(width, height);
//        }
//
//        if(display.getRotation() == Surface.ROTATION_180) {
//            parameters.setPreviewSize(height, width);
//        }
//
//        if(display.getRotation() == Surface.ROTATION_270) {
//            parameters.setPreviewSize(width, height);
//            camera.setDisplayOrientation(180);
//            parameters.setRotation(180);
//        }
        //set display orientation
//        camera.setDisplayOrientation(commons.getCorrectCameraOrientation(activity,new Camera.CameraInfo(),camera));
        //set out put image orientation
//        parameters.setRotation(commons.getCorrectCameraOrientation(activity,new Camera.CameraInfo(),camera));
//        camera.setParameters(parameters);

        // start preview with new settings
        try {
            cameraInterface.getCamera().setPreviewDisplay(surfaceHolder);
            cameraInterface.getCommon().setCameraParameters();
            cameraInterface.getCamera().setDisplayOrientation(90);
            cameraInterface.getCamera().startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.i("surface_check", "called surfaceDestroyed");
        // empty. Take care of releasing the Camera preview in your activity.

    }

    //PREVIEW SIZE PART
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

}