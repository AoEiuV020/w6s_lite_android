package com.foreveross.atwork.infrastructure.theme;

/**
 * Created by dasunsy on 2016/9/27.
 */

public enum  SystemThemeType {
    /**
     * 天空清澈蓝
     * */
    SKY_BLUE {
        @Override
        public String toString() {
            return "skyblue";
        }
    },

    /**
     * 商务蓝
     * */
    BUSINESS_BLUE {
        @Override
        public String toString() {
            return "businessblue";
        }
    },

    /**
     * 耀沙金
     * */
    SHAKIN {
        @Override
        public String toString() {
            return "shakin";
        }
    },

    /**
     * 冰川蓝
     * */
    GLACIER_BLUE {
        @Override
        public String toString() {
            return "glacierblue";
        }
    },

    /**
     * 中国红
     * */
    CHINA_RED {
        @Override
        public String toString() {
            return "chinared";
        }
    },

    /**
     * 墨玉绿
     * */
    BLACK_JADE_GREEN {
        @Override
        public String toString() {
            return "blackjadegreen";
        }
    },

    /**
     * 繁星蓝
     * */
    BLUE_STARS {
        @Override
        public String toString() {
            return "bluestars";
        }
    },

    /**
     * 活力珠光橙
     * */
    VIBRANT_ORANGE {
        @Override
        public String toString() {
            return "vibrantorange";
        }
    };

    public abstract String toString();

}
