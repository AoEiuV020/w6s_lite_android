package com.foreveross.atwork.infrastructure.beeworks;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by reyzhang22 on 17/2/16.
 */

public class BeeWorksMeeting implements Parcelable {

    @SerializedName("sdk")
    public String mSdk;

    @SerializedName("Agora")
    public AgoraEntity mAgora;

    public static BeeWorksMeeting createInstance(JSONObject jsonObject){
        return JsonUtil.fromJson(jsonObject.toString(), BeeWorksMeeting.class);
    }

    public static class AgoraEntity implements Parcelable{
        @SerializedName("appId")
        public String mAppId;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mAppId);
        }

        public AgoraEntity() {
        }

        protected AgoraEntity(Parcel in) {
            this.mAppId = in.readString();
        }

        public static final Creator<AgoraEntity> CREATOR = new Creator<AgoraEntity>() {
            @Override
            public AgoraEntity createFromParcel(Parcel source) {
                return new AgoraEntity(source);
            }

            @Override
            public AgoraEntity[] newArray(int size) {
                return new AgoraEntity[size];
            }
        };
    }

    public BeeWorksMeeting() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSdk);
        dest.writeParcelable(this.mAgora, flags);
    }

    protected BeeWorksMeeting(Parcel in) {
        this.mSdk = in.readString();
        this.mAgora = in.readParcelable(AgoraEntity.class.getClassLoader());
    }

    public static final Creator<BeeWorksMeeting> CREATOR = new Creator<BeeWorksMeeting>() {
        @Override
        public BeeWorksMeeting createFromParcel(Parcel source) {
            return new BeeWorksMeeting(source);
        }

        @Override
        public BeeWorksMeeting[] newArray(int size) {
            return new BeeWorksMeeting[size];
        }
    };
}
