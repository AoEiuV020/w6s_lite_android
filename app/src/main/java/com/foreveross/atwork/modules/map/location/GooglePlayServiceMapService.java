package com.foreveross.atwork.modules.map.location;

import android.content.Context;

import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore;
import com.foreveross.atwork.infrastructure.plugin.map.location.IGoogleMapLocationServicePlugin;
import com.foreveross.atwork.infrastructure.plugin.map.location.OnGetLocationListener;
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException;

import java.util.List;

public class GooglePlayServiceMapService  {

    private static final Object sLock = new Object();

    private static GooglePlayServiceMapService sInstance;

    private IGoogleMapLocationServicePlugin mPlugin;

    public static GooglePlayServiceMapService getInstance() {

        //double check
        if(null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new GooglePlayServiceMapService();
                }
            }
        }
        return sInstance;
    }

    private void checkPlugin() {
        if(null == mPlugin) {
            try {
//                Reflect.on("com.foreverht.workplus.googleMap.GooglePlayServiceMapService").create();
                WorkplusPluginCore.registerPresenterPlugin("com.foreverht.workplus.googleMap.GooglePlayServiceMapService");
                mPlugin = WorkplusPluginCore.getPlugin(IGoogleMapLocationServicePlugin.class);

            } catch (ReflectException e) {
                e.printStackTrace();
            }
        }
    }

    public void startLocation(Context context, long blockTime, List<String> illegalInstalledList, OnGetLocationListener getLocationListener) {
        checkPlugin();
        if(null != mPlugin) {
            mPlugin.startLocation(context, blockTime, illegalInstalledList, getLocationListener);
        }
    }
}
