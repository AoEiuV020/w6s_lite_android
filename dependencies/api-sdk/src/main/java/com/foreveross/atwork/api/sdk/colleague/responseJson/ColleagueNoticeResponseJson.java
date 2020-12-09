package com.foreveross.atwork.api.sdk.colleague.responseJson;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 15/9/17.
 */
public class ColleagueNoticeResponseJson implements Parcelable {

    public static final String DOT = "dot";

    public static final String DIGIT = "digit";

    public static final String ICON = "icon";

    public static final String NOTHING = "nothing";

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("tip")
    public Tip tip;

    public static class Tip implements Parcelable {

        @SerializedName("notify_type")
        public String notifyType;

        @SerializedName("icon_url")
        public String iconUrl;

        @SerializedName("num")
        public String num;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.notifyType);
            dest.writeString(this.iconUrl);
            dest.writeString(this.num);
        }

        public Tip() {
        }

        protected Tip(Parcel in) {
            this.notifyType = in.readString();
            this.iconUrl = in.readString();
            this.num = in.readString();
        }

        public static final Parcelable.Creator<Tip> CREATOR = new Parcelable.Creator<Tip>() {
            @Override
            public Tip createFromParcel(Parcel source) {
                return new Tip(source);
            }

            @Override
            public Tip[] newArray(int size) {
                return new Tip[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.message);
        dest.writeParcelable(this.tip, flags);
    }

    public ColleagueNoticeResponseJson() {
    }

    protected ColleagueNoticeResponseJson(Parcel in) {
        this.status = in.readInt();
        this.message = in.readString();
        this.tip = in.readParcelable(Tip.class.getClassLoader());
    }

    public static final Parcelable.Creator<ColleagueNoticeResponseJson> CREATOR = new Parcelable.Creator<ColleagueNoticeResponseJson>() {
        @Override
        public ColleagueNoticeResponseJson createFromParcel(Parcel source) {
            return new ColleagueNoticeResponseJson(source);
        }

        @Override
        public ColleagueNoticeResponseJson[] newArray(int size) {
            return new ColleagueNoticeResponseJson[size];
        }
    };
}
