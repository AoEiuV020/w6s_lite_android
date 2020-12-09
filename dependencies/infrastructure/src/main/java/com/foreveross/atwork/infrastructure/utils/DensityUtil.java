package com.foreveross.atwork.infrastructure.utils;

import android.content.res.Resources;

/**
 * Created by lingen on 15/3/27.
 * Description:
 */
public class DensityUtil {

    public static int DP_1O_TO_PX;
    public static int DP_8_TO_PX;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，
     */
    public static int sp2px(float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕密度
     * */
    public static int getDensity() {
        return Resources.getSystem().getDisplayMetrics().densityDpi;
    }


    public static float getDimen(int dimenId) {
        return Resources.getSystem().getDimension(dimenId) / Resources.getSystem().getDisplayMetrics().density;
    }
}
