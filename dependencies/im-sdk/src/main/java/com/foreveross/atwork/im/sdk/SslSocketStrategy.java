package com.foreveross.atwork.im.sdk;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.foreveross.atwork.im.sdk.socket.SSLSocketFactoryCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;

/**
 * Created by reyzhang22 on 17/3/21.
 */

public class SslSocketStrategy implements SocketStrategy {

    private static final String TAG = SslSocketStrategy.class.getSimpleName();

    private static SSLSocket sSslSocket;


    @Override
    public void initSocket(Context context, Handler handler, InetSocketAddress socketAddress, int timeout, final boolean sslVerify) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        synchronized (TAG) {
            if (sSslSocket == null) {
                final String hostName = socketAddress.getHostName();
                int port = socketAddress.getPort();

                // Create a trust manager that does not validate certificate chains
                X509TrustManager trustAllCerts = getX509TrustManager(hostName, sslVerify);

                SSLSocketFactoryCompat sslSocketFactoryCompat = new SSLSocketFactoryCompat(trustAllCerts);
                sSslSocket = (SSLSocket) sslSocketFactoryCompat.createSocket(hostName, port);

                sSslSocket.setKeepAlive(true);
            }
        }
    }

    @NonNull
    private X509TrustManager getX509TrustManager(final String hostName, final boolean sslVerify) {
        return new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                if (sslVerify) {
                    certVerify(chain, hostName);
                }

            }

        };
    }


    @Override
    public OutputStream getOutputStream() throws IOException {
        if (null == sSslSocket) {
            return null;
        }
        return sSslSocket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (null == sSslSocket) {
            return null;
        }
        return sSslSocket.getInputStream();
    }

    @Override
    public boolean isConnect() {
        if (sSslSocket == null) {
            return false;
        }
        return sSslSocket.isConnected();
    }

    @Override
    public boolean isClose() {
        if (sSslSocket == null) {
            return true;
        }
        return sSslSocket.isClosed();
    }

    @Override
    public void closeSocket() throws IOException {
        if (sSslSocket == null) {
            return;
        }
        sSslSocket.close();
        sSslSocket = null;
    }


    private void certVerify(X509Certificate[] cert, String verifyHost) throws CertificateException {
        String subjectDn = cert[0].getSubjectDN().getName();

        if (TextUtils.isEmpty(subjectDn) || TextUtils.isEmpty(verifyHost)) {
            throw new CertificateException("Parent certificate subject dn or host is empty");
        }
        String dn = subjectDn.substring(subjectDn.indexOf("CN=") + 3, subjectDn.indexOf(","));
        if (!dn.contains("*")) {
            if (!dn.equalsIgnoreCase(verifyHost)) {
                throw new CertificateException("Parent certificate of server was different than expected signing certificate");
            }
        }

        String endFix = dn.substring(dn.lastIndexOf("*") + 1, dn.length());
        if (!verifyHost.toLowerCase().endsWith(endFix.toLowerCase())) {
            throw new CertificateException("Parent certificate of server was different than expected signing certificate");
        }
    }

}
