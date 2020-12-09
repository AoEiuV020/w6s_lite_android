package com.foreveross.atwork.manager;

import android.content.Context;

import com.foreveross.atwork.infrastructure.plugin.FengMapPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore;
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException;

public class FengMapManager {

    private FengMapPlugin.IFengMapPlugin mFengMapPlugin;

    private static final FengMapManager sInstance = new FengMapManager();

    public static FengMapManager getInstance() {
        return sInstance;
    }

    public void initFengMap(Context context, String appKey, String mapId, FengMapPlugin.OnFengMapLocationListener listener) {
        try {

            WorkplusPluginCore.registerPresenterPlugin("com.foreverht.workplus.fengmap.FengMapPresenter");

        } catch (ReflectException e) {
            e.printStackTrace();
        }
        WorkplusPlugin plugin = WorkplusPluginCore.getPlugin(FengMapPlugin.IFengMapPlugin.class);
        if (plugin != null) {
            mFengMapPlugin = ((FengMapPlugin.IFengMapPlugin)plugin);
            mFengMapPlugin.initFengMap(context, appKey, mapId, listener);
        }

    }

    public void stopLocation() {
        if (mFengMapPlugin != null) {
            mFengMapPlugin.stopLocation();
        }
    }
}
