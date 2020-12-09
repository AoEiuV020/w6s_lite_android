package com.foreveross.atwork.api.sdk.app.model;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class LightNoticeData extends BasicResponseJSON {

    public static final String DOT = "dot";

    public static final String DIGIT = "digit";

    public static final String ICON = "icon";

    public static final String NOTHING = "nothing";


    @SerializedName("tip")
    public Tip tip;

    public boolean isLegal() {
        return null != tip;
    }

    public boolean isDot(){
        if(null != tip) {
            return LightNoticeData.DOT.equalsIgnoreCase(tip.notifyType);
        }

        return false;
    }

    public boolean isNothing(){
        if(null != tip) {
            return LightNoticeData.NOTHING.equalsIgnoreCase(tip.notifyType);
        }

        return false;
    }
    
    public boolean isDigit(){
        if(null != tip) {
            return LightNoticeData.DIGIT.equalsIgnoreCase(tip.notifyType);
        }
        return false;
    }

    public boolean isIcon(){
        if(null != tip) {
            return LightNoticeData.ICON.equalsIgnoreCase(tip.notifyType);

        }
        return false;
    }

    public static class Tip implements Serializable {

        @SerializedName("notify_type")
        public String notifyType;

        @SerializedName("icon_url")
        public String iconUrl;

        @SerializedName("num")
        public String num;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tip tip = (Tip) o;

            if (notifyType != null ? !notifyType.equals(tip.notifyType) : tip.notifyType != null)
                return false;
            if (iconUrl != null ? !iconUrl.equals(tip.iconUrl) : tip.iconUrl != null) return false;
            return num != null ? num.equals(tip.num) : tip.num == null;

        }

        @Override
        public int hashCode() {
            int result = notifyType != null ? notifyType.hashCode() : 0;
            result = 31 * result + (iconUrl != null ? iconUrl.hashCode() : 0);
            result = 31 * result + (num != null ? num.hashCode() : 0);
            return result;
        }
    }

    public static LightNoticeData createNothing() {
        LightNoticeData lightNoticeJson = new LightNoticeData();
        Tip tip = new Tip();
        tip.notifyType = LightNoticeData.NOTHING;
        lightNoticeJson.tip = tip;
        return lightNoticeJson;
    }

    public static LightNoticeData createDotLightNotice() {
        LightNoticeData lightNoticeJson = new LightNoticeData();
        Tip tip = new Tip();
        tip.notifyType = LightNoticeData.DOT;
        lightNoticeJson.tip = tip;
        return lightNoticeJson;
    }

    public static LightNoticeData createIconLightNotice(String iconUrl) {
        LightNoticeData lightNoticeJson = new LightNoticeData();
        Tip tip = new Tip();
        tip.notifyType = LightNoticeData.ICON;
        tip.iconUrl = iconUrl;
        lightNoticeJson.tip = tip;
        return lightNoticeJson;
    }

    public static LightNoticeData createNumLightNotice(int num) {
        LightNoticeData lightNoticeJson = new LightNoticeData();
        Tip tip = new Tip();
        tip.notifyType = LightNoticeData.DIGIT;
        tip.num = String.valueOf(num);
        lightNoticeJson.tip = tip;
        return lightNoticeJson;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeSerializable(this.tip);
    }

    public LightNoticeData() {
    }

    protected LightNoticeData(Parcel in) {
        super(in);
        this.tip = (Tip) in.readSerializable();
    }

    public static final Creator<LightNoticeData> CREATOR = new Creator<LightNoticeData>() {
        @Override
        public LightNoticeData createFromParcel(Parcel source) {
            return new LightNoticeData(source);
        }

        @Override
        public LightNoticeData[] newArray(int size) {
            return new LightNoticeData[size];
        }
    };
}
