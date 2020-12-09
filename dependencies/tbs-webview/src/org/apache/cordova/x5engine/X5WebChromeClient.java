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
package org.apache.cordova.x5engine;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.PermissionRequest;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;

import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * This class is the WebChromeClient that implements callbacks for our web view.
 * The kind of callbacks that happen here are on the chrome outside the document,
 * such as onCreateWindow(), onConsoleMessage(), onProgressChanged(), etc. Related
 * to but different than CordovaWebViewClient.
 */
public class X5WebChromeClient extends WebChromeClient {

    private static final int FILECHOOSER_RESULTCODE = 5173;
    private static final String LOG_TAG = "X5WebChromeClient";
    private long MAX_QUOTA = 100 * 1024 * 1024;
    protected final X5WebViewEngine parentEngine;

    // the video progress view
    private View mVideoProgressView;

    private CordovaDialogsHelper dialogsHelper;
    private Context appContext;

    private IX5WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mCustomView;
    //拍照之后的存储地址
    private String imgPath;

    public X5WebChromeClient(X5WebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        appContext = parentEngine.webView.getContext();
        dialogsHelper = new CordovaDialogsHelper(appContext);
    }

    /**
     * Tell the client to display a javascript alert dialog.
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        dialogsHelper.showAlert(message, new CordovaDialogsHelper.Result() {
            @Override public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    /**
     * Tell the client to display a confirm dialog to the user.
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        dialogsHelper.showConfirm(message, new CordovaDialogsHelper.Result() {
            @Override
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    /**
     * Tell the client to display a prompt dialog to the user.
     * If the client returns true, WebView will assume that the client will
     * handle the prompt dialog and call the appropriate JsPromptResult method.
     *
     * Since we are hacking prompts for our own purposes, we should not be using them for
     * this purpose, perhaps we should hack console.log to do this instead!
     */
    @Override
    public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {
        // Unlike the @JavascriptInterface bridge, this method is always called on the UI thread.
        String handledRet = parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
        if (handledRet != null) {
            result.confirm(handledRet);
        } else {
            dialogsHelper.showPrompt(message, defaultValue, new CordovaDialogsHelper.Result() {
                @Override
                public void gotResult(boolean success, String value) {
                    if (success) {
                        result.confirm(value);
                    } else {
                        result.cancel();
                    }
                }
            });
        }
        return true;
    }

    /**
     * Handle database quota exceeded notification.
     */
    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
            long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater)
    {
        LOG.d(LOG_TAG, "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", estimatedSize, currentQuota, totalUsedQuota);
        quotaUpdater.updateQuota(MAX_QUOTA);
    }

    @TargetApi(8)
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage)
    {
        if (consoleMessage.message() != null)
            LOG.d(LOG_TAG, "%s: Line %d : %s" , consoleMessage.sourceId() , consoleMessage.lineNumber(), consoleMessage.message());
         return super.onConsoleMessage(consoleMessage);
    }

    @Override
    /**
     * Instructs the client to show a prompt to ask the user to set the Geolocation permission state for the specified origin.
     *
     * This also checks for the Geolocation Plugin and requests permission from the application  to use Geolocation.
     *
     * @param origin
     * @param callback
     */
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
        //Get the plugin, it should be loaded
        CordovaPlugin geolocation = parentEngine.pluginManager.getPlugin("Geolocation");
        if(geolocation != null && !geolocation.hasPermisssion())
        {
            geolocation.requestPermissions(0);
        }

    }

    //TODO API level 7 is required for this, see if we could lower this using something else
//    @Override
//    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
//        parentEngine.getCordovaWebView().showCustomView(view, callback);
//    }

    public void onHideCustomView() {
        parentEngine.getCordovaWebView().hideCustomView();
    }

    /**
     * Ask the host application for a custom progress view to show while
     * a <video> is loading.
     * @return View The progress view.
     */
    public View getVideoLoadingProgressView() {

        if (mVideoProgressView == null) {
            // Create a new Loading view programmatically.

            // create the linear layout
            LinearLayout layout = new LinearLayout(parentEngine.getView().getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.setLayoutParams(layoutParams);
            // the proress bar
            ProgressBar bar = new ProgressBar(parentEngine.getView().getContext());
            LinearLayout.LayoutParams barLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            barLayoutParams.gravity = Gravity.CENTER;
            bar.setLayoutParams(barLayoutParams);
            layout.addView(bar);

            mVideoProgressView = layout;
        }
    return mVideoProgressView;
    }

    // <input type=file> support:
    // openFileChooser() is for pre KitKat and in KitKat mr1 (it's known broken in KitKat).
    // For Lollipop, we use onShowFileChooser().
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        this.openFileChooser(uploadMsg, "*/*");
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType ) {
        this.openFileChooser(uploadMsg, acceptType, null);
    }

    public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                Log.d(LOG_TAG, "Receive file chooser URL: " + result);
                uploadMsg.onReceiveValue(result);
            }
        }, intent, FILECHOOSER_RESULTCODE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, final FileChooserParams fileChooserParams) {

        String[] acceptTypes =  fileChooserParams.getAcceptTypes();
        if(acceptTypes[0].equalsIgnoreCase("image/*")){
            String[] selectPicTypeStr = {"拍照", "从手机相册选择"};
            AlertDialog mAlertDialog = new AlertDialog.Builder(appContext)
                    .setItems(selectPicTypeStr,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    switch (which) {
                                        // 拍照
                                        case 0:

                                            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult( parentEngine.cordova.getActivity(), new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                                                @Override
                                                public void onGranted() {
                                                    setVmPolicy();
                                                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                    imgPath = AtWorkDirUtils.getInstance().getImageDir(appContext) + System.currentTimeMillis()  + ".jpg";
                                                    File file = new File(imgPath);
                                                    if (!file.exists()) {
                                                        try {
                                                            file.createNewFile();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                                    handleOnShowFileCaptureIntent(filePathsCallback, captureIntent, true);
                                                }

                                                @Override
                                                public void onDenied(String permission) {
                                                    filePathsCallback.onReceiveValue(null);
                                                }
                                            });
                                            break;
                                        case 1:
                                            Intent intent = fileChooserParams.createIntent();
                                            handleOnShowFileChooserIntent(filePathsCallback, intent, true);
                                            break;
                                        default:
                                            break;
                                    }

                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            filePathsCallback.onReceiveValue(null);
                        }
                    }).show();
        }
        else{
            //进入相册
            Intent intent = fileChooserParams.createIntent();
            handleOnShowFileChooserIntent(filePathsCallback, intent, true);
        }
        return true;
    }

    /**
     * android 7.0系统解决拍照的问题
     */
    private void setVmPolicy(){
        //7.0之后,调用相机需要再动态给权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    /**
     * 拍照处理返回的数据
     * @param filePathsCallback
     * @param intent
     * @param needCompatible
     */
    private void handleOnShowFileCaptureIntent(final ValueCallback<Uri[]> filePathsCallback, Intent intent, boolean needCompatible) {
        try {
            parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {

                    if(FileUtil.getSize(imgPath) == 0) {
                        FileUtil.delete(imgPath);
                        filePathsCallback.onReceiveValue(null);
                        return;
                    }
                    Uri[] result = {Uri.parse(imgPath)};
                    Log.d(LOG_TAG, "Receive file capture URL: " + result);

                    filePathsCallback.onReceiveValue(result);
                }
            }, intent, FILECHOOSER_RESULTCODE);

        } catch (ActivityNotFoundException e) {
            Log.w(LOG_TAG, "No activity found to handle file capture intent." + e);
            if (needCompatible) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentCompatible = new Intent(Intent.ACTION_GET_CONTENT);
                        intentCompatible.addCategory(Intent.CATEGORY_OPENABLE);
                        intentCompatible.setType("*/*");

                        handleOnShowFileChooserIntent(filePathsCallback, intentCompatible, false);
                    }
                }, 300);


            } else {
                filePathsCallback.onReceiveValue(null);

            }
        }
    }

    private void handleOnShowFileChooserIntent(final ValueCallback<Uri[]> filePathsCallback, Intent intent, boolean needCompatible) {
        try {
            parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    if(null == intent) {
                        filePathsCallback.onReceiveValue(null);
                        return;
                    }
                    Uri[] result = android.webkit.WebChromeClient.FileChooserParams.parseResult(resultCode, intent);
                    Log.d(LOG_TAG, "Receive file chooser URL: " + result);
                    filePathsCallback.onReceiveValue(result);
                }
            }, intent, FILECHOOSER_RESULTCODE);

        } catch (ActivityNotFoundException e) {
            Log.w(LOG_TAG, "No activity found to handle file chooser intent." + e);
            if (needCompatible) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentCompatible = new Intent(Intent.ACTION_GET_CONTENT);
                        intentCompatible.addCategory(Intent.CATEGORY_OPENABLE);
                        intentCompatible.setType("*/*");

                        handleOnShowFileChooserIntent(filePathsCallback, intentCompatible, false);
                    }
                }, 300);


            } else {
                filePathsCallback.onReceiveValue(null);

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onPermissionRequest(final PermissionRequest request) {
        Log.d(LOG_TAG, "onPermissionRequest: " + Arrays.toString(request.getResources()));
        request.grant(request.getResources());
    }

    public void destroyLastDialog(){
        dialogsHelper.destroyLastDialog();
    }
}
