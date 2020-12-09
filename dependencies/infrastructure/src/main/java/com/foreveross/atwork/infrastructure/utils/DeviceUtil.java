package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;

import java.util.UUID;
import android.content.Context;
import android.content.res.Configuration;
/**
 * Created by dasunsy on 2017/12/19.
 */

public class DeviceUtil {

    private static Boolean sIsTabletValue = null;

    public static String getShowName() {
        return (Build.MANUFACTURER + " " + Build.MODEL).toUpperCase();
    }

    public static boolean isEssentialPhone(){
        return Build.BRAND.toLowerCase().contains("essential");
    }

    /**
     * 华为 HUAWEI P6-T00
     * */
    public static boolean isHUAWEI_P6_T00() {
        final String board = android.os.Build.MODEL;
        return board != null && board.toLowerCase().contains("huawei p6-t00");
    }


    public static boolean isSamsung_GT_N7100() {
        final String board = android.os.Build.MODEL;
        LogUtil.e("board   ----->   " + board);

        return board != null && board.toLowerCase().contains("gt-n7100");
    }

    /**
     * 判断是否为 ZUK Z1 和 ZTK C2016。
     * 两台设备的系统虽然为 android 6.0，但不支持状态栏icon颜色改变，因此经常需要对它们进行额外判断。
     */
    public static boolean isZUKZ1() {
        final String board = android.os.Build.MODEL;
        return board != null && board.toLowerCase().contains("zuk z1");
    }

    public static boolean isZTKC2016() {
        final String board = android.os.Build.MODEL;
        return board != null && board.toLowerCase().contains("zte c2016");
    }


    public static boolean isNubiaNX569H() {
        final String board = android.os.Build.MODEL;
        return board != null && board.toLowerCase().contains("nx569h");

    }

    /**
     * 判断是否为平板设备
     */
    public static boolean isTablet(Context context) {
        if(null == sIsTabletValue) {
            sIsTabletValue = checkIsTablet(context);

        }

        return sIsTabletValue;

    }

    private static boolean checkIsTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }




    public static void initDeviceId(Context context) {

        //已经存储了device_id, 无需再重新生成
        if(!StringUtils.isEmpty(CommonShareInfo.getDeviceId(context))) {
            AtworkConfig.setDeviceId(CommonShareInfo.getDeviceId(context));
            return;
        }

        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (androidId == null) {
            //获取不到设备ID，则以SERIAL
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                androidId = Build.getSerial();
            }  else {
                androidId = Build.SERIAL;

            }
        }

        if (androidId == null) {
            //依然a
            androidId = UUID.randomUUID().toString();
        }

        String deviceId = Base64Util.encode(androidId.getBytes()).toUpperCase();
        CommonShareInfo.setDeviceId(context, deviceId);
        AtworkConfig.setDeviceId(deviceId);
    }
}
