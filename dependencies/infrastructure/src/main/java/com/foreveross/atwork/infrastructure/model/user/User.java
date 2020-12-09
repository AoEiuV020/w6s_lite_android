package com.foreveross.atwork.infrastructure.model.user;/**
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
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.employee.MoreInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by reyzhang22 on 16/3/25.
 */
public class User implements ShowListItem, Comparable {

    public static final String STATUS_INITIALIZED = "ACTIVATED";

    @SerializedName("user_id")
    public String mUserId = StringUtils.EMPTY;

    @SerializedName("domain_id")
    public String mDomainId = StringUtils.EMPTY;

    @SerializedName("username")
    public String mUsername = StringUtils.EMPTY;

    @SerializedName("name")
    public String mName = StringUtils.EMPTY;

    @SerializedName("nickname")
    public String mNickname = StringUtils.EMPTY;

    @SerializedName("pinyin")
    public String mPinyin = StringUtils.EMPTY;

    @SerializedName("initial")
    public String mInitial = StringUtils.EMPTY;

    @SerializedName("avatar")
    public String mAvatar = StringUtils.EMPTY;

    @SerializedName("phone")
    public String mPhone = StringUtils.EMPTY;

    @SerializedName("email")
    public String mEmail = StringUtils.EMPTY;

    @SerializedName("gender")
    public String mGender = StringUtils.EMPTY;

    @SerializedName("birthday")
    public String mBirthday = StringUtils.EMPTY;

    @SerializedName("status")
    public String mStatus = StringUtils.EMPTY;

    @SerializedName("disabled")
    public String mDisabled = StringUtils.EMPTY;

    @SerializedName("first_letter")
    public String mFirstLetter = StringUtils.EMPTY;

    public boolean mSelect;

    public long readTime;

    @SerializedName("employee")
    public Employee mCurrentEmp;

    @SerializedName("accountName")
    public String mAccountName;

    @SerializedName("online")
    public boolean mOnline = false;

    @SerializedName("platform")
    public String mPlatform;

    @SerializedName("cloud_auth_enabled")
    public boolean mCloudAuthEnabled;

    @SerializedName("cloud_auth_avatar")
    public String mCloudAuthAvatar;

    @SerializedName("biological_auth_enabled")
    public boolean biologicalAuthEnable;

    @SerializedName("properties")
    public MoreInfo mMoreInfo;

    @SerializedName("moments")
    public String mSignature;

    public long mLastUpdateTime;

    public boolean mFileTransfer = false;

    public String getSignature() {
        return mSignature;
    }

    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof User)) {
            return 0;
        }
        User other = (User) another;
        if (StringUtils.isEmpty(mPinyin) && StringUtils.isEmpty(other.mPinyin)) {
            return mName.compareTo(other.mName);
        }

        if (StringUtils.isEmpty(mPinyin) && !StringUtils.isEmpty(other.mPinyin)) {
            return 1;
        }

        if (!StringUtils.isEmpty(mPinyin) && StringUtils.isEmpty(other.mPinyin)) {
            return -1;
        }

        return mPinyin.compareTo(other.mPinyin);
    }

    @Override
    public String getTitle() {
        return getShowName();
    }

    @Override
    public String getTitleI18n(Context context) {
        return getParticipantTitle();
    }

    @Override
    public String getTitlePinyin() {
        return mPinyin;
    }

    @Override
    public String getParticipantTitle() {
        return getShowName();
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getAvatar() {
        return mAvatar;
    }

    @Override
    public String getId() {
        return mUserId;
    }

    @Override
    public String getDomainId() {
        return mDomainId;
    }

    @Override
    public String getStatus() {
        return mStatus;
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
        return mOnline;
    }

    public void select() {
        mSelect = !mSelect;
    }


    public User() {
    }

    public String getPrimaryKey() {
        return mUserId;
    }

    @Override
    public boolean equals(Object o) {
        if(null == o) {
            return false;
        }

        String otherUserId;
        boolean otherFileTransfer = false;

        if (o instanceof ShowListItem) {
            ShowListItem other = (ShowListItem) o;
            otherUserId = other.getId();

            if(o instanceof User) {
                otherFileTransfer = ((User)o).mFileTransfer;
            }
        } else {
            return false;
        }



        if (this.mUserId == null || otherUserId == null) {
            return false;
        }



        return this.mFileTransfer == otherFileTransfer && this.mUserId.equals(otherUserId);
    }

    @Override
    public int hashCode() {
        if(StringUtils.isEmpty(mUserId)) {
            return super.hashCode();

        } else {
            return mUserId.hashCode();
        }
    }

    /**
     * 返回显示的名字
     * */
    public String getShowName() {

        if(!StringUtils.isEmpty(mName)) {
            return mName;
        }

        if(!StringUtils.isEmpty(mNickname)) {
             return mNickname;
        }


        if(!StringUtils.isEmpty(mUsername)) {
            return mUsername;
        }

        return StringUtils.EMPTY;
    }

    public Employee toEmployee() {
        Employee employee = new Employee();
        employee.userId = this.mUserId;
        employee.domainId = this.mDomainId;
        employee.avatar = this.mAvatar;
        employee.mobile = this.mPhone;
        employee.name = this.mName;
        employee.username = this.mUsername;
        employee.birthday = String.valueOf(this.mBirthday);
        employee.email = this.mEmail;
        employee.pinyin = this.mPinyin;
        employee.gender = this.mGender;
        employee.status = this.mStatus;

        return employee;
    }


    public UserHandleInfo toUserHandleInfo() {
        UserHandleInfo handleInfo = new UserHandleInfo();
        handleInfo.mUserId = this.mUserId;
        handleInfo.mDomainId = this.mDomainId;
        handleInfo.mShowName = this.getShowName();
        handleInfo.mAvatar = this.mAvatar;
        handleInfo.mStatus = this.mStatus;

        return handleInfo;
    }


    public static List<UserHandleInfo> toUserHandleInfoList(List<User> userList) {
        List<UserHandleInfo> handleInfoList = new ArrayList<>();

        for(User user : userList) {
            handleInfoList.add(user.toUserHandleInfo());
        }

        return handleInfoList;
    }

    /**
     * 得到 userId list
     * */
    public static List<String> toUserIdList(@Nullable List<User> userList) {
        List<String> userIdList = new ArrayList<>();

        if (!ListUtil.isEmpty(userList)) {
            for(User user : userList) {
                userIdList.add(user.mUserId);
            }
        }

        return userIdList;
    }


    /**
     * @see #isYou(Context)
     * */
    public static boolean isYou(Context context, String userId) {
        return LoginUserInfo.getInstance().getLoginUserId(context).equals(userId);

    }

    /**
     * 当前 user 是否是登录用户
     * */
    public boolean isYou(Context context) {
        return LoginUserInfo.getInstance().getLoginUserId(context).equals(mUserId);
    }

    public boolean isLegal() {
        return !(StringUtils.isEmpty(mUserId)) && !(StringUtils.isEmpty(mDomainId));
    }

    /**
     * 用户是否激活状态
     * */
    public boolean isStatusInitialized() {
        return isInitialized(mStatus);
    }

    public static boolean isInitialized(String status) {
        if(AtworkConfig.USER_INFO_VIEW_CONFIG.getCommandInitialized()) {
            return true;
        }

        return User.STATUS_INITIALIZED.equals(status);
    }

    public void refreshLastUpdateTime() {
        mLastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUserId);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mUsername);
        dest.writeString(this.mName);
        dest.writeString(this.mNickname);
        dest.writeString(this.mPinyin);
        dest.writeString(this.mInitial);
        dest.writeString(this.mAvatar);
        dest.writeString(this.mPhone);
        dest.writeString(this.mEmail);
        dest.writeString(this.mGender);
        dest.writeString(this.mBirthday);
        dest.writeParcelable(this.mMoreInfo, flags);
        dest.writeString(this.mStatus);
        dest.writeString(this.mDisabled);
        dest.writeString(this.mFirstLetter);
        dest.writeByte(this.mSelect ? (byte) 1 : (byte) 0);
        dest.writeLong(this.readTime);
        dest.writeParcelable(this.mCurrentEmp, flags);
        dest.writeString(this.mAccountName);
        dest.writeByte(this.mOnline ? (byte) 1 : (byte) 0);
        dest.writeString(this.mPlatform);
        dest.writeByte(this.mCloudAuthEnabled ? (byte) 1 : (byte) 0);
        dest.writeString(this.mCloudAuthAvatar);
        dest.writeByte(this.mFileTransfer ? (byte) 1 : (byte) 0);
        dest.writeString(this.mSignature);
    }

    protected User(Parcel in) {
        this.mUserId = in.readString();
        this.mDomainId = in.readString();
        this.mUsername = in.readString();
        this.mName = in.readString();
        this.mNickname = in.readString();
        this.mPinyin = in.readString();
        this.mInitial = in.readString();
        this.mAvatar = in.readString();
        this.mPhone = in.readString();
        this.mEmail = in.readString();
        this.mGender = in.readString();
        this.mBirthday = in.readString();
        this.mMoreInfo = in.readParcelable(MoreInfo.class.getClassLoader());
        this.mStatus = in.readString();
        this.mDisabled = in.readString();
        this.mFirstLetter = in.readString();
        this.mSelect = in.readByte() != 0;
        this.readTime = in.readLong();
        this.mCurrentEmp = in.readParcelable(Employee.class.getClassLoader());
        this.mAccountName = in.readString();
        this.mOnline = in.readByte() != 0;
        this.mPlatform = in.readString();
        this.mCloudAuthEnabled = in.readByte() != 0;
        this.mCloudAuthAvatar = in.readString();
        this.mFileTransfer = in.readByte() != 0;
        this.mSignature = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "mUserId='" + mUserId + '\'' +
                ", mDomainId='" + mDomainId + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mName='" + mName + '\'' +
                ", mNickname='" + mNickname + '\'' +
                ", mPinyin='" + mPinyin + '\'' +
                ", mInitial='" + mInitial + '\'' +
                ", mAvatar='" + mAvatar + '\'' +
                ", mPhone='" + mPhone + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mGender='" + mGender + '\'' +
                ", mBirthday='" + mBirthday + '\'' +
                ", mStatus='" + mStatus + '\'' +
                ", mDisabled='" + mDisabled + '\'' +
                ", mFirstLetter='" + mFirstLetter + '\'' +
                ", mSelect=" + mSelect +
                ", readTime=" + readTime +
                ", mCurrentEmp=" + mCurrentEmp +
                ", mAccountName='" + mAccountName + '\'' +
                ", mOnline=" + mOnline +
                ", mPlatform='" + mPlatform + '\'' +
                ", mCloudAuthEnabled=" + mCloudAuthEnabled +
                ", mCloudAuthAvatar='" + mCloudAuthAvatar + '\'' +
                ", biologicalAuthEnable=" + biologicalAuthEnable +
                ", mMoreInfo=" + mMoreInfo +
                ", mSignature='" + mSignature + '\'' +
                ", mFileTransfer=" + mFileTransfer +
                '}';
    }
}
