package com.foreveross.atwork.api.sdk.net;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.HttpUrlConnectionUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.MapUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lingen on 15/4/14.
 * Description:
 */
public class HttpURLConnectionComponent {

    public static int sSTECount = 0;
    private static String end = "\r\n";
    private static String twoHyphens = "--";
    private static String boundary = "----WebKitFormBoundary7I0PaK37MtHvvqdd";

    public HttpResult postOrPutHTTP(String urlString, String params, String method, String contentType, Integer readTimeOut) {
        HttpURLConnection connection = null;
        DataOutputStream out = null;
        try {
            connection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(urlString);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(AtworkConfig.CONNECT_TIME_OUT);

            if (null == readTimeOut) {
                connection.setReadTimeout(AtworkConfig.READ_TIME_OUT);
            } else {
                connection.setReadTimeout(readTimeOut);

            }
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", contentType);


            connection.connect();
            out = new DataOutputStream(connection
                    .getOutputStream());
            if (!TextUtils.isEmpty(params)) {
                out.write(params.getBytes());
                out.flush();
            }

            boolean redirect = false;
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                    || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
            if (statusCode == HttpURLConnection.HTTP_OK || redirect) {
                if (redirect) {
                    String newUrl = connection.getHeaderField("Location");
                    String cookies = connection.getHeaderField("Set-Cookie");
                    connection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(newUrl);
                    connection.setRequestProperty("Cookie", cookies);
                    connection.setRequestProperty("Content-Type", contentType);
                }
                sSTECount = 0;
                if("image/jpg; charset=UTF-8".equals(connection.getContentType())) {
                    InputStream is = connection.getInputStream();
                    byte[] bytes = readBytes(is);
                    return HttpResult.getInstance().netStatsOK().byteResult(bytes);

                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    //读取流
                    String result = readInput(in);
                    return HttpResult.getInstance().netStatsOK().result(result);
                }

            } else {
                return HttpResult.getInstance().netStatusNot200(statusCode);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();

            if (e instanceof SocketTimeoutException) {

                sSTECount++;
                Logger.e("SOCKET_TIMEOUT", "SOCKET TIMEOUT EXCEPTION AND COUNT IS " + sSTECount);

                return HttpResult.getInstance().netException(HttpResult.EXCEPTION_TIME_OUT);


            } else {
                return HttpResult.getInstance().netException(e.getLocalizedMessage());

            }
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public HttpResult postForMultiFormData(String urlString, Map<String, String> paramsMap) {
        HttpURLConnection connection = null;
        DataOutputStream out = null;
        try {
            connection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(urlString);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            connection.setChunkedStreamingMode(1024);
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(60 * 1000 * 10);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection
                    .setRequestProperty(
                            "Accept",
                            "image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");

            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);


            out = new DataOutputStream(connection
                    .getOutputStream());

            //发送属性信息
            StringBuilder strBuf = new StringBuilder();
            strBuf.append(twoHyphens).append(boundary).append(end);
            strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + paramsMap.get("file_name")
                    + "\"");

            strBuf.append(end).append(end);

            strBuf.append("Content-Disposition: form-data; file_size=\""
                    + paramsMap.get("file_size")
                    + "\"");

            strBuf.append(end).append(end);

            out.write(strBuf.toString().getBytes());

            out.writeBytes(end + twoHyphens + boundary + twoHyphens + end);
            out.flush();
            boolean redirect = false;
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                    || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
            if (statusCode == HttpURLConnection.HTTP_OK || redirect) {
                if (redirect) {
                    String newUrl = connection.getHeaderField("Location");
                    String cookies = connection.getHeaderField("Set-Cookie");
                    connection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(newUrl);
                    connection.setRequestProperty("Cookie", cookies);
                }
                sSTECount = 0;
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                //读取流
                String result = readInput(in);
                return HttpResult.getInstance().netStatsOK().result(result);

            } else {
                return HttpResult.getInstance().netStatusNot200(statusCode);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();

            if (e instanceof SocketTimeoutException) {

                sSTECount++;
                Logger.e("SOCKET_TIMEOUT", "SOCKET TIMEOUT EXCEPTION AND COUNT IS " + sSTECount);

                return HttpResult.getInstance().netException(HttpResult.EXCEPTION_TIME_OUT);


            } else {
                return HttpResult.getInstance().netException(e.getLocalizedMessage());

            }
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }




    /**
     * @see {@link #postHttp(String, String, Integer)}
     * */
    public HttpResult postHttp(String urlString, String postParams) {
        return postHttp(urlString, postParams, null);
    }

    /**
     * POST请求
     *
     * @param urlString
     * @param postParams
     * @param readTimeOut
     * @return
     */
    public HttpResult postHttp(String urlString, String postParams, Integer readTimeOut) {
        return postOrPutHTTP(urlString, postParams, "POST", "application/json;charset=utf-8", readTimeOut);
    }


    public HttpResult postHttpForm(String urlString, String postParams) {
        return postOrPutHTTP(urlString, postParams, "POST", "application/x-www-form-urlencoded", null);
    }



    /**
     * PUT请求
     *
     * @param urlString
     * @param postParams
     * @return
     */
    public HttpResult putHttp(String urlString, String postParams) {
        return postOrPutHTTP(urlString, postParams, "PUT", "application/json;charset=utf-8", null);
    }


    /**
     * DELETE请求
     *
     * @param urlString
     * @return
     */
    public HttpResult deleteHttp(String urlString) {
        return getOrDeleteHttp(urlString, "DELETE", null);
    }


    /**
     * @see {@link #getHttp(String, Integer)}
     * */
    public HttpResult getHttp(String urlString) {
        return getHttp(urlString, null);
    }


    /**
     * GET请求
     *
     * @param urlString
     * @return
     */
    public HttpResult getHttp(String urlString, Integer readTimeOut) {
        return getOrDeleteHttp(urlString,"GET", readTimeOut);
    }

    public HttpResult getOrDeleteHttp(String urlString,String method, Integer readTimeOut) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(urlString);
            urlConnection.setConnectTimeout(AtworkConfig.CONNECT_TIME_OUT);

            if (null == readTimeOut) {
                urlConnection.setReadTimeout(AtworkConfig.READ_TIME_OUT);
            } else {
                urlConnection.setReadTimeout(readTimeOut);

            }
            urlConnection.setRequestMethod(method);
            if ("DELETE".equals(method)){
                urlConnection.setRequestProperty("Content-Type", "application/json");
            }



            urlConnection.connect();
            boolean redirect = false;
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                    || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
            if (statusCode == HttpURLConnection.HTTP_OK || redirect) {
                if (redirect) {
                    String newUrl = urlConnection.getHeaderField("Location");
                    String cookies = urlConnection.getHeaderField("Set-Cookie");
                    urlConnection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(newUrl);
                    urlConnection.setRequestProperty("Cookie", cookies);
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //读取流
                String result = readInput(in);
                return HttpResult.getInstance().netStatsOK().result(result);
            } else {
                return HttpResult.getInstance().netStatusNot200(statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {
                return HttpResult.getInstance().netException(HttpResult.EXCEPTION_TIME_OUT);

            } else {
                return HttpResult.getInstance().netException(e.getLocalizedMessage());

            }

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private byte[] readBytes(InputStream is) throws IOException {
        byte[] bytes;ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        bytes = baos.toByteArray();
        return bytes;
    }

    private String readInput(BufferedReader in) {
        StringBuffer result = new StringBuffer();
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null)
                result.append(inputLine);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    private static HttpURLConnectionComponent httpURLConnectionComponent = new HttpURLConnectionComponent();

    public static HttpURLConnectionComponent getInstance() {
        return httpURLConnectionComponent;
    }


}

