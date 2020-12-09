package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.Gson;

/**
 * 用于存储<b>组织相关</b>非登录信息的SharedPreferences, 例如启动页广告页
 *
 * Created by dasunsy on 16/9/13.
 */
public class OrgCommonShareInfo extends CommonShareInfo {

    public final static String KEY_LAST_LOAD_START_PAGE_INFO = "key_last_load_start_page_time";

    public final static String KEY_LIGHTAPP_OFFLINE_RELEASE_UNZIP_DIGEST = "key_lightapp_offline_release_unzip_digest_v2";



    public static void setLightappOfflineReleaseUnzipDigest(Context context, AppBundles appBundle, String digest) {
        String sp = getSpName(appBundle.mOrgId);

        PreferencesUtils.putString(context, sp, KEY_LIGHTAPP_OFFLINE_RELEASE_UNZIP_DIGEST + "_" + appBundle.mBundleId , digest);
    }


    public static String getLightappOfflineReleaseUnzipDigest(Context context, AppBundles appBundle) {
        String sp = getSpName(appBundle.mOrgId);

        return PreferencesUtils.getString(context, sp, KEY_LIGHTAPP_OFFLINE_RELEASE_UNZIP_DIGEST + "_" + appBundle.mBundleId, StringUtils.EMPTY);
    }

    public static void setLastLoadStartPageTime(Context context, String orgCode, long lastLoadTime) {
        String json = PreferencesUtils.getString(context, getSpName(orgCode), KEY_LAST_LOAD_START_PAGE_INFO);
        if (StringUtils.isEmpty(json)) {
            PageAdInfo info = new PageAdInfo(lastLoadTime, -1);
            Gson gson = new Gson();
            json = gson.toJson(info);

        } else {
            Gson gson = new Gson();
            PageAdInfo info = gson.fromJson(json, PageAdInfo.class);
            if (null != info) {
                info.lastKeyLoadStartPageTime = lastLoadTime;
                json = gson.toJson(info);
            }
        }

        PreferencesUtils.putString(context, getSpName(orgCode), KEY_LAST_LOAD_START_PAGE_INFO, json);
    }

    public static void setAdShownModifyTIme(Context context, String orgCode, long adShownModifyTime) {
        String json = PreferencesUtils.getString(context, getSpName(orgCode), KEY_LAST_LOAD_START_PAGE_INFO);
        Gson gson = new Gson();
        PageAdInfo info = gson.fromJson(json, PageAdInfo.class);
        if (null != info) {
            info.adShownModifyTime = adShownModifyTime;
        }

        String newJson = gson.toJson(info);
        PreferencesUtils.putString(context, getSpName(orgCode), KEY_LAST_LOAD_START_PAGE_INFO, newJson);
    }

    public static long getLastLoadStartPageTime(Context context, String orgCode) {
        String json = PreferencesUtils.getString(context, getSpName(orgCode), KEY_LAST_LOAD_START_PAGE_INFO);
        long lastKeyLoadStartPageTime = -1;
        PageAdInfo info = new Gson().fromJson(json, PageAdInfo.class);
        if (null != info) {
            lastKeyLoadStartPageTime = info.lastKeyLoadStartPageTime;
        }
        return lastKeyLoadStartPageTime;
    }

    public static boolean isAdHasShown(Context context, String orgCode, long dataModifyTime) {
        String json = PreferencesUtils.getString(context, getSpName(orgCode), KEY_LAST_LOAD_START_PAGE_INFO);
        boolean hasShown = true;
        PageAdInfo info = new Gson().fromJson(json, PageAdInfo.class);

        if (null != info) {
            hasShown = (info.adShownModifyTime == dataModifyTime);
        }
        return hasShown;

    }

    private static String getSpName(String orgCode) {
        return SP_COMMON + "_" + orgCode;
    }

    static class PageAdInfo {
        public PageAdInfo(long lastKeyLoadStartPageTime, long adShownModifyTime) {
            this.lastKeyLoadStartPageTime = lastKeyLoadStartPageTime;
            this.adShownModifyTime = adShownModifyTime;
        }

        public long lastKeyLoadStartPageTime;
        public long adShownModifyTime;
    }
}
