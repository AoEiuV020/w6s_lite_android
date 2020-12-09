package com.foreveross.atwork.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.modules.voip.component.BaseVoipFloatCallView;

/**
 * Created by dasunsy on 2017/9/16.
 */

public class FloatWinHelper {

    public static boolean canPopFloatWin(Context context) {
        boolean canPopFloatWin = false;
        if ((RomUtil.isMeizu() || isXiaomiNeedFloatPermissionCheck())) {
            if (FloatWindowPermissionUtil.isFloatWindowOpAllowed(context)) {
                canPopFloatWin = true;
            }

        }
        /** check if we already  have permission to draw over other apps */
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)) {
            canPopFloatWin = true;
        }

        return canPopFloatWin;
    }



    public static void setFloatType(WindowManager.LayoutParams wmParams) {
        if(Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            return;
        }


        if(Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            return;
        }


        if (RomUtil.isXiaomi()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;

            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }

        } else if(RomUtil.isVivo()){
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;

            BaseVoipFloatCallView.sIsModeNeedScreenControl = true;

        } else if(RomUtil.isOppo()) {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;

        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        }
    }


    public static boolean isXiaomiNeedFloatPermissionCheck() {
        return RomUtil.isXiaomi() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isMIUICustomFloatPermissionCheckImpl();
    }


    private static boolean isMIUICustomFloatPermissionCheckImpl() {
        return RomUtil.isMIUIV5() || RomUtil.isMIUIV6() ||
                RomUtil.isMIUIV7() || RomUtil.isMIUIV8();
    }



}
