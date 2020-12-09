package com.foreveross.atwork.infrastructure.model.advertisement;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;

import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementOpsType;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementType;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementTypeSerializer;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 17/9/15.
 */

public class AdvertisementConfig implements Parcelable {

    @SerializedName("id")
    public String mId;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("org_id")
    public String mOrgId;

    @SerializedName("type")
    public String mType;

    @SerializedName("name")
    public String mName;

    @SerializedName("media_id")
    public String mMediaId;

    @SerializedName("link_url")
    public String mLinkUrl;

    @SerializedName("wifi_loading")
    public boolean mWifiLoading;

    @SerializedName("display_seconds")
    public int mDisplaySeconds;

    @SerializedName("begin_time")
    public long mBeginTime;

    @SerializedName("end_time")
    public long mEndTime;

    @SerializedName("sort")
    public int mSort;

    @SerializedName("kind")
    public String mKind;

    @SerializedName("serial_no")
    public String mSerialNo;

    @DrawableRes
    public int mDefaultHolderImg = -1;


    public AdvertisementEvent toAdvertisementEvent(AdvertisementOpsType advertisementOpsType) {
        AdvertisementEvent advertisementEvent = new AdvertisementEvent();
        advertisementEvent.advertisementId = mId;
        advertisementEvent.orgId = mOrgId;
        advertisementEvent.advertisementName = mName;
        advertisementEvent.type = mType;
        advertisementEvent.opsType = advertisementOpsType.valueOfString();
        advertisementEvent.kind = mKind;
        advertisementEvent.serialNo = mSerialNo;

        return advertisementEvent;
    }


//    public  AdminAdvertisementConfig toAdminAdvertisementConfig()  {
//        AdminAdvertisementConfig adminAdvertisementConfig = new AdminAdvertisementConfig(
//                mId,
//                mDomainId,
//                mName,
//                mMediaId,
//                mType,
//                mLinkUrl,
//                mSort
//        );
//
//        return adminAdvertisementConfig;
//    }


    public String getLocalBannerPath(Context context, String orgCode) {
        return AtWorkDirUtils.getInstance().getUserOrgAdvertisementBannerDir(context, orgCode) + mMediaId;
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AdvertisementType.class, new AdvertisementTypeSerializer());
        return gsonBuilder.create();
    }

    public boolean isValidDuration() {
        if(0 <= mBeginTime && TimeUtil.getCurrentTimeInMillis() < mBeginTime) {
            return false;
        }

        if(0 <= mEndTime && TimeUtil.getCurrentTimeInMillis() > mEndTime) {
            return false;
        }

        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdvertisementConfig that = (AdvertisementConfig) o;

        if (mWifiLoading != that.mWifiLoading) return false;
        if (mDisplaySeconds != that.mDisplaySeconds) return false;
        if (mBeginTime != that.mBeginTime) return false;
        if (mEndTime != that.mEndTime) return false;
        if (mSort != that.mSort) return false;
        if (mId != null ? !mId.equals(that.mId) : that.mId != null) return false;
        if (mDomainId != null ? !mDomainId.equals(that.mDomainId) : that.mDomainId != null)
            return false;
        if (mOrgId != null ? !mOrgId.equals(that.mOrgId) : that.mOrgId != null) return false;
        if (mType != null ? !mType.equals(that.mType) : that.mType != null) return false;
        if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;
        if (mMediaId != null ? !mMediaId.equals(that.mMediaId) : that.mMediaId != null)
            return false;
        if (mLinkUrl != null ? !mLinkUrl.equals(that.mLinkUrl) : that.mLinkUrl != null)
            return false;
        if (mKind != null ? !mKind.equals(that.mKind) : that.mKind != null) return false;
        return mSerialNo != null ? mSerialNo.equals(that.mSerialNo) : that.mSerialNo == null;
    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mDomainId != null ? mDomainId.hashCode() : 0);
        result = 31 * result + (mOrgId != null ? mOrgId.hashCode() : 0);
        result = 31 * result + (mType != null ? mType.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mMediaId != null ? mMediaId.hashCode() : 0);
        result = 31 * result + (mLinkUrl != null ? mLinkUrl.hashCode() : 0);
        result = 31 * result + (mWifiLoading ? 1 : 0);
        result = 31 * result + mDisplaySeconds;
        result = 31 * result + (int) (mBeginTime ^ (mBeginTime >>> 32));
        result = 31 * result + (int) (mEndTime ^ (mEndTime >>> 32));
        result = 31 * result + mSort;
        result = 31 * result + (mKind != null ? mKind.hashCode() : 0);
        result = 31 * result + (mSerialNo != null ? mSerialNo.hashCode() : 0);
        return result;
    }

    public AdvertisementConfig() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mOrgId);
        dest.writeString(this.mType);
        dest.writeString(this.mName);
        dest.writeString(this.mMediaId);
        dest.writeString(this.mLinkUrl);
        dest.writeByte(this.mWifiLoading ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mDisplaySeconds);
        dest.writeLong(this.mBeginTime);
        dest.writeLong(this.mEndTime);
        dest.writeInt(this.mSort);
        dest.writeString(this.mKind);
        dest.writeString(this.mSerialNo);
    }

    protected AdvertisementConfig(Parcel in) {
        this.mId = in.readString();
        this.mDomainId = in.readString();
        this.mOrgId = in.readString();
        this.mType = in.readString();
        this.mName = in.readString();
        this.mMediaId = in.readString();
        this.mLinkUrl = in.readString();
        this.mWifiLoading = in.readByte() != 0;
        this.mDisplaySeconds = in.readInt();
        this.mBeginTime = in.readLong();
        this.mEndTime = in.readLong();
        this.mSort = in.readInt();
        this.mKind = in.readString();
        this.mSerialNo = in.readString();
    }

    public static final Creator<AdvertisementConfig> CREATOR = new Creator<AdvertisementConfig>() {
        @Override
        public AdvertisementConfig createFromParcel(Parcel source) {
            return new AdvertisementConfig(source);
        }

        @Override
        public AdvertisementConfig[] newArray(int size) {
            return new AdvertisementConfig[size];
        }
    };
}
