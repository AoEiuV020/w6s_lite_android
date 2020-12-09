package com.foreveross.atwork.infrastructure.plugin;

import com.foreveross.atwork.infrastructure.utils.reflect.Reflect;
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by reyzhang22 on 2017/11/21.
 */

public class WorkplusPluginCore {

    private static Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> mPluginMap = new HashMap<>();


    public static <T extends WorkplusPlugin> T getPluginAndCheckRegisterInstance(Class<? extends WorkplusPlugin> clz, String pluginStr) {
        WorkplusPlugin plugin =  getPlugin(clz);
        if(null == plugin) {
            try {
                plugin = Reflect.on(pluginStr).call("getInstance").get();

                registerPlugin(clz, plugin);


            } catch (ReflectException e) {
                e.printStackTrace();
            }

        }

        return (T) plugin;
    }

    public static <T extends WorkplusPlugin> T getPlugin(Class clz) {
        if (clz == null) {
            return null;
        }
        WorkplusPlugin plugin = null;
        if (mPluginMap.containsKey(clz)) {
            plugin = mPluginMap.get(clz);
        }
        return (T)plugin;
    }

    public static void registerPlugin(Class<? extends WorkplusPlugin> clz, WorkplusPlugin plugin) {
        if (mPluginMap.containsKey(clz)) {
            return;
        }
        mPluginMap.put(clz, plugin);
    }


    public static boolean registerPresenterPlugin(String plugin) {
        try {
            Reflect.on(plugin).create();

            return true;

        } catch (ReflectException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void registerAllPlugin(Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> plugins) {
        if (plugins != null) {
            mPluginMap.putAll(plugins);
        }
    }

    public static void unregisterPlugin(Class<? extends WorkplusPlugin> clz) {
        if (mPluginMap.containsKey(clz)) {
            mPluginMap.remove(clz);
        }
    }
}
