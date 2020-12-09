package com.foreveross.atwork.api.sdk.upload;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.chat.LinkMediaResp;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.model.MediaCompressResponseJson;
import com.foreveross.atwork.api.sdk.upload.model.MediaDigest;
import com.foreveross.atwork.api.sdk.upload.model.MediaInfoResponseJson;
import com.foreveross.atwork.api.sdk.upload.model.MediaKeepAliveRequest;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.aliyun.CdnHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingen on 15/5/11.
 * Description:
 */
public class MediaCenterSyncNetService {
    private static final Object sLock = new Object();
    private UrlConstantManager mUrlConstantManager = UrlConstantManager.getInstance();
    private static MediaCenterSyncNetService sInstance;

    public static MediaCenterSyncNetService getInstance() {
        synchronized (sLock) {
            if(null == sInstance) {
                sInstance = new MediaCenterSyncNetService();
            }
            return sInstance;
        }
    }

    /**
     * 获取压缩接口的媒体信息
     * @param context
     * @param digest
     * */
    public HttpResult getCompressMediaInfo(Context context, String digest) {

        String url = String.format(UrlConstantManager.getInstance().V2_getCompressMediaInfo(), digest, AtworkConfig.DOMAIN_ID, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            MediaCompressResponseJson mediaInfoResponseJson = new Gson().fromJson(httpResult.result, MediaCompressResponseJson.class);
            httpResult.result(mediaInfoResponseJson);
        }

        return httpResult;
    }


    /**
     * 获取媒体信息
     * @param context
     * @param mediaIdOrDigest
     * @param type  id 或者 digest, 分别代表媒体 id 或者 md5的类型
     * */
    public HttpResult getMediaInfo(Context context, String mediaIdOrDigest, String type) {
        String url = String.format(UrlConstantManager.getInstance().V2_checkFileMedia(), mediaIdOrDigest, type, AtworkConfig.DOMAIN_ID, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            MediaInfoResponseJson mediaInfoResponseJson = new Gson().fromJson(httpResult.result, MediaInfoResponseJson.class);
            httpResult.result(mediaInfoResponseJson);
        }

        return httpResult;
    }


    /**
     * 媒体文件续期
     * @param context
     * @param mediaId
     * @param expireTime
     * */
    public HttpResult linkMedia(Context context, String mediaId, long expireTime) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().linkMedia(), mediaId, AtworkConfig.DOMAIN_ID, accessToken);
        MediaKeepAliveRequest mediaKeepAliveRequest = new MediaKeepAliveRequest();
        mediaKeepAliveRequest.expireTime = expireTime;

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(mediaKeepAliveRequest));
        if (httpResult.isNetSuccess()) {
            httpResult.result(JsonUtil.fromJson(httpResult.result, LinkMediaResp.class));
        }

        return httpResult;
    }

    /**
     * 媒体批量续期接口
     * @param context
     * @param params
     * @return
     */
    public HttpResult linkMedias(Context context, String params) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().linkMedias(), accessToken);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if (httpResult.isNetSuccess()) {
            httpResult.result(JsonUtil.fromJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    public HttpResult postMediaDigest(Context context, String mediaIdOrDigest, String fileName, long fileSize, long expireTime) {
        String userAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);

        String url = String.format(UrlConstantManager.getInstance().V2_checkFileDigest(), mediaIdOrDigest, fileSize, expireTime, AtworkConfig.DOMAIN_ID, userAccessToken);
        Map<String, String> formData = new HashMap<>();
        formData.put("file_name", fileName);
        formData.put("file_size", fileSize+"");
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postForMultiFormData(url, formData);
        if (httpResult.isNetSuccess()) {
            MediaDigest mediaDigest = new Gson().fromJson(httpResult.result, MediaDigest.class);
            httpResult.result(mediaDigest);
        }
        return httpResult;
    }
    public HttpResult getMediaContent(Context context, final String mediaId, final String filePath, MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE type) {
        String downloadUrl = String.format(mUrlConstantManager.V2_getDownloadUrl(true), mediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        downloadUrl= CdnHelper.wrapCdnUrl(downloadUrl);

        HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setDownloadId(mediaId).setDownloadUrl(downloadUrl).setDownloadPath(filePath).setDownloadType(type).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION)
        );
        if (-1 == httpResult.status) {
            if ("token not found".equalsIgnoreCase(httpResult.error)) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("TOKEN_EXPIRE"));

            }
        }

        if(404 == httpResult.statusCode) {
            RequestRemoteInterceptor.INSTANCE.addInterceptRequestId(mediaId);
        }

        return httpResult;

    }

    public String getMediaUrl(Context context, String mediaId) {
        String downloadUrl = String.format(mUrlConstantManager.V2_getDownloadUrl(true), mediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        downloadUrl= CdnHelper.wrapCdnUrl(downloadUrl);

        return downloadUrl;
    }



}
