package com.foreveross.atwork.infrastructure.newmessage.post.notify.user;

import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

/**
 * Created by dasunsy on 16/7/6.
 */
public abstract class UserNotifyMessage extends NotifyPostMessage {

    public static String FROM = "USER_HELPER";

    public enum Operation {
        SETTINGS_CHANGED,

        UNKNOWN;

        public static Operation fromStringValue(String value) {
            if ("SETTINGS_CHANGED".equalsIgnoreCase(value)) {
                return SETTINGS_CHANGED;
            }


            return UNKNOWN;
        }

    }
}
