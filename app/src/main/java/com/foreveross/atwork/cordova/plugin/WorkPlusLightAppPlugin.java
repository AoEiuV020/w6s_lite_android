package com.foreveross.atwork.cordova.plugin;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksConfig;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class WorkPlusLightAppPlugin extends CordovaPlugin {

    //获取应用配置
    private static final String ACTION_GET_CONFIG = "getConfig";

    @Override
    public boolean execute(String action, JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {


        if (ACTION_GET_CONFIG.equalsIgnoreCase(action)) {
            return getConfig(jsonArray, callbackContext);
        }

        return super.execute(action, jsonArray, callbackContext);
    }

    private boolean getConfig(JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if (!StringUtils.isEmpty(jsonArray.toString())) {
            JSONObject jsonObject = new JSONObject();
            BeeWorksConfig beeWorksConfig = BeeWorks.getInstance().config;
            if (beeWorksConfig != null) {
                for(int j = 0; j < jsonArray.length(); j++) {
                    String key = jsonArray.getString(j);
                    String compatibleKey = getCompatibleKey(key);

                    if (!TextUtils.isEmpty(key)) {
                        Object value = beeWorksConfig.getValue(compatibleKey);
                        jsonObject.put(key, value);
                    }
                }

                Map<String,String> moreInfos = BeeWorks.getInstance().config.moreInfos;
                if (moreInfos != null) {
                    for(int i = 0; i < jsonArray.length(); i++) {
                        String key = jsonArray.getString(i);
                        String compatibleKey = getCompatibleKey(key);

                        if (!TextUtils.isEmpty(key)) {
                            if("colleagueCircleUrl".equalsIgnoreCase(compatibleKey)
                                && !StringUtils.isEmpty(DomainSettingsManager.getInstance().getColleagueCircleBasicUrl()))  {
                                jsonObject.put(key, UrlHandleHelper.checkEndPath(DomainSettingsManager.getInstance().getColleagueCircleBasicUrl()));

                                continue;
                            }

                            String value = moreInfos.get(compatibleKey);
                            if(TextUtils.isEmpty(value)) {
                                continue;
                            }
                            jsonObject.put(key, value);
                        }
                    }
                }
            }
            jsonObject.put("status", 0);
            callbackContext.success(jsonObject);
            return true;

        } else {
            JSONObject jsonError = new JSONObject();
            jsonError.put("message", "未定义此KEY");
            jsonError.put("status", -1);
            callbackContext.success(jsonError);
            return false;
        }
    }

    public String getCompatibleKey(String key) {
        if("colleagueCircle".equals(key)) {
            return "colleagueCircleUrl";

        } else if("schedule".equals(key)) {
            return "scheduleUrl";
        }

        return key;
    }
}
