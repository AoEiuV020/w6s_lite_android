package com.foreverht.webview;

import android.content.Context;
import android.view.View;

import org.apache.cordova.engine.SystemWebView;
import org.crosswalk.engine.XWalkCordovaView;
import org.xwalk.core.XWalkCookieManager;


/**
 * Created by dasunsy on 2017/10/28.
 */

public class SdkEngine {

    public static void clearCookies() {
        new XWalkCookieManager().removeAllCookie();
    }

    public static void clearResourceCache(View webView) {
        if(webView instanceof XWalkCordovaView) {
            XWalkCordovaView x5WebView = (XWalkCordovaView) webView;
            x5WebView.clearCache(true);


        } else if(webView instanceof SystemWebView) {
            SystemWebView systemWebView = (SystemWebView) webView;
            systemWebView.clearCache(true);
        }

    }

    public static void setCookies(View webView, String url, String value) {
        XWalkCookieManager xWalkCookieManager = new XWalkCookieManager();
        xWalkCookieManager.setAcceptFileSchemeCookies(true);
        xWalkCookieManager.setAcceptCookie(true);

        xWalkCookieManager.setCookie(url, value);

    }




    public static void init(Context context) {

    }

    public static boolean isX5Init() {
        return false;
    }

    public static String getCookies(View webView, String url) {
        XWalkCookieManager xWalkCookieManager = new XWalkCookieManager();
        return xWalkCookieManager.getCookie(url);
    }
}

