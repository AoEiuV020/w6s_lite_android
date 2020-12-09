package com.foreveross.atwork.api.sdk.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.api.sdk.upload.model.MediaCompressResponseJson;
import com.foreveross.atwork.api.sdk.upload.model.MediaDigest;
import com.foreveross.atwork.api.sdk.upload.model.MediaInfoResponseJson;
import com.foreveross.atwork.api.sdk.upload.model.MediaUploadResultResponseJson;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.FileRangeUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.HttpUrlConnectionUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingen on 15/4/21.
 * Description:
 * 媒体中心UTIL类
 */
public class MediaCenterHttpURLConnectionUtil {


    private static final int MAX = 100;
    private static String end = "\r\n";
    private static String twoHyphens = "--";
    private static String boundary = "----WebKitFormBoundary7I0PaK37MtHvvqdd";

    public static int sResumeChuck = (20 * 99 * 1024);
    public static int sResumeUploadChuck = (20 * 99 * 1024);

    //维持消息ID与上传之间的关系
    public static Map<String, HttpURLConnection> uploadHttpURLConnection = new HashMap<>();

    //维持消息ID与下载之间的关系
    public static Map<String, HttpURLConnection> downloadHttpURLConnection = new HashMap<>();


    private static MediaCenterHttpURLConnectionUtil mediaCenterHttpURLConnectionUtil = new MediaCenterHttpURLConnectionUtil();

    private MediaCenterHttpURLConnectionUtil() {

    }

    public static MediaCenterHttpURLConnectionUtil getInstance() {
        return mediaCenterHttpURLConnectionUtil;
    }

    public boolean isDownloading(final String msgId) {
        return downloadHttpURLConnection.containsKey(msgId);
    }

    public boolean isUploading(final String msgId) {
        return uploadHttpURLConnection.containsKey(msgId);
    }

    public void breakAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (HttpURLConnection httpURLConnection : uploadHttpURLConnection.values()) {
                    httpURLConnection.disconnect();
                }
                for (HttpURLConnection httpURLConnection : downloadHttpURLConnection.values()) {
                    httpURLConnection.disconnect();
                }

                uploadHttpURLConnection.clear();
                downloadHttpURLConnection.clear();
                return null;
            }
        }.execute();
    }



    /**
     * @see {@link #brokenMedia(String, BreakMediaListener)}
     * */
    public void brokenMedia(final String msgId) {
        brokenMedia(msgId, null);
    }

    /**
     * 中断一个上传或下载行为
     *
     * @param msgId
     */
    @SuppressLint("StaticFieldLeak")
    public void brokenMedia(final String msgId, @Nullable final BreakMediaListener listener) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    HttpURLConnection httpURLConnection = uploadHttpURLConnection.get(msgId);

                    if (httpURLConnection == null) {
                        httpURLConnection = downloadHttpURLConnection.get(msgId);
                    }

                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }

                    uploadHttpURLConnection.remove(msgId);
                    downloadHttpURLConnection.remove(msgId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;

            }


            @Override
            protected void onPostExecute(Void aVoid) {
                if (null != listener) {
                    listener.onFinish();
                }
            }
        }.execute();

    }

    private static long sProgress;

    /**
     * 下载文件
     * @param paramsMaker
     * @return
     */
    public HttpResult downloadFile(final DownloadFileParamsMaker paramsMaker) {
        HttpURLConnection urlConnection = null;
        RandomAccessFile randomAccessFile;
        InputStream is;
        try {
            urlConnection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(paramsMaker.getDownloadUrl());
            downloadHttpURLConnection.put(paramsMaker.getDownloadId(), urlConnection);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(60 * 1000 * 10);
            urlConnection.setRequestMethod("GET");
            urlConnection .setRequestProperty( "Accept", "*/*");
            if (!TextUtils.isEmpty(paramsMaker.getCookie())) {
                urlConnection .setRequestProperty("Cookie", paramsMaker.getCookie());
            }
            if (paramsMaker.getDownloadPos() != -1) {
                urlConnection.setRequestProperty("Range", "bytes=" + paramsMaker.getDownloadPos() + "-*");
            }
            urlConnection.connect();

            boolean redirect = false;
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                    || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
            if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_PARTIAL || redirect) {
                if (redirect) {
                    String newUrl = urlConnection.getHeaderField("Location");
                    String cookies = urlConnection.getHeaderField("Set-Cookie");
                    urlConnection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(newUrl);
                    urlConnection.setRequestProperty("Cookie", cookies);
                }
                String contentType = urlConnection.getContentType();
                if (contentType.contains("application/json")) {
                    is = urlConnection.getInputStream(); // 获取输入流
                    byte[] data = readStream(is);   // 把输入流转换成字符数组
                    String json = new String(data);        // 把字符数组转换成字符串

                    JSONObject responseJson = new JSONObject(json);
                    int status = responseJson.getInt("status");
                    if (status == AtworkConstants.TOKEN_EXPIRED || status == AtworkConstants.TOKEN_MISSING || status == AtworkConstants.TOKEN_NOT_FOUND) {
                        String message = responseJson.getString("message");
                        if (!TextUtils.isEmpty(message) && message.contains("locked")) {
                            throw new IllegalArgumentException("token locked");
                        }
                        throw new IllegalArgumentException("token not found");

                    } else if (status == AtworkConstants.MEDIA_NOT_FOUND) {
                        throw new IllegalArgumentException("media not found");

                    }
                } else {
                    /**绝大多数手机decodeStream()遇到不是流传输类型contentType, 会返回 null, 从而被我们捕捉到异常
                     * 但在 MX 里, 会直接出现底层的崩溃, 所以这里遇到 json 类型的就不进行 decodeStream 操作了
                     * */


                    if (DOWNLOAD_TYPE.THUMBNAIL.equals(paramsMaker.getDownloadType())) {
                        Bitmap bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                        byte[] bmByteArray = BitmapUtil.Bitmap2Bytes(bitmap);
                        bmByteArray = ImageShowHelper.compressImageForThumbnail(bmByteArray);
                        FileStreamHelper.saveFile(paramsMaker.getEncrypt(), paramsMaker.getDownloadPath(), bmByteArray);
                        bitmap.recycle();

                    } else if (DOWNLOAD_TYPE.ORIGINAL.equals(paramsMaker.getDownloadType())) {
                        byte[] bytes;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = urlConnection.getInputStream().read(buffer)) > 0) {
                            baos.write(buffer, 0, len);
                        }
                        bytes = baos.toByteArray();
                        saveImageFile(paramsMaker.getEncrypt(), bytes, paramsMaker.getDownloadPath());

                    } else if (DOWNLOAD_TYPE.FILE.equals(paramsMaker.getDownloadType())) {
                        if (paramsMaker.getDownloadPos() != -1) {
                            File file = new File(paramsMaker.getDownloadPath());
                            randomAccessFile = new RandomAccessFile(file, "rw");
                            randomAccessFile.seek(paramsMaker.getDownloadPos());
                            // 将要下载的文件写到保存在保存路径下的文件中
                            is = urlConnection.getInputStream();
                            byte[] buffer = new byte[2048];
                            int length = -1;
                            int i = 0;
                            long total = paramsMaker.getDownloadPos();
                            while ((length = is.read(buffer)) != -1) {
                                if (paramsMaker.getProgressListener() != null) {
                                    randomAccessFile.write(buffer, 0, length);
                                    total += length;
                                    paramsMaker.getProgressListener().progress((total * 100) / (paramsMaker.getFileSize() != -1 ? paramsMaker.getFileSize() : urlConnection.getContentLength()), total);
                                }
                            }

                            //下载完后根据配置加密处理
                            if (paramsMaker.getEncrypt()) {
                                FileStreamHelper.encrypt(paramsMaker.getDownloadPath(), paramsMaker.getDownloadPath(), true);
                            }

                        } else {
                            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            saveToFile(paramsMaker.getEncrypt(), in, urlConnection.getContentLength(), paramsMaker.getDownloadPath(), paramsMaker.getProgressListener());
                        }


                    }

                }

                return HttpResult.getInstance().netStatsOK();
            } else {
                return HttpResult.getInstance().netStatusNot200(statusCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //删除写入的文件
            if (paramsMaker.getDownloadPos() == -1) {
                new File(paramsMaker.getDownloadPath()).delete();
            }
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }


            downloadHttpURLConnection.remove(paramsMaker.getDownloadId());
        }
    }


    public HttpResult resumeDownloadFile(final String msgId, final String urlString, long startPos, long fileSize, String filePath,
                                         final MediaProgressListener mediaProgressListener) {
        HttpURLConnection urlConnection = null;
        RandomAccessFile randomAccessFile;
        InputStream is = null;
        try {
            urlConnection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(urlString);
            downloadHttpURLConnection.put(msgId, urlConnection);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(60 * 1000 * 10);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Range", "bytes=" + startPos + "-*");

            urlConnection.connect();
            int statusCode = urlConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_PARTIAL) {
                randomAccessFile = new RandomAccessFile(filePath, "rwd");
                randomAccessFile.seek(startPos);
                // 将要下载的文件写到保存在保存路径下的文件中
                is = urlConnection.getInputStream();
                byte[] buffer = new byte[4096];
                int length = -1;
                int i = 0;
                long total = startPos;
                while ((length = is.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    total += length;
                    if (mediaProgressListener != null) {
                        i++;
                        if (i == 200) {
                            mediaProgressListener.progress((total * 100) / length, total);
                            i = 0;
                        }
                    }
                }

                //下载完后根据配置加密处理
                if (AtworkConfig.OPEN_DISK_ENCRYPTION) {
                    FileStreamHelper.encrypt(filePath, filePath, true);
                }


                return HttpResult.getInstance().netStatsOK();
            } else {
                return HttpResult.getInstance().netStatusNot200(statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            downloadHttpURLConnection.remove(msgId);
        }
    }


    public void saveImageFile(boolean needEncrypt, byte[] bytes, String saveFile) throws IOException {
        File tmpFile = new File(saveFile + ".tmp");

        FileUtil.createFile(tmpFile);

        OutputStream fos = FileStreamHelper.getOutputStream(tmpFile, needEncrypt);
        try {
            fos.write(bytes);
            fos.flush();

            File newFile = new File(saveFile);
            tmpFile.renameTo(newFile);

        } catch (Exception e) {
            if (tmpFile != null) {
                tmpFile.delete();
            }
            throw e;

        } finally {
            fos.close();
        }
    }

    /**
     * 上传一个聊天中存储的图片原图
     */
    public HttpResult uploadImageFile(Context context, UploadFileParamsMaker uploadFileParamsMaker) {

        String fileName = uploadFileParamsMaker.mFilePath.substring(uploadFileParamsMaker.mFilePath.lastIndexOf("/") + 1);
        String url = String.format(UrlConstantManager.getInstance().V2_uploadImageMedia(), uploadFileParamsMaker.mFileDigest, new File(uploadFileParamsMaker.mFilePath).length(), uploadFileParamsMaker.mExpireLimit, AtworkConfig.DOMAIN_ID, LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        uploadFileParamsMaker
                .setUrl(url)
                .setImg(true)
                .setFileName(fileName)
                .setReconnected(false);

        return uploadFile(uploadFileParamsMaker);
    }


    public HttpResult uploadFullImageFile(Context context, final String msgId, String fileDigest, String filePath, final MediaProgressListener mediaProgressListener) {

        long expireTime = DomainSettingsManager.getInstance().getChatFileExpiredTime();
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String url = String.format(UrlConstantManager.getInstance().V2_uploadImageMediaAndCompress(), fileDigest, expireTime, AtworkConfig.DOMAIN_ID, LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setMsgId(msgId)
                .setUrl(url)
                .setFilePath(filePath)
                .setFileName(fileName)
                .setReconnected(false)
                .setImg(true)
                .setOriginalImg(true)
                .setMediaProgressListener(mediaProgressListener);

        return uploadFileEnsured(uploadFileParamsMaker);
    }


    public HttpResult uploadCommonFile(Context context, UploadFileParamsMaker uploadFileParamsMaker) {


        String fileName = uploadFileParamsMaker.mFilePath.substring(uploadFileParamsMaker.mFilePath.lastIndexOf("/") + 1);
        String url = String.format(UrlConstantManager.getInstance().V2_uploadFileMedia(), uploadFileParamsMaker.mFileDigest, new File(uploadFileParamsMaker.mFilePath).length(), uploadFileParamsMaker.mExpireLimit, AtworkConfig.DOMAIN_ID, LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        uploadFileParamsMaker
                .setUrl(url)
                .setFileName(fileName)
                .setReconnected(true);


        return uploadFileEnsured(uploadFileParamsMaker);
    }

    @Nullable
    public String getMediaId(HttpResult httpResult) {
        String mediaId = null;
        if (httpResult.isNetSuccess()) {
            BasicResponseJSON basicResponseJSON = httpResult.resultResponse;

            //get mediaId
            if (null != basicResponseJSON) {
                if (basicResponseJSON instanceof MediaUploadResultResponseJson) {
                    mediaId = ((MediaUploadResultResponseJson) basicResponseJSON).mediaId;

                } else if (basicResponseJSON instanceof MediaInfoResponseJson) {
                    mediaId = ((MediaInfoResponseJson) basicResponseJSON).mediaInfo.id;
                }
            }

        }

        return mediaId;
    }

    @Nullable
    public String syncUploadFileAndGetMediaId(Context context, String filePath, boolean needCheckSum) {
        HttpResult httpResult = syncCheckSumAndUploadFile(context, filePath, needCheckSum);
        return getMediaId(httpResult);
    }

    public HttpResult syncCheckSumAndUploadFile(Context context, String filePath, boolean needCheckSum) {
        return syncCheckSumAndUploadFile(context, filePath, System.currentTimeMillis() + "", needCheckSum);
    }


    public HttpResult syncCheckSumAndUploadFile(Context context, String filePath, String fileId, boolean needCheckSum) {

        String checkSum = MD5Utils.getDigest(filePath);
        if (needCheckSum) {
            HttpResult httpResult = MediaCenterSyncNetService.getInstance().getMediaInfo(context, checkSum, "digest");
            //说明服务端有该文件
            if(httpResult.isRequestSuccess()) {
                MediaInfoResponseJson mediaInfoResponseJson = (MediaInfoResponseJson) httpResult.resultResponse;
                if (mediaInfoResponseJson.isLegal()) {
                    //TODO: link question
                    //+1s 文件
                    MediaCenterNetManager.linkMedia(context, mediaInfoResponseJson.mediaInfo.id, -1, null);

                    return httpResult;

                }

            }
        }


        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setMsgId(fileId)
                .setFileDigest(checkSum)
                .setFilePath(filePath)
                .setExpireLimit(-1);

        return MediaCenterHttpURLConnectionUtil.getInstance().uploadCommonFile(context, uploadFileParamsMaker);
    }



    public HttpResult resumeUploadAllFile(Context context, final String msgId, String domainId, Dropbox.SourceType sourceType, String sourceId,  String fileDigest, final String filePath, long startPos, long fileSize, final MediaProgressListener mediaProgressListener) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        long expireTime = DomainSettingsManager.getInstance().getChatFileExpiredTime();
        String url = String.format(UrlConstantManager.getInstance().dropboxUploadMediaFile(), domainId, sourceType.toString(), sourceId, fileDigest, fileSize, expireTime, loginUserAccessToken);
        return resumeUploadFile(msgId, url, filePath, fileName, startPos, fileSize, mediaProgressListener);
    }

    public HttpResult resumeUploadAllFile(String url, String fileId, String filePath, long fileSize, long startPos, final MediaProgressListener mediaProgressListener) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        return resumeUploadFile(fileId, url, filePath, fileName, startPos, fileSize, mediaProgressListener);
    }


    /**
     * 当tcp链接遇到服务器已经关闭了, 但 android 端没有对应关闭时, 在一开始写入时就会出现
     * sendto failed: EPIPE (Broken pipe)(与机型有关, note2 概率比较高), 此时该方法
     * 通过重新请求来对服务器重新建立新的 tcp链接
     */
    private HttpResult uploadFileEnsured(UploadFileParamsMaker uploadFileParamsMaker) {
        int tryTimes = 4;
        HttpResult httpResult = null;
        for (int i = 0; i < tryTimes; i++) {
            httpResult = uploadFile(uploadFileParamsMaker);

            if (httpResult.isNetSuccess() || !httpResult.isIOError()) {
                break;

            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return httpResult;
    }

    private HttpResult uploadFile(UploadFileParamsMaker uploadFileParamsMaker) {
        String msgId = uploadFileParamsMaker.mMsgId;
        MediaProgressListener mediaProgressListener = uploadFileParamsMaker.mMediaProgressListener;


        HttpURLConnection connection = null;
        DataOutputStream dos = null;
        InputStream is = null;
        try {
            connection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(uploadFileParamsMaker.mUrl);
            uploadHttpURLConnection.put(msgId, connection);
            setUploadConnectionHeader(uploadFileParamsMaker.mIsReconnected, connection);

            dos = new DataOutputStream(connection
                    .getOutputStream());

            StringBuilder strBuf = makeFormData(uploadFileParamsMaker);

            dos.write(strBuf.toString().getBytes());

            is = new FileInputStream(uploadFileParamsMaker.mFilePath);

            doWriteData(dos, is, mediaProgressListener);

            return handleResult(uploadFileParamsMaker, connection);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } catch (IOException e) {
            String errorMsg;

            if (e instanceof EOFException) {
                errorMsg = HttpResult.EXCEPTION_EOF;
            } else {
                errorMsg = e.getLocalizedMessage();
            }

            Logger.e("Audio RECORD", "e.getLocalizedMessage() => " + errorMsg);
            e.printStackTrace();
            return HttpResult.getInstance().netException(errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } finally {

            checkClose(msgId, connection, dos, is);
        }

    }

    private void doWriteData(DataOutputStream dos, InputStream is, MediaProgressListener mediaProgressListener) throws IOException {
        long total = is.available();
        byte[] bytes = new byte[1024];
        int len = 0;
        int length = 0;
        int i = 0;

        while ((len = is.read(bytes)) != -1) {
            dos.write(bytes, 0, len);
            length += len;
            i++;

            if (i == getProgeressStandard(total)) {
                if (mediaProgressListener != null) {
                    mediaProgressListener.progress((length / (float) total) * 100, length);
                    i = 0;
                }
            }
        }
        is.close();
        dos.writeBytes(end + twoHyphens + boundary + twoHyphens + end);
        dos.flush();
    }

    private void setUploadConnectionHeader(boolean isReconnected, HttpURLConnection connection) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setChunkedStreamingMode(1024);
        connection.setConnectTimeout(10 * 1000);
        connection.setReadTimeout(60 * 1000 * 10);
        if (isReconnected) {
            connection.setRequestProperty("Connection", "close");

        } else {
            connection.setRequestProperty("Connection", "Keep-Alive");
        }
        connection.setRequestProperty("Charset", "UTF-8");
        connection
                .setRequestProperty(
                        "Accept",
                        "image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");

        connection.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=" + boundary);



    }

    @NonNull
    private StringBuilder makeFormData(UploadFileParamsMaker uploadFileParamsMaker) {
        StringBuilder strBuf = new StringBuilder();

        //发送文件信息
        strBuf.append(end).append(twoHyphens).append(boundary).append(end);
        String fileName = uploadFileParamsMaker.mFileName;
        if (fileName.endsWith("-original")) {
            fileName += ".jpg";
        }
        if (uploadFileParamsMaker.mIsOriginalImg) {

            strBuf.append("Content-Disposition: form-data; name=\"media\"; filename=\"").append(fileName).append("\"");

        } else {
            strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"");
        }


        if (uploadFileParamsMaker.mIsImg) {
            strBuf.append(end);
            strBuf.append("Content-Type: image/jpeg");
        }

        strBuf.append(end).append(end);
        return strBuf;
    }

    private void checkClose(String msgId, HttpURLConnection connection, DataOutputStream dos, InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            connection.disconnect();
        }
        uploadHttpURLConnection.remove(msgId);
    }

    private HttpResult handleResult(UploadFileParamsMaker uploadFileParamsMaker, HttpURLConnection connection) throws IOException {
        int statusCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == statusCode) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = readInput(in);

            LogUtil.e("media", "handle result ->   " + result);

            HttpResult httpResult = HttpResult.getInstance().netStatsOK().result(result);
            if(StringUtils.isEmpty(result)) {
                httpResult.resultResponse = new BasicResponseJSON();
                httpResult.resultResponse.status = -1;
                return httpResult;
            }

            if(uploadFileParamsMaker.mIsOriginalImg) {
                httpResult.result(JsonUtil.fromJson(result, MediaCompressResponseJson.class));

            } else {
                BasicResponseJSON basicResponseJSON = JsonUtil.fromJson(result, MediaUploadResultResponseJson.class);
                if(null == basicResponseJSON) {
                    basicResponseJSON = JsonUtil.fromJson(result, BasicResponseJSON.class);
                }
                httpResult.result(basicResponseJSON);
            }


            return httpResult;
        } else {
            return HttpResult.getInstance().netStatusNot200(statusCode);
        }
    }




    private HttpResult resumeUploadFile(final String msgId, final String url, final String filePath, String fileName, long startPos, long fileSize,
                                        final MediaProgressListener mediaProgressListener) {

        HttpURLConnection connection = null;
        DataOutputStream dos = null;
        InputStream is = null;
        try {
            connection = HttpUrlConnectionUtil.getInstance().handleUrlConnection(url);
            uploadHttpURLConnection.put(msgId, connection);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setChunkedStreamingMode(1024);
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(60 * 1000 * 10);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Accept",
                    "image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");

            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);



            //500K
            long endPos = startPos + sResumeChuck;
            if (endPos > fileSize) {
                endPos = fileSize - 1;
            }
            connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos + "/" + fileSize);


            dos = new DataOutputStream(connection
                    .getOutputStream());
            //发送属性信息
            Map<String, String> paramsMap = new HashMap<>();
            File file = new File(filePath);
            if (file.exists()) {
                paramsMap.put("file_name", fileName);
                paramsMap.put("file_size", file.length() + "");
            }

            StringBuilder strBuf = new StringBuilder();

            strBuf.append(twoHyphens).append(boundary).append(end);
            strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + fileName
                    + "\"");

            strBuf.append(end).append(end);
            dos.write(strBuf.toString().getBytes());

            is = new FileInputStream(filePath);
            FileRangeUtil.copy(is, dos, fileSize, startPos, (endPos - startPos + 1));
            is.close();
            dos.writeBytes(end + twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = readInput(in);
                MediaDigest mediaDigest = new Gson().fromJson(result, MediaDigest.class);
                if (mediaDigest != null && mediaDigest.status == 0) {
                    mediaProgressListener.progress(endPos, endPos);
                }
                return HttpResult.getInstance().netStatsOK().result(result).result(mediaDigest);
            } else {
                return HttpResult.getInstance().netStatusNot200(statusCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } catch (IOException e) {
            String errorMsg;

            if (e instanceof EOFException) {
                errorMsg = HttpResult.EXCEPTION_EOF;
            } else {
                errorMsg = e.getLocalizedMessage();
            }

            Logger.e("Audio RECORD", "e.getLocalizedMessage() => " + errorMsg);
            e.printStackTrace();
            return HttpResult.getInstance().netException(errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.getInstance().netException(e.getLocalizedMessage());
        } finally {
            checkClose(msgId, connection, dos, is);
        }
    }

    /**
     * 制定通知 progress 的标准, 当大于1M 时每100k 通知一次, 其余的每10k 通知一次
     */
    private int getProgeressStandard(long totalSize) {
        if (1024 * 1024 < totalSize) {
            return 100;
        } else {
            return 10;
        }
    }

    /**
     * @param needEncrypt
     * @param input
     * @param lengthOfFile
     * @param saveFile
     * @param mediaProgressListener
     * */
    private void saveToFile(boolean needEncrypt, BufferedInputStream input, final long lengthOfFile, String saveFile, @Nullable final MediaProgressListener mediaProgressListener) throws IOException {

        File tmpFile = new File(saveFile + ".tmp");

        FileUtil.createFile(tmpFile);

        OutputStream os = FileStreamHelper.getOutputStream(tmpFile, needEncrypt);
        try {
            int nRead;
            byte[] data = new byte[1024];
            long total = 0;
            int i = 0;

            if (mediaProgressListener != null) {
                mediaProgressListener.progress(0, 0);
            }

            while ((nRead = input.read(data, 0, data.length)) != -1) {
                total += nRead; //total = total + len1
                os.write(data, 0, nRead);
                if (mediaProgressListener != null) {
                        mediaProgressListener.progress((total * 100) / lengthOfFile, total);
                }
            }
            os.flush();

            File newFile = new File(saveFile);
            tmpFile.renameTo(newFile);
        } catch (Exception e) {
            if (tmpFile != null) {
                tmpFile.delete();
            }
            throw e;

        } finally {
            os.close();
        }
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

    /**
     * 媒体中心上传下载进度条
     */
    public interface MediaProgressListener {
        void progress(double value, double size);

    }




    public enum DOWNLOAD_TYPE {
        FILE, THUMBNAIL, ORIGINAL
    }

    /**
     * 把输入流转换成字符数组
     *
     * @param inputStream 输入流
     * @return 字符数组
     * @throws Exception
     */
    public byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();

        return bout.toByteArray();
    }
}
