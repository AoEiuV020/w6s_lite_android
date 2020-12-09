package com.foreveross.atwork.infrastructure.model.voip;

import androidx.annotation.Nullable;

/**
 * Created by dasunsy on 16/8/8.
 */
public enum MeetingStatus {
    /**
     * 接通电话
     * */
    SUCCESS {
        @Override
        public int intValue() {
            return 1;
        }
    },

    /**
     * 通话没接通
     * */
    FAILED {
        @Override
        public int intValue() {
            return 2;
        }
    };



    @Nullable
    public static MeetingStatus valueOf(int value) {
        if (value == 1) {
            return SUCCESS;
        } else if (value == 2) {
            return FAILED;
        }

        return null;
    }

    public abstract int intValue();
}
