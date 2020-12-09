package com.foreveross.atwork.infrastructure.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * 屏幕设置相关类
 * Created by ReyZhang on 2015/5/7.
 */
public class ScreenUtils {


    public final static  int ORIENTATION_AUTO = 0;

    public final static  int ORIENTATION_LOCK_PORTRAIT = 1;

    public final static  int ORIENTATION_LOCK_LANDSCAPE = 2;

    private ScreenUtils() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static int[] sPortraitRealSizeCache = null;
    private static int[] sLandscapeRealSizeCache = null;

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public static void setBackgroundAlpha( Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取屏幕宽度
     * */
    public static int getScreenWidth(Context context){

        if(isAllScreenDevice(context)) {
            return getRealScreenSize(context)[0];

        }

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * */
    public static int getScreenHeight(Context context){

        if(isAllScreenDevice(context)) {
            return getRealScreenSize(context)[1];

        }


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    private static Boolean sIsAllScreenDevice = null;

    public static boolean isAllScreenDevice(Context context) {
        if (null != sIsAllScreenDevice) {
            return sIsAllScreenDevice;
        }

        return isAllScreenDeviceByRough(context);


    }

    private static boolean isAllScreenDeviceByRough(Context context) {
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }

        sIsAllScreenDevice = false;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                sIsAllScreenDevice = true;
            }
        }


        return sIsAllScreenDevice;
    }


    /**
     * 获取屏幕的真实宽高
     *
     * @param context
     * @return
     */

    public static int[] getRealScreenSize(Context context) {
        if (DeviceUtil.isEssentialPhone() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Essential Phone 8.0版本后，Display size 会根据挖孔屏的设置而得到不同的结果，不能信任 cache
            return doGetRealScreenSize(context);
        }
        int orientation = context.getResources().getConfiguration().orientation;
        int[] result;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            result = sLandscapeRealSizeCache;
            if (result == null) {
                result = doGetRealScreenSize(context);
                if(result[0] > result[1]){
                    // the result may be wrong sometimes, do not cache !!!!
                    sLandscapeRealSizeCache = result;
                }
            }
            return result;
        } else {
            result = sPortraitRealSizeCache;
            if (result == null) {
                result = doGetRealScreenSize(context);
                if(result[0] < result[1]){
                    // the result may be wrong sometimes, do not cache !!!!
                    sPortraitRealSizeCache = result;
                }
            }
            return result;
        }
    }


    private static int[] doGetRealScreenSize(Context context) {
        int[] size = new int[2];
        int widthPixels, heightPixels;
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
        try {
            // used when 17 > SDK_INT >= 14; includes window decorations (statusbar bar/menu bar)
            widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
            heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
        } catch (Exception ignored) {
        }
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                // used when SDK_INT >= 17; includes window decorations (statusbar bar/menu bar)
                Point realSize = new Point();
                d.getRealSize(realSize);


                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }

        size[0] = widthPixels;
        size[1] = heightPixels;
        return size;
    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 当前是否横屏
     * @param context
     * */
    public static boolean isLandscape(Context context) {
        Configuration cf= context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = cf.orientation ; //获取屏幕方向

        if(ori == Configuration.ORIENTATION_LANDSCAPE){
            return true;

        }

        return false;
    }

    /**
     * 横屏竖屏模式切换
     * @param activity
     * @param isLandScape 是否横屏
     * @param isLock 是否锁定, 去除自动旋转配置, 默认为 false
     *
     * @return 是否做了处理
     */
    public static boolean landscapeMode(Activity activity, @Nullable Boolean isLandScape, @Nullable Boolean isLock) {
        if(null == isLandScape && null == isLock) {
            return false;
        }

        //若控制旋转的参数为空, 则只设置手机是否跟随系统旋转设置
        if(null == isLandScape) {

            if (isLock) {
                isLandScape = ScreenUtils.isLandscape(activity);
                if (setScreenOrientationWithLock(activity, isLandScape)) return true;

            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                return true;
            }
            return false;
        }

        if(null == isLock) {
            isLock = false;
        }

        if(isLock) {
            if (setScreenOrientationWithLock(activity, isLandScape)) return true;

        } else {
            if (setScreenOrientationWithUnlock(activity, isLandScape)) return true;
        }

        return false;

    }

    private static boolean setScreenOrientationWithUnlock(Activity activity,  Boolean isLandScape) {
        setScreenOrientationWithLock(activity, isLandScape);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        return true;
    }

    private static boolean setScreenOrientationWithLock(Activity activity, Boolean isLandScape) {
        if(isLandScape) {
            if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE != activity.getRequestedOrientation()) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                return true;
            }
        } else {
            if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT != activity.getRequestedOrientation()) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return false;
    }


    public static boolean handleOrientation(Activity activity, Integer orientation) {
        if (null != orientation) {
            switch (orientation) {
                case ORIENTATION_AUTO:
                    return ScreenUtils.landscapeMode(activity, null, false);


                case ORIENTATION_LOCK_PORTRAIT:
                    return ScreenUtils.landscapeMode(activity, false, true);

                case ORIENTATION_LOCK_LANDSCAPE:
                    return ScreenUtils.landscapeMode(activity, true, true);
            }

        }

        return false;
    }

    public static void hideInput(final Activity activity, final EditText editText) {
        if (activity == null) {
            return;
        }
        activity.getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }, 200);

    }

    public static void hideInput(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        hideInput(weakReference);
    }
    /**
     * 隐藏键盘
     */
    public static void hideInput(WeakReference<Activity> activityWeakReference) {
        Activity activity = activityWeakReference.get();
        if (null != activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
