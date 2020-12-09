package com.foreveross.atwork.infrastructure.model.orgization;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.i18n.CommonI18nInfoData;
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.LongUtil;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by reyzhang22 on 16/3/23.
 */
public class Organization extends I18nInfo implements Parcelable, Comparable<Organization>, ShowListItem {

    @SerializedName("id")
    public String mId;

    @SerializedName("org_code")
    public String mOrgCode;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("name")
    public String mName;

    @SerializedName("en_name")
    public String mEnName;

    @SerializedName("tw_name")
    public String mTwName;

    @SerializedName("pinyin")
    public String mPinYin;

    @SerializedName("initial")
    public String mInitial;

    @SerializedName("sn")
    public String mSn;

    @SerializedName("tel")
    public String mTel;

    @SerializedName("type")
    public String mType;

    @SerializedName("sort_order")
    public String mSortOrder;

    @SerializedName("level")
    public String mLevel;

    @SerializedName("expired")
    public long mExpired;

    @SerializedName("created")
    public long mCreated;

    @SerializedName("disabled")
    public String mDisabled;

    @SerializedName("last_modified")
    public long mRefreshTime;

    @SerializedName("logo")
    public String mLogo;

    @SerializedName("operator")
    public String mOperator;

    @SerializedName("path")
    public String mPath;

    @SerializedName("uuid")
    public String mUUID;

    @SerializedName("owner")
    public String mOwner;

    /***
     * 用管理权限的用户 Id
     */
    public String mAdminUserId;


    public boolean mSelect;


    public long getSortOrder() {
        return LongUtil.parseLong(mSortOrder);
    }

    public boolean isMeOwner(Context context) {
        return LoginUserInfo.getInstance().getLoginUserId(context).equalsIgnoreCase(mOwner);
    }

    public boolean isMeAdmin(Context context) {
        return LoginUserInfo.getInstance().getLoginUserId(context).equalsIgnoreCase(mAdminUserId);
    }

    public Organization() {
    }

    public Scope transfer(Context context) {
       return new Scope(mId, mPath, getNameI18n(context), this);
    }

    public static ArrayList<String> getOrgCodeList(List<Organization> organizationList) {
        ArrayList<String> orgCodeList = new ArrayList<>();
        for(Organization organization : organizationList) {
            orgCodeList.add(organization.mOrgCode);
        }

        return orgCodeList;
    }


    @Nullable
    @Override
    public String getStringName() {
        return mName;
    }

    @Nullable
    @Override
    public String getStringTwName() {
        return mTwName;
    }

    @Nullable
    @Override
    public String getStringEnName() {
        return mEnName;
    }

    public CommonI18nInfoData getI18nInfo() {
        CommonI18nInfoData commonI18NInfoData = new CommonI18nInfoData();
        commonI18NInfoData.setName(mName);
        commonI18NInfoData.setEnName(mEnName);
        commonI18NInfoData.setTwName(mTwName);
        return commonI18NInfoData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return mId.equals(that.mId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId);
    }

    @Override
    public int compareTo(Organization another) {

        long resultSortOrderLong = this.getSortOrder() - another.getSortOrder();
        if(0 < resultSortOrderLong) {
            return 1;

        }

        if(0 > resultSortOrderLong) {
            return -1;
        }


        long resultCreatedLong = this.mCreated - another.mCreated;

        int result;
        if(0 < resultCreatedLong) {
            result = 1;
        } else if(0 == resultCreatedLong){
            result = 0;
        } else {
            result = -1;
        }

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mOrgCode);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mName);
        dest.writeString(this.mEnName);
        dest.writeString(this.mTwName);
        dest.writeString(this.mPinYin);
        dest.writeString(this.mInitial);
        dest.writeString(this.mSn);
        dest.writeString(this.mTel);
        dest.writeString(this.mType);
        dest.writeString(this.mSortOrder);
        dest.writeString(this.mLevel);
        dest.writeLong(this.mExpired);
        dest.writeLong(this.mCreated);
        dest.writeString(this.mDisabled);
        dest.writeLong(this.mRefreshTime);
        dest.writeString(this.mLogo);
        dest.writeString(this.mOperator);
        dest.writeString(this.mPath);
        dest.writeString(this.mUUID);
        dest.writeString(this.mOwner);
        dest.writeString(this.mAdminUserId);
    }

    protected Organization(Parcel in) {
        this.mId = in.readString();
        this.mOrgCode = in.readString();
        this.mDomainId = in.readString();
        this.mName = in.readString();
        this.mEnName = in.readString();
        this.mTwName = in.readString();
        this.mPinYin = in.readString();
        this.mInitial = in.readString();
        this.mSn = in.readString();
        this.mTel = in.readString();
        this.mType = in.readString();
        this.mSortOrder = in.readString();
        this.mLevel = in.readString();
        this.mExpired = in.readLong();
        this.mCreated = in.readLong();
        this.mDisabled = in.readString();
        this.mRefreshTime = in.readLong();
        this.mLogo = in.readString();
        this.mOperator = in.readString();
        this.mPath = in.readString();
        this.mUUID = in.readString();
        this.mOwner = in.readString();
        this.mAdminUserId = in.readString();
    }

    public static final Creator<Organization> CREATOR = new Creator<Organization>() {
        @Override
        public Organization createFromParcel(Parcel source) {
            return new Organization(source);
        }

        @Override
        public Organization[] newArray(int size) {
            return new Organization[size];
        }
    };

    @Override
    public String getTitle() {
        return mName;
    }

    @Override
    public String getTitleI18n(Context context) {
        return getNameI18n(context);
    }

    @Override
    public String getTitlePinyin() {
        return mPinYin;
    }

    @Override
    public String getParticipantTitle() {
        return mName;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public String getAvatar() {
        return mLogo;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getDomainId() {
        return mDomainId;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return mSelect;
    }

    @Override
    public void select(boolean isSelect) {
        mSelect = isSelect;
    }

    @Override
    public boolean isOnline() {
        return false;
    }
}
