package com.foreveross.atwork.infrastructure.newmessage.messageEnum;/**
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


import android.content.Context;

import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;

/**
 * 聊天发送类型
 * Created by reyzhang22 on 16/4/13.
 */
public enum ChatSendType {

    SENDER {
        @Override
        public int intValue() {
            return 0;
        }
    },

    RECEIVER {
        @Override
        public int intValue() {
            return 1;
        }
    },
    UNKNOWN {
        @Override
        public int intValue() {
            return -1;
        }
    };

    public static ChatSendType toChatSendType(int value) {
        if (value == 0) {
            return SENDER;
        } else if (value == 1) {
            return RECEIVER;
        }

        return UNKNOWN;
    }

    public static ChatSendType parseFrom(Context context, String msgFrom) {
        String currentUser = LoginUserInfo.getInstance().getLoginUserId(context);
        if(currentUser.equals(msgFrom)) {
            return SENDER;
        }

        return RECEIVER;

    }

    public abstract int intValue();
}
