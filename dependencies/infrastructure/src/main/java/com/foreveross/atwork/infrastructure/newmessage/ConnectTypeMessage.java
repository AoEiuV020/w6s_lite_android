package com.foreveross.atwork.infrastructure.newmessage;

import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.google.gson.annotations.Expose;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class ConnectTypeMessage extends HasBodyMessage {

    public static final String SECRET = "secret";

    public static final String NONCE = "nonce";

    public static final String SIGATURE = "signature";

    public static final String DOMAIN_ID = "domainId";

    public static final String CLIENT_ID = "clientId";

    public static final String DEVICE_ID = "deviceId";

    public static final String TIMESTAMP = "timestamp";
    /**
     * 密钥
     */
    @Expose
    protected String secret;

    /**
     * 随机加密数
     */
    @Expose
    protected long nonce;
    /**
     * 签名
     */
    @Expose
    protected String signature;
    /**
     * 客户端授权码
     */
    @Expose
    protected String tenantId;
    /**
     * 用户ID
     */
    @Expose
    protected String clientId;
    /**
     * 设备ID
     */
    @Expose
    protected String deviceId;
    /**
     * 当前时间
     */
    @Expose
    protected long timestamp;

    public static ConnectTypeMessage newInstance() {
        ConnectTypeMessage connectTypeMessage = new ConnectTypeMessage();
        connectTypeMessage.nonce = new Random().nextInt(10000000);
        connectTypeMessage.timestamp = System.currentTimeMillis();
        return connectTypeMessage;
    }

    @Override
    public int getMsgType() {
        return Message.CONNECT_TYPE;
    }

    @Override
    public Map<String, Object> getMessageBody() {
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put(SECRET, secret);
        messageBody.put(NONCE, nonce);
        messageBody.put(SIGATURE, signature);
        messageBody.put(DOMAIN_ID, tenantId);
        messageBody.put(CLIENT_ID, clientId);
        messageBody.put(DEVICE_ID, deviceId);
        messageBody.put(TIMESTAMP, timestamp);
        return messageBody;
    }

    private byte[] sign() {
        try {
            String raw = secret + timestamp + nonce;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return digest.digest(raw.getBytes(Charset.forName("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectTypeMessage secret(String secret) {
        this.secret = secret;
        return this;
    }

    public ConnectTypeMessage clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ConnectTypeMessage deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public ConnectTypeMessage tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public ConnectTypeMessage signature() {
        this.signature = Base64Util.encode(sign());
        return this;
    }

    @Override
    public boolean encryptHandle() {
        return false;
    }
}
