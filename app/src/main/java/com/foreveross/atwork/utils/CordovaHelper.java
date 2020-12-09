package com.foreveross.atwork.utils;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dasunsy on 2016/9/23.
 */
public class CordovaHelper {

    /**
     * 兼容2.0 cordova 的contact 字段
     * */
    public static JSONObject getCompatibleContact(Employee emp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("identifier", emp.id);
            jsonObject.put("accountName", emp.username);
            jsonObject.put("username", emp.username);
            jsonObject.put("tenantId", emp.domainId);
            jsonObject.put("domain_id", emp.domainId);
            jsonObject.put("userId", emp.userId);
            jsonObject.put("user_id", emp.userId);
            jsonObject.put("name", emp.name);
            jsonObject.put("email", emp.email);
            jsonObject.put("mobile", emp.mobile);
            jsonObject.put("avatar", emp.avatar);
            jsonObject.put("gender", emp.gender);
            jsonObject.put("firstNameLetter", emp.initial);
            jsonObject.put("sort", emp.sortOrder);
            jsonObject.put("sort_order", emp.sortOrder);
            jsonObject.put("status", emp.status);

            String jobTitle = StringUtils.EMPTY;
            String orgName = StringUtils.EMPTY;
            String companyName = StringUtils.EMPTY;

            if(!ListUtil.isEmpty(emp.positions)) {
                jobTitle = emp.positions.get(0).jobTitle;
                orgName = emp.positions.get(0).orgName;
                companyName = emp.positions.get(0).corpName;
                jsonObject.put("positions", getCompatiblePositions(emp));
            }
            jsonObject.put("post", jobTitle);
            jsonObject.put("jobTitle", jobTitle);
            jsonObject.put("job_title", jobTitle);
            jsonObject.put("orgName", orgName);
            jsonObject.put("org_name", orgName);
            jsonObject.put("corpName", companyName);
            jsonObject.put("com_name", companyName);
            jsonObject.put("platform", "ANDROID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    public static JSONArray getCompatiblePositions(Employee emp) {
        JSONArray jsonArray = new JSONArray();
        try {
            for(Position position : emp.positions) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("com_name", position.corpName);
                jsonObject.put("org_name", position.orgName);
                jsonObject.put("job_title", position.jobTitle);
                jsonObject.put("path", position.path);
                jsonObject.put("type", position.type);
                jsonObject.put("primary", position.primary);

                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static ArrayList<User> getCompatibleSelectedUsers(String jsonArr) {
        ArrayList<User> userList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonArr);
            if (jsonArray == null) {
                return null;
            }
            if (jsonArray.length() == 0) {
                return null;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (json == null) {
                    continue;
                }
                User user = new User();
                user.mName = getJsonObjString(json, "identifier");
                user.mUserId = getJsonObjString(json, "userId");
                user.mDomainId = getJsonObjString(json, "domainId");
                user.mUsername = getJsonObjString(json, "accountName");
                user.mUsername = getJsonObjString(json, "username");
                user.mEmail = getJsonObjString(json, "email");
                user.mPhone = getJsonObjString(json, "mobile");
                user.mAvatar = getJsonObjString(json, "avatar");
                user.mName = getJsonObjString(json, "name");
                user.mGender = getJsonObjString(json, "gender");
                user.mSelect = true;

                userList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return userList;
    }

    public static String getJsonObjString(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }

    public static void doSuccess(@NonNull Object object, @NonNull CallbackContext callbackContext) {
        try {
            JSONObject jsonObj = new JSONObject(JsonUtil.toJson(object));
            doSuccess(jsonObj, callbackContext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void doSuccess(@NonNull JSONObject jsonObj, @NonNull CallbackContext callbackContext) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObj);
        callbackContext.sendPluginResult(pluginResult);
        callbackContext.success();
    }





}
