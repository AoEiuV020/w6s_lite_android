package com.foreveross.atwork.im.sdk.receive;

import com.foreveross.atwork.im.sdk.BodyDecode;
import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.DecodeHandle;
import com.foreveross.atwork.infrastructure.newmessage.ConnectAckTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ConnectRstTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ConnectTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.DisconnectTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.newmessage.PingMessage;
import com.foreveross.atwork.infrastructure.newmessage.PongMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceOnlineMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceOutlineMessage;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;

import java.nio.ByteBuffer;

/**
 * Created by lingen on 15/4/7.
 */
public class BodyDecodeHandle implements DecodeHandle {

    private BodyDecode bodyDecode;

    public BodyDecodeHandle(BodyDecode bodyDecode) {
        this.bodyDecode = bodyDecode;
    }

    public boolean decodeMessage(Protocol protocol) throws Exception {

        byte[] bodyLengthByte = new byte[2];
        System.arraycopy(protocol.getContent(), 2 + protocol.getDigest().length, bodyLengthByte, 0, 2);

        int bodyLength = ByteBuffer.wrap(bodyLengthByte).getShort();
        byte[] body = new byte[bodyLength];
        System.arraycopy(protocol.getContent(), 4 + protocol.getDigest().length, body, 0, bodyLength);
        protocol.setBody(body);

        Message message = null;

        if (protocol.getType() == Message.POST_TYPE || protocol.getType() == Message.CONNECT_ACK_TYPE) {
            message = bodyDecode.getMessage(protocol.getType(), body);
        }

        if (protocol.getType() == Message.CONNECT_ACK_TYPE) {
            message = MessageCovertUtil.covertHasTimestampResponse(body, new ConnectAckTypeMessage());
        }

        if (protocol.getType() == Message.CONNECT_RST_TYPE) {
            message = new ConnectRstTypeMessage();
        }

        if (protocol.getType() == Message.CONNECT_TYPE) {
            message = new ConnectTypeMessage();
        }

        if (protocol.getType() == Message.PING_TYPE) {
            message = new PingMessage();
        }

        if (protocol.getType() == Message.PONG_TYPE) {
            message = MessageCovertUtil.covertHasTimestampResponse(body, new PongMessage());
        }

        if (protocol.getType() == Message.DISCONNECT_TYPE) {
            message = new DisconnectTypeMessage();
        }

        if (protocol.getType() == Message.DEVICE_ONLINE) {
            message = MessageCovertUtil.parseDeviceInfoResponse(body, new DeviceOnlineMessage());
        }

        if (protocol.getType() == Message.DEVICE_OUTLINE) {
            message = MessageCovertUtil.parseDeviceInfoResponse(body, new DeviceOutlineMessage());
        }

        if (protocol.getType() == Message.USER_TYPING) {
            message = MessageCovertUtil.parseUserTypingMessage(body);
        }

        protocol.setMessage(message);


        return true;
    }
}
