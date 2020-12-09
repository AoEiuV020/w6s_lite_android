package com.foreveross.atwork.modules.app.model;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import com.google.gson.annotations.SerializedName;

/**
 * webview 顶部titlebar 右侧控件数据格式
 * Created by reyzhang22 on 16/3/4.
 */
public class WebRightButton {

    public enum Action {
        /**
         * 显示list动作
         */
        List {
            @Override
            public String toString() {
                return "list";
            }
        },
        /**
         * 调用js动作
         */
        JS {
            @Override
            public String toString() {
                return "js";
            }
        },

        Url {
            @Override
            public String toString() {
                return "URL";
            }
        },

        System {
            @Override
            public String toString() {
                return "System";
            }
        },

        /**
         * 未知动作
         */
        Unknown {
            @Override
            public String toString() {
                return "unknown";
            }
        };

        public static Action fromString(String action) {
            if (action.equalsIgnoreCase("list")) {
                return List;
            }

            if (action.equalsIgnoreCase("js")) {
                return JS;
            }

            if (action.equalsIgnoreCase("url")) {
                return Url;
            }

            if (action.equalsIgnoreCase("system")) {
                return System;
            }

            return Unknown;
        }


        public abstract String toString();
    }


    /**
     * 显示的图标 -1为无图标显示
     */
    @SuppressWarnings("icon")
    public String mIcon = "";

    /**
     * 显示文字,空值不显示
     */
    @SuppressWarnings("title")
    public String mTitle = "";

    /**
     * 指定动作
     */
    @SuppressWarnings("action")
    public Action mAction;


    /**
     * 相对应action的动作值
     */
    @SuppressWarnings("value")
    public String mActionValue;

    @SerializedName("fontColor")
    public String mFontColor;

    public String mType;

    @SerializedName("disable")
    public boolean mDisable;

}
