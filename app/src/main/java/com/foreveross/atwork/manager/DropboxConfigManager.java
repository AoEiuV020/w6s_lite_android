package com.foreveross.atwork.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.cache.DropboxCache;
import com.foreverht.db.service.repository.DropboxConfigRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;

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
 * Created by reyzhang22 on 16/9/14.
 */
public class DropboxConfigManager {

    private static final DropboxConfigManager sInstance = new DropboxConfigManager();

    public static DropboxConfigManager getInstance() {
        return sInstance;
    }

    /**
     * 根据源id获取到网盘配置信息
     * @param context
     * @param sourceId
     */
    public void getDropboxConfigBySourceId(Context context, String sourceId, OnDropboxConfigListener listener) {
        new AsyncTask<Void, Void, DropboxConfig>() {
            @Override
            protected DropboxConfig doInBackground(Void... voids) {
                return DropboxConfigRepository.getInstance().getDropboxConfigBySourceId(sourceId);
            }

            @Override
            protected void onPostExecute(DropboxConfig config) {
                super.onPostExecute(config);
                if (listener == null) {
                    return;
                }
                listener.onDropboxConfigCallback(config);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 同步获取网盘配置信息，注意同步阻塞
     * @param context
     * @param sourceId
     */
    public DropboxConfig syncGetDropboxConfigBySourceId(Context context, String sourceId) {
        DropboxConfig dropboxConfig = DropboxCache.getInstance().getDropboxConfigCache(sourceId);
        if (dropboxConfig == null) {
            dropboxConfig = DropboxConfigRepository.getInstance().getDropboxConfigBySourceId(sourceId);
        }
        if (dropboxConfig != null) {
            DropboxCache.getInstance().setDropboxConfigCache(dropboxConfig);
        }
        return dropboxConfig;
    }

    /**
     * Dropbox config 监听器
     */
    public interface OnDropboxConfigListener {
        void onDropboxConfigCallback(DropboxConfig config);
    }
}
