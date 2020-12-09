package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by dasunsy on 2016/12/12.
 */

public class UriUtil {

    public static String getPathFromURI(Context context, Uri contentUri) {
        String filePath = contentUri.getPath();
        if(!FileUtil.isExist(filePath)) {
            filePath = getRealPathFromURI(context, contentUri);
        }

        return filePath;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        String path = StringUtils.EMPTY;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return path;

    }

}
