package com.foreveross.atwork.tab.nativeTab.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Created by lingen on 15/12/24.
 */
public class H5TabJson implements Serializable {

    public String url;

    public boolean showNavigation;

    public static H5TabJson createInstanceFromTab(String tabId){
        H5TabJson h5TabJson = new H5TabJson();
        String fileName = "tab_" + tabId + ".json";
        try {
            String content = getContent(fileName);
            if (content==null){
                return null;
            }
            JSONObject jsonObject = new JSONObject(content);

            h5TabJson.url = jsonObject.optString("url");
            h5TabJson.showNavigation = jsonObject.optBoolean("showNavigation");
            return h5TabJson;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static H5TabJson createInstanceFromString(String content){
        H5TabJson h5TabJson = new H5TabJson();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(content);
            h5TabJson.url = jsonObject.optString("url");
            h5TabJson.showNavigation = jsonObject.optBoolean("showNavigation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return h5TabJson;
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
            InputStream in = H5TabJson.class.getResourceAsStream("/assets/" + fileName);
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
