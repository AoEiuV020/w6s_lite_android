package com.foreveross.atwork.manager.model;

import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by dasunsy on 2017/9/13.
 */

public class SetReadableNameParams {

    public TextView mTextView;

    public String mUserId;

    public String mDomainId;

    @Nullable
    public String mDiscussionId;

    @Nullable
    public String mOrgCode;

    @Nullable
    public String mTitleHolder;

    public int mHighlightColor = -1;

    public String mFailName;


    /**
     * 是否需要读取中的 textView "占位", 使用 空字符 作为 "占位"
     * */
    public boolean mLoadingHolder = false;

    public static SetReadableNameParams newSetReadableNameParams() {
        return new SetReadableNameParams();
    }


    public SetReadableNameParams setTextView(TextView textView) {
        this.mTextView = textView;
        return this;
    }

    public SetReadableNameParams setUserId(String userId) {
        this.mUserId = userId;
        return this;
    }

    public SetReadableNameParams setDomainId(String domainId) {
        this.mDomainId = domainId;
        return this;
    }

    public SetReadableNameParams setOrgCode(@Nullable String orgCode) {
        this.mOrgCode = orgCode;
        return this;
    }

    public SetReadableNameParams setTitleHolder(@Nullable String titleHolder) {
        this.mTitleHolder = titleHolder;
        return this;
    }


    public SetReadableNameParams setDiscussionId(@Nullable String discussionId) {
        mDiscussionId = discussionId;
        return this;
    }

    public SetReadableNameParams setHighlightColor(int highlightColor) {
        mHighlightColor = highlightColor;
        return this;
    }

    public boolean neeHighlight() {
        return -1 != mHighlightColor;
    }


    public SetReadableNameParams setFailName(String failName) {
        mFailName = failName;
        return this;
    }

    public SetReadableNameParams setLoadingHolder(boolean loadingHolder) {
        mLoadingHolder = loadingHolder;
        return this;
    }
}
