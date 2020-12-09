package com.foreveross.atwork.infrastructure.model.app.appEnum;/**
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


/**
 * 应用类型枚举
 * Created by reyzhang22 on 16/4/14.
 */
public enum AppKind {

    /**
     * 原生应用
     */
    NativeApp {
        @Override
        public String stringValue() {
            return NATIVE_APP;
        }
    },

    /**
     * 自带邮箱
     */
    NativeEmail {
        @Override
        public String stringValue() {
            return NATIVE_EMAIL;
        }
    },

    /**
     * 轻应用
     * */
    LightApp {
        @Override
        public String stringValue() {
            return LIGHT_APP;
        }
    },

    /**
     * 服务号
     * */
    ServeNo {
        @Override
        public String stringValue() {
            return SERVE_NO;
        }
    };

    public abstract String stringValue();

    public static AppKind fromStringValue(String value) {
        if (NATIVE_APP.equalsIgnoreCase(value)) {
            return NativeApp;
        }

        if (NATIVE_EMAIL.equalsIgnoreCase(value)) {
            return NativeEmail;
        }

        if (LIGHT_APP.equalsIgnoreCase(value)) {
            return LightApp;
        }

        if (SERVE_NO.equalsIgnoreCase(value)) {
            return ServeNo;
        }
        return LightApp;
    }

    public static final String NATIVE_APP = "NATIVE_APP";

    public static final String NATIVE_EMAIL = "NATIVE_EMAIL";

    public static final String LIGHT_APP = "LIGHT_APP";

    public static final String SERVE_NO = "SERVE_NO";
}
