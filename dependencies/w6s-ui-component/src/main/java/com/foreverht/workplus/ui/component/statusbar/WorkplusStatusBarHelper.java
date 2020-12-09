package com.foreverht.workplus.ui.component.statusbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.foreverht.workplus.ui.component.R;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;

/**
 * Created by dasunsy on 2017/11/30.
 */

public class WorkplusStatusBarHelper {


    public static boolean isStatusBarLegal() {
        if(Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT) {
            return false;
        }


        if(Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            return true;
        }

        if(RomUtil.isMeizu()) {
            return true;
        }

        if(RomUtil.isXiaomi()) {
            return true;
        }

        return false;
    }

    public static void setDialogFragmentStatusBar(ViewGroup viewGroup, Window window) {
        boolean result = StatusBarUtil.setStatusBarMode(window, true);

        if(result) {
            StatusBarUtil.setColorNoTranslucent(viewGroup, window, ContextCompat.getColor(viewGroup.getContext(), R.color.white));
        } else {
            StatusBarUtil.setColorNoTranslucent(viewGroup, window, Color.parseColor("#666666"));
        }
    }

    /**
     * 设置公共的statusBar, 白底黑字(不兼容的情况使用 #666666 颜色值)
     * */
    public static void setCommonStatusBar(Activity activity) {
        if(null == activity) {
            return;
        }

        boolean result = StatusBarUtil.setStatusBarMode(activity, true);

        if(result) {
            StatusBarUtil.setColorNoTranslucent(activity, ContextCompat.getColor(activity, R.color.white));
        } else {
            StatusBarUtil.setColorNoTranslucent(activity, Color.parseColor("#666666"));
        }
    }

    public static void setCommonFullScreenStatusBar(Activity activity, boolean darkmode) {
        if(null == activity) {
            return;
        }

        if(StatusBarUtil.supportStatusBarMode()) {
            StatusBarUtil.setTransparentFullScreenAndStatusBar(activity.getWindow(), darkmode);

        } else {
            StatusBarUtil.setColorNoTranslucent(activity, Color.parseColor("#666666"));

        }
    }

    public static void setTransparentFullScreen(Window window) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);


            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


    }





    /**
     * 设置应用中心的 statusBar
     * */
    public static void setAppBlueStatusBar(Activity activity) {
        StatusBarUtil.setStatusBarMode(activity, true);
        int color = ContextCompat.getColor(activity, R.color.common_blue_bg);
        StatusBarUtil.setColor(activity, color, 20);
    }
}
