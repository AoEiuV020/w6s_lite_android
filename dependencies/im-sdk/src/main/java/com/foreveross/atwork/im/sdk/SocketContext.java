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
        Log.v(TAG, "??????????????????????????????");
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
     * @return ????????????
     * @throws Exception
     * @?????? ?????????
     */
    private byte[] readStream(InputStream inStream) throws IOException, InterruptedException {
        //???????????????4??????????????????
//        int avaliable = inStream.available();
//        if (avaliable < 4) {
//            return new byte[0];
//        }


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //???????????????????????????????????????
        byte[] buffer = new byte[2];

        inStream.read(buffer);
        os.write(buffer, 0, buffer.length);

        byte digest = buffer[1];
        int digestLength = digest;

        if(digestLength < 0) {
            return new byte[0];
        }
        //??????digest??????????????????????????????
        buffer = new byte[digestLength];
        inStream.read(buffer);
        os.write(buffer, 0, buffer.length);

        //????????????????????????body??????
        buffer = new byte[2];

        inStream.read(buffer);
        os.write(buffer, 0, buffer.length);

        //??????byte?????????
        int bodyLength = ByteBuffer.wrap(buffer).getShort();
        if(0 >= bodyLength) {
            return new byte[0];
        }


        buffer = new byte[bodyLength];
        // ????????????????????????????????????
        int readCount = 0;

        //socket ????????????????????????????????????, ????????????????????????????????????
        while (readCount < bodyLength) {
            readCount += inStream.read(buffer, readCount, bodyLength - readCount);
        }

        os.write(buffer, 0, buffer.length);

        return os.toByteArray();
    }

}
