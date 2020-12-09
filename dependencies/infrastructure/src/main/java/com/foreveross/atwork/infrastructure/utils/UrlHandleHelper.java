package com.foreveross.atwork.infrastructure.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by dasunsy on 2017/2/7.
 */

public class UrlHandleHelper {

    public static final String HIDDEN_SHARE = "HIDDEN_SHARE=1";

    public static final String DISABLE_CACHE = "DISABLE_CACHE=1";

    public static final String CLEAR_CACHE = "CLEAR_CACHE=1";

    public static boolean shouldDisableCache(String url) {
//            return true;
        return null != url && url.contains(DISABLE_CACHE);
    }

    public static boolean shouldClearCache(String url) {
//        return true;
        return null != url && url.contains(CLEAR_CACHE);
    }

    public static String replaceOrgKeyParam(Context context, String url, @Nullable AppBundles appBundle) {
        if (!StringUtils.isEmpty(url)) {

            url = replaceOrgCodeParam(context, url, appBundle, "{{orgCode}}");
            url = replaceOrgCodeParam(context, url, appBundle, "{{orgcode}}");
            url = replaceOrgCodeParam(context, url, appBundle, "{{org_id}}");
            url = replaceOrgCodeParam(context, url, appBundle, "{{orgId}}");
        }

        return url;
    }

    private static String replaceOrgCodeParam(Context context, String url, @Nullable AppBundles appBundle, String keyCheck) {
        if(url.contains(keyCheck)) {
            String orgCode = null;
            if(null != appBundle) {
                orgCode = appBundle.mOrgId;
            }

            if(StringUtils.isEmpty(orgCode)) {
                orgCode = PersonalShareInfo.getInstance().getCurrentOrg(context);
            }

            if(!StringUtils.isEmpty(orgCode)) {
                url = url.replace(keyCheck, orgCode);

            }
        }


        return url;
    }


    public static String replaceConventionalKeyParams(Context context, String url) {
        url = replaceOrgKeyParam(context, url, null);
        url = replaceBasicKeyParams(context, url);

        return url;
    }

    @Nullable
    public static String replaceBasicKeyParams(Context context, String url) {
        if (!StringUtils.isEmpty(url)) {

            String loginUserUserName = LoginUserInfo.getInstance().getLoginUserUserName(context);

            if (url.contains("{{username}}")) {
                url = url.replace("{{username}}", loginUserUserName);
            }

            if (url.contains("{{username/name}}")) {
                url = url.replace("{{username/name}}", loginUserUserName + "/" + LoginUserInfo.getInstance().getLoginUserName(context) );
            }

            if (url.contains("{{name}}")) {
                url = url.replace("{{name}}", LoginUserInfo.getInstance().getLoginUserName(context) );
            }

            if(url.contains("{{language}}")) {
                url = url.replace("{{language}}", LanguageUtil.getWorkplusLocaleTag(context));

            }


            if(url.contains("{{userId}}")) {
                url = url.replace("{{userId}}", LoginUserInfo.getInstance().getLoginUserId(context));

            }


            if(url.contains("{{userid}}")) {
                url = url.replace("{{userid}}", LoginUserInfo.getInstance().getLoginUserId(context));

            }

            if(url.contains("{{user_id}}")) {
                url = url.replace("{{user_id}}", LoginUserInfo.getInstance().getLoginUserId(context));

            }

            if(url.contains("{{domainId}}")) {
                url = url.replace("{{domainId}}", AtworkConfig.DOMAIN_ID);

            }

            if(url.contains("{{domainid}}")) {
                url = url.replace("{{domainid}}", AtworkConfig.DOMAIN_ID);

            }

            if(url.contains("{{domain_id}}")) {
                url = url.replace("{{domain_id}}", AtworkConfig.DOMAIN_ID);

            }

        }

        return url;
    }


    public static String makeSuffix(String url) {
        if(!url.endsWith("/")) {
            url += "/";
        }

        return url;
    }

    public static String handle(Context context, String url, AppBundles appBundle) {
        if(null != appBundle && appBundle.useOfflinePackage()) {
            url = "file://" + getReleasePath(context, appBundle) + url.substring("local://".length());

        } else {
            url = "file:///android_asset/www/" + url.substring("local://".length());

        }

        return url;
    }

    public static String getReleasePath(Context context, AppBundles appBundle) {
        String currentOrg = PersonalShareInfo.getInstance().getCurrentOrg(context);

        return AtWorkDirUtils.getInstance().getLightAppOfflineDataOrgDir(currentOrg, appBundle.mBundleId) + appBundle.mSettings.mMobileBehaviour.mRelease + "/";
    }

    /**
     * url 横屏竖屏的控制
     * @param activity
     * @param url
     * @return url 是否含有指定参数, 并作出处理
     * */
    public static boolean handleLandScape(Activity activity, String url) {
        url = fixProtocolHead(url);
        Uri uri = Uri.parse(url);
        String isLandscapeStr = null;
        String isLockStr = null;
        try {
            isLandscapeStr = uri.getQueryParameter("landscape");
            isLockStr = uri.getQueryParameter("lock");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean isLock = null;
        Boolean isLandScape = null;

        if(null != isLockStr) {
            isLock = Boolean.parseBoolean(isLockStr);
        }

        if(null != isLandscapeStr) {
            isLandScape = Boolean.parseBoolean(isLandscapeStr);
        }

        //因 ios 不支持 lock 跟 isLandScape 同时处理, 所以暂时不考虑 lock(暂时加回去 20190916)
        if(null != isLandScape) {
            return ScreenUtils.landscapeMode(activity, isLandScape, isLock);
        }

        return false;

    }

    public static boolean isHiddenShare(@Nullable String url) {
        return (null != url && url.contains(HIDDEN_SHARE));
    }

    /**
     * 初始化的 url, 非轻应用的时候
     * */
    @Nullable
    public static Boolean handleInitUrlFullscreen(String url) {
        url = fixProtocolHead(url);
        Uri uri = Uri.parse(url);

        String isFullScreenStr = uri.getQueryParameter("fullScreen");
        if(null != isFullScreenStr) {
            int isFullScreen = 0;
            try {
                isFullScreen = Integer.parseInt(isFullScreenStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;

            }
            return 1 == isFullScreen;
        }

        return null;
    }

    /**
     * 是否支持 viewport
     * */
    @Nullable
    public static Boolean supportViewport(String url) {
        url = fixProtocolHead(url);
        Uri uri = Uri.parse(url);

        String viewportSupportStr = uri.getQueryParameter("w6s_viewport_support");
        if(null != viewportSupportStr) {
            int viewportSupport = 0;
            try {
                viewportSupport = Integer.parseInt(viewportSupportStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;

            }
            return 1 == viewportSupport;
        }

        return null;
    }



    public static Boolean getWatermarkEnable(String url) {
        if(StringUtils.isEmpty(url)) {
            return false;
        }

        url = fixProtocolHead(url);
        Uri uri = Uri.parse(url);

        String isFullScreenStr = uri.getQueryParameter("watermark_enable");
        if(null != isFullScreenStr) {
            int isFullScreen = 0;
            try {
                isFullScreen = Integer.parseInt(isFullScreenStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;

            }
            return 1 == isFullScreen;
        }

        return null;
    }


    public static String getTheme(String url) {
        if(StringUtils.isEmpty(url)) {
            return "default";
        }

        url = fixProtocolHead(url);
        Uri uri = Uri.parse(url);
        String theme = uri.getQueryParameter("theme");
        if(StringUtils.isEmpty(theme)) {
            return "default";
        }

        return theme;

    }

    public static String fixProtocolHead(String url) {
        if(StringUtils.isEmpty(url)) {
            return url;
        }

        String lowerUrl = url.intern().toLowerCase();
        if (!lowerUrl.startsWith("file://") && !lowerUrl.startsWith("http://") && !lowerUrl.startsWith("https://")) {
            url = "http://" + url;
        }

        return url;
    }

    public static Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> paramsInPairs = new LinkedHashMap<>();
        try {
            URL urlString = new URL(url);
            String query = urlString.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                paramsInPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        } catch (Exception e) {

        }
        return paramsInPairs;

    }


    public static String addParam(String url, String key, String value) {
        Map<String, String> kvs = new HashMap<>();
        kvs.put(key, value);

        return addParams(url, kvs);
    }

    public static String addParams(String url, Map<String, String> keyValues) {

        //firstly, try replace
        List<String> keyHandledList = new ArrayList<>();
        for(Map.Entry<String, String> kv : keyValues.entrySet()) {
            String keyEqual = kv.getKey() + "=";

            if(url.contains(keyEqual)) {
                url = url.replaceAll("(" + keyEqual + ")[^&]*", keyEqual + kv.getValue());
                keyHandledList.add(kv.getKey());
            }

        }
        keyHandledList.stream().forEach(keyValues::remove);

        //then, try add
        StringBuilder newUrl = new StringBuilder(url);
        for(Map.Entry<String, String> kv : keyValues.entrySet()) {
            if(newUrl.toString().contains("?") ) {
                newUrl.append("&");

            } else {
                newUrl.append("?");
            }

            newUrl.append(kv.getKey()).append("=").append(kv.getValue());
        }

        return newUrl.toString();

    }


    public static String addPathInfo(String url, String pathInfo) {
        String handleUrl = checkEndPath(url);
        return handleUrl + pathInfo;
    }

    @NotNull
    public static String checkEndPath(String url) {
        return url.endsWith("/") ? url : url + "/";
    }


    public static String getValueEnsured(String url, String key) {

        if(StringUtils.isEmpty(url)) {
            return StringUtils.EMPTY;
        }

        url = fixProtocolHead(url);
        Uri uri = Uri.parse(url);

        String value = uri.getQueryParameter(key);
        if(!StringUtils.isEmpty(value)) {
            return value;
        }

        String keyParam = key + "=";
        int keyStartIndex = url.indexOf(keyParam);

        if(-1 == keyStartIndex) {
            return StringUtils.EMPTY;
        }

        int keyLastIndex = url.indexOf("&", keyStartIndex);

        if(keyStartIndex <= keyLastIndex) {
            value = url.substring(keyStartIndex + keyParam.length(), keyLastIndex);

        } else {
            value = url.substring(keyStartIndex + keyParam.length());

        }

        return value;
    }

}
