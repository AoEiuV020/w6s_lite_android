package com.foreveross.atwork.api.sdk.app.requestJson;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.bugFix.W6sBugFixManager;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.shared.bugFix.W6sBugFixPersonShareInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by reyzhang22 on 16/4/15.
 */
public class AppRequestJson {

    /**
     * 组装请求json
     * @param accessList
     * @param adminList
     * @return
     */
    public static JSONObject makeAppRequest(String orgCode, List<App> accessList, List<App> adminList) {
        JSONObject timeJson = new JSONObject();
        JSONArray accessArray = makeListRequest(orgCode, accessList, timeJson);
        JSONArray adminArray = makeListRequest(orgCode, adminList, timeJson);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_list", accessArray);
            jsonObject.put("admin_list", adminArray);
            if (timeJson.length() > 0) {
                jsonObject.put("shortcut_refresh_time", timeJson.getLong("maxTime"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static  JSONArray makeListRequest(String orgCode, List<App> list, JSONObject timeJson) {
        JSONArray jsonArray = new JSONArray();
        if (list.isEmpty()) {
            return jsonArray;
        }

        boolean forcedRefresh = W6sBugFixManager.getInstance().isNeedForcedCheckAppRefresh(orgCode);
        long maxShortTime = -1;
        for (App app : list) {
            if (app == null) {
                continue;
            }
            if (ListUtil.isEmpty(app.mBundles)){
                continue;
            }
            JSONObject json = new JSONObject();
            try {
                json.put("app_id", app.mAppId);
                json.put("category_id", app.mCategoryId);
                if (forcedRefresh) {
                    json.put("refresh_time", -1);

                } else {
                    json.put("refresh_time", Math.max(app.mAppRefreshTime, app.mCategoryRefreshTime));

                }
                JSONArray entrances = new JSONArray();
                for (AppBundles bundle : app.mBundles) {
                    entrances.put(bundle.mBundleId);
                    maxShortTime = Math.max(bundle.mUploadTime, maxShortTime);
                }
                json.put("app_entries", entrances);
                jsonArray.put(json);
                timeJson.put("maxTime", maxShortTime);
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }


}
