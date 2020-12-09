package com.foreveross.atwork.infrastructure.newmessage.post;

import com.foreveross.atwork.infrastructure.newmessage.Message;

public class DeviceOutlineMessage extends DeviceInfoMessage {
    @Override
    public int getMsgType() {
        return Message.DEVICE_OUTLINE;
    }
}
