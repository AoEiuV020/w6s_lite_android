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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.util.LruCache;

import com.foreveross.atwork.infrastructure.model.user.User;

/**
 * Created by reyzhang22 on 16/3/29.
 */
public class UserCache extends BaseCache {

    private static String TAG = UserCache.class.getSimpleName();

    private static UserCache sInstance = new UserCache();

    private LruCache<String, User> mUserCache = new LruCache<>(mMaxMemory / 10);

    private UserCache() {

    }

    public static UserCache getInstance() {
        return sInstance;
    }


    /**
     * 根据用户id获取缓存用户
     * @param userId
     * @return
     */
    public User getUserCache(String userId) {
        return mUserCache.get(userId);
    }

    /**
     * 根据userId移除缓存user
     * @param userId
     */
    public void removeUserCache(String userId) {
        mUserCache.remove(userId);
    }

    /**
     * 添加或更新用户缓存, 使用 userId 作为 key
     * @param user
     */
    public void setUserCache(User user) {
        setUserCache(user, "id");
    }


    /**
     * 添加或更新用户缓存
     * @param user
     * @param keyType key 类型
     */
    public void setUserCache(User user, String keyType) {
        if (user == null) {
            return;
        }
        if("id".equals(keyType)) {
            mUserCache.put(user.mUserId, user);

        } else if("username".equals(keyType)) {
            mUserCache.put(user.mUsername, user);
        }
    }

}
