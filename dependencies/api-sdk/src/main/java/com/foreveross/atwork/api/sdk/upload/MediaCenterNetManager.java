package com.foreveross.atwork.api.sdk.upload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.foreveross.atwork.api.sdk.MediaFailListener;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.chat.LinkMediaResp;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.BreakMediaListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.model.LinkMediasRequest;
import com.foreveross.atwork.api.sdk.upload.model.MediaCompressResponseJson;
import com.foreveross.atwork.api.sdk.upload.model.MediaDigest;
import com.foreveross.atwork.api.sdk.upload.model.MediaInfoResponseJson;
import com.foreveross.atwork.api.sdk.upload.model.MediaUploadResultResponseJson;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.aliyun.CdnHelper;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lingen on 15/4/21.
 * Description:
 */

public class MediaCenterNetManager {


    private UrlConstantManager mUrlConstantManager = UrlConstantManager.getInstance();

    public static String IMAGE_FULL_FILE = "FULL_IMAGE";
    public static String IMAGE_FILE = "IMAGE";
    public static String COMMON_FILE = "COMMON";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();



    public static CopyOnWriteArrayList<MediaUploadListener> sMediaUploadListenerList = new CopyOnWriteArrayList<>();
    //下载监听接口
    public static CopyOnWriteArrayList<MediaDownloadListener> sMediaDownloadListeners = new CopyOnWriteArrayList<>();
    private static Map<String, String> sUploadSuccessList = new HashMap<>();
    private static Map<String, Integer> sUploadFailList = new HashMap<>();

    public static Set<String> sDownloadingList = new HashSet<>();
    public static Set<String> sUploadingList = new HashSet<>();


    private static CopyOnWriteArraySet<String> sReDownloadingSet = new CopyOnWriteArraySet<>();

    public static ExecutorService UPLOAD_THREAD_POOL = Executors.newFixedThreadPool(CPU_COUNT + 1);
    public static ExecutorService DOWNLOAD_THREAD_POOL = Executors.newFixedThreadPool(CPU_COUNT + 1);

    private Context context;

    public MediaCenterNetManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void addReDownloading(String msgId) {
        sReDownloadingSet.add(msgId);
    }

    public static void removeReDownloading(String msgId) {
        sReDownloadingSet.remove(msgId);
    }

    public static boolean isReDownloading(String msgId) {
        return sReDownloadingSet.contains(msgId);
    }


    public static boolean havingMediaUploadListenerByMsgId(String msgId) {
        boolean having = false;

        for (MediaUploadListener mediaUploadListener : sMediaUploadListenerList) {
            if (msgId.equals(mediaUploadListener.getMsgId())) {
                having = true;
                break;
            }
        }

        return having;
    }

    public static MediaUploadListener getMediaUploadListenerById(String msgId, UploadType type) {
        MediaUploadListener mediaUploadListenerResult = null;

        for (MediaUploadListener mediaUploadListener : sMediaUploadListenerList) {
            if (msgId.equals(mediaUploadListener.getMsgId())
                    && type.equals(mediaUploadListener.getType())) {
                mediaUploadListenerResult = mediaUploadListener;
                break;
            }
        }

        return mediaUploadListenerResult;
    }

    public static void removeMediaUploadListenerById(String msgId, UploadType type) {
        MediaUploadListener mediaUploadListener = getMediaUploadListenerById(msgId, type);
        if (null != mediaUploadListener) {
            removeMediaUploadListener(mediaUploadListener);
        }
    }


    public static void removeMediaUploadListener(final MediaUploadListener mediaUploadListener) {
        sMediaUploadListenerList.remove(mediaUploadListener);
    }

    public static void addMediaUploadListener(final MediaUploadListener mediaUploadListener) {
        if (sUploadSuccessList.containsKey(mediaUploadListener.getMsgId())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaUploadListener.uploadProgress(100d);
                    mediaUploadListener.uploadSuccess(sUploadSuccessList.get(mediaUploadListener.getMsgId()));
                }
            }, 500);

        } else if (sUploadFailList.containsKey(mediaUploadListener.getMsgId())) {
            int errorStatus = sUploadFailList.get(mediaUploadListener.getMsgId());
            //-99 为中断操作, 中断时已经通知了 uploadFailed
            if (-99 != errorStatus) {
                mediaUploadListener.uploadFailed(errorStatus, "", true);
            } else {
                sMediaUploadListenerList.add(mediaUploadListener);

                removeUploadFailList(mediaUploadListener.getMsgId());
            }

        } else {
            sMediaUploadListenerList.add(mediaUploadListener);
        }

    }

    public static void removeUploadFailList(String msgId) {
        sUploadFailList.remove(msgId);
    }


    public static void breakAll(boolean doRefreshView) {
        try {
            for (MediaUploadListener mediaUploadListener : sMediaUploadListenerList) {
                mediaUploadListener.uploadFailed(-99, null, false);
                sUploadFailList.put(mediaUploadListener.getMsgId(), -99);
            }

            for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                mediaDownloadListener.downloadFailed(-99, null, false);
            }

            sMediaDownloadListeners.clear();
            sMediaUploadListenerList.clear();
            sDownloadingList.clear();
            sUploadFailList.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isDownloading(String keyId) {
        return sDownloadingList.contains(keyId);
    }



    public static boolean isUploading(String keyId) {
        return sUploadingList.contains(keyId);

    }

    public static boolean havingMediaDownloadListenerByMsgId(String msgId) {
        boolean having = false;

        for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
            if (msgId.equals(mediaDownloadListener.getMsgId())) {
                having = true;
                break;
            }
        }

        return having;
    }

    public static MediaDownloadListener getMediaDownloadListenerById(String msgId) {
        MediaDownloadListener mediaDownloadListenerResult = null;

        for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
            if (msgId.equals(mediaDownloadListener.getMsgId())) {
                mediaDownloadListenerResult = mediaDownloadListener;
                break;
            }
        }

        return mediaDownloadListenerResult;
    }

    public static void removeMediaDownloadListener(MediaDownloadListener mediaDownloadListener) {
        sMediaDownloadListeners.remove(mediaDownloadListener);
    }

    public static void removeMediaDownloadListenerById(String msgId) {
        MediaDownloadListener mediaDownloadListener = getMediaDownloadListenerById(msgId);
        if (null != mediaDownloadListener) {
            removeMediaDownloadListener(mediaDownloadListener);
        }
    }

    public static void addMediaDownloadListener(final MediaDownloadListener mediaDownloadListener) {
        sMediaDownloadListeners.add(mediaDownloadListener);
    }


    /**
     * @see {@link #brokenDownloadingOrUploading(String, BreakMediaListener)}
     * */
    public static void brokenDownloadingOrUploading(final String msgId) {
        brokenDownloadingOrUploading(msgId, null);
    }

    /**
     * 中断一个下载行为
     *
     * @param msgId
     */
    public static void brokenDownloadingOrUploading(final String msgId, @Nullable final BreakMediaListener listener) {
        List<MediaUploadListener> uploadRemoves = new ArrayList<>();
        for (MediaUploadListener mediaUploadListener : sMediaUploadListenerList) {
            if (mediaUploadListener.getMsgId().equals(msgId)) {
                mediaUploadListener.uploadFailed(-99, null, true);
                uploadRemoves.add(mediaUploadListener);

                sUploadFailList.put(mediaUploadListener.getMsgId(), -99);
            }
        }


        List<MediaDownloadListener> downloadRemoves = new ArrayList<>();

        for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
            if (mediaDownloadListener.getMsgId().equals(msgId)) {
                mediaDownloadListener.downloadFailed(-99, null, true);
                downloadRemoves.add(mediaDownloadListener);
            }
        }

        sMediaUploadListenerList.removeAll(uploadRemoves);
        sMediaDownloadListeners.removeAll(downloadRemoves);

        MediaCenterHttpURLConnectionUtil.getInstance().brokenMedia(msgId, listener);
    }

    public byte[] syncDownloadFile(final String mediaId, final String msgId, final String filePath) {
        byte[] content = new byte[0];
        String downloadUrl = String.format(mUrlConstantManager.V2_getDownloadUrl(true), mediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        downloadUrl = CdnHelper.wrapCdnUrl(downloadUrl);
        sDownloadingList.add(mediaId);


        HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setDownloadId(msgId).setDownloadUrl(downloadUrl).
                        setDownloadPath(filePath).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION).
                        setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE));

        sDownloadingList.remove(mediaId);

        if (httpResult.isNetSuccess()) {
            return httpResult.byteResult;
        }
        return content;
    }


    /**
     * 下载文件
     *
     */
    @SuppressLint("StaticFieldLeak")
    public void downloadFile(final DownloadFileParamsMaker paramsMaker) {
        String downloadUrl;
        String media = paramsMaker.getMediaId();
        downloadUrl = getDownloadUrl(context, media);

        sDownloadingList.add(media);
        paramsMaker.setDownloadUrl(downloadUrl).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION);

        Log.d("DOWNLOADIMAGE", downloadUrl);
        final String finalDownloadUrl = downloadUrl;
        new AsyncTask<String, Double, HttpResult>() {
            @Override
            protected HttpResult doInBackground(String... params) {

                HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                        paramsMaker.setProgressListener(new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                            @Override
                            public void progress(double value, double size) {
                                publishProgress(value, size);
                            }
                        })
                );
                return httpResult;
            }


            @Override
            protected void onProgressUpdate(Double... values) {
                for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                    if (mediaDownloadListener.getMsgId().equals(paramsMaker.getDownloadId())) {
                        mediaDownloadListener.downloadProgress(values[0], values[1]);
                    }
                }
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                sDownloadingList.remove(paramsMaker.getMediaId());

                if (httpResult.isNetError()) {

                    for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                        if (mediaDownloadListener.getMsgId().equals(paramsMaker.getDownloadId())) {
                            mediaDownloadListener.downloadFailed(httpResult.status, httpResult.error, true);
                        }
                    }

                } else if (httpResult.isNetFail()) {
                    for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                        if (mediaDownloadListener.getMsgId().equals(paramsMaker.getDownloadId())) {
                            mediaDownloadListener.downloadFailed(httpResult.statusCode, null, true);
                        }


                    }
                } else {
                    for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                        if (mediaDownloadListener.getMsgId().equals(paramsMaker.getDownloadId())) {
                            mediaDownloadListener.downloadSuccess();
                        }
                    }

                }

                if (isReDownloading(paramsMaker.getDownloadId())) {
                    removeReDownloading(paramsMaker.getDownloadId());
                }
            }
        }.executeOnExecutor(DOWNLOAD_THREAD_POOL);
    }

    @NotNull
    public static String getDownloadUrl(Context context, String media) {
        String downloadUrl;
        if (media.startsWith("http")) {
            downloadUrl = media;
        } else {
            downloadUrl = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), media, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
            downloadUrl = CdnHelper.wrapCdnUrl(downloadUrl);
        }
        return downloadUrl;
    }

    @SuppressLint("StaticFieldLeak")
    public void resumeDownloadFile(final String mediaId, final String msgId, final String domainId, final Dropbox.SourceType sourceType, final String sourceId, final long starPos, final long fileSize, final String filePath) {
        final String downloadUrl = String.format(mUrlConstantManager.dropboxMediaFile(), domainId, sourceType, sourceId, msgId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        sDownloadingList.add(msgId);

        new AsyncTask<String, Double, HttpResult>() {
            @Override
            protected HttpResult doInBackground(String... params) {
                HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().resumeDownloadFile(msgId, downloadUrl, starPos, fileSize, filePath, new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                    @Override
                    public void progress(double value, double size) {
                        publishProgress(value, size);
                    }
                });
                return httpResult;
            }


            @Override
            protected void onProgressUpdate(Double... values) {
                for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                    if (mediaDownloadListener.getMsgId().equals(msgId)) {
                        mediaDownloadListener.downloadProgress(values[0], values[1]);
                    }
                }
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                sDownloadingList.remove(msgId);

                if (httpResult.isNetError()) {

                    for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                        if (mediaDownloadListener.getMsgId().equals(msgId)) {
                            mediaDownloadListener.downloadFailed(httpResult.status, httpResult.error, true);
                        }
                    }

                } else if (httpResult.isNetFail()) {
                    for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                        if (mediaDownloadListener.getMsgId().equals(msgId)) {
                            mediaDownloadListener.downloadFailed(httpResult.statusCode, null, true);
                        }
                    }
                } else {
                    for (MediaDownloadListener mediaDownloadListener : sMediaDownloadListeners) {
                        if (mediaDownloadListener.getMsgId().equals(msgId)) {
//                            int status = NetWorkHttpResultHelper.getResultStatus(httpResult.result);
//                            if (status != 0) {
//                                mediaDownloadListener.downloadFailed(status, NetWorkHttpResultHelper.getResultMessage(httpResult.result), true);
//                                return;
//                            }
                            mediaDownloadListener.downloadSuccess();
                        }
                    }
                }
            }
        }.executeOnExecutor(DOWNLOAD_THREAD_POOL);
    }


    /**
     * 上传原图
     * */
    @SuppressLint("StaticFieldLeak")
    public static void uploadFullImageFile(final Context context, final String msgId, final String filePath) {

        sUploadingList.add(msgId);

        new AsyncTask<String, Double, HttpResult>() {
            String originalPath;

            @Override
            protected HttpResult doInBackground(String... strings) {
                HttpResult httpResult = null;
                originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(filePath, true);

                String checkSum = MD5Utils.getDigest(originalPath);
                long expiredTime = DomainSettingsManager.getInstance().getChatFileExpiredTime();
                httpResult = MediaCenterSyncNetService.getInstance().getCompressMediaInfo(context, checkSum);
                //说明服务端有该文件
                if (httpResult.isRequestSuccess()) {
                    MediaCompressResponseJson compressResponseJson = (MediaCompressResponseJson) httpResult.resultResponse;
                    if(compressResponseJson.isLegal()) {
                        //TODO: link question: digest返回来的mediaId有没有必要再link一次？？（待后台确认）
                        //+1s 原图
                        linkMedia(context, compressResponseJson.mMediaCompressInfo.mOriginalImg.mMediaId, expiredTime, null);

                        //继续+1s 压缩图
                        linkMedia(context, compressResponseJson.mMediaCompressInfo.mCompressImg.mMediaId, expiredTime, null);

                        publishProgress(100.0);
                        return httpResult;

                    }

                }

                httpResult = MediaCenterHttpURLConnectionUtil.getInstance().uploadFullImageFile(context, msgId, checkSum, originalPath, new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                    @Override
                    public void progress(double value, double size) {
                        publishProgress(value, size);
                    }
                });

                return httpResult;
            }

            @Override
            protected void onProgressUpdate(Double... values) {
                handleUploadProgressUpdate(msgId, values[0]);

            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                handleUploadOriginalImage(httpResult, msgId, originalPath);
            }
        }.executeOnExecutor(UPLOAD_THREAD_POOL);
    }

    /**
     * 上传一个FILE
     * @param uploadFileParamsMaker
     */
    @SuppressLint("StaticFieldLeak")
    public static void uploadFile(final Context context, final UploadFileParamsMaker uploadFileParamsMaker) {
        if (StringUtils.isEmpty(uploadFileParamsMaker.mFilePath)) {
            return;
        }

        sUploadingList.add(uploadFileParamsMaker.mMsgId);


        new AsyncTask<String, Double, HttpResult>() {
            String originalPath;
            @Override
            protected HttpResult doInBackground(String... params) {
                HttpResult httpResult = null;
                originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(uploadFileParamsMaker.mFilePath, true);
                uploadFileParamsMaker.setFilePath(originalPath);

                String checkSum = MD5Utils.getDigest(originalPath);


                if (uploadFileParamsMaker.mNeedCheckSum) {


                    //step1. 优先检查网络端文件MD5, 如果存在，直接使用返回的MediaId，否则上传文件
                    httpResult = MediaCenterSyncNetService.getInstance().getMediaInfo(context, checkSum, "digest");

                    //说明服务端有该文件
                    if(httpResult.isRequestSuccess()) {
                        MediaInfoResponseJson mediaInfoResponseJson = (MediaInfoResponseJson) httpResult.resultResponse;
                        if (mediaInfoResponseJson.isLegal()) {
                            //TODO: link question: digest返回来的mediaId有没有必要再link一次？？（待后台确认）
                            //+1s 文件
                            MediaCenterNetManager.linkMedia(context, mediaInfoResponseJson.mediaInfo.id, uploadFileParamsMaker.mExpireLimit, null);

                            publishProgress(100.0);
                            return httpResult;

                        }

                    }

                }

                uploadFileParamsMaker.setFileDigest(checkSum).setMediaProgressListener(new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                    @Override
                    public void progress(double value, double size) {
                        publishProgress(value, size);
                    }
                });

                if (IMAGE_FILE.equalsIgnoreCase(uploadFileParamsMaker.mType)) {
                    httpResult = MediaCenterHttpURLConnectionUtil.getInstance().uploadImageFile(context, uploadFileParamsMaker);
                } else {
                    httpResult = MediaCenterHttpURLConnectionUtil.getInstance().uploadCommonFile(context, uploadFileParamsMaker);
                }
                return httpResult;
            }


            @Override
            protected void onProgressUpdate(Double... values) {
                super.onProgressUpdate(values);
                handleUploadProgressUpdate(uploadFileParamsMaker.mMsgId, values[0]);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                handleUploadFilesFinish(httpResult, uploadFileParamsMaker.mMsgId, originalPath);

            }

        }.executeOnExecutor(UPLOAD_THREAD_POOL);
    }


    @SuppressLint("StaticFieldLeak")
    public void resumeUploadFile(final Context context, final String msgId, final String domainId, final Dropbox.SourceType sourceType, final String sourceId, final String fileDigest, final String mediaId, final String filePath, final long startPos, final long fileSize, final MediaFailListener mediaFailListener) {
        if (StringUtils.isEmpty(filePath) && TextUtils.isEmpty(mediaId)) {
            return;
        }

        sUploadingList.add(msgId);

        new AsyncTask<String, Double, HttpResult>() {
            @Override
            protected HttpResult doInBackground(String... params) {
                HttpResult httpResult = null;
                if (!TextUtils.isEmpty(mediaId)) {
                    MediaDigest mediaDigest = new MediaDigest();
                    MediaDigest.MediaDigestResult mediaDigestResult = new MediaDigest.MediaDigestResult();
                    mediaDigestResult.mStart = 0;
                    mediaDigestResult.mCompleted = true;
                    mediaDigestResult.mFileSize = fileSize;
                    mediaDigestResult.mId = mediaId;
                    mediaDigest.mMediaDigestResult = mediaDigestResult;
                    httpResult = HttpResult.getInstance().netStatsOK().result(mediaDigest);
                    publishProgress(100.0);
                    return httpResult;
                }

                if (TextUtils.isEmpty(fileDigest)) {
                    String checkSum = MD5Utils.getDigest(filePath);
                    File file = new File(filePath);
                    //step1. 优先检查网络端文件MD5, 如果存在，直接使用返回的MediaId，否则上传文件
                    httpResult = MediaCenterSyncNetService.getInstance().postMediaDigest(context, checkSum, file.getName(), fileSize, DomainSettingsManager.getInstance().getChatFileExpiredTime());
                    MediaDigest mediaDigest = null;
                    //说明服务端有该文件
                    if (httpResult.isNetSuccess()) {

                        if (0 == httpResult.resultResponse.status) {
                            mediaDigest = (MediaDigest) httpResult.resultResponse;
                            if (null != mediaDigest && mediaDigest.mMediaDigestResult != null && mediaDigest.mMediaDigestResult.mState == 0) {
                                publishProgress(100.0);
                                return httpResult;
                            }

                        }
                    }
                    if (mediaDigest == null || mediaDigest.mMediaDigestResult == null) {
                        return httpResult;
                    }
                    httpResult = MediaCenterHttpURLConnectionUtil.getInstance().resumeUploadAllFile(context, msgId, domainId, sourceType, sourceId, mediaDigest.mMediaDigestResult.mId, filePath, startPos, fileSize, new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                        @Override
                        public void progress(double value, double size) {
                            publishProgress(value, size);
                        }
                    });
                    return httpResult;
                }
                httpResult = MediaCenterHttpURLConnectionUtil.getInstance().resumeUploadAllFile(context, msgId, domainId, sourceType, sourceId, fileDigest, filePath, startPos, fileSize, new MediaCenterHttpURLConnectionUtil.MediaProgressListener() {
                    @Override
                    public void progress(double value, double size) {
                        publishProgress(value, size);
                    }
                });
                return httpResult;
            }


            @Override
            protected void onProgressUpdate(Double... values) {
                super.onProgressUpdate(values);
                handleUploadProgressUpdate(msgId, values[0]);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                Vector<MediaUploadListener> mediaUploadListeners = new Vector<>();
                mediaUploadListeners.addAll(MediaCenterNetManager.sMediaUploadListenerList);
                if (httpResult.isNetSuccess()) {

                    BasicResponseJSON basicResponseJSON = httpResult.resultResponse;
                    String mediaId = "";
                    //get mediaId
                    if (null != basicResponseJSON) {
                        MediaDigest digest = (MediaDigest) basicResponseJSON;
                        if (digest.mMediaDigestResult.mState == 1) {
                            resumeUploadFile(context, msgId, domainId, sourceType, sourceId, digest.mMediaDigestResult.mId, "", filePath, startPos + MediaCenterHttpURLConnectionUtil.sResumeChuck, fileSize, mediaFailListener);
                            return;
                        }
                        mediaId = digest.mMediaDigestResult.mId;
                    }

                    if (null != basicResponseJSON && !StringUtils.isEmpty(mediaId)) {

                        EncryptCacheDisk.getInstance().removeUsing(filePath);

                        sUploadSuccessList.put(msgId, mediaId);

                        for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
                            if (mediaUploadListener.getMsgId().equals(msgId)) {
                                mediaUploadListener.uploadSuccess(mediaId);

                            }
                        }

                    } else {
                        for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
                            if (mediaUploadListener.getMsgId().equals(msgId)) {
                                if (basicResponseJSON == null) {
                                    mediaUploadListener.uploadFailed(-1, null, true);
                                } else {
                                    mediaUploadListener.uploadFailed(basicResponseJSON.status, null, true);
                                }

                            }
                        }

                    }
                } else if (httpResult.isNetError()) {
                    handleUploadNetError(httpResult, msgId, mediaUploadListeners);


                } else if (httpResult.isNetFail()) {
                    handleUploadNetFail(httpResult, msgId, mediaUploadListeners);
                }

                sUploadingList.remove(msgId);
                mediaUploadListeners.clear();

            }
        }.executeOnExecutor(UPLOAD_THREAD_POOL);
    }

    private static void handleUploadProgressUpdate(String msgId, Double progress) {
        if (progress != 0) {
            List<MediaUploadListener> mediaUploadListeners = new ArrayList<>();
            mediaUploadListeners.addAll(sMediaUploadListenerList);
            for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
                if (mediaUploadListener.getMsgId().equals(msgId)) {
                    mediaUploadListener.uploadProgress(progress);
                }
            }
            mediaUploadListeners.clear();
        }
    }

    private static void handleUploadOriginalImage(HttpResult httpResult, String msgId, String originalPath) {
        sUploadingList.remove(msgId);

        Vector<MediaUploadListener> mediaUploadListeners = new Vector<>();
        mediaUploadListeners.addAll(MediaCenterNetManager.sMediaUploadListenerList);
        if (httpResult.isNetSuccess()) {


            if (httpResult.isRequestSuccess()) { //success

                EncryptCacheDisk.getInstance().removeUsing(originalPath);

                sUploadSuccessList.put(msgId, httpResult.result);

                for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
                    if (mediaUploadListener.getMsgId().equals(msgId)) {
                        mediaUploadListener.uploadSuccess(httpResult.result);

                    }
                }

            } else {
                for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
                    if (mediaUploadListener.getMsgId().equals(msgId)) {
                        mediaUploadListener.uploadFailed(httpResult.resultResponse.status, null, true);
                    }
                }

            }
        } else if (httpResult.isNetError()) {
            handleUploadNetError(httpResult, msgId, mediaUploadListeners);


        } else if (httpResult.isNetFail()) {
            handleUploadNetFail(httpResult, msgId, mediaUploadListeners);
        }

        mediaUploadListeners.clear();

    }

    private static void handleUploadFilesFinish(HttpResult httpResult, String msgId, String originalPath) {
        sUploadingList.remove(msgId);

        Vector<MediaUploadListener> mediaUploadListeners = new Vector<>();
        mediaUploadListeners.addAll(MediaCenterNetManager.sMediaUploadListenerList);
        if (httpResult.isNetSuccess()) {

            handleUploadFileNetSuccess(httpResult, msgId, originalPath, mediaUploadListeners);
        } else if (httpResult.isNetError()) {
            handleUploadNetError(httpResult, msgId, mediaUploadListeners);


        } else if (httpResult.isNetFail()) {
            handleUploadNetFail(httpResult, msgId, mediaUploadListeners);
        }

        mediaUploadListeners.clear();
    }



    private static void handleUploadFileNetSuccess(HttpResult httpResult, String msgId, String originalPath, Vector<MediaUploadListener> mediaUploadListeners) {
        BasicResponseJSON basicResponseJSON = httpResult.resultResponse;
        String mediaId = getMediaId(basicResponseJSON);

        if (null != basicResponseJSON && !StringUtils.isEmpty(mediaId)) { //success

            EncryptCacheDisk.getInstance().removeUsing(originalPath);

            sUploadSuccessList.put(msgId, mediaId);

            for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
                if (mediaUploadListener.getMsgId().equals(msgId)) {
                    mediaUploadListener.uploadSuccess(mediaId);

                }
            }

        } else {
            for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
                if (mediaUploadListener.getMsgId().equals(msgId)) {
                    int status = -1;
                    if(null != basicResponseJSON) {
                        status = basicResponseJSON.status;
                    }
                    mediaUploadListener.uploadFailed(status, null, true);
                }
            }

        }
    }

    public static String getMediaId(BasicResponseJSON basicResponseJSON) {
        String mediaId = "";
        //get mediaId
        if (null != basicResponseJSON) {
            if (basicResponseJSON instanceof MediaUploadResultResponseJson) {
                mediaId = ((MediaUploadResultResponseJson) basicResponseJSON).mediaId;

            } else if (basicResponseJSON instanceof MediaInfoResponseJson) {
                mediaId = ((MediaInfoResponseJson) basicResponseJSON).mediaInfo.id;

            }
        }
        return mediaId;
    }

    private static void handleUploadNetFail(HttpResult httpResult, String msgId, Vector<MediaUploadListener> mediaUploadListeners) {
        sUploadFailList.put(msgId, httpResult.statusCode);

        for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
            if (mediaUploadListener.getMsgId().equals(msgId)) {
                mediaUploadListener.uploadFailed(httpResult.statusCode, null, true);
            }
        }
    }

    private static void handleUploadNetError(HttpResult httpResult, String msgId, Vector<MediaUploadListener> mediaUploadListeners) {
        //-99为主动取消下载, 若 sUploadFailList 已经存在-99, 则不用中断的 error覆盖
        if (!sUploadFailList.containsKey(msgId) || -99 != sUploadFailList.get(msgId)) {
            sUploadFailList.put(msgId, httpResult.status);
        }

        for (MediaUploadListener mediaUploadListener : mediaUploadListeners) {
            if (mediaUploadListener.getMsgId().equals(msgId)) {
                mediaUploadListener.uploadFailed(httpResult.status, httpResult.error, true);
            }
        }
    }


    public static void linkMedia(final Context context, final String mediaId, final OnMediaLinkedListener listener) {
        if(StringUtils.isEmpty(mediaId)) {
            return;
        }

        linkMedia(context, mediaId, DomainSettingsManager.getInstance().getChatFileExpiredTime(), listener);
    }

    public static void linkMedia(final Context context, final String mediaId, final long expireTime, final OnMediaLinkedListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                return MediaCenterSyncNetService.getInstance().linkMedia(context, mediaId, expireTime);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if (httpResult.isRequestSuccess()) {
                    listener.mediaLinked(((LinkMediaResp)httpResult.resultResponse).getResult().linkedId);
                    return;
                }
                listener.mediaLinked("");
            }
        }.execute();
    }

    public static void linkMedias(final Context context, final List<String> mediaIds, final OnMediaLinkedListener listener) {
        if(ListUtil.isEmpty(mediaIds)) {
            return;
        }

        linkMedias(context, mediaIds, DomainSettingsManager.getInstance().getChatFileExpiredTime(), listener);
    }

    public static void linkMedias(final Context context, final List<String> mediaIds, final long expireTime, final OnMediaLinkedListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                LinkMediasRequest request = new LinkMediasRequest();
                request.expireTime = expireTime;
                request.mediaIds = mediaIds;
                return MediaCenterSyncNetService.getInstance().linkMedias(context, JsonUtil.toJson(request));
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if (httpResult.isRequestSuccess()) {
                    listener.mediaLinked(((LinkMediaResp)httpResult.resultResponse).getResult().linkedId);
                    return;
                }
                listener.mediaLinked("");
            }
        }.execute();
    }

    public interface OnMediaLinkedListener {
        void mediaLinked(String linkedMediaId);
    }


    public interface MediaDownloadListener {

        /**
         * 返回此监听器的MSGID
         */
        String getMsgId();

        /**
         * 下载成功
         */
        void downloadSuccess();

        /**
         * 下载失败
         *
         * @param errorCode
         * @param errorMsg
         */
        void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView);

        /**
         * 下载进度
         *
         * @param progress
         */
        void downloadProgress(double progress, double size);

    }

    public interface MediaUploadListener {


        /**
         * 返回此监听器的MSGID
         */
        String getMsgId();

        /**
         * 返回上传Listener的类型
         */
        UploadType getType();


        /**
         * 上传成功
         */
        void uploadSuccess(String mediaInfo);

        /**
         * 上传失败
         *
         * @param errorCode
         * @param errorMsg
         */
        void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView);

        /**
         * 上传进度
         *
         * @param progress
         */
        void uploadProgress(double progress);
    }

    /**
     * 媒体中心数据检查
     */
    public interface OnMediaCheckSumListener extends NetWorkFailListener {
        /**
         * 检查媒体中心数据成功
         */
        void onCheckSumSuccess();

    }

    public enum UploadType {
        COMMON_FILE,

        EMAIL_ATTACH,

        CHAT_IMAGE,

        CHAT_FILE,

        STATUS_VIEW_FILE,

        MICRO_VIDEO,

        VOICE,

        MULTIPART
    }

}
