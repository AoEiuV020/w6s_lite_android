package com.foreveross.atwork.im.sdk.protocol;

import com.foreveross.atwork.infrastructure.newmessage.Message;

import java.io.Serializable;

/**
 * Created by lingen on 15/4/7.
 */
public class Protocol implements Serializable {

    private static final byte[] EMPTY = new byte[0];

    /**
     * 协议类型
     */
    private int type;

    private int qos;

    private byte[] digest = EMPTY;

    private byte[] body = EMPTY;

    /**
     * 消息的byte[] 格式
     */
    private byte[] content;

    /**
     * 消息的message格式
     */
    private Message message;

    private Protocol(byte[] content) {
        this.content = content;
    }

    public Protocol(Message message) {
        this.message = message;
    }

    public static Protocol newReceiveProtocol(byte[] content) {
        Protocol protocol = new Protocol(content);
        return protocol;
    }

    public static Protocol newSendProtocol(Message message) {
        Protocol protocol = new Protocol(message);
        return protocol;
    }


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public byte[] getDigest() {
        return digest;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;

    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
