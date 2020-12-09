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

import java.util.ArrayList;
import java.util.List;

/**
 * beeworks顶部栏基础动作
 * Created by reyzhang22 on 16/5/4.
 */
public class BeeworksNaviBaseAction implements Parcelable {

    @SerializedName("type")
    public String mType;

    @SerializedName("contents")
    public List<BeeworksNaviActionContent> mContents;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mType);
        dest.writeList(this.mContents);
    }

    public BeeworksNaviBaseAction() {
    }

    protected BeeworksNaviBaseAction(Parcel in) {
        this.mType = in.readString();
        this.mContents = new ArrayList<BeeworksNaviActionContent>();
        in.readList(this.mContents, BeeworksNaviActionContent.class.getClassLoader());
    }

    public static final Creator<BeeworksNaviBaseAction> CREATOR = new Creator<BeeworksNaviBaseAction>() {
        @Override
        public BeeworksNaviBaseAction createFromParcel(Parcel source) {
            return new BeeworksNaviBaseAction(source);
        }

        @Override
        public BeeworksNaviBaseAction[] newArray(int size) {
            return new BeeworksNaviBaseAction[size];
        }
    };
}
