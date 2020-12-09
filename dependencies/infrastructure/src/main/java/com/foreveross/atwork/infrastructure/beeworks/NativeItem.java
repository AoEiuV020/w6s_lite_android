package com.foreveross.atwork.infrastructure.beeworks;

import org.json.JSONObject;

/**
 * Created by lingen on 15/12/23.
 */
public class NativeItem {


    public String mIcon;

    public String mTitle;

    public String mActionType;

    public String mValue;

    public String mTipUrl;

    public String mShowType;

    public String mFontColor;

    public String mDisplayMode;

    public static NativeItem createInstance(JSONObject jsonObject){
        NativeItem nativeItem = new NativeItem();
        nativeItem.mIcon = jsonObject.optString("icon");
        nativeItem.mTitle = jsonObject.optString("title");
        nativeItem.mActionType = jsonObject.optString("actionType");
        nativeItem.mValue = jsonObject.optString("value");
        nativeItem.mTipUrl = jsonObject.optString("tipUrl");
        nativeItem.mShowType = jsonObject.optString("showType");
        nativeItem.mFontColor = jsonObject.optString("fontColor");
        nativeItem.mDisplayMode = jsonObject.optString("mScreenMode");
        return nativeItem;
    }
}
