package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.utils.CloneUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.LightAppDownloadManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;

/**
 * Created by dasunsy on 2017/1/23.
 */

public class LightAppSessionHelper {
    public static void startActivityFromLightAppSession(Context context, Session session, WebViewControlAction webViewControlAction) {
        AppManager.getInstance().queryApp(context, session.identifier, session.orgId, new AppManager.GetAppFromMultiListener() {
            @Override
            public void onSuccess(@NonNull App app) {

                App appCloned = CloneUtil.cloneTo(app);

                if(appCloned instanceof LightApp) {
                    LightApp lightAppCloned = (LightApp) appCloned;
                    lightAppCloned.mRouteUrl = webViewControlAction.mUrl;

                    startActivityFromLightApp(context, lightAppCloned, webViewControlAction);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

            }
        });
    }

    public static void startActivityFromLightApp(Context context, LightApp app, WebViewControlAction webViewControlAction) {
        if (!TextUtils.isEmpty(webViewControlAction.mUrl) && !webViewControlAction.mUrl.startsWith("local://")) {
            Intent webIntent = WebViewActivity.getIntent(context, webViewControlAction);
            context.startActivity(webIntent);
            return;
        }
        if (app.mBundles.isEmpty()) {
            Intent webIntent = WebViewActivity.getIntent(context, webViewControlAction);
            context.startActivity(webIntent);

            return;
        }
        AppBundles mainBundle = null;
        for (AppBundles bundle : app.mBundles) {
            if (bundle.isMainBundle()) {
                mainBundle = bundle;
            }
        }
        if (mainBundle != null) {
            toWebView(context, mainBundle, webViewControlAction);
            return;
        }
        toWebView(context, app.mBundles.get(0), webViewControlAction);
    }

    private static void toChatDetail(Context context, String appId) {
        Intent notificationIntent = new Intent();
        notificationIntent.putExtra(ChatDetailActivity.IDENTIFIER, appId);
        notificationIntent.setClass(context, ChatDetailActivity.class);
        context.startActivity(notificationIntent);
    }

    private static void toWebView(Context context, AppBundles appBundles, WebViewControlAction webViewControlAction) {
        webViewControlAction.setLightApp(appBundles);
        Intent webIntent = WebViewActivity.getIntent(context, webViewControlAction);
        if(AppManager.getInstance().useOfflinePackageNeedReLoad(appBundles)) {
            loadOfflineData(context, appBundles, webIntent);
            return;
        }
        context.startActivity(webIntent);
    }

    public static void loadOfflineData(final Context context, AppBundles appBundle, final Intent webIntent) {
        AtworkAlertDialog alert = new AtworkAlertDialog(context);
        alert.setContent("检测到应用离线包更新，是否立即更新？")
                .setBrightBtnText("立即更新")
                .setDeadBtnText("不更新")
                .setTitleText(context.getString(R.string.tip))
                .setClickBrightColorListener(dialog -> {
                    LightAppDownloadManager.getInstance().startDownload(context, appBundle);
                })
                .setCanceledOnTouchOutside(false);
        alert.show();

    }

}
