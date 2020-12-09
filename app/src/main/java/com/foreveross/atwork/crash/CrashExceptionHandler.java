package com.foreveross.atwork.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lingen on 15/4/9.
 * Description:
 */
public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {

    //设备厂商
    private static final String BOARD = "BOARD";

    //手机品牌
    private static final String BRAND = "BRAND";

    //设备型号
    private static final String DEVICE = "DEVICE";

    //ROM型号
    private static final String FINGERPRINT = "FINGERPRINT";

    //手机时间
    private static final String TIME = "TIME";

    //Android 版本号
    private static final String VERSION_SDK = "VERSION_SDK";

    private static final String VERSION_RELEASE = "VERSION_RELEASE";

    private CrashInfo crashInfo;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        collectDeviceInfo();

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        crashInfo.error = sw.toString();

        writeToLog(BaseApplicationLike.baseContext);
        exit();
    }

    /**
     * 收集设备信息
     */
    public void collectDeviceInfo() {
        crashInfo = new CrashInfo();
        try {
            PackageManager pm = BaseApplicationLike.baseContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(BaseApplicationLike.baseContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                crashInfo.appVersion = pi.versionCode;
                crashInfo.versionName = pi.versionName;
                crashInfo.packageName = pi.packageName;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        crashInfo.addDeviceInfo(BOARD, Build.BOARD);
        crashInfo.addDeviceInfo(BRAND, Build.BRAND);
        crashInfo.addDeviceInfo(DEVICE, Build.DEVICE);
        crashInfo.addDeviceInfo(FINGERPRINT, Build.FINGERPRINT);
        crashInfo.addDeviceInfo(TIME, String.valueOf(Build.TIME));
        crashInfo.addDeviceInfo(VERSION_SDK, String.valueOf(Build.VERSION.SDK_INT));
        crashInfo.addDeviceInfo(VERSION_RELEASE, Build.VERSION.RELEASE);
    }

    private void writeToLog(Context context) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = AtWorkDirUtils.getInstance().getCrashLogDir();
//            if (new File(filePath).exists() == false) {
//                new File(filePath).mkdirs();
//            }
            String logFile = formatter.format(new Date()) + ".txt";

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filePath + File.separator + logFile);
                fos.write(crashInfo.printLog().getBytes(Charset.forName("UTF-8")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void exit() {
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

