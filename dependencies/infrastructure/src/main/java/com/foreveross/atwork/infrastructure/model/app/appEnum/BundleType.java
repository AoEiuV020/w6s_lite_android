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
 * Created by reyzhang22 on 16/4/14.
 */
public enum BundleType {
    System {
        @Override
        public String stringValue() {
            return SYSTEM;
        }
    },
    Native {
        @Override
        public String stringValue() {
            return NATIVE;
        }
    },
    Light {
        @Override
        public String stringValue() {
            return LIGHT;
        }
    },
    Base {
        @Override
        public String stringValue() {
            return BASE;
        }
    },
    Unknown {
        @Override
        public String stringValue() {
            return UNKNOWN;
        }
    };

    public static BundleType toBundleType(String type) {
        if (SYSTEM.equalsIgnoreCase(type)){
            return System;
        }
        if (NATIVE.equalsIgnoreCase(type)) {
            return Native;
        }
        if (LIGHT.equalsIgnoreCase(type)) {
            return Light;
        }
        if (BASE.equalsIgnoreCase(type)) {
            return Base;
        }
        return Unknown;
    }

    public static final String SYSTEM = "system";

    public static final String NATIVE = "native";

    public static final String BASE = "base";

    public static final String LIGHT = "light";

    public static final String UNKNOWN = "unknown";

    public abstract String stringValue();
}
