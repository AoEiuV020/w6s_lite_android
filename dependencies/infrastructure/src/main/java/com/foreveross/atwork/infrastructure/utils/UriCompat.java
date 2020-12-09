package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Created by dasunsy on 2016/12/20.
 */

public class UriCompat {

    public static Uri getFileUriCompat(Context context, File file) {
        Uri uri;
        if(24 <= Build.VERSION.SDK_INT) {
            uri = getUriForFile(context, file);

        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, getFileProviderName(context), file);
    }


    private static String getFileProviderName(Context context) {
        return AppUtil.getPackageName(context) + ".fileProvider";
    }


}
