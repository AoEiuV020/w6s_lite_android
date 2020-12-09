package com.foreveross.atwork.modules.app.activity;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.app.Activity;

import com.foreveross.atwork.cordova.plugin.WebViewPlugin;
import com.foreveross.atwork.support.AtworkBaseActivity;

import org.json.JSONArray;

/**
 * Created by reyzhang22 on 16/3/8.
 */
public abstract class WebViewBaseActivity extends AtworkBaseActivity {

    /**
     * 头部标题更改
     * @param title
     */
    public abstract void onTitleChange(String title);

    /**
     * 左键按钮功能更改
     * @param leftButtonAction
     */
    public abstract void onLeftButtonChange(String leftButtonAction);

    /**
     * 右边按钮功能设定
     * @param jsonArray
     */
    public abstract void onRightButtonChange(JSONArray jsonArray);

    /**
     * 右边按钮功能设定(可选保留刷新和应用信息)
     * @param jsonArray
     */
    public abstract void onRightButtonChange1(JSONArray jsonArray);

    /**
     * 左边按钮功能设定
     * @param jsonArray
     */
    public abstract void onChangeLeftButton(JSONArray jsonArray);

    /**
     * 重置右边按钮
     */
    public abstract void onRightButtonReset(Activity activity, WebViewPlugin.ActionCallbackListener listener);

    /**
     * 重置左边按钮
     */
    public abstract void onLeftButtonReset(Activity activity, WebViewPlugin.ActionCallbackListener listener);

    /**
     * 锁定顶部栏
     * @param isLock
     */
    public abstract void onTitleBarLock(boolean isLock);

    /**
     * 左边按钮控制
     * @param showBack
     * @param showClose
     */
    public abstract void onLeftButtonVisible(boolean showBack, boolean showClose);

    /**
     * 显示水印
     * @param show 是否显示水印
     * @param orgId
     * @param textColor
     */
    public abstract void setWatermark(boolean show, String orgId, String textColor, int textSize, int paddingTop, double alpha, String addValue);

    public abstract void setForwardMode(String forwardMode);
}
