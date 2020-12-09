package com.foreveross.atwork.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 媒体中心工具类，主要处理语音视频图片文件
 * Created by ReyZhang on 2015/5/7.
 */
public class MediaCenterUtils {

    /**
     * 保存bitmap到文件
     */
    public static boolean saveBitmapToFile(String filePath, Bitmap bitmap) {
        if (bitmap == null)
            return false;
        File bitmapFile = new File(filePath);
        try {
            if (bitmapFile.exists()) {
                bitmapFile.delete();
            }
            bitmapFile.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
