package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 17/9/16.
 */

public class AdvertisementInfo {

    private static final AdvertisementInfo sInstance = new AdvertisementInfo();

    private static final String SP_ADVERTISEMENT_ORG_FILE = "_SP_ADVERTISEMENT_ORG_";

    private static final String ADVERTISEMENT_DATA = "ADVERTISEMENT_DATA";

    private static final String LAST_VIEW_TIME = "LAST_VIEW_TIME";

    public static AdvertisementInfo getInstance() {
        return sInstance;
    }

    /**
     * 保存组织广告
     * @param context
     * @param orgId
     * @param data
     */
    public void setOrgAdvertisementData(Context context, String orgId, String data) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        PreferencesUtils.putString(context, userId + SP_ADVERTISEMENT_ORG_FILE + orgId, ADVERTISEMENT_DATA, data);
    }

    /**
     * 保存这次看广告的时间
     * @param context
     * @param orgId
     * @param currentViewTime
     */
    public void setLastViewTime(Context context, String orgId, long currentViewTime) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        PreferencesUtils.putLong(context, userId + SP_ADVERTISEMENT_ORG_FILE + orgId, LAST_VIEW_TIME, currentViewTime);
    }

    /**
     * 获取组织广告
     * @param context
     * @param orgId
     * @return
     */
    public List<AdvertisementConfig> getOrgAdvertisementData(Context context, String orgId) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        String data = PreferencesUtils.getString(context, userId + SP_ADVERTISEMENT_ORG_FILE + orgId, ADVERTISEMENT_DATA, "");
        List<AdvertisementConfig> list = null;
        if (!TextUtils.isEmpty(data)) {
            list = AdvertisementConfig.getGson().fromJson(data, new TypeToken<List<AdvertisementConfig>>(){}.getType());
        }

        if(null == list) {
            list = new ArrayList<>();
        }

        return list;
    }

    /**
     * 查看上一次预览广告的时间
     * @param context
     * @param orgId
     * @return
     */
    public long getLastViewTime(Context context, String orgId) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        return PreferencesUtils.getLong(context, userId + SP_ADVERTISEMENT_ORG_FILE + orgId, LAST_VIEW_TIME, -1);
    }

    public void removeAdvertisementData(Context context, String orgId) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        PreferencesUtils.remove(context, userId + SP_ADVERTISEMENT_ORG_FILE + orgId, ADVERTISEMENT_DATA);
    }

}
