package com.foreveross.atwork.im.sdk.send;

import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.EncodeHandle;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by lingen on 15/4/7.
 */
public class MessageEncodeHandle implements EncodeHandle {

    private static final int TYPE_LENGTH = 4;

    private static final int QOS_LENGTH = 4;

    private static final int DIGIEST_LENGTH = 8;

    private static final int BODY_LENGTH = 8;

    private static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    @Override
    public Protocol encode(Protocol protocol) {

        byte type = (byte) ((protocol.getType()) << 4);

        byte[] typeBytes = new byte[]{type};

        //后4个BIG，用于标识POS，先置为0
//        byte[] qosBytes = ByteBuffer.allocate(4).putInt(protocol.getQos() & 0xff ).array();

        //下8个BIG，表示签名的长度
        byte digest = (byte) (protocol.getDigest().length);
        byte[] digetLength = new byte[]{digest};

        //后续是签名体 this.digest

        byte[] bodyLength = ByteBuffer.allocate(2).putShort((short) (protocol.getBody().length & 0xffff)).array();

        byte[] content = concatAll(typeBytes, digetLength, protocol.getDigest(), bodyLength, protocol.getBody());

        protocol.setContent(content);
        return protocol;
    }

    public  int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        return addr;
    }
}
