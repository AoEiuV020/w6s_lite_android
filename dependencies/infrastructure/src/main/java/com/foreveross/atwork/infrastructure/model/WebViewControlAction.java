package com.foreveross.atwork.infrastructure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;

/**
 * Created by dasunsy on 2017/5/17.
 */

public class WebViewControlAction implements Parcelable {

    /**
     * 从摇一摇跳转过来
     * */
    public final static int FROM_SHAKE = 1;

    public final static int FROM_URL_HOOKING_FLOAT = 2;

    public final static int FROM_MOMENTS = 3;

    public final static int FROM_OTHER = 0;

    public final static int FROM_FAVORIT = 4;

    public String mUrl;

    public String mMessageId;

    public String mCoverUrl;

    public String mTitle;

    public boolean mHideTitle = false;

    public Integer mOrientation = null;

    public String mSessionId;

    public boolean mNeedShare = true;

    public boolean mNeedClose = true;

    public boolean mNeedChangeStatusBar = true;

    public AppBundles mAppBundle;

    public ArticleItem mArticleItem = new ArticleItem();

    public boolean mFromNotice = false;

    public boolean mIsPreJumpUrl = false;

    public boolean mNeedCodeLock = true;

    public boolean mNeedAuth = false;

    public boolean mUseSystem = false;

    public int mFrom;

    public boolean mHookingFloatMode = false;

    private Boolean mWatermark = null;

    public Boolean mBackToHome = false;

    public FilePreviewOnlineData filePreviewOnlineData;

    public static WebViewControlAction newAction() {
        return new WebViewControlAction();
    }

    /**
     * 设置 url, 并且根据参数控制全屏, username 替换等
     * ps: 如果后面有调用{@link #setHideTitle(boolean)}, 全屏则以后者为准
     * */
    public WebViewControlAction setUrl(String url) {
        url = UrlHandleHelper.replaceBasicKeyParams(BaseApplicationLike.baseContext, url);
        checkHideTitle(url);

        this.mUrl = url;

        return this;
    }

    private void checkHideTitle(String url) {
        if (!StringUtils.isEmpty(url)) {

            Boolean fullScreenParams = UrlHandleHelper.handleInitUrlFullscreen(url);
            if(null != fullScreenParams) {
                mHideTitle = fullScreenParams;
            }
        }
    }



    public WebViewControlAction setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public WebViewControlAction setNeedAuth(boolean needAuth) {
        this.mNeedAuth = needAuth;
        return this;
    }

    public WebViewControlAction setBackHome(boolean backHome) {
        this.mBackToHome = backHome;
        return this;
    }

    public WebViewControlAction setHideTitle(boolean hideTitle) {
        this.mHideTitle = hideTitle;
        return this;
    }

    public WebViewControlAction setOrientation(Integer orientation) {
        this.mOrientation = orientation;
        return this;
    }

    public WebViewControlAction setSessionId(String sessionId) {
        this.mSessionId = sessionId;
        return this;
    }

    public WebViewControlAction setNeedShare(boolean needShare) {
        this.mNeedShare = needShare;
        return this;
    }

    public WebViewControlAction setNeedChangeStatusBar(boolean needChangeStatusBar) {
        this.mNeedChangeStatusBar = needChangeStatusBar;
        return this;
    }


    public WebViewControlAction setLightApp(AppBundles appBundle) {
        this.mAppBundle = appBundle;
        return this;
    }

    public WebViewControlAction setArticleItem(ArticleItem articleItem) {
        this.mArticleItem = articleItem;
        return this;
    }

    public WebViewControlAction setFromNotice(boolean fromNotice) {
        this.mFromNotice = fromNotice;
        return this;
    }

    public WebViewControlAction setIsPreJumpUrl(boolean isPreJumpUrl) {
        this.mIsPreJumpUrl = isPreJumpUrl;
        return this;
    }

    public WebViewControlAction setNeedCodeLock(boolean needCodeLock) {
        this.mNeedCodeLock = needCodeLock;
        return this;
    }

    public WebViewControlAction setNeedClose(boolean needClose) {
        this.mNeedClose = needClose;
        return this;
    }


    public WebViewControlAction setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
        return this;
    }

    public WebViewControlAction setMessageId(String messageId) {
        mMessageId = messageId;
        return this;
    }

    public WebViewControlAction setPreJumpUrl(boolean preJumpUrl) {
        mIsPreJumpUrl = preJumpUrl;
        return this;
    }

    public WebViewControlAction setUseSystem(boolean useSystem) {
        mUseSystem = useSystem;
        return this;
    }

    public WebViewControlAction setFrom(int from) {
        mFrom = from;
        return this;
    }


    public WebViewControlAction setHookingFloatMode(boolean hookingFloatMode) {
        mHookingFloatMode = hookingFloatMode;
        return this;
    }

    public WebViewControlAction setWatermark(boolean watermark) {
        mWatermark = watermark;
        return this;
    }

    public WebViewControlAction setFilePreviewOnlineData(FilePreviewOnlineData filePreviewOnlineData) {
        this.filePreviewOnlineData = filePreviewOnlineData;
        return this;
    }

    @Nullable
    public Boolean getWatermark() {
        if (null == mWatermark) {
            return UrlHandleHelper.getWatermarkEnable(getInitLoadUrl());
        }
        return mWatermark;
    }

    public boolean isFromShake() {
        return FROM_SHAKE == mFrom;
    }

    public boolean isFromFavorit() {
        return FROM_FAVORIT == mFrom;
    }

    public boolean isFroMoments() {
        return FROM_MOMENTS == mFrom;
    }

    public boolean isFromUrlHookingFloat() {
        return FROM_URL_HOOKING_FLOAT == mFrom;
    }

    public String  getInitLoadUrl() {
        if(!StringUtils.isEmpty(mUrl)) {
            return mUrl;
        }


        if (mAppBundle != null && !StringUtils.isEmpty(mAppBundle.mAccessEndPoints.get(LightApp.MOBILE_ENDPOINT))) {
            return mAppBundle.mAccessEndPoints.get(LightApp.MOBILE_ENDPOINT);
        }

        return StringUtils.EMPTY;
    }

    private WebViewControlAction() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUrl);
        dest.writeString(this.mMessageId);
        dest.writeString(this.mCoverUrl);
        dest.writeString(this.mTitle);
        dest.writeByte(this.mHideTitle ? (byte) 1 : (byte) 0);
        dest.writeValue(this.mOrientation);
        dest.writeString(this.mSessionId);
        dest.writeByte(this.mNeedShare ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mNeedClose ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mNeedChangeStatusBar ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mAppBundle, flags);
        dest.writeSerializable(this.mArticleItem);
        dest.writeByte(this.mFromNotice ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsPreJumpUrl ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mNeedCodeLock ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mNeedAuth ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mUseSystem ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mFrom);
        dest.writeByte(this.mHookingFloatMode ? (byte) 1 : (byte) 0);
        dest.writeValue(this.mWatermark);
        dest.writeValue(this.mBackToHome);
        dest.writeParcelable(this.filePreviewOnlineData, flags);
    }

    protected WebViewControlAction(Parcel in) {
        this.mUrl = in.readString();
        this.mMessageId = in.readString();
        this.mCoverUrl = in.readString();
        this.mTitle = in.readString();
        this.mHideTitle = in.readByte() != 0;
        this.mOrientation = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mSessionId = in.readString();
        this.mNeedShare = in.readByte() != 0;
        this.mNeedClose = in.readByte() != 0;
        this.mNeedChangeStatusBar = in.readByte() != 0;
        this.mAppBundle = in.readParcelable(AppBundles.class.getClassLoader());
        this.mArticleItem = (ArticleItem) in.readSerializable();
        this.mFromNotice = in.readByte() != 0;
        this.mIsPreJumpUrl = in.readByte() != 0;
        this.mNeedCodeLock = in.readByte() != 0;
        this.mNeedAuth = in.readByte() != 0;
        this.mUseSystem = in.readByte() != 0;
        this.mFrom = in.readInt();
        this.mHookingFloatMode = in.readByte() != 0;
        this.mWatermark = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mBackToHome = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.filePreviewOnlineData = in.readParcelable(FilePreviewOnlineData.class.getClassLoader());
    }

    public static final Creator<WebViewControlAction> CREATOR = new Creator<WebViewControlAction>() {
        @Override
        public WebViewControlAction createFromParcel(Parcel source) {
            return new WebViewControlAction(source);
        }

        @Override
        public WebViewControlAction[] newArray(int size) {
            return new WebViewControlAction[size];
        }
    };
}
