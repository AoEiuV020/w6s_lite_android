package com.foreveross.atwork.infrastructure.beeworks;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/8/29.
 */
public class BeeWorksLoginPage implements Parcelable {

    @SerializedName("backgroupColor")
    public String mBackgroundColor;

    @SerializedName("backgroupImage")
    public String mBackgroundImage;

    @SerializedName("custom")
    public String mCustomer;

    @SerializedName("defaultRegisterUrl")
    public String mDefaultRegisterUrl;

    @SerializedName("registerUrl")
    public String mRegisterUrl;

    @SerializedName("multi_domain")
    public boolean mMultiDomain;

    public static BeeWorksLoginPage createInstance(JSONObject jsonObject){
        BeeWorksLoginPage beeWorksLoginPage = null;
        if (null != jsonObject) {
            beeWorksLoginPage = JsonUtil.fromJson(jsonObject.toString(), BeeWorksLoginPage.class);
        }
        return beeWorksLoginPage;
    }

    public BeeWorksLoginPage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBackgroundColor);
        dest.writeString(this.mBackgroundImage);
        dest.writeString(this.mCustomer);
        dest.writeString(this.mDefaultRegisterUrl);
        dest.writeString(this.mRegisterUrl);
        dest.writeByte(this.mMultiDomain ? (byte) 1 : (byte) 0);
    }

    protected BeeWorksLoginPage(Parcel in) {
        this.mBackgroundColor = in.readString();
        this.mBackgroundImage = in.readString();
        this.mCustomer = in.readString();
        this.mDefaultRegisterUrl = in.readString();
        this.mRegisterUrl = in.readString();
        this.mMultiDomain = in.readByte() != 0;
    }

    public static final Creator<BeeWorksLoginPage> CREATOR = new Creator<BeeWorksLoginPage>() {
        @Override
        public BeeWorksLoginPage createFromParcel(Parcel source) {
            return new BeeWorksLoginPage(source);
        }

        @Override
        public BeeWorksLoginPage[] newArray(int size) {
            return new BeeWorksLoginPage[size];
        }
    };
}
