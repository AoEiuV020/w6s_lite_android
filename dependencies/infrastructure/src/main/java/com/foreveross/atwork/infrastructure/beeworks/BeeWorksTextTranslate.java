package com.foreveross.atwork.infrastructure.beeworks;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dasunsy on 2017/7/14.
 */

public class BeeWorksTextTranslate implements Serializable {

    @SerializedName("enabled")
    public boolean mEnabled = false;

    @SerializedName("type")
    public String mType;

    @SerializedName("google")
    public TextTranslateItem mGoogle;

    @SerializedName("youdao")
    public TextTranslateItem mYoudao;


    public static BeeWorksTextTranslate createInstance(JSONObject textTranslateObj) {
        BeeWorksTextTranslate beeWorksTextTranslate = new BeeWorksTextTranslate();
        if (null != textTranslateObj) {
            beeWorksTextTranslate.mEnabled = textTranslateObj.optBoolean("enabled");
            beeWorksTextTranslate.mType = textTranslateObj.optString("type");

            JSONObject googleObj = textTranslateObj.optJSONObject("google");
            createTextTranslateItem(beeWorksTextTranslate, googleObj, "google");

            JSONObject youdaoObj = textTranslateObj.optJSONObject("youdao");
            createTextTranslateItem(beeWorksTextTranslate, youdaoObj, "youdao");
        }
        return beeWorksTextTranslate;
    }

    private static void createTextTranslateItem(BeeWorksTextTranslate beeWorksTextTranslate, JSONObject itemObj, String type) {
        if(null != itemObj) {
            if("google".equalsIgnoreCase(type)) {
                beeWorksTextTranslate.mGoogle = createTextTranslateItem(itemObj);

            } else if("youdao".equalsIgnoreCase(type)) {
                beeWorksTextTranslate.mYoudao = createTextTranslateItem(itemObj);
            }

        }
    }

    private static TextTranslateItem createTextTranslateItem(@NonNull JSONObject itemObj) {
        TextTranslateItem textTranslateItem = new TextTranslateItem();
        textTranslateItem.mAppId = itemObj.optString("appId");
        textTranslateItem.mAppSecret = itemObj.optString("appSecret");

        return textTranslateItem;
    }

    public boolean isLegal() {
        return mEnabled && !StringUtils.isEmpty(getKey());
    }

    public String getKey() {
        if("google".equalsIgnoreCase(mType) && null != mGoogle) {
            return mGoogle.mAppSecret;

        } else if("youdao".equalsIgnoreCase(mType) && null != mYoudao) {
            return mYoudao.mAppId;

        }

        return StringUtils.EMPTY;
    }
}
