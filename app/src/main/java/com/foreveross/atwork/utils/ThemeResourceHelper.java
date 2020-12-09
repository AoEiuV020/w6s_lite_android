package com.foreveross.atwork.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.theme.manager.SkinMaster;
import com.foreveross.theme.model.Theme;

/**
 * Created by dasunsy on 2016/10/10.
 */

public class ThemeResourceHelper {

    @NonNull
    public static String getCurrentThemeResourcePath(String image, boolean needAssetPrefix) {
        Theme currentTheme = SkinMaster.getInstance().getCurrentTheme();
        return getThemeResourcePath(image, currentTheme, needAssetPrefix);
    }

    @NonNull
    public static String getThemeResourcePath(String image, Theme theme, boolean needAssetPrefix) {
        String path = "theme/system/" + theme.mName + "/" + image + ".png";
        if(needAssetPrefix) {
            return "assets://" + path;
        }
        return path;
    }

    public static void setChatRightViewColorBg9Drawable(View view) {
        String resourcePath = ThemeResourceHelper.getChatRightColorBgResourcePath();

        setViewThemeBackground9Drawable(view, resourcePath);
    }

    public static void setViewThemeBackground9Drawable(View view, String resourcePath) {
//        if(!StringUtils.isEmpty(resourcePath)) {
//            Bitmap bitmap = ImageCacheHelper.loadImageSync(resourcePath, ImageCacheHelper.getDefaultImageOptions(false, false, false));
//            if(null != bitmap) {
//                ImageViewUtil.setViewBgNineDrawable(view, bitmap);
//
//            }
//
//        }
    }

    public static void setChatRightViewWhiteBgDrawable(View view) {
        String resourcePath = ThemeResourceHelper.getChatRightWhiteBgResourcePath();

        setViewThemeBackgroundDrawable(view, resourcePath);
    }

    public static void setViewThemeBackgroundDrawable(View view, String resourcePath) {
//        if(!StringUtils.isEmpty(resourcePath)) {
//            Bitmap bitmap = ImageCacheHelper.loadImageSync(resourcePath, ImageCacheHelper.getDefaultImageOptions(false, false, false));
//            if(null != bitmap) {
//                BitmapDrawable drawable = new BitmapDrawable(view.getResources(), bitmap);
//
//                ViewCompat.setBackground(view, drawable);
//            }
//
//        }
    }


    public static String getChatRightColorBgResourcePath() {
        Theme currentTheme = SkinMaster.getInstance().getCurrentTheme();

        if(null == currentTheme) {
            return StringUtils.EMPTY;
        }

        if(DensityUtil.getDensity() > 320) {
            return getThemeResourcePath("chat_bg_color_sender_x.9", currentTheme, true);

        } else {
            return getThemeResourcePath("chat_bg_color_sender_h.9", currentTheme, true);
        }
    }

    public static String getChatRightWhiteBgResourcePath() {
        Theme currentTheme = SkinMaster.getInstance().getCurrentTheme();

        if(null == currentTheme) {
            return StringUtils.EMPTY;
        }

        if(DensityUtil.getDensity() > 320) {
            return getThemeResourcePath("chat_bg_white_sender_x", currentTheme, true);

        } else {
            return getThemeResourcePath("chat_bg_white_sender_h", currentTheme, true);

        }

    }


    @Nullable
    public static Drawable getThemeResourceBitmapDrawable(Context context, String resourceName) {
        return getThemeResourceBitmapDrawable(context, resourceName, -1);
    }


    @Nullable
    public static Drawable getThemeResourceBitmapDrawable(Context context, String resourceName, float size) {
        BitmapDrawable bitmapDrawable = null;
        Bitmap bitmap = getThemeResourceBitmap(resourceName);
        if(null != bitmap) {
            //缩放操作
            if (-1 != size) {
                int bitmapheight = bitmap.getHeight();
                int bitmapwidth = bitmap.getWidth();
                Matrix matrix = new Matrix();
                float scale = size / bitmapheight;
                matrix.postScale(scale, scale); // 长和宽放大缩小的比例
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
                        bitmapheight, matrix, true);
            }

            bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        } else {
            int resId = context.getResources().getIdentifier(resourceName, "mipmap", context.getPackageName());
            bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, resId);
        }

        return bitmapDrawable;
    }

    @Nullable
    public static Bitmap getThemeResourceBitmap(String resourceName) {

        Theme currentTheme = SkinMaster.getInstance().getCurrentTheme();

        if(null == currentTheme) {
            return null;
        }

        String assetPath = getThemeResourcePath(resourceName, currentTheme, true);

        Bitmap bitmap = null;

        if(!StringUtils.isEmpty(assetPath)) {
            bitmap = ImageCacheHelper.loadImageSync(assetPath, ImageCacheHelper.getDefaultImageOptions(false, false, false));
        }

        return bitmap;
    }
}
