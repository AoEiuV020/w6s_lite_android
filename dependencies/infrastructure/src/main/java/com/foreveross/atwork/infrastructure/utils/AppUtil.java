package com.foreveross.atwork.infrastructure.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

/**
 *  跟App相关的辅助类
 * Created by ReyZhang on 2015/5/6.
 */
public class AppUtil {

    //工具类，不允许实例化
    private AppUtil() {
        /**cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        return getAppName(context, "");
    }

    public static String getAppName(Context context, String packName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            if (TextUtils.isEmpty(packName)) {
                packName = context.getPackageName();
            }
            applicationInfo = packageManager.getApplicationInfo(packName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return null;
        }
        return packageInfo.versionName;
    }

    /**
     * 获取到versionCode
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return 0;
        }
        return packageInfo.versionCode;
    }

    /**
     * 获取packageName
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return "";
        }
        return packageInfo.packageName;
    }

    @Nullable
    private static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo;

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public static void unInstallApp(Activity context, String pkgName) {
        Intent i = new Intent();
        Uri uri = Uri.parse("package:" +pkgName);//获取删除包名的URI
        i.setAction(Intent.ACTION_DELETE);//设置我们要执行的卸载动作
        i.setData(uri);//设置获取到的URI
        context.startActivity(i);
    }



    public static String getAPKPkgName(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.applicationInfo.packageName;
        }
        return "";
    }


    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}
