package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class EmailSettings implements Parcelable, Comparable<EmailSettings> {

    @SerializedName("suffix")
    public String mSuffix;

    @SerializedName("suffix_enabled")
    public boolean mSuffixEnable = true;

    @SerializedName("imap")
    public EmailImap mImap;

    @SerializedName("pop3")
    public EmailPop3 mPop3;

    @SerializedName("smtp")
    public EmailSmtp mSmpt;

    @SerializedName("exchange")
    public String mExchange;

    @SerializedName("priority")
    public int mPriority;

    @SerializedName("scopes")
    public List<String> mScopes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSuffix);
        dest.writeByte(this.mSuffixEnable ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mImap, flags);
        dest.writeParcelable(this.mPop3, flags);
        dest.writeParcelable(this.mSmpt, flags);
        dest.writeString(this.mExchange);
        dest.writeInt(this.mPriority);
    }

    public EmailSettings() {
    }

    protected EmailSettings(Parcel in) {
        this.mSuffix = in.readString();
        this.mSuffixEnable = in.readByte() != 0;
        this.mImap = in.readParcelable(EmailImap.class.getClassLoader());
        this.mPop3 = in.readParcelable(EmailPop3.class.getClassLoader());
        this.mSmpt = in.readParcelable(EmailSmtp.class.getClassLoader());
        this.mExchange = in.readString();
        this.mPriority = in.readInt();
    }

    public static final Creator<EmailSettings> CREATOR = new Creator<EmailSettings>() {
        @Override
        public EmailSettings createFromParcel(Parcel source) {
            return new EmailSettings(source);
        }

        @Override
        public EmailSettings[] newArray(int size) {
            return new EmailSettings[size];
        }
    };

    @Override
    public int compareTo(@NonNull EmailSettings o) {
        return compare(this.mPriority, o.mPriority);
    }

    public static int compare(int priority1, long priority2) {
        return (priority1 > priority2 ? 1 :
                (priority1 == priority2 ? 0 : -1));
    }
}
