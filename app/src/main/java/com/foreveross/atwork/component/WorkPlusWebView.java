package com.foreveross.atwork.component;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by dasunsy on 16/3/31.
 */
public class WorkPlusWebView extends WebView{

    private static String mCoverUrl;


    public WorkPlusWebView(Context context) {
        super(context);
    }

    public WorkPlusWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCoverUrl = "";
    }


    public void evaluateJavascript(String script, ValueCallbackImpl callback) {
        if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            super.evaluateJavascript(script, callback);

        } else {
//            addJavascriptInterface(callback, );
        }
    }

    public String getCoverUrl() {

        String url = "";
        if(!StringUtils.isNull(mCoverUrl)) {
            url = mCoverUrl.replaceAll("\"", "");
        }
        return url;
    }


    public static class ValueCallbackImpl implements ValueCallback<String> {

        @JavascriptInterface
        @Override
        public void onReceiveValue(String value) {
            mCoverUrl = value;

        }
    }
}
