package com.foreveross.atwork.infrastructure.beeworks;

import androidx.annotation.Nullable;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.beeworks.cdn.BeeWorksCdn;
import com.foreveross.atwork.infrastructure.beeworks.share.BeeWorksShare;
import com.foreveross.atwork.infrastructure.beeworks.voice.BeeworksVoice;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LongUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lingen on 15/12/21.
 */
public class BeeWorksConfig {

    /**
     * 单机版
     */
    public static final int SPECIALIZED_LITE = 1;

    @SerializedName("apiUrl")
    public String apiUrl;

    @SerializedName("imHost")
    public String imHost;

    @SerializedName("apiMediaUrl")
    public String apiMediaUrl;

    @SerializedName("checkInUrl")
    public String checkInUrl;

    @SerializedName("enableCheckIn")
    public boolean enableCheckIn = true;

    @SerializedName("articleContentURL")
    public String articleContentURL;

    @SerializedName("beeWorksUrl")
    public String beeWorksUrl;

    @SerializedName("mediaServerUrl")
    public String mediaServerUrl;

    @SerializedName("domainId")
    public String domainId;

    @SerializedName("specialized")
    public int specialized;

    @SerializedName("scheme")
    public String scheme;

    @SerializedName("profile")
    public String profile;

    @SerializedName("androidDownloadUrl")
    public String androidDownloadUrl;

    @SerializedName("adminUrl")
    public String adminUrl;

    @SerializedName("duplicateRemoval")
    public boolean duplicateRemoval;

    @SerializedName("adminMediaUrl")
    public String adminMediaUrl;

    @SerializedName("disableCloudDisk")
    public boolean disableCloudDisk;

    @SerializedName("disableFavorite")
    public boolean disableFavorite;

    @SerializedName("copyright")
    public BeeWorksCopyright copyright;

    @SerializedName("uMeng")
    public BeeWorksUMeng beeWorksUMeng;

    @SerializedName("bugly")
    public BeeWorksBugly beeWorksBugly;

    @SerializedName("tinyun")
    public BeeWorksTinYun beeWorksTinYun;

    @SerializedName("encrypt")
    public BeeWorksEncryption beeWorkEncryption;


    @SerializedName("xfyun")
    public BeeWorksXunfei beeWorkXunfei;

    @SerializedName("voice")
    public BeeworksVoice beeworksVoice;

    @SerializedName("translation")
    public BeeWorksTextTranslate beeWorkTextTranslate;

    @SerializedName("share")
    public BeeWorksShare beeWorksShare;

    @SerializedName("amap")
    public BeeWorksAmap beeWorksAmap;

    @SerializedName("deling")
    public BeeWorksDeling beeWorksDeling;


    @SerializedName("cdn")
    public BeeWorksCdn beeWorksCdn;

    @SerializedName("email")
    public BeeWorksEmail beeWorksEmail;

    @SerializedName("setting")
    public BeeWorksSetting beeWorksSetting;


    @SerializedName("zoom")
    public BeeWorksZoom beeWorksZoom;

    @SerializedName("vote")
    public BeeWorksVote beeWorksVote;


    @SerializedName("scanner")
    public BeeWorksScanner beeWorksScanner;

    @SerializedName("moreInfo")
    public Map<String, String> moreInfos;

    @SerializedName("protocol")
    public boolean protocol;

    public String appModel;

    public String getKeyForMoreInfos(String key) {
        return moreInfos.get(key);
    }

    public String getScheduleUrl() {
        return getKeyForMoreInfos("scheduleUrl");
    }

    public String getColleagueCircleUrl() {
        return getKeyForMoreInfos("colleagueCircleUrl");
    }

    public String getShakeUrl() {
        return getKeyForMoreInfos("shakeUrl");
    }

    @Nullable
    public Long getSafeCheckTime() {
        String value = getKeyForMoreInfos("safeCheckTime");
        if(StringUtils.isEmpty(value)) {
            return null;
        }

        return LongUtil.parseLong(value);
    }


    @Nullable
    public int getCrackCheck() {
        String value = getKeyForMoreInfos("crackCheck");
        if(StringUtils.isEmpty(value)) {
            return -1;
        }

        return Integer.parseInt(value);
    }


    public boolean canScreenCapture() {
        String value = getKeyForMoreInfos("screenCapture");
        if(StringUtils.isEmpty(value)) {
            return true;
        }

        return Boolean.parseBoolean(value);
    }

    public boolean isLite() {
        return true;
    }

    public static BeeWorksConfig createInstance(JSONObject jsonObject) {
        BeeWorksConfig beeWorksConfig = new BeeWorksConfig();
        beeWorksConfig.apiUrl = jsonObject.optString("apiUrl");
        beeWorksConfig.imHost = jsonObject.optString("imHost");
        beeWorksConfig.apiMediaUrl = jsonObject.optString("apiMediaUrl");
        beeWorksConfig.checkInUrl = jsonObject.optString("checkInUrl");
        beeWorksConfig.enableCheckIn = jsonObject.optBoolean("enableCheckIn", true);
        beeWorksConfig.articleContentURL = jsonObject.optString("articleContentURL");
        beeWorksConfig.domainId = jsonObject.optString("domainId");
        beeWorksConfig.specialized = jsonObject.optInt("specialized");
        beeWorksConfig.scheme = jsonObject.optString("scheme");
        beeWorksConfig.profile = jsonObject.optString("profile", "default");
        beeWorksConfig.androidDownloadUrl = jsonObject.optString("androidDownloadUrl");
        beeWorksConfig.disableCloudDisk = jsonObject.optBoolean("disableCloudDisk", true);
        beeWorksConfig.disableFavorite = jsonObject.optBoolean("disableFavorite");
        beeWorksConfig.adminUrl = jsonObject.optString("adminUrl");
        beeWorksConfig.duplicateRemoval = jsonObject.optBoolean("duplicateRemoval");
        beeWorksConfig.adminMediaUrl = jsonObject.optString("adminMediaUrl");
        beeWorksConfig.beeWorksUrl = jsonObject.optString("beeWorksUrl");
        beeWorksConfig.mediaServerUrl = jsonObject.optString("mediaServerUrl");
        beeWorksConfig.copyright = BeeWorksCopyright.createInstance(jsonObject.optJSONObject("copyright"));
        beeWorksConfig.beeWorksUMeng = BeeWorksUMeng.createInstance(jsonObject.optJSONObject("uMeng"));
        beeWorksConfig.beeWorksTinYun = BeeWorksTinYun.createInstance(jsonObject.optJSONObject("tinyun"));
        beeWorksConfig.beeWorksBugly = BeeWorksHelper.createInstance(jsonObject.optJSONObject("bugly"), BeeWorksBugly.class);
        beeWorksConfig.beeWorksShare = BeeWorksShare.createInstance(jsonObject.optJSONObject("share"));
        beeWorksConfig.beeWorkEncryption = BeeWorksEncryption.createInstance(jsonObject.optJSONObject("encrypt"));
        beeWorksConfig.beeWorkXunfei = BeeWorksXunfei.createInstance(jsonObject.optJSONObject("xfyun"));
        beeWorksConfig.beeworksVoice = BeeWorksHelper.createInstance(jsonObject.optJSONObject("voice"), BeeworksVoice.class);
        beeWorksConfig.beeWorkTextTranslate = BeeWorksTextTranslate.createInstance(jsonObject.optJSONObject("translation"));
        beeWorksConfig.beeWorksAmap = BeeWorksHelper.createInstance(jsonObject.optJSONObject("amap"), BeeWorksAmap.class);
        beeWorksConfig.beeWorksDeling = BeeWorksHelper.createInstance(jsonObject.optJSONObject("deling"), BeeWorksDeling.class);
        beeWorksConfig.beeWorksCdn = BeeWorksHelper.createInstance(jsonObject.optJSONObject("cdn"), BeeWorksCdn.class);
        beeWorksConfig.beeWorksEmail = BeeWorksHelper.createInstance(jsonObject.optJSONObject("email"), BeeWorksEmail.class);
        beeWorksConfig.beeWorksSetting = BeeWorksHelper.createInstance(jsonObject.optJSONObject("setting"), BeeWorksSetting.class);
        beeWorksConfig.beeWorksZoom = BeeWorksHelper.createInstance(jsonObject.optJSONObject("zoom"), BeeWorksZoom.class);
        beeWorksConfig.beeWorksVote = BeeWorksHelper.createInstance(jsonObject.optJSONObject("vote"), BeeWorksVote.class);
        beeWorksConfig.beeWorksScanner =BeeWorksScanner.createInstance(jsonObject.optJSONObject("scanner"));
        beeWorksConfig.protocol = jsonObject.optBoolean("protocol");

        Map<String, String> moreInfo = new HashMap<>();
        JSONObject moreInfoObject = jsonObject.optJSONObject("moreInfo");

        Iterator<String> keyIterator = moreInfoObject.keys();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            moreInfo.put(key, moreInfoObject.optString(key));
        }
        beeWorksConfig.moreInfos = moreInfo;
        return beeWorksConfig;
    }

    public Object getValue(String key) {
        String value = "";
        if ("apiUrl".equalsIgnoreCase(key) || "_ApiUrl".equalsIgnoreCase(key)) {
            return apiUrl;
        }
        if ("articleContentURL".equalsIgnoreCase(key)) {
            return articleContentURL;
        }
        if ("domainId".equalsIgnoreCase(key)) {
            return AtworkConfig.DOMAIN_ID;
        }
        if ("beeWorksUrl".equalsIgnoreCase(key)) {
            return beeWorksUrl;
        }
        if ("adminUrl".equalsIgnoreCase(key) || "_AdminUrl".equalsIgnoreCase(key)) {
            return adminUrl;
        }
        if ("duplicateRemoval".equalsIgnoreCase(key) || "duplicate_Removal".equalsIgnoreCase(key)) {
            return duplicateRemoval;
        }

        if("encrypt_login".equalsIgnoreCase(key) || "encryptLogin".equalsIgnoreCase(key)) {
            return null != beeWorkEncryption && beeWorkEncryption.mLogin;
        }

        return value;
    }
}
