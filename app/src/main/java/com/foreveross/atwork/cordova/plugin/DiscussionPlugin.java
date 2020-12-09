package com.foreveross.atwork.cordova.plugin;


import android.text.TextUtils;

import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.SearchDiscussionResponse;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by wuzejie on 19/10/10.
 */
public class DiscussionPlugin extends WorkPlusCordovaPlugin {

    //获取个人所有群聊
    private static final String GET_DISCUSSION_DATA = "getDiscussionData";
    //搜索群聊
    private static final String SEARCH_DISCUSSION_DATA = "searchDiscussion";

    @Override
    public boolean execute(String action, String rawArgs, final CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        //return super.execute(action, rawArgs, callbackContext);
        if (GET_DISCUSSION_DATA.equalsIgnoreCase(action)) {
            cordova.getThreadPool().execute(() -> {
                List<Discussion>  discussionList = DiscussionRepository.getInstance().queryAllDiscussions();
                getDiscussionData(callbackContext, discussionList, true);
            });

            return true;
        } else if(SEARCH_DISCUSSION_DATA.equalsIgnoreCase(action)){
            SearchDiscussionResponse searchDiscussionResponse = NetGsonHelper.fromCordovaJson(rawArgs, SearchDiscussionResponse.class);
            if(searchDiscussionResponse != null && !TextUtils.isEmpty(searchDiscussionResponse.getSearchValue())) {
                cordova.getThreadPool().execute(() -> {
                    List<Discussion> discussionList = DiscussionRepository.getInstance().searchDiscussion(searchDiscussionResponse.getSearchValue());
                    searchData(callbackContext, discussionList,true);
                });
            }else {
                searchData(callbackContext, null,false);
            }
            return true;
        }
        return false;
    }

    /**
     * 获取个人所有群聊
     */
    private void getDiscussionData(CallbackContext callbackContext, List<Discussion>  discussionList, Boolean success) {
        AtworkApplicationLike.runOnMainThread(() -> {
            Gson gson = new Gson();
            try {
                if(success) {
                    JSONArray jsonArray = new JSONArray(gson.toJson(discussionList));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", 0);
                    jsonObject.put("message", "获取成功");
                    jsonObject.put("data", jsonArray);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }else{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", -1);
                    jsonObject.put("message", "获取失败");
                    jsonObject.put("data", "");
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 搜索群聊
     */
    private void searchData(CallbackContext callbackContext, List<Discussion>  discussionList, Boolean success) {
        AtworkApplicationLike.runOnMainThread(() -> {
            Gson gson = new Gson();
            try {
                if(success) {
                    JSONArray jsonArray = new JSONArray(gson.toJson(discussionList));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", 0);
                    jsonObject.put("message", "搜索成功");
                    jsonObject.put("data", jsonArray);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", -1);
                    jsonObject.put("message", "搜索失败");
                    jsonObject.put("data", "");
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
