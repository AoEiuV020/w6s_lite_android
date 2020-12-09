package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by dasunsy on 15/11/23.
 */
public class PreferencesUtils {


    public static String makeNameLegal(String preferenceName) {

        return preferenceName.replace("/", "_");
    }

    public static void clear(Context context, String preferenceName) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);
        SharedPreferences.Editor edit = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        edit.clear().apply();
    }

    public static void putString(Context context, String preferenceName, String key, String value) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static void putStringToList(Context context, String preferenceName, String key, String value) {
        List<String> originalList = getStringList(context, preferenceName, key);
        if(null == originalList) {
            originalList = new ArrayList<>();
        }

        originalList.add(value);

        putStringList(context, preferenceName, key, originalList);
    }


    public static void putStringList(Context context, String preferenceName, String key, @Nullable List<String> values) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);


        if(ListUtil.isEmpty(values)) {
            remove(context, preferenceName, key);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(String value: values) {
            sb.append(value).append(",");
        }

        putString(context, preferenceName, key, sb.toString());
    }

    @Nullable
    public static List<String> getStringList(Context context, String preferenceName, String key) {
        String valuesStr = getString(context, preferenceName, key);
        if(StringUtils.isEmpty(valuesStr)) {
            return null;
        }

        return Arrays.asList(valuesStr.split(","));
    }


    public static void putStringToSet(Context context, String preferenceName, String key, String value) {
        Set<String> originalSet = getStringSet(context, preferenceName, key);
        if(null == originalSet) {
            originalSet = new HashSet<>();
        }

        originalSet.add(value);

        putStringSet(context, preferenceName, key, originalSet);
    }

    public static void putStringSet(Context context, String preferenceName, String key, @Nullable Set<String> values) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(key, values);
        editor.apply();
    }


    public static Set<String> getStringSet(Context context, String preferenceName, String key) {
        return getStringSet(context, preferenceName, key, null);
    }

    @Nullable
    public static Set<String> getStringSet(Context context, String preferenceName, String key, @Nullable Set<String> defValues) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getStringSet(key, defValues);
    }

    public static String getString(Context context, String preferenceName, String key) {
        context = findContext(context);
        return getString(context, preferenceName, key, null);
    }

    public static String getString(Context context, String preferenceName, String key, String defaultValue) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    @Nullable
    public static <T> T getObject(Context context, String preferenceName, String key, Class<T> classOfT) {
        String objectStr = getString(context, preferenceName, key);
        if(StringUtils.isEmpty(objectStr)) {
            return null;
        }

        return JsonUtil.fromJson(objectStr, classOfT);
    }

    public static void updateObject(Context context, String preferenceName, String key, Object obj) {
        String objectStr = JsonUtil.toJson(obj);
        putString(context, preferenceName, key, objectStr);
    }

    public static void putInt(Context context, String preferenceName, String key, int value) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String preferenceName, String key) {
        context = findContext(context);
        return getInt(context, preferenceName, key, -1);
    }


    public static int getInt(Context context, String preferenceName, String key, int defaultValue) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }


    public static void putLong(Context context, String preferenceName, String key, long value) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
    }


    public static long getLong(Context context, String preferenceName, String key) {
        context = findContext(context);
        return getLong(context, preferenceName, key, -1);
    }

    public static long getLong(Context context, String preferenceName, String key, long defaultValue) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public static void putFloat(Context context, String preferenceName, String key, float value) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String preferenceName,  String key) {
        context = findContext(context);
        return getFloat(context, preferenceName, key, -1);
    }


    public static float getFloat(Context context, String preferenceName, String key, float defaultValue) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    public static void putBoolean(Context context, String preferenceName, String key, boolean value) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static boolean getBoolean(Context context, String preferenceName, String key) {
        context = findContext(context);
        return getBoolean(context, preferenceName, key, false);
    }


    public static boolean getBoolean(Context context, String preferenceName, String key, boolean defaultValue) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }


    public static void remove(Context context, String preferenceName, String key) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);

        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();
    }


    public static Map<String, ?> getAll(Context context, String preferenceName) {
        context = findContext(context);
        preferenceName = makeNameLegal(preferenceName);
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getAll();
    }

    private static Context findContext(Context context) {
        if (context == null) {
            context = BaseApplicationLike.baseContext;
        }
        return context;
    }

}
