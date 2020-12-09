package com.foreveross.atwork.api.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lingen on 15/4/30.
 * Description:
 * 响应RSEPONSE
 */
public class BasicResponseJSON implements Parcelable {

    @SerializedName("status")
    public Integer status = -1;

    @SerializedName("message")
    public String message;


    public BasicResponseJSON() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.status);
        dest.writeString(this.message);
    }

    protected BasicResponseJSON(Parcel in) {
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.message = in.readString();
    }

}
