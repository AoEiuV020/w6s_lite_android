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


import android.text.TextUtils;
import android.util.LruCache;

import com.foreveross.atwork.infrastructure.model.discussion.Discussion;

/**
 * 群组缓存类
 * Created by reyzhang22 on 16/4/5.
 */
public class DiscussionCache extends BaseCache {

    private static final String TAG = DiscussionCache.class.getSimpleName();

    private static DiscussionCache sInstance = new DiscussionCache();

    private LruCache<String, Discussion> mDiscussionCache = new LruCache<>(mMaxMemory / 10);

    private DiscussionCache() {

    }

    public static DiscussionCache getInstance() {
        return sInstance;
    }

    /**
     * 根据用户id获取缓存群组
     * @param discussionId
     * @return
     */
    public Discussion getDiscussionCache(String discussionId) {
        return mDiscussionCache.get(discussionId);
    }

    /**
     * 根据userId移除缓存群组
     * @param discussionId
     */
    public void removeDiscussionCache(String discussionId) {
        mDiscussionCache.remove(discussionId);
    }

    /**
     * 添加或更新群组缓存
     * @param discussionId
     * @param discussion
     */
    public void setDiscussionCache(String discussionId, Discussion discussion) {
        if (TextUtils.isEmpty(discussionId) || discussion == null) {
            return;
        }
        mDiscussionCache.put(discussionId, discussion);
    }

}
