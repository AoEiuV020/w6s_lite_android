/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package org.apache.cordova.engine;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebView;

import org.apache.cordova.ICordovaCookieManager;

public class SystemCookieManager implements ICordovaCookieManager {

    protected final WebView mWebView;
    private final CookieManager mCookieManager;

    //Added because lint can't see the conditional RIGHT ABOVE this
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SystemCookieManager(WebView webview) {
        this.mWebView = webview;
        mCookieManager = CookieManager.getInstance();

        setAcceptCookie(webview);
    }


    public static void setAcceptCookie(WebView webView) {
        CookieManager cookieManager = CookieManager.getInstance();
        //REALLY? Nobody has seen this UNTIL NOW?
        cookieManager.setAcceptFileSchemeCookies(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
    }

    public void setCookiesEnabled(boolean accept) {
        mCookieManager.setAcceptCookie(accept);
    }

    public void setCookie(final String url, final String value) {
        mCookieManager.setCookie(url, value);
    }

    public String getCookie(final String url) {
        return mCookieManager.getCookie(url);
    }

    public void clearCookies() {
        mCookieManager.removeAllCookie();
    }

    public void flush() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCookieManager.flush();
        }
    }
};
