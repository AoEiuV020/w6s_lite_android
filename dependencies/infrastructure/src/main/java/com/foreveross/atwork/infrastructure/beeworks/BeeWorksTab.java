package com.foreveross.atwork.infrastructure.beeworks;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/12/21.
 */
public class BeeWorksTab {

    @SerializedName("_id")
    public String id;

    @SerializedName("tabId")
    public String tabId;

    @SerializedName("name")
    public String name;

    @SerializedName("type")
    public String type;

    @SerializedName("iconUnSelected")
    public String iconUnSelected;

    @SerializedName("iconSelected")
    public String iconSelected;

    @SerializedName("value")
    public String value;

    @SerializedName("url")
    public String url;

    public NativeJson mNativeJson;

    @SerializedName("immersion")
    public boolean immersion;

    @SerializedName("customTitleView")
    public boolean customTitleView;

    public static List<BeeWorksTab> getBeeWorksTab(JSONArray array){
        List<BeeWorksTab> tabs = new ArrayList<>();
        for (int i=0;i<array.length();i++){
            JSONObject jsonObject = array.optJSONObject(i);
            tabs.add(createInstance(jsonObject));
        }
        return tabs;
    }

    public static BeeWorksTab createInstance(JSONObject jsonObject){
        BeeWorksTab beeWorksTab = new BeeWorksTab();
        beeWorksTab.id = jsonObject.optString("_id");
        beeWorksTab.tabId = jsonObject.optString("tabId");
        beeWorksTab.name = jsonObject.optString("name");
        beeWorksTab.type = jsonObject.optString("type");
        beeWorksTab.iconUnSelected = jsonObject.optString("iconUnSelected");
        beeWorksTab.iconSelected = jsonObject.optString("iconSelected");
        beeWorksTab.value = jsonObject.optString("value");
        beeWorksTab.url = jsonObject.optString("url");
        beeWorksTab.immersion = jsonObject.optBoolean("immersion", true);
        beeWorksTab.customTitleView = jsonObject.optBoolean("customTitleView", false);
        return beeWorksTab;
    }




}
