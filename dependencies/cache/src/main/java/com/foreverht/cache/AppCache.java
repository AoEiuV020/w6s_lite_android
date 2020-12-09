package com.foreverht.cache;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.util.LruCache;

import com.foreveross.atwork.infrastructure.model.app.App;

import java.util.List;

/**
 * 应用缓存
 * Created by reyzhang22 on 16/4/12.
 */
public class AppCache extends BaseCache {

    private static String TAG = AppCache.class.getSimpleName();

    private static AppCache sInstance = new AppCache();

    private LruCache<String, App> mAppCache = new LruCache<>(mMaxMemory / 10);

    private AppCache() {

    }

    public static AppCache getInstance() {
        return sInstance;
    }


    /**
     * 根据app
     * @param appId
     * @return
     */
    public App getAppCache(String appId) {
        return mAppCache.get(appId);
    }

    public void removeAppCache(String appId) {
        mAppCache.remove(appId);
    }

    public void setAppCache(App app) {
        if (app == null) {
            return;
        }
        mAppCache.put(app.mAppId, app);
    }

    /**
     * 保存应用缓存列表
     * @param apps
     */
    public void setAppsCache(List<App> apps) {
        for (App app : apps) {
            if (app == null) {
                continue;
            }
            setAppCache(app);
        }
    }
}
