package com.foreveross.atwork.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UriUtil;

/**
 * Created by dasunsy on 2016/12/12.
 */

public class CropHelper {


    /**
     * 部分ROM 调用系统crop 后返回的 intent 里面即没路径, 也没 data, 所以此处需要做个兼容处理, 把初始设置的 output 路径存进去,
     * 然后再获取 crop 返回来的 intent 时, 若没带任何东西, 则使用初始设置的 output.
     *
     * @param cropOutputPath 兼容部分rom, 可能用到的crop 地址
     * @param originalUri 原始丢给系统crop的uri数据, 用以作为结果筛选对比, 防止部分rom该uri当成data返回来
     *
     * @see {@link #getCropFilePath(Context, Intent)}
     * */
    public static void makeCropIntentCompatible(Intent data, String cropOutputPath, Uri originalUri) {
        data.putExtra("crop_output", cropOutputPath);
        data.putExtra("original_uri", originalUri);
    }

    public static String getCropFilePath(Context context, Intent data) {
        Uri fileUri = data.getData();
        Uri originalUri = data.getParcelableExtra("original_uri");

        String filePath = StringUtils.EMPTY;
        if (isDataUriLegal(fileUri, originalUri)) {
            filePath = UriUtil.getPathFromURI(context, fileUri);

        } else {

            Bundle extras = data.getExtras();
            if (null != extras) {
                Bitmap bm = extras.getParcelable("data");

                if (null != bm) {
                    filePath = AtWorkDirUtils.getInstance().getImageDir(context) + System.currentTimeMillis() + "_avatar.jpg";
                    MediaCenterUtils.saveBitmapToFile(filePath, bm);
                }
            }
        }

        if(StringUtils.isEmpty(filePath)) {
            filePath = data.getStringExtra("crop_output");
        }

        return filePath;
    }

    private static boolean isDataUriLegal(Uri fileUri, @Nullable Uri originalUri) {
        if(null == fileUri) {
            return false;
        }

        if(null != originalUri && originalUri.equals(fileUri)) {
            return false;
        }

        return true;


    }
}
