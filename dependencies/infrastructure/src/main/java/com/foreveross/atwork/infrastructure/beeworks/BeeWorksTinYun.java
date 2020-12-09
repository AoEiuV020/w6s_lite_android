package com.foreveross.atwork.infrastructure.beeworks;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lingen on 16/1/21.
 */
public class BeeWorksTinYun implements Serializable {

    public boolean enalbed;

    public String appKey;

    public static BeeWorksTinYun createInstance(JSONObject jsonObject){
        BeeWorksTinYun beeWorksTinYun = new BeeWorksTinYun();
        if (jsonObject!=null){
            beeWorksTinYun.enalbed = jsonObject.optBoolean("enabled");
            beeWorksTinYun.appKey = jsonObject.optString("appKey");
        }
        return beeWorksTinYun;
    }
}
