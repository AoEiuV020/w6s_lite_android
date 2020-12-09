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

import com.ies.link.IESException;
import com.ies.link.net.IESTcpSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Created by reyzhang22 on 16/1/12.
 */
public class IesSocketStrategy implements SocketStrategy {

    private static final String TAG = IesSocketStrategy.class.getSimpleName();

    private static IESTcpSocket sIesTcpSocket;


    @Override
    public void initSocket(Context context, Handler handler, InetSocketAddress socketAddress, int timeout, boolean sslVerify) throws IOException, IESException {
        synchronized (TAG) {
            if (sIesTcpSocket == null) {
                sIesTcpSocket = new IESTcpSocket(socketAddress.getAddress(), socketAddress.getPort());
//                sIesTcpSocket.connect(socketAddress, timeout);
                sIesTcpSocket.setKeepAlive(true);
            }
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if(null == sIesTcpSocket) {
            return null;
        }
        return sIesTcpSocket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if(null == sIesTcpSocket) {
            return null;
        }
        InputStream is = sIesTcpSocket.getInputStream();
        is.available();
        return sIesTcpSocket.getInputStream();
    }

    @Override
    public boolean isClose() {
        if (sIesTcpSocket == null) {
            return true;
        }
        return true;
    }

    @Override
    public boolean isConnect() {
        return sIesTcpSocket != null;
    }

    @Override
    public void closeSocket() throws IOException {
        if (sIesTcpSocket != null) {
            sIesTcpSocket.close();
        }
        sIesTcpSocket = null;
    }
}
