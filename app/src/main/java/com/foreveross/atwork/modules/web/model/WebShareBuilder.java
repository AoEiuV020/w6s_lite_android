package com.foreveross.atwork.modules.web.model;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.modules.web.component.WebSharePopupWindow;

import org.apache.cordova.CallbackContext;

import java.util.List;

public class WebShareBuilder {
    private Context mContext;
    private Fragment mFragment;
    private List<Organization> mOrgListHavingCircle;
    private ArticleItem mArticleItem;
    private ShareChatMessage.ShareType mShareType;
    private boolean mNeedFetchInfoFromRemote = true;
    private WebSharePopupWindow mWebSharePopupWindow;
    private String mMessageId;
    private String mSessionId;
    private CallbackContext mCallbackContext;


    public Context getContext() {
        return mContext;
    }

    public String getMessageId() {
        return mMessageId;
    }
    public String getSessionId() {
        return mSessionId;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public List<Organization> getOrgListHavingCircle() {
        return mOrgListHavingCircle;
    }

    public ArticleItem getArticleItem() {
        return mArticleItem;
    }

    public ShareChatMessage.ShareType getShareType() {
        return mShareType;
    }

    public boolean isNeedFetchInfoFromRemote() {
        return mNeedFetchInfoFromRemote;
    }

    public WebSharePopupWindow getWebSharePopupWindow() {
        return mWebSharePopupWindow;
    }

    public CallbackContext getCallbackContext() {
        return mCallbackContext;
    }

    public WebShareBuilder setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public WebShareBuilder setFragment(Fragment fragment) {
        this.mFragment = fragment;
        return this;
    }

    public WebShareBuilder setOrgListHavingCircle(List<Organization> orgListHavingCircle) {
        this.mOrgListHavingCircle = orgListHavingCircle;
        return this;
    }

    public WebShareBuilder setArticleItem(ArticleItem articleItem) {
        this.mArticleItem = articleItem;
        return this;
    }

    public WebShareBuilder setShareType(ShareChatMessage.ShareType shareType) {
        this.mShareType = shareType;
        return this;
    }

    public WebShareBuilder setNeedFetchInfoFromRemote(boolean needFetchInfoFromRemote) {
        this.mNeedFetchInfoFromRemote = needFetchInfoFromRemote;
        return this;
    }

    public WebShareBuilder setWebSharePopupWindow(WebSharePopupWindow webSharePopupWindow) {
        this.mWebSharePopupWindow = webSharePopupWindow;
        return this;
    }

    public WebShareBuilder setMessageId(String messageId) {
        mMessageId = messageId;
        return this;
    }

    public WebShareBuilder setSessionId(String sessionId) {
        mSessionId = sessionId;
        return this;
    }

    public WebSharePopupWindow buildWebSharePopupWindow() {
        WebSharePopupWindow webSharePopupWindow = new WebSharePopupWindow();
        webSharePopupWindow.setBuilder(this);
        return webSharePopupWindow;
    }


    public WebShareBuilder setCallbackContext(CallbackContext callbackContext) {
        this.mCallbackContext = callbackContext;
        return this;
    }
}
