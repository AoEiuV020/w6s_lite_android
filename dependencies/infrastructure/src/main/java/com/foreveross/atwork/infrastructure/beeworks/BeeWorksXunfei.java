package com.foreveross.atwork.infrastructure.beeworks;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dasunsy on 2017/7/14.
 */

public class BeeWorksXunfei implements Serializable {

    @SerializedName("enabled")
    public boolean mEnabled = false;

    @SerializedName("android")
    public XunfeiItem mXunfeiItem;

    public static BeeWorksXunfei createInstance(JSONObject jsonObject) {
        BeeWorksXunfei beeWorksXunfei = new BeeWorksXunfei();
        if (null != jsonObject) {
            beeWorksXunfei.mEnabled = jsonObject.optBoolean("enabled");

            JSONObject itemObj = jsonObject.optJSONObject("android");
            if(null != itemObj) {
               beeWorksXunfei.mXunfeiItem = new XunfeiItem();
                beeWorksXunfei.mXunfeiItem.mAppId = itemObj.optString("appId");
            }
        }

        return beeWorksXunfei;
    }

    public boolean isLegal() {
        return mEnabled && null != mXunfeiItem && !StringUtils.isEmpty(mXunfeiItem.mAppId);
    }

    public String getKey() {
        return mXunfeiItem.mAppId;
    }


}
