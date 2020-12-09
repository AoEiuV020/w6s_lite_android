package com.foreveross.atwork.infrastructure.newmessage.post;

import com.foreveross.atwork.infrastructure.newmessage.Message;

public class DeviceOnlineMessage extends DeviceInfoMessage {
    @Override
    public int getMsgType() {
        return Message.DEVICE_ONLINE;
    }
}
