package com.foreveross.atwork.infrastructure.utils.rom;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.IOUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by dasunsy on 16/9/6.
 */
public class RomUtil {

    private final static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private static final String KEY_EMUI_VERSION_NAME = "ro.build.version.emui";

    private static String sMiuiVersionName;
    private static String sFlymeVersionName;


    public static boolean isMeizu() {
        String meizuFlymeOSFlag  = getSystemProperty(KEY_FLYME_VERSION_NAME);
        if (TextUtils.isEmpty(meizuFlymeOSFlag)){
            return Build.MANUFACTURER.toLowerCase().contains("meizu");
        }else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")){
            return  true;
        }else {
            return false;
        }

    }

    public static boolean isHuawei() {
        return Build.MANUFACTURER.toLowerCase().contains("huawei");
    }

    public static boolean isSamsung() {
        return Build.MANUFACTURER.toLowerCase().contains("samsung");

    }

    public static String getPushTokenByRom(Context context) {
        if (isHuawei()) {
            return CommonShareInfo.getHuaweiPushToken(context);
        }
        if (isXiaomi()) {
            return CommonShareInfo.getXiaomiPushToken(context);
        }
        if (isMeizu()) {
            return CommonShareInfo.getMeiZuPushToken(context);
        }
        if (isOppo()) {
            return CommonShareInfo.getOPPOPushToken(context);
        }
        if (isVivo()) {
            return CommonShareInfo.getVIVOPushToken(context);
        }
        return "";
    }

    public static String getRomChannel() {
        if (isHuawei()) {
            return "HuaWei";
        }
        if (isXiaomi()) {
            return "XiaoMi";
        }
        if (isMeizu()) {
            return "MeiZu";
        }
        if (isOppo()) {
            return "Oppo";
        }
        if (isVivo()) {
            return "Vivo";
        }
        return "";
    }

    public static boolean isXiaomi() {
        String miuiVersionName = getSystemProperty(KEY_MIUI_VERSION_NAME);
        return TextUtils.isEmpty(miuiVersionName) ? Build.MANUFACTURER.toLowerCase().contains("xiaomi") : true;
    }

    public static boolean isVivo() {
        return Build.MANUFACTURER.toLowerCase().contains("vivo");
    }

    public static boolean isOppo() {
        return Build.MANUFACTURER.toLowerCase().contains("oppo");
    }

    public static boolean is360Rom() {
        return Build.MANUFACTURER.contains("QiKU") || Build.MANUFACTURER.contains("360");
    }


    private static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }



    static {

        long startTime = System.currentTimeMillis();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            Properties properties = new Properties();
            properties.load(fileInputStream);
            Class<?> clzSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
            // miui
            sMiuiVersionName =getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
            //flyme
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME);

            long endTime = System.currentTimeMillis();

            LogUtil.e("load SystemProperties duration ->  " + (endTime - startTime));

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("getProperty error");
        } finally {
            IOUtil.release(fileInputStream);
        }
    }

    /**
     * 获取 emui 版本号
     * @return
     */
    public static double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty(KEY_EMUI_VERSION_NAME);
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        String version = getSystemProperty(KEY_MIUI_VERSION_NAME);
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
            }
        }
        return -1;
    }



    @Nullable
    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Exception ignored) {}
        }
        if (name != null) name = name.toLowerCase();
        return name;
    }

    public static boolean isMIUIV5() {
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6() {
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7() {
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8() {
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9() {
        return "v9".equals(sMiuiVersionName);
    }





    public static boolean commandX5Rom() {

        String model = Build.MODEL;

        if (isVivo()) {
            return "vivo Y51A".equals(model);
        }

        return false;
    }


    public static boolean isSamsungS8() {
        if(isSamsung()) {
            return "SM-G9550".equals(Build.MODEL);
        }

        return false;
    }

}
