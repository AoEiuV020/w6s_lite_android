package com.foreveross.atwork.infrastructure.model.user;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/7/20.
 */
public class UserHandleInfo extends UserHandleBasic implements Parcelable {

    @Expose
    @SerializedName("nickname")
    public String mShowName;

    @Expose
    @SerializedName("avatar")
    public String mAvatar;

    @Expose
    @SerializedName("status")
    public String mStatus = StringUtils.EMPTY;

    /**
     * 得到 userId list
     */
    public static List<String> toUserIdList(List<? extends UserHandleInfo> userList) {
        List<String> userIdList = new ArrayList<>();
        for (UserHandleInfo handleInfo : userList) {
            userIdList.add(handleInfo.mUserId);
        }

        return userIdList;
    }


    @Nullable
    public static UserHandleInfo getUserHandleInfo(Object bodyMap) {
        if(null == bodyMap) {
            return null;
        }


        LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) bodyMap;

        UserHandleInfo userHandleInfo = new UserHandleInfo();
        //user  赋值
        userHandleInfo.mUserId = (String) linkedTreeMap.get("user_id");
        userHandleInfo.mDomainId = (String) linkedTreeMap.get("domain_id");
        userHandleInfo.mShowName = (String) linkedTreeMap.get("nickname");
        userHandleInfo.mAvatar = (String) linkedTreeMap.get("avatar");

        return userHandleInfo;

    }

    public static void userHandleInfoListRemoveSelf(Context context, List<? extends UserHandleInfo> userHandleInfoList) {
        //排除掉自己
        UserHandleInfo userRemoved = null;
        for (UserHandleInfo userHandleInfo : userHandleInfoList) {
            if (User.isYou(context, userHandleInfo.mUserId)) {
                userRemoved = userHandleInfo;
                break;
            }
        }

        if (null != userRemoved) {
            userHandleInfoList.remove(userRemoved);
        }

    }

    @Nullable
    public static UserHandleInfo findLoginUserHandleInfo(Context context, List<UserHandleInfo> contactList) {
        for(UserHandleInfo contact : contactList) {
            if(User.isYou(context, contact.mUserId)) {
                return contact;
            }
        }

        return null;
    }


    public UserHandleInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mShowName);
        dest.writeString(this.mAvatar);
        dest.writeString(this.mStatus);
        dest.writeString(this.mUserId);
        dest.writeString(this.mDomainId);
    }

    protected UserHandleInfo(Parcel in) {
        this.mShowName = in.readString();
        this.mAvatar = in.readString();
        this.mStatus = in.readString();
        this.mUserId = in.readString();
        this.mDomainId = in.readString();
    }

    public static final Creator<UserHandleInfo> CREATOR = new Creator<UserHandleInfo>() {
        @Override
        public UserHandleInfo createFromParcel(Parcel source) {
            return new UserHandleInfo(source);
        }

        @Override
        public UserHandleInfo[] newArray(int size) {
            return new UserHandleInfo[size];
        }
    };
}
