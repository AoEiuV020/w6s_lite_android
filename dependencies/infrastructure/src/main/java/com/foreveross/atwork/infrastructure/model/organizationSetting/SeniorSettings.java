package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class SeniorSettings implements Parcelable {

    @SerializedName("show_senior")
    public boolean mShowSenior;

    @SerializedName("chat_nonsupport_prompt")
    public String mChatNonsupportPrompt;

    @SerializedName("chat_auth_scope")
    public String mChatAuthScope;


    public SeniorSettings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mShowSenior ? (byte) 1 : (byte) 0);
        dest.writeString(this.mChatNonsupportPrompt);
        dest.writeString(this.mChatAuthScope);
    }

    protected SeniorSettings(Parcel in) {
        this.mShowSenior = in.readByte() != 0;
        this.mChatNonsupportPrompt = in.readString();
        this.mChatAuthScope = in.readString();
    }

    public static final Creator<SeniorSettings> CREATOR = new Creator<SeniorSettings>() {
        @Override
        public SeniorSettings createFromParcel(Parcel source) {
            return new SeniorSettings(source);
        }

        @Override
        public SeniorSettings[] newArray(int size) {
            return new SeniorSettings[size];
        }
    };
}

