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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by reyzhang22 on 16/1/12.
 */
public class WorkPlusSocketStrategy implements SocketStrategy {

    private static final String TAG = WorkPlusSocketStrategy.class.getSimpleName();

    private static Socket sSocket;

    @Override
    public void initSocket(Context context, Handler handler, InetSocketAddress socketAddress, int timeout, boolean sslVerify) throws IOException {
        synchronized (TAG) {
            if (sSocket == null) {
                sSocket = new Socket();
                sSocket.connect(socketAddress, timeout);
//                HwBastetManager.getInstance().init(context, handler, sSocket);
                sSocket.setKeepAlive(true);
            }
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if(null == sSocket){
            return null;
        }

        return sSocket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if(null == sSocket){
            return null;
        }

        return sSocket.getInputStream();
    }

    @Override
    public boolean isConnect() {
        if (sSocket == null) {
            return false;
        }
        return sSocket.isConnected();
    }

    @Override
    public boolean isClose() {
        if (sSocket == null) {
            return true;
        }
        return sSocket.isClosed();
    }

    @Override
    public void closeSocket() throws IOException {
        if (sSocket != null) {
            sSocket.close();
        }
        sSocket = null;
    }
}
