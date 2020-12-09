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
package com.foreverht.webview.x5.tbs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.infrastructure.webview.AtworkWebView;
import com.foreveross.atwork.infrastructure.webview.OnSetWebUiChangeListener;
import com.foreveross.atwork.infrastructure.webview.OnWebActivityActionListener;
import com.foreveross.atwork.infrastructure.webview.WorkplusDownloadListener;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.LOG;
import org.apache.cordova.OnWebEngineHandleListener;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.PluginManager;
import org.apache.cordova.engine.SystemCookieManager;
import org.apache.cordova.engine.SystemWebChromeClient;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.apache.cordova.x5engine.X5WebChromeClient;
import org.apache.cordova.x5engine.X5WebView;
import org.apache.cordova.x5engine.X5WebViewClient;
import org.apache.cordova.x5engine.X5WebViewEngine;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


/**
 * This class is the main Android activity that represents the Cordova
 * application. It should be extended by the user to load the specific
 * html file that contains the application.
 * <p>
 * As an example:
 * <p>
 * <pre>
 *     package org.apache.cordova.examples;
 *
 *     import android.os.Bundle;
 *     import org.apache.cordova.*;
 *
 *     public class Example extends CordovaActivity {
 *       &#64;Override
 *       public void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         super.init();
 *         // Load your application
 *         loadUrl(launchUrl);
 *       }
 *     }
 * </pre>
 * <p>
 * Cordova xml configuration: Cordova uses a configuration file at
 * res/xml/config.xml to specify its settings. See "The config.xml File"
 * guide in cordova-docs at http://cordova.apache.org/docs for the documentation
 * for the configuration. The use of the set*Property() methods is
 * deprecated in favor of the config.xml file.
 */
public abstract class CordovaFragment extends Fragment implements AtworkWebView {

    public static String TAG = "CordovaFragment";

    // The webview for our app
    public CordovaWebView appView;


    public CordovaWebView getAppView() {
        return appView;
    }

    // Keep app running when pause is received. (default = true)
    // If true, then the JavaScript and native code continue to run in the background
    // when another application (activity) is started.
    protected boolean keepRunning = true;

    // Read from config.xml:
    protected CordovaPreferences preferences;
    protected String launchUrl;
    protected ArrayList<PluginEntry> pluginEntries;
    protected CordovaFragmentInterfaceImpl cordovaInterface;

    private View contentView;

    //---------------------------------- workplus variables --------------------------------------
    public static final String INJECTION_TOKEN = "applocal://";
    public static final String HTTS_INJECTION_TOKEN = "cordova.min.js";


    public static final String SHOW_HIDDEN_CLOSE = "show_hidden_close";
    public static final String LIGHT_APP_VIEW = "LIGHT_APP_VIEW";
    public static final String INIT_URL = "INIT_URL";
    public static final String HIDE_TITLE = "HIDE_TITLE";
    public static final String NO_NEED_SHOW_TITLE = "$No_Title$";
    public static final String NEED_AUTH = "NEED_AUTH";
    public static final String USE_SYSTEM = "USE_SYSTEM";
    public static final String VIEWPORT_SUPPORT = "VIEWPORT_SUPPORT";

    private boolean mNeedAuth;

    private boolean mUseSystem;

    private OnSetWebUiChangeListener mOnSetWebUiChangeListener;

    public String mLoadUrl;

    private String mCoverUrl;

    protected Activity mActivity;

    private AppBundles mAppBundle;

    private String mInitUrl;

    private boolean mNeedHideTitle;

    private OnWebActivityActionListener showOrHiddenWebViewCloseView;

    /**
     * 控制屏幕旋转的周期, 只在 url start->   url finish 期间做一次操作,  期间的内置 url 跳转不管
     */
    private boolean mHandlingScreenOrientation = false;

    private WorkplusDownloadListener mWorkplusDownloadListener;
    private Boolean mViewportSupport = true;

    private String mW6sWebLocalPackage;

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData();

        if (contentView == null) {
            init();
        }
        initVFakeStatusBar(mNeedHideTitle);
        refreshProgressBarColor(mAppBundle);

        return contentView;
    }


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.i(TAG, "Apache Cordova native platform version " + CordovaWebView.CORDOVA_VERSION + " is starting");
        LOG.d(TAG, "CordovaActivity.onCreate()");

        // need to activate preferences before super.onCreate to avoid "requestFeature() must be called before adding content" exception
        loadConfig();

//        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);

        super.onCreate(savedInstanceState);

        cordovaInterface = makeCordovaInterface();
        if (savedInstanceState != null) {
            cordovaInterface.restoreInstanceState(savedInstanceState);
        }
    }

    protected void init() {
        appView = makeWebView();
        createViews();


        if (appView.getEngine() instanceof X5WebViewEngine) {
            X5WebViewEngine x5WebViewEngine = (X5WebViewEngine) appView.getEngine();
            x5WebViewEngine.setViewport(mViewportSupport);

        } else if(appView.getEngine() instanceof SystemWebViewEngine) {
            SystemWebViewEngine systemWebViewEngine = (SystemWebViewEngine) appView.getEngine();
            systemWebViewEngine.setViewport(mViewportSupport);
        }

        if (!appView.isInitialized()) {
            appView.init(cordovaInterface, pluginEntries, preferences);
        }

        //expose 修改字体大小
        if(null != mOnSetWebUiChangeListener) {
            mOnSetWebUiChangeListener.onSetTextZoom();
        }

        cordovaInterface.onCordovaInit(appView.getPluginManager());

        checkClear(appView.getView(), UrlHandleHelper.shouldClearCache(mInitUrl));

        // Wire the hardware volume controls to control media if desired.
        String volumePref = preferences.getString("DefaultVolumeStream", "");
        if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }

        appView.getEngine().setOnWebEngineHandleListener(new OnWebEngineHandleListener() {
            @Override
            public void onBackHistory() {
                if (AtworkConfig.COMMAND_RECEIVE_WEBVIEW_GOBACK) {
                    loadJS("goBack()");
                }
            }
        });

        if (appView.getEngine() instanceof X5WebViewEngine) {
            X5WebViewEngine x5WebViewEngine = (X5WebViewEngine) appView.getEngine();
            x5WebViewEngine.setEnableCache(UrlHandleHelper.shouldDisableCache(mInitUrl));
            x5WebViewEngine.setDownloadListener(mWorkplusDownloadListener);
            x5WebViewEngine.setViewport(mViewportSupport);
            makeX5WebViewClient();
            return;
        }

        SystemWebViewEngine systemWebViewEngine = (SystemWebViewEngine) appView.getEngine();
        systemWebViewEngine.setDownloadListener(mWorkplusDownloadListener);
        systemWebViewEngine.setViewport(mViewportSupport);
        makeSystemWebViewClient();
    }


    @SuppressWarnings("deprecation")
    protected void loadConfig() {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this.getActivity());
        preferences = parser.getPreferences();
        preferences.setPreferencesBundle(getActivity().getIntent().getExtras());
        launchUrl = parser.getLaunchUrl();
        pluginEntries = parser.getPluginEntries();
//        Config.parser = parser;
    }

    //Suppressing warnings in AndroidStudio
    @SuppressWarnings({"deprecation", "ResourceType"})
    protected void createViews() {
        //Why are we setting a constant as the ID? This should be investigated
//        appView.getView().setId(100);
//        appView.getView().setLayoutParams(new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//
        setContentView(getInflaterView());
        appView.getView().requestFocusFromTouch();

    }

    /**
     * Construct the default web view object.
     * <p>
     * Override this to customize the webview that is used.
     */
    protected CordovaWebView makeWebView() {
        return new CordovaWebViewImpl(makeWebViewEngine());
    }

    protected CordovaWebViewEngine makeWebViewEngine() {
        if (shouldUseSystemWebViewEngine()) {
            return CordovaWebViewImpl.createEngine(this.getActivity(), preferences, SystemWebViewEngine.class.getCanonicalName());
        }
        return CordovaWebViewImpl.createEngine(this.getActivity(), preferences, "org.apache.cordova.x5engine.X5WebViewEngine");
    }

    private boolean shouldUseSystemWebViewEngine() {
        if(mUseSystem) {
            return true;
        }

        if(AtworkConfig.WEBVIEW_CONFIG.isForcedUseX5()) {
            return false;
        }

        if(forcedUseSystemWebViewEngine()) {
            return true;
        }

        if(QbSdk.isTbsCoreInited()) {
            return false;
        }



        return true;
    }

    private boolean forcedUseSystemWebViewEngine() {
        return !RomUtil.commandX5Rom() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }


    @Override
    public void setDownloadListener(WorkplusDownloadListener downloadListener) {
        mWorkplusDownloadListener = downloadListener;
    }

    @Override
    public View getWebView() {
        return getAppView().getView();
    }

    protected CordovaFragmentInterfaceImpl makeCordovaInterface() {
        return new CordovaFragmentInterfaceImpl(getActivity(), this) {
            @Override
            public Object onMessage(String id, Object data) {
                // Plumb this to CordovaActivity.onMessage for backwards compatibility
                return CordovaFragment.this.onMessage(id, data);
            }
        };
    }


    /**
     * Called when the activity will start interacting with the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        LOG.d(TAG, "Resumed the activity.");

        if (this.appView == null) {
            return;
        }
        // Force window to have focus, so application always
        // receive user input. Workaround for some devices (Samsung Galaxy Note 3 at least)
        this.getActivity().getWindow().getDecorView().requestFocus();

        this.appView.handleResume(this.keepRunning);

    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    @Override
    public void onStop() {
        super.onStop();
        LOG.d(TAG, "Stopped the activity.");

        if (this.appView == null) {
            return;
        }
        this.appView.handleStop();
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    public void onStart() {
        super.onStart();
        LOG.d(TAG, "Started the activity.");

        if (this.appView == null) {
            return;
        }
        this.appView.handleStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.appView == null) {
            return;
        }
        this.appView.handlePause(this.keepRunning);
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    public void onDestroy() {
        LOG.d(TAG, "CordovaActivity.onDestroy()");
        super.onDestroy();
        if (this.appView != null && appView.getEngine() instanceof X5WebViewEngine) {
            appView.handleDestroy();
            appView.clearCache();
            appView.clearHistory();
        }
        if (appView != null && appView.getEngine() instanceof SystemWebViewEngine) {
            appView.handleDestroy();
            appView.clearCache();
            appView.clearHistory();
        }
        PermissionsManager.getInstance().clear();
        System.gc();
        System.runFinalization();
        System.gc();
        super.onDestroy();
//        appView = null;


    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        // Capture requestCode here so that it is captured in the setActivityResultCallback() case.
        cordovaInterface.setActivityResultRequestCode(requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
        super.onActivityResult(requestCode, resultCode, intent);
        cordovaInterface.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable).
     * The errorCode parameter corresponds to one of the ERROR_* constants.
     *
     * @param errorCode   The error code corresponding to an ERROR_* value.
     * @param description A String describing the error.
     * @param failingUrl  The url that failed to load.
     */
    public void onReceivedError(final int errorCode, final String description, final String failingUrl) {
        final CordovaFragment me = this;

        // If errorUrl specified, then load it
        final String errorUrl = preferences.getString("errorUrl", null);
        if ((errorUrl != null) && (!failingUrl.equals(errorUrl)) && (appView != null)) {
            // Load URL on UI thread
            me.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    me.appView.showWebPage(errorUrl, false, true, null);
                }
            });
        }
        Logger.e(TAG,  description + " (" + failingUrl + ")");
//        // If not, then display error dialog
//        else {
//            final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
//            me.getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    if (exit) {
//                        me.appView.getView().setVisibility(View.GONE);
//                        me.displayError("Application Error", description + " (" + failingUrl + ")", "OK", exit);
//                    }
//                }
//            });
//        }
    }

    /**
     * Display an error dialog and optionally exit application.
     */
    public void displayError(final String title, final String message, final String button, final boolean exit) {
        final CordovaFragment me = this;
        me.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(me.getActivity());
                    dlg.setMessage(message);
                    dlg.setTitle(title);
                    dlg.setCancelable(false);
                    dlg.setPositiveButton(button,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (exit) {
                                        getActivity().finish();
                                    }
                                }
                            });
                    dlg.create();
                    dlg.show();
                } catch (Exception e) {
                    getActivity().finish();
                }
            }
        });
    }

    /*
     * Hook in Cordova for menu plugins
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (appView != null) {
            appView.getPluginManager().postMessage("onCreateOptionsMenu", menu);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (appView != null) {
            appView.getPluginManager().postMessage("onPrepareOptionsMenu", menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (appView != null) {
            appView.getPluginManager().postMessage("onOptionsItemSelected", item);
        }
        return true;
    }

    /**
     * Called when a message is sent to plugin.
     *
     * @param id   The message id
     * @param data The message data
     * @return Object or null
     */
    public Object onMessage(String id, Object data) {
        if ("onReceivedError".equals(id)) {
            JSONObject d = (JSONObject) data;
            try {
                this.onReceivedError(d.getInt("errorCode"), d.getString("description"), d.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if ("exit".equals(id)) {
//            getActivity().finish();
        }
        return null;
    }

    public void onSaveInstanceState(Bundle outState) {
        cordovaInterface.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Called by the system when the device configuration changes while your activity is running.
     *
     * @param newConfig The new device configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.appView == null) {
            return;
        }
        PluginManager pm = this.appView.getPluginManager();
        if (pm != null) {
            pm.onConfigurationChanged(newConfig);
        }
    }

    /**
     * Called by the system when the user grants permissions
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        try {
            cordovaInterface.onRequestPermissionResult(requestCode, permissions, grantResults);
        } catch (JSONException e) {
            LOG.d(TAG, "JSONException: Parameters fed into the method are not valid");
            e.printStackTrace();
        }

    }

    //------------------------------------- workplus logic method --------------------------------------

    public void initBundle(OnWebActivityActionListener showOrHiddenWebViewCloseView, AppBundles lightApp, String initUrl, boolean hideTitle, boolean needAuth, boolean useSystem, Boolean viewportSupport) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SHOW_HIDDEN_CLOSE, showOrHiddenWebViewCloseView);
        bundle.putString(INIT_URL, initUrl);
        bundle.putParcelable(LIGHT_APP_VIEW, lightApp);
        bundle.putBoolean(HIDE_TITLE, hideTitle);
        bundle.putBoolean(NEED_AUTH, needAuth);
        bundle.putBoolean(USE_SYSTEM, useSystem);
        if (null != viewportSupport) {
            bundle.putBoolean(VIEWPORT_SUPPORT, viewportSupport);
        }
        setArguments(bundle);
    }

    private void initData() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            showOrHiddenWebViewCloseView = (OnWebActivityActionListener) bundle.getSerializable(SHOW_HIDDEN_CLOSE);
            mAppBundle = bundle.getParcelable(LIGHT_APP_VIEW);
            mInitUrl = bundle.getString(INIT_URL);
            mNeedHideTitle = bundle.getBoolean(HIDE_TITLE);
            mNeedAuth = bundle.getBoolean(NEED_AUTH, false);
            mUseSystem = bundle.getBoolean(USE_SYSTEM, false);
            mViewportSupport = bundle.getBoolean(VIEWPORT_SUPPORT, true);
        }

    }


    @Override
    public String getCrawlerCoverUrl() {

        String url = "";
        if (!StringUtils.isNull(mCoverUrl)) {
            url = mCoverUrl.replaceAll("\"", "");
        }
        return url;
    }


    @Override
    public boolean canGoBack() {
        return null != appView && appView.canGoBack();
    }

    @Override
    public boolean backHistory() {
        boolean back = appView.backHistory();
        return back;
    }

    @Override
    public void reload() {
        appView.reload();

    }

    @Override
    public void makeKeyboardCompatible() {
        //do nothing
    }


    @Override
    public void changeTextSize(int size) {
        appView.changeTextSize(size);
    }

    @Override
    public void setCmdFinishCheckNoGoBack(boolean cmd) {
        appView.setCmdFinishCheckNoGoBack(cmd);
    }

    /**
     * Load the url into the webview.
     */
    @Override
    public void loadUrl(String url) {
        if (appView == null) {
            init();
        }

        // If keepRunning
        this.keepRunning = preferences.getBoolean("KeepRunning", true);
        mLoadUrl = url;

        mW6sWebLocalPackage =UrlHandleHelper.getValueEnsured(url, "w6s_local_package");


        url = handleWebFormUrl(url);
        BaseApplicationLike.sOrgId = parseParams(UrlHandleHelper.getParamsFromUrl(url));
        if (url.contains("{") && url.contains("}")) {
            url = url.replaceAll("\\{", URLEncoder.encode("{"));
            url = url.replaceAll("\\}", URLEncoder.encode("}"));
        }
        appView.loadUrl(url);
    }

    @Override
    public void evaluateJavascript(String js, android.webkit.ValueCallback<String> callback) {
        if(this.appView == null) {
            return;
        }

        appView.evaluateJavascript(js, callback);
    }

    @Override
    public void loadJS(String js) {
        if (this.appView == null) {
            Logger.e(TAG, "appview is null");
            return;
        }

        js = makeCompatible(js);
        js = makeCallSafely(js);

        appView.loadUrl("javascript:" + js);
    }


    private String makeCallSafely(String js) {
        return "try { " + js + " } catch(e) {}";
    }

    @NonNull
    private String makeCompatible(String js) {
        if(!js.contains("(") && !js.contains(")")) {
            js += "()";
        }
        return js;
    }

    /**
     * 系统语言变化
     */
    public void onChangeLanguage() {
        if (null != appView) {
            ((X5WebViewEngine)appView.getEngine()).updateLanguageUserAgent();
        }
    }


    protected boolean isNeedHandled(String url) {
        return false;
    }

    protected String handleWebFormUrl(String url) {
        return url;
    }

    /**
     * 是否需要过滤 url(后期继续扩展)
     *
     * @param url
     */
    public boolean filterUrl(String url) {
        return url.startsWith("intent://");
    }


    @Override
    public void setOnSetWebTitleListener(OnSetWebUiChangeListener onSetWebUiChangeListener) {
        mOnSetWebUiChangeListener = onSetWebUiChangeListener;
    }

    @Override
    public ProgressBar getProgressBarLoading() {
        if (appView == null) {
            return null;
        }
        if (appView.getEngine() instanceof X5WebViewEngine) {
            return ((X5WebViewEngine)appView.getEngine()).getProgressBarLoading();
        }
        return ((SystemWebViewEngine)appView.getEngine()).getProgressBarLoading();
    }

    @Override
    public View getInflaterView() {
        if (appView.getEngine() instanceof X5WebViewEngine) {
            return ((X5WebViewEngine) appView.getEngine()).getInflaterView();
        }
        return ((SystemWebViewEngine)appView.getEngine()).getInflaterView();
    }


    @Override
    public View getWatermarkView() {
        if (appView.getEngine() instanceof X5WebViewEngine) {
            return ((X5WebViewEngine) appView.getEngine()).getWatermarkView();
        }
        return ((SystemWebViewEngine)appView.getEngine()).getWatermarkView();
    }

    @Override
    public View getVFakeStatusBar() {
        if (appView.getEngine() instanceof X5WebViewEngine) {
            return ((X5WebViewEngine) appView.getEngine()).getVFakeStatusBar();
        }
        return ((SystemWebViewEngine) appView.getEngine()).getVFakeStatusBar();
    }


    @Override
    public TextView getTitleTextView() {
        if(appView.getEngine() instanceof X5WebViewEngine) {
            return ((X5WebViewEngine) appView.getEngine()).getTitleTextView();

        }

        return ((SystemWebViewEngine) appView.getEngine()).getTitleTextView();
    }

    @Override
    public View getTitleBarView() {
        if(appView.getEngine() instanceof X5WebViewEngine) {
            return ((X5WebViewEngine) appView.getEngine()).getTitleBarView();

        }

        return ((SystemWebViewEngine) appView.getEngine()).getTitleBarView();
    }


    @Override
    public ImageView getReloadImageView() {
        if(appView.getEngine() instanceof X5WebViewEngine) {
            return ((X5WebViewEngine) appView.getEngine()).getReloadImageView();

        }
        return ((SystemWebViewEngine) appView.getEngine()).getReloadImageView();
    }


    public abstract String getNavigationColor();

    /**
     * 解析url中的org_id_
     * 如果url没有，则赋值为当前组织的org_id;
     *
     * @param urlParams
     * @return
     */
    protected abstract String parseParams(Map<String, String> urlParams);

    protected abstract void refreshProgressBarColor(AppBundles lightApp);

    protected abstract void checkClear(View webview, boolean forcedClear);

    protected abstract void initVFakeStatusBar(boolean needHideTitle);

    private void makeX5WebViewClient() {
        final X5WebViewClient client = new X5WebViewClient((X5WebViewEngine) appView.getEngine()) {
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
//                super.onReceivedHttpAuthRequest(view, handler, host, realm);
                Logger.e(TAG, "onReceivedHttpAuthRequest");
                if(mNeedAuth) {
                    String loginUserRealUserName = LoginUserInfo.getInstance().getLoginUserRealUserName(getActivity());
                    String secret = LoginUserInfo.getInstance().getLoginSecret(getActivity());

//                    Logger.e("test", "loginUserRealUserName -> " + loginUserRealUserName + "   secret->" + secret);
                    handler.proceed(loginUserRealUserName, secret);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, com.tencent.smtt.export.external.interfaces.SslError error) {
                super.onReceivedSslError(view, handler, error);
                Logger.e(TAG, "Load fail error on ssl" + error.toString());
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
                handler.proceed();
            }

            private String checkWebCache(String url) {
                Uri uri = Uri.parse(url);
                String cacheMd5 = mW6sWebLocalPackage;
                if (uri == null || uri.getHost() == null || TextUtils.isEmpty(cacheMd5)) {
                    return url;
                }
                String path = uri.getPath();
                String localPath = AtWorkDirUtils.getInstance().getWebCacheDir() + File.separator + cacheMd5 + path;
                File localFile = new File(localPath);
                if (!localFile.exists()) {
                    return url;
                }
                url = url.replace(uri.getScheme() + "://" + uri.getHost(), "content://" + AtworkConfig.APP_ID + ".webCacheProvider" + File.separator + cacheMd5);
                return url;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                Logger.e(TAG, "shouldInterceptRequest :" + url);
                if (url.startsWith("local://")) {
                    url = UrlHandleHelper.handle(getActivity(), url, mAppBundle);
                }

                if (isNeedHandled(url)) {
                    url = handleWebFormUrl(url);
                    view.loadUrl(url);
                }

                if (!mHandlingScreenOrientation) {
                    UrlHandleHelper.handleLandScape(getActivity(), url);
                    mHandlingScreenOrientation = true;
                }
                url = checkWebCache(url);
                if (url != null && !(url.contains(INJECTION_TOKEN))) {
                    return super.shouldInterceptRequest(view, url);
                }
                WebResourceResponse response = null;
                String assetPath = "";
                if (null != url && url.contains(INJECTION_TOKEN)) {
                    assetPath = url.substring(url.indexOf(INJECTION_TOKEN) + INJECTION_TOKEN.length(), url.length());
                }
                try {
                    response = new WebResourceResponse("application/javascript","UTF8", getActivity().getAssets().open(assetPath));
                } catch (IOException e) {
                    e.printStackTrace();

                    Logger.e(TAG, "shouldInterceptRequest :" + url + "     assets IOException " + e.getLocalizedMessage());

                }
                return response;
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                if(null != showOrHiddenWebViewCloseView
                        && showOrHiddenWebViewCloseView.handleSchemaUrlJump(getActivity(), url)) {
                    return true;
                }

                return filterUrl(url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Logger.e(TAG, "onReceivedError");
                Logger.e("Webview onReceivedLoadError", "errorCode = " + errorCode + " description = " + description + " failUrl = " + failingUrl);
                if (mOnSetWebUiChangeListener == null) {
                    return;
                }
                mOnSetWebUiChangeListener.onUrlWrong();

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Logger.e(TAG, "onPageStarted");
                url = handleWebFormUrl(url);
                super.onPageStarted(view, url, favicon);
                if (null != mOnSetWebUiChangeListener) {
                    mOnSetWebUiChangeListener.onUrlStart();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String log =  "onPageFinished and cordova obj = ";
                Logger.e(TAG, log);
                super.onPageFinished(view, url);

                UrlHandleHelper.handleLandScape(getActivity(), url);

                mHandlingScreenOrientation = false;

                if (showOrHiddenWebViewCloseView != null) {
                    if (appView.canGoBack()) {
                        showOrHiddenWebViewCloseView.showCloseView();

                    } else {
                        showOrHiddenWebViewCloseView.hiddenCloseView();
                    }
                }

                //暴露接口出来让设定标题
                if (null != mOnSetWebUiChangeListener) {
                    handleOnWebSetTitle(view, url);
                    mOnSetWebUiChangeListener.onUrlFinish(url);
                }



                view.evaluateJavascript(getNavigationColor(), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        LogUtil.e("color", "color -> " + value);
                        String color = value.replaceAll("\"", "");
                        if (mNeedHideTitle) {

                            if (!StringUtils.isNull(color)) {
                                try {
                                    getVFakeStatusBar().setBackgroundColor(Color.parseColor(color));
                                    mOnSetWebUiChangeListener.onStatusBarChange(color);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                Logger.e(TAG, "onReceivedClientCertRequest");
            }

            @Override
            public void onDetectedBlankScreen(String s, int i) {
                Logger.e(TAG, "onDetectedBlankScreen");
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                String log =  "onReceivedError and error = " + webResourceError.getDescription() + " and code = " + webResourceError.getErrorCode();
                Logger.e(TAG, log);

//                super.onReceivedError(webView, webResourceRequest, webResourceError);
//                onReceivedError(webView, webResourceError.getErrorCode(), webResourceError.getDescription().toString(), webResourceRequest.getUrl().toString());
            }

            @Override
            public void onLoadResource(WebView webView, String url) {
                Logger.e(TAG, "onLoadResource = " + url);

                if(null != url && url.contains(INJECTION_TOKEN)) {
                    injectScriptFile(webView, "www/cordova.min.js");
                }
            }

            @Override
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                String log = "onReceivedHttpError requestURL = " + webResourceRequest.getUrl()  + "respone code = " + webResourceResponse.getStatusCode() + "response " + webResourceResponse.getReasonPhrase();
                Logger.e(TAG, log);
            }

            @Override
            public void onTooManyRedirects(WebView webView, Message message, Message message1) {
                Logger.e(TAG, "onTooManyRedirects");
            }
        };

        X5WebChromeClient x5WebChromeClient = new X5WebChromeClient((X5WebViewEngine) appView.getEngine()) {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                ProgressBar progressBar = getProgressBarLoading();
                if (progressBar == null) {
                    return;
                }
                if (i == 100) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(i);
            }
        };


        ((X5WebView) appView.getView()).setWebViewClient(client);
        ((X5WebView) appView.getView()).setWebChromeClient(x5WebChromeClient);
    }


    private void handleOnWebSetTitle(WebView view, String url) {
        String str = view.getTitle();
        if (NO_NEED_SHOW_TITLE.equalsIgnoreCase(str)) {
            return;
        }

        Log.e("url_title", str + "   -> " + url);

        mOnSetWebUiChangeListener.onSetWebTitle(str, url);
    }

    private void injectScriptFile(WebView view, String scriptFile) {
        String jsStr =  FileUtil.getFromAssets(view.getContext(), scriptFile);
        if (!StringUtils.isEmpty(jsStr)) {
            view.loadUrl("javascript:" + jsStr);
        }
    }


    private void makeSystemWebViewClient() {
        SystemCookieManager.setAcceptCookie((android.webkit.WebView) getWebView());

        final SystemWebViewClient client = new SystemWebViewClient((SystemWebViewEngine) appView.getEngine()) {
            @Override
            public void onReceivedHttpAuthRequest(android.webkit.WebView view, android.webkit.HttpAuthHandler handler, String host, String realm) {
                if(mNeedAuth) {
                    String loginUserRealUserName = LoginUserInfo.getInstance().getLoginUserRealUserName(getActivity());
                    String secret = LoginUserInfo.getInstance().getLoginSecret(getActivity());

//                    Logger.e("test", "loginUserRealUserName -> " + loginUserRealUserName + "   secret->" + secret);
                    handler.proceed(loginUserRealUserName, secret);
                }
            }

            @Override
            public void onReceivedSslError(android.webkit.WebView view, android.webkit.SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            boolean isInject = false;
            @Override
            public android.webkit.WebResourceResponse shouldInterceptRequest(android.webkit.WebView view, String url) {
                Logger.e("WEBKIT", "url");
                if (isInject) {
                    return super.shouldInterceptRequest(view, url);
                }
                if (url.startsWith("local://")) {
                    url = UrlHandleHelper.handle(getActivity(), url, mAppBundle);
                }

                if (isNeedHandled(url)) {
                    url = handleWebFormUrl(url);
                    view.loadUrl(url);
                }

                if (!mHandlingScreenOrientation) {
                    UrlHandleHelper.handleLandScape(getActivity(), url);
                    mHandlingScreenOrientation = true;
                }

                if (url != null && !(url.contains(INJECTION_TOKEN) || (mLoadUrl.startsWith("https://") && url.contains(HTTS_INJECTION_TOKEN)))) {
                    return super.shouldInterceptRequest(view, url);
                }

                android.webkit.WebResourceResponse response = null;
                String assetPath = "";
                boolean needInject = false;
                if (null != url && url.contains(INJECTION_TOKEN)) {
                    assetPath = url.substring(url.indexOf(INJECTION_TOKEN) + INJECTION_TOKEN.length(), url.length());
                    needInject = true;
                }
                if (mLoadUrl.startsWith("https://") && url.contains(HTTS_INJECTION_TOKEN)) {
                    assetPath = "www/cordova.min.js";
                }
                if (url.startsWith("https://") && url.contains(HTTS_INJECTION_TOKEN)) {
                    assetPath = "www/cordova.min.js";
                    needInject = true;
                }
                if (!needInject) {
                    return super.shouldInterceptRequest(view, url);
                }

                try {
                    response = new android.webkit.WebResourceResponse(
                            "application/javascript",
                            "UTF8",
                            getActivity().getAssets().open(assetPath)
                    );
                    isInject = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {

                if(null != showOrHiddenWebViewCloseView
                        && showOrHiddenWebViewCloseView.handleSchemaUrlJump(getActivity(), url)) {
                    return true;
                }

                return filterUrl(url);
            }

            @Override
            public void onReceivedError(android.webkit.WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (mOnSetWebUiChangeListener == null) {
                    return;
                }
                mOnSetWebUiChangeListener.onUrlWrong();
                Logger.e("Webview onReceivedLoadError", "errorCode = " + errorCode + " description = " + description + " failUrl = " + failingUrl);
            }

            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                url = handleWebFormUrl(url);
                super.onPageStarted(view, url, favicon);
                if (null != mOnSetWebUiChangeListener) {
                    mOnSetWebUiChangeListener.onUrlStart();
                }
            }

            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                UrlHandleHelper.handleLandScape(getActivity(), url);
                mHandlingScreenOrientation = false;

                if (showOrHiddenWebViewCloseView != null) {
                    if (null != appView && appView.canGoBack()) {
                        showOrHiddenWebViewCloseView.showCloseView();

                    } else {
                        showOrHiddenWebViewCloseView.hiddenCloseView();
                    }
                }

                //暴露接口出来让设定标题
                if (null != mOnSetWebUiChangeListener) {
                    String str = view.getTitle();
                    if (NO_NEED_SHOW_TITLE.equalsIgnoreCase(str)) {
                        return;
                    }

                    Log.e("url_title", str + "   -> " + url);

                    mOnSetWebUiChangeListener.onSetWebTitle(str, url);
                    mOnSetWebUiChangeListener.onUrlFinish(url);
                }

                if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {


                    view.evaluateJavascript(getNavigationColor(), new android.webkit.ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogUtil.e("color", "color -> " + value);
                            String color = value.replaceAll("\"", "");
                            if (mNeedHideTitle) {

                                if (!StringUtils.isNull(color)) {
                                    try {
                                        getVFakeStatusBar().setBackgroundColor(Color.parseColor(color));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                mOnSetWebUiChangeListener.onStatusBarChange(color);
                            }
                        }
                    });
                }
            }
        };

        SystemWebChromeClient systemWebChromeClient = new SystemWebChromeClient((SystemWebViewEngine) appView.getEngine()) {

            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                ProgressBar progressBar = getProgressBarLoading();
                if (progressBar == null) {
                    return;
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Logger.e("system_webview", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        };

        ((SystemWebView) appView.getView()).setWebViewClient(client);
        ((SystemWebView) appView.getView()).setWebChromeClient(systemWebChromeClient);
    }

}