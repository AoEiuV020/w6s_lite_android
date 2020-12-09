package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.os.Build;

/**
 * Created by dasunsy on 2016/11/10.
 */

public class CustomerHelper {


    public static boolean isWorkplusV4(Context context) {
        return "com.foreverht.workplus.v4".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isTest(Context context) {
        return "com.foreveross.atwork.test".equalsIgnoreCase(AppUtil.getPackageName(context))
                || "com.foreverht.workplus.test".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isDev(Context context) {
        return "com.foreveross.atwork.dev".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isCimc(Context context) {
        return "com.foreveross.eim.android".equalsIgnoreCase(AppUtil.getPackageName(context))
                || "com.foreveross.eim.android.test".equalsIgnoreCase(AppUtil.getPackageName(context));

    }


    public static boolean isKwg(Context context) {
        return "com.foreveross.workplus.kwg".equalsIgnoreCase(AppUtil.getPackageName(context))
                || "com.foreveross.workplus.kwg.test".equalsIgnoreCase(AppUtil.getPackageName(context));

    }

    public static boolean isHcbm(Context context) {
        return AppUtil.getPackageName(context).contains("hcbm");

    }


    public static boolean isRfchina(Context context) {
        return isRfChinaTest(context) || isRfChinaRelease(context);

    }

    public static boolean isRfChinaTest(Context context) {
        return "com.foreveross.atwork.rfchina.test".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isRfChinaRelease(Context context) {
        return "com.RFChinaAndroid.mOffice".equalsIgnoreCase(AppUtil.getPackageName(context))
                || "com.RFChinaAndroid.mOffice.v4".equalsIgnoreCase(AppUtil.getPackageName(context))
                || "com.RFChinaAndroid.mOffice.v4.release".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isXtbank(Context context) {
        return "com.foreveross.atwork.xtbank".equalsIgnoreCase(AppUtil.getPackageName(context));

    }


    public static boolean isKnowfuture(Context context) {
        return "com.foreverht.workplus.knowfuture".equalsIgnoreCase(AppUtil.getPackageName(context));

    }

    public static boolean isH3c(Context context) {
        return AppUtil.getPackageName(context).startsWith("com.foreveross.atwork.h3c");

    }

    public static boolean isNmgat(Context context) {
        return "com.foreveross.workplus.nmgat".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isAlog(Context context) {
        return "com.workplus.alog".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isYsky(Context context) {
        return "com.foreveross.atwork.yskj".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isYskyTest(Context context) {
        return "com.foreveross.atwork.yskj.test".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isYueHai(Context context) {
        return "com.foreverht.workplus.yuehai".equalsIgnoreCase(AppUtil.getPackageName(context));
    }


    public static boolean isOct(Context context) {
        return "com.foreveross.atwork.oct".equalsIgnoreCase(AppUtil.getPackageName(context))
                || "com.foreverht.oct.test".equalsIgnoreCase(AppUtil.getPackageName(context));

    }

    public static boolean isH3CFangZhou(Context context) {
        return "com.foreveross.atwork.h3c.fangzhou".equalsIgnoreCase(AppUtil.getPackageName(context))
                || "com.foreveross.atwork.h3c.fangzhou_test".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isMinJie(Context context) {
        return "com.foreverht.workplus.minjietest".equalsIgnoreCase(AppUtil.getPackageName(context)) ||
                "com.foreverht.workplus.minjie".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isRuYuan(Context context) {
        return "com.foreverht.workplus.ruyuan".equalsIgnoreCase(AppUtil.getPackageName(context));
    }


    public static boolean isNewland(Context context) {
//        return isTest(context);
        return "com.foreverht.newlandtest.workplus".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean moveWorkBenchToNext(Context context) {
        return isRuYuan(context);
    }

    /**
     * 客户高安全性的打包，全局水印(忽略局部配置水印)， 不允许截屏
     * @param context
     * @return
     */
    public static boolean isHighSecurity(Context context) {
        return "com.foreverht.workplus.onet".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean isSinoSteel(Context context) {
        return "com.foreveross.atwork.sinosteel".equalsIgnoreCase(AppUtil.getPackageName(context))  || "com.foreverht.workplus.zgtest".equalsIgnoreCase(AppUtil.getPackageName(context));
    }

    public static boolean useSystemWebViewDevice() {
        String brand = Build.BRAND;
        String model = Build.MODEL;
        if ("HUAWEI".equalsIgnoreCase(brand)) {
            return "TAG-AL00".equalsIgnoreCase(model) || "TAG-TL00".equalsIgnoreCase(model) || "TIT-AL00".equalsIgnoreCase(model)
                    || "CUN-AL00".equalsIgnoreCase(model) || "TIT-CL10".equalsIgnoreCase(model);
        }
        if ("OPPO".equalsIgnoreCase(brand)) {
            return "A59S".equalsIgnoreCase(model) || "R9M".equalsIgnoreCase(model) || "R9TM".equalsIgnoreCase(model)
                    || "A37M".equalsIgnoreCase(model) || "A59M".equalsIgnoreCase(model) || "X9007".equalsIgnoreCase(model)
                    || "A37T".equalsIgnoreCase(model);
        }
        if ("VIVO".equalsIgnoreCase(brand)) {
            return "X6D".equalsIgnoreCase(model) || "X6PLUS D".equalsIgnoreCase(model) || "V3M A".equalsIgnoreCase(model);
        }
        if ("SAMSUNG".equalsIgnoreCase(brand)) {
            return "SM-J3199".equalsIgnoreCase(model);
        }

        return false;
    }
}
