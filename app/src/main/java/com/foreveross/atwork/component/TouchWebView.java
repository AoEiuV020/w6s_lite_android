package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class TouchWebView extends WebView {
    public boolean canIntercept = true;

    public TouchWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchWebView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canIntercept) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }

    public void setCanIntercept(boolean canIntercept){
        this.canIntercept = canIntercept;
    }

}