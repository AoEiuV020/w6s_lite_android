package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

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
 * Created by reyzhang22 on 16/9/27.
 */

public class DropboxSearchHistoryData {

    private static final String TAG = DropboxSearchHistoryData.class.getSimpleName();

    private static DropboxSearchHistoryData sInstance = new DropboxSearchHistoryData();

    private static final String SP_DROPBOX_SEARCH_HISTORY = "_SP_DROPBOX_SEARCH_HISTORY";

    private static final String DROPBOX_SEARCH_HISTORY = "DROPBOX_SEARCH_HISTORY";

    public static DropboxSearchHistoryData getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new DropboxSearchHistoryData();
            }
            return sInstance;
        }
    }

    /**
     * @param context
     * @param data
     */
    public void setDropboxSearchHistoryData(Context context, String userId, String data) {
        PreferencesUtils.putString(context, userId + SP_DROPBOX_SEARCH_HISTORY, DROPBOX_SEARCH_HISTORY, data);
    }

    /**
     * @param context
     * @return
     */
    public String getDropboxSearchHistoryData(Context context, String userId) {
        return PreferencesUtils.getString(context, userId + SP_DROPBOX_SEARCH_HISTORY, DROPBOX_SEARCH_HISTORY, "");
    }

    /**
     * @param context
     */
    public void clearDropboxHistoryData(Context context, String userId) {
        PreferencesUtils.clear(context, userId + SP_DROPBOX_SEARCH_HISTORY);
    }
}
