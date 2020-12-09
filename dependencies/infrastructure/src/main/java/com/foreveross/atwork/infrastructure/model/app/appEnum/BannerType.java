package com.foreveross.atwork.infrastructure.model.app.appEnum;

/**
 * Created by dasunsy on 2017/1/22.
 */

public enum BannerType {
    DEFAULT {
        @Override
        public int intValue() {
            return 0;
        }
    },

    CUSTOM_COLOR {
        @Override
        public int intValue() {
            return 1;
        }
    },

    CUSTOM_PIC {
        @Override
        public int intValue() {
            return 2;
        }
    };

    public abstract int intValue();

}
