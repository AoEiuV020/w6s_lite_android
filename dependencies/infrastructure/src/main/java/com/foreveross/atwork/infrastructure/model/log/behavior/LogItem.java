package com.foreveross.atwork.infrastructure.model.log.behavior;

import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 2018/3/12.
 */

public class LogItem {

    @SerializedName("id")
    public String mId;

    @SerializedName("keyTag")
    public String mKeyTag;

    @SerializedName("upload")
    public boolean mUpload;

    @SerializedName("day")
    public String mDay;

    @Expose
    @SerializedName("type")
    public Type mType;

    @Expose
    @SerializedName("status")
    public Status mStatus;

    @Expose
    @SerializedName("platform")
    public Platform mPlatform;

    @Expose
    @SerializedName("client_name")
    public String mClientName;

    @Expose
    @SerializedName("client_id")
    public String mClientId;

    @Expose
    @SerializedName("client_domain_id")
    public String mClientDomainId;

    @Expose
    @SerializedName("client_org_code")
    public String mClientOrgCode;

    @Expose
    @SerializedName("product_version")
    public String mProductVersion;

    @Expose
    @SerializedName("system_version")
    public String mSystemVersion;

    @Expose
    @SerializedName("system_model")
    public String mSystemModel;

    @Expose
    @SerializedName("bundle")
    public String mBundle;

    @Expose
    @SerializedName("release")
    public String mRelease;

    @Expose
    @SerializedName("build_no")
    public String mBuildNo;

    @Expose
    @SerializedName("ip")
    public String mIp;


    @Expose
    @SerializedName("begin")
    public long mBegin = -1;


    @Expose
    @SerializedName("end")
    public long mEnd = -1;

    @Expose
    @SerializedName("intro")
    public String mIntro;

    @Expose
    @SerializedName("positions")
    public List<Position> mPositions;

    @Expose
    @SerializedName("country")
    private String mCountry;

    @Expose
    @SerializedName("city")
    private String mCity;

    @Expose
    @SerializedName("province")
    private String mProvince;

    private LogItem(Builder builder) {
        mId = builder.mId;
        mKeyTag = builder.mKeyTag;
        mUpload = builder.mUpload;
        mDay = builder.mDay;
        mType = builder.mType;
        mStatus = builder.mStatus;
        mPlatform = builder.mPlatform;
        mClientName = builder.mClientName;
        mClientId = builder.mClientId;
        mClientDomainId = builder.mClientDomainId;
        mClientOrgCode = builder.mClientOrgCode;
        mProductVersion = builder.mProductVersion;
        mSystemVersion = builder.mSystemVersion;
        mSystemModel = builder.mSystemModel;
        mBundle = builder.mBundle;
        mRelease = builder.mRelease;
        mBuildNo = builder.mBuildNo;
        mIp = builder.mIp;
        mBegin = builder.mBegin;
        mEnd = builder.mEnd;
        mIntro = builder.mIntro;
        mCountry = builder.mCountry;
        mCity = builder.mCity;
        mProvince = builder.mProvince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogItem logItem = (LogItem) o;

        return mId.equals(logItem.mId);
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String mId;
        private String mKeyTag;
        private boolean mUpload;
        private String mDay;
        private Type mType;
        private Status mStatus;
        private Platform mPlatform;
        private String mClientName;
        private String mClientId;
        private String mClientDomainId;
        private String mClientOrgCode;
        private String mProductVersion;
        private String mSystemVersion;
        private String mSystemModel;
        private String mBundle;
        private String mRelease;
        private String mBuildNo;
        private String mIp;
        private long mBegin = -1;
        private long mEnd = -1;
        private String mIntro;
        private String mCountry;
        private String mCity;
        private String mProvince;
        private List<Position> mPositions;

        private Builder() {
        }

        public Builder setDay(String day) {
            this.mDay = day;
            return this;
        }

        public Builder setUpload(boolean upload) {
            this.mUpload = upload;
            return this;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setKeyTag(String keyTag) {
            this.mKeyTag = keyTag;
            return this;
        }

        public Builder setType(Type type) {
            this.mType = type;
            return this;
        }

        public Builder setStatus(Status status) {
            this.mStatus = status;
            return this;
        }

        public Builder setPlatform(Platform platform) {
            this.mPlatform = platform;
            return this;
        }

        public Builder setClientName(String clientName) {
            this.mClientName = clientName;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.mClientId = clientId;
            return this;
        }

        public Builder setClientDomainId(String clientDomainId) {
            this.mClientDomainId = clientDomainId;
            return this;
        }

        public Builder setClientOrgCode(String clientOrgCode) {
            this.mClientOrgCode = clientOrgCode;
            return this;
        }

        public Builder setProductVersion(String productVersion) {
            this.mProductVersion = productVersion;
            return this;
        }

        public Builder setSystemVersion(String systemVersion) {
            this.mSystemVersion = systemVersion;
            return this;
        }

        public Builder setSystemModel(String systemModel) {
            this.mSystemModel = systemModel;
            return this;
        }

        public Builder setBundle(String bundle) {
            this.mBundle = bundle;
            return this;
        }

        public Builder setRelease(String release) {
            this.mRelease = release;
            return this;
        }

        public Builder setBuildNo(String buildNo) {
            this.mBuildNo = buildNo;
            return this;
        }

        public Builder setIp(String ip) {
            this.mIp = ip;
            return this;
        }

        public Builder setBegin(long begin) {
            this.mBegin = begin;
            return this;
        }

        public Builder setEnd(long end) {
            this.mEnd = end;
            return this;
        }

        public Builder setIntro(String intro) {
            mIntro = intro;
            return this;
        }

        public Builder setCountry(String country) {
            this.mCountry = country;
            return this;
        }

        public Builder setCity(String city) {
            this.mCity = city;
            return this;
        }

        public Builder setProvince(String province) {
            this.mProvince = province;
            return this;
        }

        public Builder setPositions(List<Position> positions) {
            mPositions = positions;
            return this;
        }

        public LogItem build() {
            return new LogItem(this);
        }
    }
}
