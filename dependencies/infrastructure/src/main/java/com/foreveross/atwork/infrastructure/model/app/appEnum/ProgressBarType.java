package com.foreveross.atwork.infrastructure.model.app.appEnum;

/**
 * Created by dasunsy on 2017/1/22.
 */

public enum ProgressBarType {

    DEFAULT {
        @Override
        public int intValue() {
            return 0;
        }
    },

    CUSTOM {
        @Override
        public int intValue() {
            return 1;
        }
    };

    public abstract int intValue();

}
