package com.foreveross.atwork.modules.app.activity;

import android.content.Context;
import android.content.Intent;

import com.foreveross.atwork.infrastructure.model.WebViewControlAction;

public class LandscapeWebViewActivity extends WebViewActivity {


    public static Intent getIntent(Context context, WebViewControlAction webViewControlAction) {
        Intent intent = new Intent();
        intent.setClass(context, LandscapeWebViewActivity.class);
        intent.putExtra(DATA_WEBVIEW_CONTROL_ACTION, webViewControlAction);
        return intent;
    }
}
