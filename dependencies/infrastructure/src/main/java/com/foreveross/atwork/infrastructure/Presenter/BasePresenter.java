package com.foreveross.atwork.infrastructure.Presenter;

import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by reyzhang22 on 2017/11/21.
 */

public abstract class BasePresenter {
    private Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> mPluginMap;

    public BasePresenter() {
        onCreate();
    }

    private void onCreate() {
        registerPlugin();
    }

    public void onDestroy() {
        unregisterPlugin();
    }

    public void registerPlugin() {
        if (mPluginMap ==null) {
            mPluginMap = getWorkplusPlugin();
        }
        if (mPluginMap != null && !mPluginMap.isEmpty()) {
            WorkplusPluginCore.registerAllPlugin(mPluginMap);
        }
    }


    private void unregisterPlugin() {
        if (mPluginMap == null) {
            mPluginMap = getWorkplusPlugin();
        }
        if (mPluginMap != null && !mPluginMap.isEmpty()) {
            Set<Class<? extends WorkplusPlugin>> set = mPluginMap.keySet();
            Iterator<Class<? extends WorkplusPlugin>> iterator = set.iterator();
            while (iterator.hasNext()) {
                WorkplusPluginCore.unregisterPlugin(iterator.next());
            }
        }
    }

    public abstract Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> getWorkplusPlugin();
}
