package com.foreveross.atwork.api.sdk.util;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKindSerializer;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BannerType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BannerTypeSerializer;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleTypeSerializer;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayModeSerializer;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DistributeMode;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DistributeModeSerializer;
import com.foreveross.atwork.infrastructure.model.app.appEnum.ProgressBarType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.ProgressBarTypeSerializer;
import com.foreveross.atwork.infrastructure.model.app.appEnum.RecommendMode;
import com.foreveross.atwork.infrastructure.model.app.appEnum.RecommendModeSerializer;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by reyzhang22 on 16/4/21.
 */
public class NetGsonHelper {

    /**
     * @see #fromNetJson(String, boolean, Class)
     */
    @Nullable
    public static <T extends BasicResponseJSON> BasicResponseJSON fromNetJson(String json, Class<T> classOfT) {
        return fromNetJson(json, false, classOfT);
    }


    @Nullable
    public static <T extends BasicResponseJSON> BasicResponseJSON fromNetJson(String json, boolean handleApp, Class<T> classOfT) {
        BasicResponseJSON response = null;
        try {
            if (handleApp) {
                response = fromAppJson(json, classOfT);

            } else {
                response = JsonUtil.fromJson(json, classOfT);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("NetGsonHelper", Log.getStackTraceString(e));
        }

        return response;
    }

    @Nullable
    public static<T>  T fromCordovaJson(String cordovaJson, Class<T> classOfT) {
        T jsonObj = null;
        try {
            if(!TextUtils.isEmpty(cordovaJson) && !cordovaJson.equals("[]")) {
                JSONArray jsonArray = new JSONArray(cordovaJson);

                jsonObj = fromCordovaJson(jsonArray, classOfT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @Nullable
    public static<T> T fromCordovaJson(JSONArray jsonArray, Class<T> classOfT) {
        T jsonObj = null;
        try {
            String jsonStr = jsonArray.getString(0);
            jsonObj = JsonUtil.fromJson(jsonStr, classOfT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }



    /**
     * 专门处理 app 类型的 json(需要模板转换)
     */
    private static <T> T fromAppJson(String json, Class<T> classOfT) {
        Gson gson = getAppGson();
        return gson.fromJson(json, classOfT);
    }


    private static Gson getAppGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AppKind.class, new AppKindSerializer());
        gsonBuilder.registerTypeAdapter(BundleType.class, new BundleTypeSerializer());
        gsonBuilder.registerTypeAdapter(DisplayMode.class, new DisplayModeSerializer());
        gsonBuilder.registerTypeAdapter(DistributeMode.class, new DistributeModeSerializer());
        gsonBuilder.registerTypeAdapter(RecommendMode.class, new RecommendModeSerializer());
        gsonBuilder.registerTypeAdapter(BannerType.class, new BannerTypeSerializer());
        gsonBuilder.registerTypeAdapter(ProgressBarType.class, new ProgressBarTypeSerializer());
        return gsonBuilder.create();

    }
}
