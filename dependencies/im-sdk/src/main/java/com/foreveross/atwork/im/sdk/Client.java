package com.foreveross.atwork.im.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.foreverht.threadGear.IMThreadPoolExecutor;
import com.foreveross.atwork.im.sdk.body.GsonBodyEncode;
import com.foreveross.atwork.im.sdk.digest.NoDigest;
import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.send.BodyEncodeHandle;
import com.foreveross.atwork.im.sdk.send.DigestEncodeHandle;
import com.foreveross.atwork.im.sdk.send.MessageEncodeHandle;
import com.foreveross.atwork.im.sdk.send.QosEncodeHandle;
import com.foreveross.atwork.im.sdk.send.TypeEncodeHandle;
import com.foreveross.atwork.im.sdk.socket.ClientBuildParams;
import com.foreveross.atwork.im.sdk.socket.EncodeHandle;
import com.foreveross.atwork.infrastructure.newmessage.ConnectTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.newmessage.PingMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/7.
 * IM Android SDK客户端工具
 */
public class Client {

    public static final String TAG = "IM_SOCKET:";

    private final Object mConnectLock = new Object();

    private static List<EncodeHandle> sendHandleList = new ArrayList<>();

    private static Client sClient = new Client();

    /**
     * socket策略
     */
    public SocketContext mSocketContext;


    static {
        sendHandleList.add(new TypeEncodeHandle());
        sendHandleList.add(new QosEncodeHandle());
        sendHandleList.add(new DigestEncodeHandle(new NoDigest()));
        sendHandleList.add(new BodyEncodeHandle(new GsonBodyEncode()));
        sendHandleList.add(new MessageEncodeHandle());
    }

    /**
     * 接收消息的HANDEL
     */
    private ReceiveListener receiveListener;
    /**
     * SOCKET参数构建类
     */
    private ClientBuildParams clientBuild;

    private Client() {
        this.clientBuild = new ClientBuildParams();
    }

    /**
     * 链式初始化，构建一个连接CLIENT
     *
     * @return
     */
    public static Client build() {
        synchronized (TAG) {
            if (sClient == null) {
                sClient = new Client();
            }
        }
        return sClient;
    }

    /**
     * 设置连接参数
     *
     * @param clientBuildParams
     * @return
     */
    public Client clientBuild(ClientBuildParams clientBuildParams) {
        clientBuildParams.checkValid();
        this.clientBuild = clientBuildParams;
        return this;
    }

    /**
     * 设定接收消息监听器
     *
     * @param receiveListener
     * @return
     */
    public Client setReceiveListener(ReceiveListener receiveListener) {
        this.receiveListener = receiveListener;
        return this;
    }


    private void initSocketContext() {
        mSocketContext = SocketContext.getInstance();
        mSocketContext.setThreadPoolExecutor(IMThreadPoolExecutor.getInstance());

        if (AtworkConfig.VPN_CHANNEL && AtworkConfig.H3C_CONFIG) {
            mSocketContext.setSocketStrategy(new IesSocketStrategy());
            return;
        }
        if (clientBuild.getSslEnabled()) {
            mSocketContext.setSocketStrategy(new SslSocketStrategy());
            return;
        }
        mSocketContext.setSocketStrategy(new WorkPlusSocketStrategy());
    }

    /**
     * 建立SOCKET连接
     *
     * @return
     */
    public Client connect(Context context, Handler handler) {

        boolean reSetConnectStatus = true;

        try {


            if (mSocketContext != null) {
                mSocketContext.close();
            }

            synchronized (mConnectLock) {
                InetSocketAddress socketAddress = new InetSocketAddress(clientBuild.getHost(), clientBuild.getPort());
                initSocketContext();
                Logger.e(TAG, "ready to connect to IM server");
                mSocketContext.connect(context, handler, socketAddress, clientBuild.getSslVerify());
                if (mSocketContext.isConnect()) {
                    //接入授权
                    signatureMessage();
                }
                reSetConnectStatus = false;
            }

            mSocketContext.receive(receiveListener);
        } catch (Exception e) {
            if (mSocketContext != null) {
                try {
                    mSocketContext.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Logger.e(TAG, Log.getStackTraceString(e));
            notifySocketFail(reSetConnectStatus, e.getMessage());
            e.printStackTrace();

        }

        return this;

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void writeProtocol(Protocol protocol) throws IOException {
        if (null != mSocketContext) {
            mSocketContext.send(protocol);
        }
    }


    /**
     * 退出SOCKET
     */
    public void close() {
        synchronized (mConnectLock) {

            Logger.d(TAG, "主动关闭SOCKET...");
            try {
                if(null != mSocketContext) {
                    mSocketContext.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "关闭socket错误，错误码 e = " + e.getMessage());
            }
            mSocketContext = null;

        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public synchronized void send(Message message) throws IOException {
        Log.d(TAG, "发送一条消息:" + message.toString());
        Protocol protocol = Protocol.newSendProtocol(message);
        for (EncodeHandle sendHandle : sendHandleList) {
            sendHandle.encode(protocol);
        }
        writeProtocol(protocol);
    }


    private void notifySocketFail(boolean reSetConnectStatus, String error) {
        if (receiveListener != null) {
            receiveListener.receiveError(reSetConnectStatus, error);
        }
    }

    private void signatureMessage() throws IOException {
        ConnectTypeMessage signatureMessage = clientBuild.getSignatureMessage();
        send(signatureMessage);
    }

    /**
     * 发送一个心跳到SOCKET服务器
     */
    public void ping(Context context) {
        try {
            Logger.e(TAG, "send ping message");

            if (null!= mSocketContext && mSocketContext.isClose()) {
                Logger.e(TAG, "SOCKET IS CLOSED ON PING...");
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ACTION_IM_RECONNECT"));
                return;
            }
            PingMessage pingMessage = new PingMessage();
            send(pingMessage);

        } catch (Exception e) {
            Logger.e("IMSOCKET", "send ping exception " + Log.getStackTraceString(e));
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ACTION_IM_RECONNECT"));
        }
    }

    public void typing(UserTypingMessage typingMessage) {
        try {
            send(typingMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
