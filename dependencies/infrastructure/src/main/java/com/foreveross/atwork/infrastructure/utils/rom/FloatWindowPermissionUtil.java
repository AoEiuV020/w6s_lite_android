package com.foreveross.atwork.infrastructure.utils.rom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by dasunsy on 16/9/6.
 */
public class FloatWindowPermissionUtil {

    public static final int COMMON_REQUEST_FLOAT_CODE = 1213;

    private static final String TAG = "POP_PERMISSIONS";

    /**
     * 判断悬浮窗权限
     * @param context
     * @return
     */
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            return (context.getApplicationInfo().flags & 1 << 27) == 1 << 27;
        }
    }


    /**
     * 申请悬浮窗权限
     * @param fragment
     */
    public static void requestFloatPermission(Fragment fragment) {
        if (fragment.getContext() == null) {
            return;
        }
        Context context = fragment.getContext().getApplicationContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (RomUtil.isHuawei()) {
                HuaweiRomUtils.applyPermission(context);
            }
            if (RomUtil.isXiaomi()) {
                XiaomiRomUtils.applyPermission(context);
            }
            if (RomUtil.isMeizu()) {
                MeizuRomUtils.applyPermission(fragment);
            }
            if (RomUtil.isOppo()) {
                OppoRomUtils.applyPermission(context);
            }
            commonRomPermissionApply(fragment);
            return;
        }
        commonRomPermissionApply(fragment);

    }




    public static void commonRomPermissionApply(Fragment fragment) {
        if (RomUtil.isMeizu()) {
            MeizuRomUtils.applyPermission(fragment);
        }
        try {
            Intent intent = getPermissionIntent(fragment.getContext());
            /** request permission via start activity for result */
            fragment.startActivityForResult(intent, COMMON_REQUEST_FLOAT_CODE);
        } catch (Exception e) {
            Toast.makeText(fragment.getContext().getApplicationContext(), "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    public static void commonRomPermissionApply(Activity activity) {
        if (RomUtil.isMeizu()) {
            MeizuRomUtils.applyPermission(activity);
        }
        try {
            Intent intent = getPermissionIntent(activity);
            /** request permission via start activity for result */
            activity.startActivityForResult(intent, COMMON_REQUEST_FLOAT_CODE);
        } catch (Exception e) {
            Toast.makeText(activity.getApplicationContext(), "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    public static void commonRomPermissionApply(Context context) {
        try {
            Intent intent = getPermissionIntent(context);
            /** request permission via start activity for result */
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }

    private static Intent getPermissionIntent(Context context) {
        return  new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getApplicationContext().getPackageName()));
    }

    /**
     *
     * @param context
     * @param op
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {

                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());

                Log.e(TAG, AppOpsManager.MODE_ALLOWED + " invoke " + property);

                return AppOpsManager.MODE_ALLOWED == property;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }
}
