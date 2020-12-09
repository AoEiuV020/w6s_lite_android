package com.foreveross.atwork.infrastructure.model.setting;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil;

/**
 * Created by dasunsy on 2017/3/31.
 */

public enum SourceType {

    USER {
        @Override
        public int initValue() {
            return 0;
        }
    },

    DISCUSSION {
        @Override
        public int initValue() {
            return 1;
        }
    },

    APP {
        @Override
        public int initValue() {
            return 2;
        }
    },

    DROPBOX {
        @Override
        public int initValue() {
            return 3;
        }
    },

    UNKNOWN {
        @Override
        public int initValue() {
            return -1;
        }
    };

    public static SourceType valueStringOf(@Nullable String value) {
        if(null == value) {
            return UNKNOWN;
        }

        SourceType type = EnumLookupUtil.lookup(SourceType.class, value.toUpperCase());
        if(null != type) {
            return type;
        }

        return UNKNOWN;
    }


    public static SourceType valueOf(int value) {
        if(0 == value) {
            return USER;

        } else if(1 == value) {
            return DISCUSSION;

        } else if(2 == value) {
            return APP;

        } else if(3 == value) {
            return DROPBOX;
        }

        return UNKNOWN;
    }

    public static SourceType valueOf(SessionType type) {
        if(SessionType.User == type) {
            return USER;

        } else if(SessionType.Discussion == type) {
            return DISCUSSION;

        } else if(SessionType.LightApp == type) {
            return APP;

        } else if(SessionType.NativeApp == type) {
            return APP;

        }else if(SessionType.SystemApp == type) {
            return APP;
        }

        return UNKNOWN;
    }


    public abstract int initValue();

}
