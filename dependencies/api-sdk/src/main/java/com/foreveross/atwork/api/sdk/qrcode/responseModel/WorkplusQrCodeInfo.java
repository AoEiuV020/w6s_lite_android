package com.foreveross.atwork.api.sdk.qrcode.responseModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/2/19.
 */
public class WorkplusQrCodeInfo implements Parcelable {
    @SerializedName("pinCode")
    private String pinCode;


    @SerializedName("pin_code")
    private String pin_code;

    @SerializedName("survival_seconds")
    private String survival_seconds;

    @SerializedName("survivalSeconds")
    private String survivalSeconds;

    @SerializedName("discussion_id")
    private String discussion_id;

    @SerializedName("discussionId")
    private String discussionId;



    @SerializedName("intro")
    public String intro;

    @SerializedName("name")
    public String name;

    @SerializedName("from")
    public String from;

    @SerializedName("inviter")
    public String inviter;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("domain_id")
    private String domain_id;

    @SerializedName("domainId")
    private String domainId;

    @SerializedName("avatar")
    public String avatar;

    public WorkplusQrCodeInfo() {
    }

    public void setPinCode(String pinCode) {
        this.pin_code = pinCode;
        this.pinCode = pinCode;
    }

    public String getDomainId() {
        if(!StringUtils.isEmpty(domain_id)) {
            return domain_id;
        }

        return domainId;
    }


    public String getUserId() {
        if(!StringUtils.isEmpty(user_id)) {
            return user_id;
        }

        return userId;
    }

    public String getDiscussionId() {
        if(!StringUtils.isEmpty(discussion_id)) {
            return discussion_id;
        }

        return discussionId;
    }

    public String getPinCode() {
        if(!StringUtils.isEmpty(pin_code)) {
            return pin_code;
        }

        return pinCode;
    }

    public String getSurvivalSeconds() {
        if(!StringUtils.isEmpty(survival_seconds)) {
            return survival_seconds;
        }

        return survivalSeconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pinCode);
        dest.writeString(this.pin_code);
        dest.writeString(this.survival_seconds);
        dest.writeString(this.survivalSeconds);
        dest.writeString(this.discussion_id);
        dest.writeString(this.discussionId);
        dest.writeString(this.intro);
        dest.writeString(this.name);
        dest.writeString(this.from);
        dest.writeString(this.inviter);
        dest.writeString(this.user_id);
        dest.writeString(this.userId);
        dest.writeString(this.domain_id);
        dest.writeString(this.domainId);
        dest.writeString(this.avatar);
    }

    protected WorkplusQrCodeInfo(Parcel in) {
        this.pinCode = in.readString();
        this.pin_code = in.readString();
        this.survival_seconds = in.readString();
        this.survivalSeconds = in.readString();
        this.discussion_id = in.readString();
        this.discussionId = in.readString();
        this.intro = in.readString();
        this.name = in.readString();
        this.from = in.readString();
        this.inviter = in.readString();
        this.user_id = in.readString();
        this.userId = in.readString();
        this.domain_id = in.readString();
        this.domainId = in.readString();
        this.avatar = in.readString();
    }

    public static final Creator<WorkplusQrCodeInfo> CREATOR = new Creator<WorkplusQrCodeInfo>() {
        @Override
        public WorkplusQrCodeInfo createFromParcel(Parcel source) {
            return new WorkplusQrCodeInfo(source);
        }

        @Override
        public WorkplusQrCodeInfo[] newArray(int size) {
            return new WorkplusQrCodeInfo[size];
        }
    };
}
