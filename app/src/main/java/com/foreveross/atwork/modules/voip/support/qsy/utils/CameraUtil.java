package com.foreveross.atwork.modules.voip.support.qsy.utils;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by RocXu on 2016/1/22.
 */
public class CameraUtil {
    private static final String TAG = "CameraUtil";

    public static boolean getCameraPermission() {
        Log.d(TAG, "getCameraPermission");

        Camera camera = null;
        boolean bIsOpen = false;

        try {
            // up to Android 2.3
            if (android.os.Build.VERSION.SDK_INT > 8) {
                Log.d(TAG, "getCameraPermission up to Android 2.3");

                int num = Camera.getNumberOfCameras();
                Log.d(TAG, "getCameraPermission: num " + num);

                for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, info);

                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        Log.d(TAG, "getCameraPermission Camera " + i + ", Facing back, Orientation " + info.orientation);
                    } else {
                        Log.d(TAG, "getCameraPermission Camera " + i + ", Facing front, Orientation " + info.orientation);
                    }

                    camera = Camera.open(i);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }

                bIsOpen = true;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to getCameraPermission ex" + ex.getLocalizedMessage());
            bIsOpen = false;
        }

        Log.d(TAG, "getCameraPermission Camera bIsOpen: " + bIsOpen);
        return bIsOpen;
    }

}
