package com.foreveross.atwork.infrastructure.beeworks;/**
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
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * beeworks导航动作细节
 * Created by reyzhang22 on 16/5/4.
 */
public class BeeworksNaviActionContent implements Parcelable{

    @SerializedName("icon")
    public String mIcon;

    @SerializedName("fontColor")
    public String mFontColor;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("action")
    public String mAction;

    @SerializedName("value")
    public String mValue;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mIcon);
        dest.writeString(this.mFontColor);
        dest.writeString(this.mTitle);
        dest.writeString(this.mAction);
        dest.writeString(this.mValue);
    }

    public BeeworksNaviActionContent() {
    }

    protected BeeworksNaviActionContent(Parcel in) {
        this.mIcon = in.readString();
        this.mFontColor = in.readString();
        this.mTitle = in.readString();
        this.mAction = in.readString();
        this.mValue = in.readString();
    }

    public static final Creator<BeeworksNaviActionContent> CREATOR = new Creator<BeeworksNaviActionContent>() {
        @Override
        public BeeworksNaviActionContent createFromParcel(Parcel source) {
            return new BeeworksNaviActionContent(source);
        }

        @Override
        public BeeworksNaviActionContent[] newArray(int size) {
            return new BeeworksNaviActionContent[size];
        }
    };
}
