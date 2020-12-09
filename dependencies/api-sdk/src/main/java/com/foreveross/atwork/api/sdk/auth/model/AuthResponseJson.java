package com.foreveross.atwork.api.sdk.auth.model;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/6/7.
 */
public class AuthResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public boolean result;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.result ? (byte) 1 : (byte) 0);
    }

    public AuthResponseJson() {
    }

    protected AuthResponseJson(Parcel in) {
        super(in);
        this.result = in.readByte() != 0;
    }

    public static final Creator<AuthResponseJson> CREATOR = new Creator<AuthResponseJson>() {
        @Override
        public AuthResponseJson createFromParcel(Parcel source) {
            return new AuthResponseJson(source);
        }

        @Override
        public AuthResponseJson[] newArray(int size) {
            return new AuthResponseJson[size];
        }
    };
}
