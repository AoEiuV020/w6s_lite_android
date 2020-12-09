package com.foreveross.atwork.infrastructure.beeworks;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lingen on 15/12/21.
 */
public class BeeWorksUMeng implements Serializable {


    public boolean enabled;

    public String appId;

    public String channelId;

    public static BeeWorksUMeng createInstance(JSONObject jsonObject){
        BeeWorksUMeng beeWorksUMeng = new BeeWorksUMeng();
        if (jsonObject!=null){
            beeWorksUMeng.appId = jsonObject.optString("appId");
            beeWorksUMeng.channelId = jsonObject.optString("channelId");
            beeWorksUMeng.enabled = jsonObject.optBoolean("enabled");
        }
        return beeWorksUMeng;
    }
}
