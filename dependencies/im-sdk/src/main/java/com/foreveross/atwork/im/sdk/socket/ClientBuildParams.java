package com.foreveross.atwork.im.sdk.socket;

import com.foreveross.atwork.infrastructure.newmessage.ConnectTypeMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by lingen on 15/4/7.
 */
public class ClientBuildParams implements Serializable {
    /**
     * 超时时间
     */

    int timeout = 10 * 1000;

    /**
     * 心跳时间
     */
    long heartBeat = 2 * 60 * 1000;

    /**
     * HOST地址
     */
    String host;

    /**
     * 端口
     */
    int port;


    /**
     * SOCKET连接秘钥
     */
    String secret;

    /**
     * 用户ID
     */
    String clientId;

    /**
     * 设备ID
     */
    String deviceId;

    /**
     * tenantId
     */
    String tenantId;

    boolean sslVerify;

    boolean sslEnabled;


    /**
     * 设定超时时间
     *
     * @param timeout 超时时间
     * @return
     */
    public ClientBuildParams timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 设定连接的HOST
     *
     * @param host
     * @return
     */
    public ClientBuildParams host(String host) {
        this.host = host;
        return this;
    }

    /**
     * 设定SOCKET端口
     *
     * @param port
     * @return
     */
    public ClientBuildParams port(int port) {
        this.port = port;
        return this;
    }


    /**
     * 设定本次连接的秘钥
     *
     * @param secret
     */
    public ClientBuildParams secret(String secret) {
        this.secret = secret;
        return this;
    }

    /**
     * 用户ID
     *
     * @param clientId
     */
    public ClientBuildParams clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * 设备ID
     *
     * @param deviceId
     */
    public ClientBuildParams deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }


    public ClientBuildParams tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public ClientBuildParams heartBeat(long mil) {
        heartBeat = mil;
        return this;
    }

    public ClientBuildParams sslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
        return this;
    }

    public ClientBuildParams sslVerify(boolean sslVerify) {
        this.sslVerify = sslVerify;
        return this;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean getSslVerify() {
        return sslVerify;
    }

    public boolean getSslEnabled() {
        return sslEnabled;
    }

    public long getHeartBeat() {
        return this.heartBeat;
    }

    public static ClientBuildParams newInstance() {
        return new ClientBuildParams();
    }




    /**
     * 检验值的正确性
     */
    public void checkValid() {
        assert StringUtils.isEmpty(deviceId) ==false: "DeviceId Not Allowed null";

        assert StringUtils.isEmpty(host) == false: "Host Not Allowed null";

        assert port > 0 : "Port Must greater than 0";

        assert StringUtils.isEmpty(secret) == false: "Secret Not Allowed null";

        assert StringUtils.isEmpty(clientId) ==false : "ClientId Not Allowed null";

        assert StringUtils.isEmpty(tenantId) ==false : "tenantId Not Allowed null";
    }

    /**
     * 获取授权对象
     *
     * @return
     */
    public ConnectTypeMessage getSignatureMessage() {
        ConnectTypeMessage signatureMessage = ConnectTypeMessage.newInstance().secret(secret).clientId(clientId).deviceId(deviceId).tenantId(tenantId).signature();
        return signatureMessage;
    }

}

