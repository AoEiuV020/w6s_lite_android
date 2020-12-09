package com.foreveross.atwork.infrastructure.utils;

import android.text.TextUtils;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.BuildConfig;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksSetting;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.infrastructure.BaseApplication;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksEncryption;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by dasunsy on 16/7/21.
 */
public class HttpUrlConnectionUtil {

    private static final String HTTP_ALI_DNS_URL = "http://203.107.1.33/196022/sign_d";
    private String w6sAuthKey = "pfcyYTOfrTSNaQYTvO0AezCV39NhMSnt";


    private static HttpUrlConnectionUtil sInstance = new HttpUrlConnectionUtil();

    private HttpDnsService mHttpDns;

    private HttpUrlConnectionUtil() {
        BeeWorks beeWorks = BeeWorks.getInstance();
        if (AtworkConfig.HTTP_DNS_ENABLE && beeWorks.config != null && beeWorks.config.beeWorksSetting != null ) {
            BeeWorksSetting.HttpDnsSetting httpDnsSetting = beeWorks.config.beeWorksSetting.getHttpDnsSetting();
            mHttpDns = HttpDns.getService(BaseApplicationLike.sApp, httpDnsSetting.getAccountId(), httpDnsSetting.getSecret());
            mHttpDns.setPreResolveAfterNetworkChanged(true);
            mHttpDns.setHTTPSRequestEnabled(true);
            mHttpDns.setExpiredIPEnabled(true);
        }
    }

    public static HttpUrlConnectionUtil getInstance() {
        return sInstance;
    }

    public HttpURLConnection handleUrlConnection(String urlString) throws IOException {

        LogUtil.e("old url -> " + urlString);


        TkTypeHandleResult tkTypeHandleResult = handleTkType(urlString);


        if(null != tkTypeHandleResult && !StringUtils.isEmpty(tkTypeHandleResult.newUrl)) {
            urlString = tkTypeHandleResult.newUrl;
        }

        URL url = new URL(urlString);
        trustAllHosts();
        String host = url.getHost();
        HttpURLConnection urlConnection = getConnection(url);

        if (AtworkConfig.HTTP_DNS_ENABLE) {
            String ip = mHttpDns.getIpByHostAsync(host);
            if (!TextUtils.isEmpty(ip)) {
                String newUrl = urlString.replaceFirst(host, ip);
                urlConnection = getConnection(new URL(newUrl));
                urlConnection.setRequestProperty("Host", host);
            }
        }

        urlConnection.setRequestProperty("X-WP-LANG", LanguageUtil.getCurrentSettingLocale(BaseApplicationLike.baseContext).getLanguage());
        urlConnection.setRequestProperty("X-WP-VERSION", AppUtil.getVersionName(BaseApplicationLike.baseContext));
        urlConnection.setRequestProperty("X-WP-PLATFORM", "ANDROID");


        if(null != tkTypeHandleResult && !StringUtils.isEmpty(tkTypeHandleResult.tk)) {
            urlConnection.setRequestProperty("X-WP-TOKEN", tkTypeHandleResult.tk);
        }

        assembleConnectionW6sAuthKey(urlConnection);


        return urlConnection;
    }

    private HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection urlConnection;
        if (url.getProtocol().toLowerCase().equals("https")) {
            HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY);
            urlConnection = https;

        } else {
            urlConnection = (HttpURLConnection)url.openConnection();
        }

        return urlConnection;
    }

    @Nullable
    private static TkTypeHandleResult handleTkType(String urlString) {

        if(BaseApplicationLike.sIsDebug) {
            return null;
        }

        TkTypeHandleResult tkTypeHandleResult = null;

        if(BeeWorksEncryption.TK_TYPE_HEADER == AtworkConfig.TK_TYPE && urlString.contains("access_token=")) {
            tkTypeHandleResult = new TkTypeHandleResult();
            Uri uri = Uri.parse(urlString);

            tkTypeHandleResult.tk = uri.getQueryParameter("access_token");
            HashSet<String> urlKeys = new HashSet<>(uri.getQueryParameterNames());

            urlKeys.remove("access_token");

            String path;
            if(urlString.contains("?")) {
                path = urlString.substring(0, urlString.indexOf("?"));
            } else if(urlString.contains("&")) {
                path = urlString.substring(0, urlString.indexOf("&"));

            } else {
                path = urlString;
            }


            StringBuilder newUrl = new StringBuilder(path);
            boolean addFirstKey = false;
            for(String urlKey: urlKeys) {

                List<String> parameters = uri.getQueryParameters(urlKey);

                for(String parameter: parameters) {

                    if (addFirstKey) {
                        newUrl.append("&");
                    } else {
                        addFirstKey = true;

                        newUrl.append("?");

                    }

                    try {
                        newUrl.append(urlKey).append("=").append(URLEncoder.encode(parameter, "utf-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            }

            tkTypeHandleResult.newUrl = newUrl.toString();


            LogUtil.e("new url -> " + tkTypeHandleResult.newUrl);

        }
        return tkTypeHandleResult;
    }


    public  void assembleConnectionW6sAuthKey(HttpURLConnection connection) {

        LogUtil.e("assembleConnectionW6sAuthKey -> " + connection.getURL().getHost());
        if(!connection.getURL().getHost().contains("gateway.workplus.io")) {
            return;
        }

        HashMap<String, String> headerInfo = null;
        if (CustomerHelper.isWorkplusV4(BaseApplicationLike.baseContext)) {
            headerInfo = new HashMap<>();
            headerInfo.put("auth_key", w6sAuthKey);
        }

        if (!MapUtil.isEmpty(headerInfo)) {
            for(Map.Entry<String, String> entry : headerInfo.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                LogUtil.e(TAG, "checkServer  Trusted name-> " + chain[0].getIssuerDN().getName());

//
                LogUtil.e(TAG, "checkServer  Trusted authType-> " + authType);

                if(CustomerHelper.isKnowfuture(BaseApplicationLike.baseContext)
                        && !"CN=Let's Encrypt Authority X3,O=Let's Encrypt,C=US".equals(chain[0].getIssuerDN().getName())) {
                    throw new CertificateException("Parent certificate of server was different than expected signing certificate");
                }


            }

        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static String dnsAuthSign(String host) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(host).append("-").append(AtworkConfig.DOMAIN_ID).append("-").append((System.currentTimeMillis() + 3600* 1000) + "");
        return MD5Utils.encoderByMd5(stringBuilder.toString());
    }

    private static String dnsParseUrl(String host, String sign) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append(HTTP_ALI_DNS_URL).append("?host=").append(host).append("&t=").append(sign).toString();
    }




    static class TkTypeHandleResult {
        public String tk;
        public String newUrl;
    }
}
