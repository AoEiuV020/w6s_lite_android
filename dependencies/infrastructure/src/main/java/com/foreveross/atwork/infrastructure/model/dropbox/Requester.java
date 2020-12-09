package com.foreveross.atwork.infrastructure.model.dropbox;

import com.foreveross.atwork.infrastructure.support.DropboxConfig;

public class Requester {
    public String mSourceId;
    public String mDomainId;
    public String mSourceType;
    public long mRefreshTime;
    public int mLimit;
    public int mSkip;
    public String mParent;

    private Requester(Builder builder) {
        this.mSourceId = builder.mSourceId;
        this.mDomainId = builder.mDomainId;
        this.mSourceType = builder.mSourceType;
        this.mRefreshTime = builder.mRefreshTime;
        this.mLimit = builder.mLimit;
        this.mSkip = builder.mSkip;
        this.mParent = builder.mParent;
    }

    public static Builder newRequester() {
        return new Builder();
    }

    public static final class Builder {
        private String mSourceId;
        private String mDomainId;
        private String mSourceType;
        private long mRefreshTime;
        private int mLimit;
        private int mSkip;
        private String mParent;

        private Builder() {
        }

        public Requester build() {
            return new Requester(this);
        }

        public Builder mSourceId(String mSourceId) {
            this.mSourceId = mSourceId;
            return this;
        }

        public Builder mDomainId(String mDomainId) {
            this.mDomainId = mDomainId;
            return this;
        }

        public Builder mSourceType(String mSourceType) {
            this.mSourceType = mSourceType;
            return this;
        }

        public Builder mRefreshTime(long mRefreshTime) {
            this.mRefreshTime = mRefreshTime;
            return this;
        }

        public Builder mLimit(int mLimit) {
            this.mLimit = mLimit;
            return this;
        }

        public Builder mSkip(int mSkip) {
            this.mSkip = mSkip;
            return this;
        }

        public Builder mParent(String mParent) {
            this.mParent = mParent;
            return this;
        }
    }
}
