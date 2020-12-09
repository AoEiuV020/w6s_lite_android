package com.foreveross.atwork.im.sdk;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                       __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
                            |__|
 */


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.ReceiveRunnable;
import com.ies.link.IESException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by reyzhang22 on 16/1/13.
 */
public class SocketContext {

    private static final String TAG = SocketContext.class.getSimpleName();

    private static SocketContext sSocketContext = new SocketContext();

    private static final int SOCKET_TIMEOUT = 5 * 1000;

    private static int WAIT_TIME = 50;

    private  SocketStrategy mSocketStrategy;

    private ThreadPoolExecutor mThreadPoolExecutor;

    private static volatile int continuousErrorBodyLengthTime = 0;

    public static SocketContext getInstance() {
        synchronized (TAG) {
            if (sSocketContext == null) {
                sSocketContext = new SocketContext();
            }
        }
        return sSocketContext;
    }

    public void setSocketStrategy(SocketStrategy socketStrategy) {
        mSocketStrategy = socketStrategy;
    }

    public void connect(Context context, Handler handler, InetSocketAddress socketAddress, boolean sslVerify) throws IOException, IESException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException {
        mSocketStrategy.initSocket(context, handler, socketAddress, SOCKET_TIMEOUT, sslVerify);
    }

    public void send(Protocol protocol) throws IOException {
        OutputStream outputStream = mSocketStrategy.getOutputStream();
        if (outputStream == null) {
            return;
        }
        outputStream.write(protocol.getContent());
        outputStream.flush();
        Log.v(TAG, "发送数据组服务器成功");
    }

    public void receive(ReceiveListener receiveListener) throws IOException, InterruptedException {
        InputStream inputStream = mSocketStrategy.getInputStream();
        while (true) {
            Thread.sleep(WAIT_TIME);
            if(null != inputStream){
                byte[] content = readStream(inputStream);
                if (content.length > 0) {

                    mThreadPoolExecutor.execute(new ReceiveRunnable(content, receiveListener));
                }
            }

        }
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.mThreadPoolExecutor = threadPoolExecutor;
    }

    public void close() throws IOException {
        mSocketStrategy.closeSocket();
    }

    public boolean isConnect() {
        if (mSocketStrategy == null) {
            return false;
        }
        return mSocketStrategy.isConnect();
    }

    public boolean isClose() {
        if (mSocketStrategy == null) {
            return false;
        }
        return mSocketStrategy.isClose();
    }


    /**
     * @param inStream
     * @return 字节数组
     * @throws Exception
     * @功能 读取流
     */
    private byte[] readStream(InputStream inStream) throws IOException, InterruptedException {
        //不处理少于4个字节的数据
//        int avaliable = inStream.available();
//        if (avaliable < 4) {
//            return new byte[0];
//        }


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //读取第一个字节与第二个字节
        byte[] buffer = new byte[2];

        inStream.read(buffer);
        os.write(buffer, 0, buffer.length);

        byte digest = buffer[1];
        int digestLength = digest;

        if(digestLength < 0) {
            return new byte[0];
        }
        //根据digest长度，读取第三个字节
        buffer = new byte[digestLength];
        inStream.read(buffer);
        os.write(buffer, 0, buffer.length);

        //读取下二个字节，body长度
        buffer = new byte[2];

        inStream.read(buffer);
        os.write(buffer, 0, buffer.length);

        //放进byte缓存中
        int bodyLength = ByteBuffer.wrap(buffer).getShort();
        if(0 >= bodyLength) {
            return new byte[0];
        }


        buffer = new byte[bodyLength];
        // 已经成功读取的字节的个数
        int readCount = 0;

        //socket 接收到的数据可能分批收到, 此处需要确保数据的完整性
        while (readCount < bodyLength) {
            readCount += inStream.read(buffer, readCount, bodyLength - readCount);
        }

        os.write(buffer, 0, buffer.length);

        return os.toByteArray();
    }

}
