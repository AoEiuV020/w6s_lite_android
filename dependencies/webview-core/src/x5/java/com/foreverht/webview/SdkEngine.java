package com.foreverht.webview;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.tencent.smtt.sdk.QbSdk;

import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.x5engine.X5CookieManager;
import org.apache.cordova.x5engine.X5WebView;

/**
 * Created by dasunsy on 2017/10/28.
 */

public class SdkEngine {
    public static void clearCookies() {
        com.tencent.smtt.sdk.CookieManager.getInstance().removeAllCookie();

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            cookieManager.removeAllCookies(null);

        } else {
            cookieManager.removeAllCookie();

        }

    }

    public static void clearResourceCache(View webView) {
        if(webView instanceof X5WebView) {
            X5WebView x5WebView = (X5WebView) webView;
            x5WebView.clearCache(true);


        } else if(webView instanceof SystemWebView) {
            SystemWebView systemWebView = (SystemWebView) webView;
            systemWebView.clearCache(true);
        }
    }


    public static void setCookies(View webView, String url, String value) {
        if (webView instanceof X5WebView) {
            X5CookieManager x5CookieManager = new X5CookieManager((X5WebView) webView);
            x5CookieManager.setCookiesEnabled(true);

            x5CookieManager.setCookie(url, value);

        } else if(webView instanceof SystemWebView) {
            SystemWebView systemWebView = (SystemWebView) webView;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.createInstance(systemWebView.getContext());
            }

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setCookie(url, value);

        }

    }

    public static String getCookies(View webView, String url) {
        if (webView instanceof X5WebView) {
            X5CookieManager x5CookieManager = new X5CookieManager((X5WebView) webView);
            return x5CookieManager.getCookie(url);

        } else if(webView instanceof SystemWebView) {
            CookieManager cookieManager = CookieManager.getInstance();

            return cookieManager.getCookie(url);
        }

        return StringUtils.EMPTY;
    }

    public static void init(Context context) {
        //因为需要用到 x5 的文件服务, 所以 x5 初始化独立处理
    }


    public static boolean isX5Init() {
        return QbSdk.isTbsCoreInited();
    }
}
