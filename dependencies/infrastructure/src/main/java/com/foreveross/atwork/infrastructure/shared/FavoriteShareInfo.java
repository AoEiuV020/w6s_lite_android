package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

public class FavoriteShareInfo extends PersonalShareInfo {

    private final static String SP_KEY_FAV_REFRESH_TIME = "SP_KEY_FAV_REFRESH_TIME";
    private final static String SP_KEY_FAV_WHITE_LIST = "SP_KEY_FAV_WHITE_LIST";
    private final static String SP_KEY_FAV_TOTAL_COST = "SP_KEY_FAV_TOTAL_COST";

    private static FavoriteShareInfo sInstance;

    public static final FavoriteShareInfo getInstance() {
        synchronized (sLock) {
            if (null == sInstance) {
                sInstance = new FavoriteShareInfo();
            }
            return sInstance;
        }
    }

    public void setFavRefreshTime(Context context, long refreshTime) {
        PreferencesUtils.putLong(context, getPersonalSpName(context), SP_KEY_FAV_REFRESH_TIME, refreshTime);
    }

    public long getFavRefreshTime(Context context) {
        return PreferencesUtils.getLong(context, getPersonalSpName(context), SP_KEY_FAV_REFRESH_TIME, -1);
    }

    public void setFavWhiteList(Context context, boolean whiteList) {
        PreferencesUtils.putBoolean(context, getPersonalSpName(context), SP_KEY_FAV_WHITE_LIST, whiteList);
    }

    public boolean getFavWhiteList(Context context) {
        return PreferencesUtils.getBoolean(context, getPersonalSpName(context), SP_KEY_FAV_WHITE_LIST, false);
    }

    public void setFavTotalCost(Context context, long totalCost) {
        PreferencesUtils.putLong(context, getPersonalSpName(context), SP_KEY_FAV_TOTAL_COST, totalCost);
    }

    public long getFavTotalCost(Context context) {
        return PreferencesUtils.getLong(context, getPersonalSpName(context), SP_KEY_FAV_TOTAL_COST, 0);
    }

}
