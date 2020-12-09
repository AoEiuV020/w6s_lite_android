package com.foreveross.atwork.infrastructure.beeworks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/12/23.
 */
public class NativeContent {

    public String mType;

    public String mGroupName;

    public String mLayout;

    public List<NativeItem> mValues;

    public int mColumns;

    public int mRows = 3;

    public int mHeight;

    public String mUrl;

    public String mBackgroundColor;

    public String mTitleLayout;

    public boolean mShowLine;

    public int mInterval;

    public int mMarginTop;

    public int mWidth;

    public static NativeContent createInstance(JSONObject jsonObject){
        NativeContent nativeContent = new NativeContent();
        nativeContent.mType = jsonObject.optString("type");
        nativeContent.mGroupName = jsonObject.optString("groupName");
        nativeContent.mColumns = jsonObject.optInt("column");
        nativeContent.mRows = jsonObject.optInt("row");
        nativeContent.mHeight = jsonObject.optInt("height");
        nativeContent.mWidth = jsonObject.optInt("width");
        nativeContent.mUrl = jsonObject.optString("url");
        nativeContent.mShowLine = jsonObject.optBoolean("showLine");
        nativeContent.mTitleLayout = jsonObject.optString("titleLayout");
        nativeContent.mBackgroundColor = jsonObject.optString("backgroundColor");
        nativeContent.mInterval = jsonObject.optInt("interval");
        nativeContent.mMarginTop = jsonObject.optInt("marginTop");
        List<NativeItem> items = new ArrayList<>();
        JSONArray arrays = jsonObject.optJSONArray("values");
        if (arrays == null) {
            nativeContent.mValues = items;
            return nativeContent;
        }
        for (int i=0;i<arrays.length();i++){
            JSONObject itemJSONObject = arrays.optJSONObject(i);
            NativeItem item = NativeItem.createInstance(itemJSONObject);
            items.add(item);
        }
        nativeContent.mValues = items;
        return nativeContent;
    }


}
