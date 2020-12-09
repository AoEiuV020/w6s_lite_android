package com.foreveross.atwork.infrastructure.model.orgization;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
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
 *
 * 组织申请模型
 * Created by reyzhang22 on 16/8/16.
 */
public class OrgApply implements Parcelable {

    @SerializedName("org_code")
    public String mOrgCode;

    @SerializedName("msg_id")
    public String mMsgId;

    public String mLastMsgText;

    public long mLastMsgTime;

    public String mExtendsion1;

    public String mExtendsion2;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOrgCode);
        dest.writeString(this.mMsgId);
        dest.writeString(this.mLastMsgText);
        dest.writeLong(this.mLastMsgTime);
        dest.writeString(this.mExtendsion1);
        dest.writeString(this.mExtendsion2);
    }

    public OrgApply() {
    }

    protected OrgApply(Parcel in) {
        this.mOrgCode = in.readString();
        this.mMsgId = in.readString();
        this.mLastMsgText = in.readString();
        this.mLastMsgTime = in.readLong();
        this.mExtendsion1 = in.readString();
        this.mExtendsion2 = in.readString();
    }

    public static final Parcelable.Creator<OrgApply> CREATOR = new Parcelable.Creator<OrgApply>() {
        @Override
        public OrgApply createFromParcel(Parcel source) {
            return new OrgApply(source);
        }

        @Override
        public OrgApply[] newArray(int size) {
            return new OrgApply[size];
        }
    };
}
