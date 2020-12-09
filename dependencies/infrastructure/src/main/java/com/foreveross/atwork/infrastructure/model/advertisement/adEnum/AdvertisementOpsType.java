package com.foreveross.atwork.infrastructure.model.advertisement.adEnum;

/**
 * Created by reyzhang22 on 17/9/16.
 * 广告动作类型
 */

public enum AdvertisementOpsType {

    Display {
        @Override
        public String valueOfString() {
            return "DISPLAY";
        }
    },
    Click {
        @Override
        public String valueOfString() {
            return "CLICK";
        }
    },
    Skip {
        @Override
        public String valueOfString() {
            return "SKIP";
        }
    },
    None {
        @Override
        public String valueOfString() {
            return "NONE";
        }
    };

    abstract public String valueOfString();

    public static String toString(AdvertisementOpsType opsType) {
        if (Display.equals(opsType)) {
            return "DISPLAY";
        }
        if (Click.equals(opsType)) {
            return "CLICK";
        }
        if (Skip.equals(opsType)) {
            return "SKIP";
        }
        return "NONE";
    }

    public static AdvertisementOpsType fromString(String value) {
        if ("DISPLAY".equalsIgnoreCase(value)) {
            return Display;
        }
        if ("CLICK".equalsIgnoreCase(value)) {
            return Click;
        }
        if ("SKIP".equalsIgnoreCase(value)) {
            return Skip;
        }
        return None;
    }
}
