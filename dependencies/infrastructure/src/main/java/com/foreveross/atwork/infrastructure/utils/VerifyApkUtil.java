package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class VerifyApkUtil {


    public static String getApkMd5(Context context){
        String apkPath = getApkPath(context);
        return MD5Utils.getDigest(apkPath);
    }

    public static String getApkPath(Context context) {
        return context.getPackageResourcePath();
    }

    public static boolean isApkPathLegal(Context context) {
        return !StringUtils.isEmpty(getApkPath(context));
    }


    public static long getDexCrc(Context context) {
        String apkPath = getApkPath(context);
        try {
            ZipFile zipfile = new ZipFile(apkPath);

            ZipEntry dexentry = zipfile.getEntry("classes.dex");

            return dexentry.getCrc();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;

    }
}
