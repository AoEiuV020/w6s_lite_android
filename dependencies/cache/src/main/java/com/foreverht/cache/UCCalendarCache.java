package com.foreverht.cache;

import android.util.LruCache;

import com.foreveross.atwork.infrastructure.model.uccalendar.UCCalendarToken;

/**
 * Created by reyzhang22 on 2017/11/22.
 */

public class UCCalendarCache extends BaseCache {

    private static String TAG = AppCache.class.getSimpleName();

    private static UCCalendarCache sInstance = new UCCalendarCache();

    private LruCache<String, UCCalendarToken> mUCCalendarCache = new LruCache<>(mMaxMemory / 10);

    private UCCalendarCache() {

    }

    public static UCCalendarCache getInstance() {
        return sInstance;
    }


    public UCCalendarToken getUCCalendarCache(String username) {
        return mUCCalendarCache.get(username);
    }

    public void clearUCCalendarCache() {
        mUCCalendarCache.evictAll();
    }

    public void setUCCalendarCache(UCCalendarToken ucCalendarToken) {
        if (ucCalendarToken == null) {
            return;
        }
        mUCCalendarCache.put(ucCalendarToken.mUsername, ucCalendarToken);
    }

    public void updateUCCalendarLoginStatus(String username, boolean isLogin) {
        UCCalendarToken token = getUCCalendarCache(username);
        if (token == null) {
            return;
        }
        token.mIsLogin = isLogin;
        setUCCalendarCache(token);
    }

}
