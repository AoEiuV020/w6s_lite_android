package com.foreveross.atwork.infrastructure.webview;

/**
 * Created by dasunsy on 2017/10/27.
 */

public interface OnSetWebUiChangeListener {
    void onSetWebTitle(String title, String url);
    void onUrlStart();
    void onUrlFinish(String url);
    void onUrlWrong();
    void onStatusBarChange(String color);
    void onSetTextZoom();
}

