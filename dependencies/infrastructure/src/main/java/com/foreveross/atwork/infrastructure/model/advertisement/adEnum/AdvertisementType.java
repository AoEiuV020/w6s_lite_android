package com.foreveross.atwork.infrastructure.model.advertisement.adEnum;

/**
 * Created by reyzhang22 on 17/9/15.
 */

/**
 * 广告类型
 */
public enum AdvertisementType {
    Image {
        @Override
        public String valueOfString() {
            return "IMAGE";
        }
    },
    Video {
        @Override
        public String valueOfString() {
            return "VIDEO";
        }
    },
    Unknown {
        @Override
        public String valueOfString() {
            return "UNKNOWN";
        }
    };

    abstract public String valueOfString();

    public static String toString(AdvertisementType type) {
        if (type.equals(Image)) {
            return "IMAGE";
        }
        if (type.equals(Video)) {
            return "VIDEO";
        }
        return "Unknown";
    }

    public static AdvertisementType fromString(String value) {
        if ("IMAGE".equalsIgnoreCase(value)) {
            return Image;
        }
        if ("VIDEO".equalsIgnoreCase(value)) {
            return Video;
        }
        return Unknown;
    }
}
