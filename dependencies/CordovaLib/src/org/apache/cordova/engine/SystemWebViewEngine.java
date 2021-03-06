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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.infrastructure.webview.WorkplusDownloadListener;

import org.apache.cordova.CordovaBridge;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.ICordovaCookieManager;
import org.apache.cordova.LOG;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.OnWebEngineHandleListener;
import org.apache.cordova.PluginManager;
import org.apache.cordova.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Glue class between CordovaWebView (main Cordova logic) and SystemWebView (the actual View).
 * We make the Engine separate from the actual View so that:
 * A) We don't need to worry about WebView methods clashing with CordovaWebViewEngine methods
 * (e.g.: goBack() is void for WebView, and boolean for CordovaWebViewEngine)
 * B) Separating the actual View from the Engine makes API surfaces smaller.
 * Class uses two-phase initialization. However, CordovaWebView is responsible for calling .init().
 */
public class SystemWebViewEngine implements CordovaWebViewEngine {
    public static final String TAG = "SystemWebViewEngine";

    protected final SystemWebView webView;
    protected final SystemCookieManager cookieManager;
    protected CordovaPreferences preferences;
    protected CordovaBridge bridge;
    protected CordovaWebViewEngine.Client client;
    protected CordovaWebView parentWebView;
    protected CordovaInterface cordova;
    protected PluginManager pluginManager;
    protected CordovaResourceApi resourceApi;
    protected NativeToJsMessageQueue nativeToJsMessageQueue;
    private BroadcastReceiver receiver;

    protected Context mContext;
    protected View mRootView;
    protected ProgressBar mProgressBarLoading;
    protected View mVFakeStatusBar;
    protected View mWatermark;

    protected View mVTitleBar;
    protected TextView mTvTitle;
    protected ImageView mIvReload;

    private OnWebEngineHandleListener mOnWebEngineHandleListener;

    private WorkplusDownloadListener mDownloadListener;

    private Boolean mViewportSupport = true;


    /** Used when created via reflection. */
    public SystemWebViewEngine(Context context, CordovaPreferences preferences) {

        this(new SystemWebView(context), preferences);
    }

    public SystemWebViewEngine(SystemWebView webView) {
        this(webView, null);
    }

    public SystemWebViewEngine(SystemWebView webView, CordovaPreferences preferences) {
        this.preferences = preferences;
        mContext = webView.getContext();
        LayoutInflater inflater = (LayoutInflater) webView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.fragment_system_webview, null);
        this.webView = mRootView.findViewById(R.id.system_webview);
        this.webView.setVisibility(View.VISIBLE);
        mProgressBarLoading = mRootView.findViewById(R.id.webview_loading);
        mVFakeStatusBar = mRootView.findViewById(R.id.v_statusbar);
        mWatermark = mRootView.findViewById(R.id.watermark_view);
        mVTitleBar = mRootView.findViewById(R.id.webview_title_bar);
        mTvTitle = mVTitleBar.findViewById(R.id.webview_title);
        mIvReload = mVTitleBar.findViewById(R.id.iv_webview_refresh);
        cookieManager = new SystemCookieManager(webView);
    }

    public View getInflaterView() {
        return mRootView;
    }

    public ProgressBar getProgressBarLoading() {
        return mProgressBarLoading;
    }

    public View getVFakeStatusBar() {
        return mVFakeStatusBar;
    }

    public View getWatermarkView() {
        return mWatermark;
    }

    public View getTitleBarView() {
        return mVTitleBar;
    }

    public TextView getTitleTextView() {
        return mTvTitle;
    }

    public ImageView getReloadImageView() {
        return mIvReload;
    }

    @Override
    public void init(CordovaWebView parentWebView, CordovaInterface cordova, CordovaWebViewEngine.Client client,
                     CordovaResourceApi resourceApi, PluginManager pluginManager,
                     NativeToJsMessageQueue nativeToJsMessageQueue) {
        if (this.cordova != null) {
            throw new IllegalStateException();
        }
        // Needed when prefs are not passed by the constructor
        if (preferences == null) {
            preferences = parentWebView.getPreferences();
        }
        this.parentWebView = parentWebView;
        this.cordova = cordova;
        this.client = client;
        this.resourceApi = resourceApi;
        this.pluginManager = pluginManager;
        this.nativeToJsMessageQueue = nativeToJsMessageQueue;
        webView.init(this, cordova);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if(null != mDownloadListener) {
                    mDownloadListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
                }
            }
        });


        initWebViewSettings();

        nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.OnlineEventsBridgeMode(new NativeToJsMessageQueue.OnlineEventsBridgeMode.OnlineEventsBridgeModeDelegate() {
            @Override
            public void setNetworkAvailable(boolean value) {
                webView.setNetworkAvailable(value);
            }

            @Override
            public void runOnUiThread(Runnable r) {
                SystemWebViewEngine.this.cordova.getActivity().runOnUiThread(r);
            }
        }));


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
            nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.EvalBridgeMode(this, cordova));
        bridge = new CordovaBridge(pluginManager, nativeToJsMessageQueue);
        exposeJsInterface(webView, bridge);
    }

    public void setViewport(Boolean viewport) {
        this.mViewportSupport = viewport;
    }

    public void setDownloadListener(WorkplusDownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }

    @Override
    public CordovaWebView getCordovaWebView() {
        return parentWebView;
    }

    @Override
    public ICordovaCookieManager getCookieManager() {
        return cookieManager;
    }

    @Override
    public View getView() {
        return webView;
    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    @SuppressWarnings("deprecation")
    private void initWebViewSettings() {
        webView.setInitialScale(0);
        webView.setVerticalScrollBarEnabled(false);
        // Enable JavaScript
        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);

        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Set the nav dump for HTC 2.x devices (disabling for ICS, deprecated entirely for Jellybean 4.2)
        try {
            Method gingerbread_getMethod = WebSettings.class.getMethod("setNavDump", boolean.class);

            String manufacturer = android.os.Build.MANUFACTURER;
            LOG.d(TAG, "CordovaWebView is running on device made by: " + manufacturer);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB &&
                    android.os.Build.MANUFACTURER.contains("HTC")) {
                gingerbread_getMethod.invoke(settings, true);
            }
        } catch (NoSuchMethodException e) {
            LOG.d(TAG, "We are on a modern version of Android, we will deprecate HTC 2.3 devices in 2.8");
        } catch (IllegalArgumentException e) {
            LOG.d(TAG, "Doing the NavDump failed with bad arguments");
        } catch (IllegalAccessException e) {
            LOG.d(TAG, "This should never happen: IllegalAccessException means this isn't Android anymore");
        } catch (InvocationTargetException e) {
            LOG.d(TAG, "This should never happen: InvocationTargetException means this isn't Android anymore.");
        }

        //We don't save any form data in the application
        settings.setSaveFormData(false);
        settings.setSavePassword(false);

        // Jellybean rightfully tried to lock this down. Too bad they didn't give us a whitelist
        // while we do this
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        // Enable database
        // We keep this disabled because we use or shim to get around DOM_EXCEPTION_ERROR_16
        String databasePath = webView.getContext().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(databasePath);


        //Determine whether we're in debug or release mode, and turn on Debugging!
        ApplicationInfo appInfo = webView.getContext().getApplicationContext().getApplicationInfo();
        if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 &&
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            enableRemoteDebugging();
        }

        settings.setGeolocationDatabasePath(databasePath);

        // Enable DOM storage
        settings.setDomStorageEnabled(true);

        // Enable built-in geolocation
        settings.setGeolocationEnabled(true);

        // Enable AppCache
        // Fix for CB-2282
        settings.setAppCacheMaxSize(5 * 1048576);
        settings.setAppCachePath(databasePath);
        settings.setAppCacheEnabled(true);

        // Enable scaling
        // Fix for CB-12015
        if (null == mViewportSupport || mViewportSupport) {
            settings.setUseWideViewPort(true);
        } else {
            settings.setUseWideViewPort(false);

        }


        settings.setLoadWithOverviewMode(true);


        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT >= 11) {
            PackageManager pm = mContext.getPackageManager();
            boolean supportsMultiTouch =
                    pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH) ||
                            pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);

            settings.setDisplayZoomControls(!supportsMultiTouch);
        }

//        if (AtworkConfig.WEBVIEW_CONFIG.isTextZoomOnCommonTextSizeSetting()) {
//            int textSizeLevel = PersonalShareInfo.getInstance().getTextSizeLevel(mContext);
//
//            settings.setTextZoom((int) (100 + (textSizeLevel -1) * 0.2 * 100));
//        }


        // Fix for CB-1405
        // Google issue 4641
        String defaultUserAgent = settings.getUserAgentString();

        // Fix for CB-3360
        String overrideUserAgent = preferences.getString("OverrideUserAgent", null);
        if (overrideUserAgent != null) {
            settings.setUserAgentString(overrideUserAgent);
        } else {
            String appendUserAgent = preferences.getString("AppendUserAgent", null);
            if (appendUserAgent != null) {
                settings.setUserAgentString(defaultUserAgent + " " + appendUserAgent);
            }
        }


        String appendUserAgent = " atwork/android/2 workplus " + LanguageUtil.getWorkplusLocaleTag(webView.getContext().getApplicationContext());
        settings.setUserAgentString(settings.getUserAgentString() + " " + appendUserAgent);

        // End CB-3360

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        if (this.receiver == null) {
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    settings.getUserAgentString();
                }
            };
            webView.getContext().registerReceiver(this.receiver, intentFilter);
        }
        // end CB-1405
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void enableRemoteDebugging() {
        try {
            WebView.setWebContentsDebuggingEnabled(true);
        } catch (IllegalArgumentException e) {
            LOG.d(TAG, "You have one job! To turn on Remote Web Debugging! YOU HAVE FAILED! ");
            e.printStackTrace();
        }
    }

    private static void exposeJsInterface(WebView webView, CordovaBridge bridge) {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            LOG.i(TAG, "Disabled addJavascriptInterface() bridge since Android version is old.");
            // Bug being that Java Strings do not get converted to JS strings automatically.
            // This isn't hard to work-around on the JS side, but it's easier to just
            // use the prompt bridge instead.
            return;
        }
        SystemExposedJsApi exposedJsApi = new SystemExposedJsApi(bridge);
        webView.addJavascriptInterface(exposedJsApi, "_cordovaNative");
    }


    @Override
    public void changeTextSize(int size) {
        webView.getSettings().setTextZoom(size);
    }

    /**
     * Load the url into the webview.
     */
    @Override
    public void loadUrl(final String url, boolean clearNavigationStack) {
        webView.loadUrl(url);
    }

    @Override
    public void reload() {
        webView.reload();
    }

    @Override
    public String getUrl() {
        return webView.getUrl();
    }

    @Override
    public void stopLoading() {
        webView.stopLoading();
    }

    @Override
    public void clearCache() {
        webView.clearCache(true);
    }

    @Override
    public void clearHistory() {
        webView.clearHistory();
    }

    @Override
    public boolean canGoBack() {
        if(AtworkConfig.WEBVIEW_CONFIG.isUrlNeedComboBack(webView.getUrl())) {
            return webView.canGoBackOrForward(-2);
        }

        return webView.canGoBack();
    }

    /**
     * Go to previous page in history.  (We manage our own history)
     *
     * @return true if we went back, false if we are already at top
     */
    @Override
    public boolean goBack() {
        // Check webview first to see if there is a history
        // This is needed to support curPage#diffLink, since they are added to parentEngine's history, but not our history url array (JQMobile behavior)
        if (canGoBack()) {
            if(AtworkConfig.WEBVIEW_CONFIG.isUrlNeedComboBack(getUrl())) {
                webView.goBackOrForward(-2);

            } else {
                webView.goBack();

            }

            if(null != mOnWebEngineHandleListener) {
                mOnWebEngineHandleListener.onBackHistory();
            }

            return true;
        }
        return false;
    }

    @Override
    public void setPaused(boolean value) {
        if (value) {
            webView.onPause();
            webView.pauseTimers();
        } else {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    public void destroy() {
        webView.chromeClient.destroyLastDialog();
        webView.destroy();
        // unregister the receiver
        if (receiver != null) {
            try {
                webView.getContext().unregisterReceiver(receiver);
            } catch (Exception e) {
                LOG.e(TAG, "Error unregistering configuration receiver: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void evaluateJavascript(String js, ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(js, callback);
        } else {
            LOG.d(TAG, "This webview is using the old bridge");
        }
    }

    public void setOnWebEngineHandleListener(OnWebEngineHandleListener onWebEngineHandleListener) {
        mOnWebEngineHandleListener = onWebEngineHandleListener;
    }
}
