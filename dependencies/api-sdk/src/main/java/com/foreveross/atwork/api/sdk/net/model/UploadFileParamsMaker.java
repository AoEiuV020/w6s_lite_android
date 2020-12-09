package com.foreveross.atwork.api.sdk.net.model;

import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;

/**
 * Created by dasunsy on 2017/6/14.
 */

public class UploadFileParamsMaker {


    public String mUrl;
    public String mFileName;
    public String mFileDigest;
    public boolean mIsReconnected;
    public boolean mIsImg;
    public boolean mIsOriginalImg;

    //basic info
    public String mFilePath;
    public String mMsgId;
    public String mType;
    public long mExpireLimit;
    public boolean mNeedCheckSum = true;


    public MediaCenterHttpURLConnectionUtil.MediaProgressListener mMediaProgressListener;

    private UploadFileParamsMaker() {
        mExpireLimit = DomainSettingsManager.getInstance().getChatFileExpiredTime();
    }

    public static UploadFileParamsMaker newRequest() {
        return new UploadFileParamsMaker();
    }

    public UploadFileParamsMaker setMsgId(String msgId) {
        this.mMsgId = msgId;
        return this;
    }

    public UploadFileParamsMaker setUrl(String url) {
        this.mUrl = url;
        return this;
    }

    public UploadFileParamsMaker setFilePath(String filePath) {
        this.mFilePath = filePath;
        return this;
    }

    public UploadFileParamsMaker setFileName(String fileName) {
        this.mFileName = fileName;
        return this;
    }

    public UploadFileParamsMaker setReconnected(boolean reconnected) {
        this.mIsReconnected = reconnected;
        return this;
    }

    public UploadFileParamsMaker setMediaProgressListener(MediaCenterHttpURLConnectionUtil.MediaProgressListener mediaProgressListener) {
        this.mMediaProgressListener = mediaProgressListener;
        return this;
    }

    public UploadFileParamsMaker setImg(boolean img) {
        mIsImg = img;
        return this;
    }

    public UploadFileParamsMaker setOriginalImg(boolean isOriginalImg) {
        this.mIsOriginalImg = isOriginalImg;
        return this;
    }

    public UploadFileParamsMaker setFileDigest(String fileDigest) {
        mFileDigest = fileDigest;
        return this;
    }

    public UploadFileParamsMaker setType(String type) {
        mType = type;
        return this;

    }

    public UploadFileParamsMaker setExpireLimit(long expireLimit) {
        mExpireLimit = expireLimit;
        return this;

    }

    public UploadFileParamsMaker setNeedCheckSum(boolean needCheckSum) {
        mNeedCheckSum = needCheckSum;
        return this;

    }
}
