package com.foreverht.webview;

import android.content.Context;
import android.view.View;

/**
 * Created by dasunsy on 2017/10/28.
 */

public class WebkitSdkUtil {


    public static void clearCookies() {
        SdkEngine.clearCookies();
    }

    public static void clearResourceCache(View webView) {
        SdkEngine.clearResourceCache(webView);
    }

    public static void setCookies(View webView, String url, String value) {
        SdkEngine.setCookies(webView, url, value);

    }

    public static String getCookies(View webView, String url) {
        return SdkEngine.getCookies(webView, url);
    }

    public static void init(Context context) {
        SdkEngine.init(context);
    }


    public static boolean isX5Init() {
        return SdkEngine.isX5Init();
    }


}
