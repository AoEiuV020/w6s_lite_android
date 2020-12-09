package com.foreveross.atwork.cordova.plugin.model;

import android.content.Context;

import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/3/23.
 */

public class SetLightNoticeDataRequest extends LightNoticeData {

    @SerializedName("tab_id")
    private String tabId;


    public String getTabId(Context context) {

        //tabId 为新增字段, 固需兼容旧版本
        if(StringUtils.isEmpty(tabId)) {
            if(CustomerHelper.isOct(context)) {
                return "oct_portal";
            }
        }

        return tabId;
    }
}
