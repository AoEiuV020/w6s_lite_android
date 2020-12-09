package com.foreverht.cache;

import androidx.collection.LruCache;
import androidx.collection.LruCache;

import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;

import java.util.List;

/**
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
 * Created by reyzhang22 on 16/9/23.
 */

public class DropboxCache extends BaseCache {

    private static final DropboxCache sInstance = new DropboxCache();

    private LruCache<String, Dropbox> mDropboxCache = new LruCache<>(mMaxMemory / 10);

    private LruCache<String, DropboxConfig> mDropboxConfigCache = new LruCache<>(mMaxMemory / 10);

    public static DropboxCache getInstance() {
        return sInstance;
    }


    /**
     * 根据app
     * @param fileId
     * @return
     */
    public Dropbox getDropboxCache(String fileId) {
        return mDropboxCache.get(fileId);
    }

    public void removeDropboxCache(String fileId) {
        mDropboxCache.remove(fileId);
    }

    public void setDropboxCache(Dropbox dropbox) {
        if (dropbox == null) {
            return;
        }
        mDropboxCache.put(dropbox.mFileId, dropbox);
    }

    /**
     * 保存应用缓存列表
     * @param dropboxes
     */
    public void setDropboxListCache(List<Dropbox> dropboxes) {
        for (Dropbox dropbox : dropboxes) {
            if (dropbox == null) {
                continue;
            }
            setDropboxCache(dropbox);
        }
    }

    public void setDropboxConfigCache(DropboxConfig dropboxConfig) {
        if (dropboxConfig == null) {
            return;
        }
        mDropboxConfigCache.put(dropboxConfig.mSourceId, dropboxConfig);
    }

    public DropboxConfig getDropboxConfigCache(String sourceId) {
        return mDropboxConfigCache.get(sourceId);
    }
}
