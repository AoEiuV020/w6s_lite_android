package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

public class StickerShareInfo {
    private static final String USER_STICKER_FILE = "user_sticker_file_";

    private static final String KEY_USER_STICKER_DATA = "USER_STICKER_DATA";

    private static final String KEY_LAST_REFRESH_TIME = "LAST_REFRESH_TIME";

    private static final String KEY_USER_STICKER_UPDATE_LIST = "KEY_USER_STICKER_UPDATE_LIST";

    private static volatile StickerShareInfo sInstance = new StickerShareInfo();

    public static StickerShareInfo getInstance() {
        return sInstance;
    }

    public void setStickerData(Context context, String data) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, USER_STICKER_FILE + username, KEY_USER_STICKER_DATA, data);

    }

    public String getStickerData(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, USER_STICKER_FILE + username, KEY_USER_STICKER_DATA, "");
    }

    public void setLastRefreshTime(Context context, long refreshTime) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong(context, USER_STICKER_FILE + username, KEY_LAST_REFRESH_TIME, refreshTime);
    }

    public long getLastRefreshTime(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getLong(context, USER_STICKER_FILE + username, KEY_LAST_REFRESH_TIME, -1);
    }

    public void setStickerUpdateList(Context context, String updateList) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, USER_STICKER_FILE + username, KEY_USER_STICKER_UPDATE_LIST, updateList);
    }

    public String getStickerUpdateList(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, USER_STICKER_FILE + username, KEY_USER_STICKER_UPDATE_LIST, "");
    }

}
