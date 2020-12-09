package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by dasunsy on 2016/12/13.
 */

public class CameraUtil {
    public static boolean checkCameraFront(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

}
