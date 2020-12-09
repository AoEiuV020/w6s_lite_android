package com.foreveross.atwork.infrastructure.beeworks.share;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 * Created by reyzhang22 on 16/7/27.
 */
public class BeeWorksShare implements Parcelable {

    @SerializedName("qq")
    public QQShare mShareQQ = new QQShare();

    @SerializedName("weixin")
    public BasicShare mShareWX = new BasicShare();

    @SerializedName("weibo")
    public WBShare mShareWB = new WBShare();


    public static BeeWorksShare createInstance(JSONObject jsonObject){

        return JsonUtil.fromJson(jsonObject.toString(), BeeWorksShare.class);
    }

    public BeeWorksShare() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mShareQQ, flags);
        dest.writeParcelable(this.mShareWX, flags);
        dest.writeParcelable(this.mShareWB, flags);
    }

    protected BeeWorksShare(Parcel in) {
        this.mShareQQ = in.readParcelable(QQShare.class.getClassLoader());
        this.mShareWX = in.readParcelable(BasicShare.class.getClassLoader());
        this.mShareWB = in.readParcelable(WBShare.class.getClassLoader());
    }

    public static final Creator<BeeWorksShare> CREATOR = new Creator<BeeWorksShare>() {
        @Override
        public BeeWorksShare createFromParcel(Parcel source) {
            return new BeeWorksShare(source);
        }

        @Override
        public BeeWorksShare[] newArray(int size) {
            return new BeeWorksShare[size];
        }
    };
}
