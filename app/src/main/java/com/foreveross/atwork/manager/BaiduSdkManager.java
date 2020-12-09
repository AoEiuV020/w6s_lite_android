package com.foreveross.atwork.manager;

import android.content.Context;

import com.foreveross.atwork.infrastructure.plugin.BaiduSdkPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore;
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException;

public class BaiduSdkManager {

    private BaiduSdkPlugin.IBaiduSdkPlugin mPlugin;

    private static final BaiduSdkManager sInstance = new BaiduSdkManager();

    public static final BaiduSdkManager getInstance() {
        return sInstance;
    }


    public void initBaiduTraceSdk(Context context, long serviceId, String traceName, int gatherInterval, int packInterval) {
        try {

            WorkplusPluginCore.registerPresenterPlugin("com.foreverht.baiduLib.BaiduSdkPresenter");

        } catch (ReflectException e) {
            e.printStackTrace();
        }
        WorkplusPlugin plugin = WorkplusPluginCore.getPlugin(BaiduSdkPlugin.IBaiduSdkPlugin.class);
        if (plugin != null) {
            mPlugin = ((BaiduSdkPlugin.IBaiduSdkPlugin)plugin);
            mPlugin.initBaiduTrace(context, serviceId, traceName, gatherInterval, packInterval);
        }
    }

    public void startBaiduTrace() {
        mPlugin.startTrace();
    }

    public void stopBaiduTrace() {
        mPlugin.stopTrace();
    }

}
