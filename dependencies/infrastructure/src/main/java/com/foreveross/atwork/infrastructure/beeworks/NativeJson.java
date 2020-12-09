package com.foreveross.atwork.infrastructure.beeworks;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/12/23.
 */
public class NativeJson {

    public List<NativeContent> contents;

    public String mConfigType;

    public BeeWorksNavigation mNavigation;

    public boolean mShowNavi = true;

    public static NativeJson createInstance(String tabId) {
        String fileName = "tab_" + tabId + ".json";
        try {
            String content = getContent(fileName);
            if (content == null){
                return null;
            }
            return createInstance(new JSONObject(content));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NativeJson createInstanceFromString(String content){
        try {
            return createInstance(new JSONObject(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static NativeJson createInstance(JSONObject jsonObject) {
        NativeJson nativeJson = new NativeJson();

        List<NativeContent> contents = new ArrayList<>();
        nativeJson.mConfigType = jsonObject.optString("configType");
        nativeJson.mNavigation = new Gson().fromJson(jsonObject.optString("navigation"), BeeWorksNavigation.class);
        nativeJson.mShowNavi = jsonObject.optBoolean("showNavigation", true);
        JSONArray arrays = jsonObject.optJSONArray("contents");
        if (arrays == null) {
            return nativeJson;
        }
        for (int i = 0; i < arrays.length(); i++) {
            NativeContent content = NativeContent.createInstance(arrays.optJSONObject(i));
            contents.add(content);
        }
        nativeJson.contents = contents;
        return nativeJson;
    }

    /**
     * 读取本地文件中JSON字符串
     *
     * @return
     */
    private static String getContent(String fileName) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;
        try{
            InputStream in = NativeJson.class.getResourceAsStream("/assets/" + fileName);
            if (in == null){
                return null;
            }
            InputStreamReader reader = new InputStreamReader(in);
            bf = new BufferedReader(reader);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        }finally {
            if (bf!=null){
                bf.close();
            }
        }

        return stringBuilder.toString();
    }
}
