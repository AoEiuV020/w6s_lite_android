package com.foreverht.cache;

import android.text.TextUtils;
import android.util.LruCache;

import com.w6s.module.MessageTags;

import java.util.List;

public class MessageTagCache extends BaseCache {

    private static String TAG = AppCache.class.getSimpleName();

    private static MessageTagCache sInstance = new MessageTagCache();

    private LruCache<String, MessageTags> mMessageTagCache = new LruCache<>(mMaxMemory / 10);

    private MessageTagCache() {

    }

    public static MessageTagCache getInstance() {
        return sInstance;
    }

    public MessageTags getMessageTagCache(String appId) {
        if (TextUtils.isEmpty(appId)) {
            return null;
        }
        return mMessageTagCache.get(appId);
    }

    public void removeCache() {
        mMessageTagCache.evictAll();
    }

    public void setMessageTagCache(MessageTags messageTag) {
        if (messageTag == null) {
            return;
        }
        mMessageTagCache.put(messageTag.getTagId(), messageTag);
    }

    public void setMessageTagsCache(List<MessageTags> messageTags) {
        for (MessageTags messageTag : messageTags) {
            if (messageTag == null) {
                continue;
            }
            setMessageTagCache(messageTag);
        }
    }
}
