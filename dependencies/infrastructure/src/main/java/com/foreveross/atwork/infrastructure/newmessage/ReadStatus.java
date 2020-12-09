package com.foreveross.atwork.infrastructure.newmessage;

/**
 * Created by dasunsy on 16/6/3.
 */
public enum ReadStatus {
    Unread {
        @Override
        public int intValue() {
            return 0;
        }
    }, AbsolutelyRead {
        @Override
        public int intValue() {
            return 1;
        }
    }, LocalRead {
        @Override
        public int intValue() {
            return 2;
        }
    }

    ;

    public static ReadStatus fromIntValue(int value) {
        if (0 == value) {
            return Unread;
        } else if (1 == value) {
            return AbsolutelyRead;
        } else if (2 == value) {
            return LocalRead;
        }
        return null;
    }

    public abstract int intValue();
}

