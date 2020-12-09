package com.foreveross.atwork.qrcode.zxing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;

import com.foreveross.atwork.qrcode.zxing.camera.CameraManager;
import com.google.zxing.Result;


/**
 * Created by dasunsy on 15/12/9.
 */
public interface ZxingQrcodeInterface {
    Activity getQrActivity();

    CameraManager getCameraManager();

    Handler getHandler();

    void handleDecode(Result result, Bitmap barcode);
}
