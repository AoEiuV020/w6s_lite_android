package com.foreveross.atwork.infrastructure.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by dasunsy on 16/6/5.
 */
public class JsonUtil {


    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static String toJsonExpose(Object obj) {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        return gson.toJson(obj);
    }


    public static <T> T fromJson(String json, Class<T> classOfT) {
        T result = null;
        try {
            result = new Gson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static <T> T fromJsonExpose(String json, Class<T> classOfT) {

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();


        T result = null;
        try {
            result = gson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String toJsonList(List jsonList) {
        Gson gson = new Gson();
        return gson.toJson(jsonList, new TypeToken<List>(){}.getType());
    }


}
