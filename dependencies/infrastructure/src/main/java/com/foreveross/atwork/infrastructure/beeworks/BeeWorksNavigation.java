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
 * Created by reyzhang22 on 16/5/4.
 */
public class BeeWorksNavigation implements Parcelable {

    private static final String TAG = BeeWorksNavigation.class.getSimpleName();

    @SerializedName("backgroundColor")
    public String mBackgroundColor;

    @SerializedName("fontColor")
    public String mFontColor;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("layout")
    public String mLayout;

    @SerializedName("leftAction")
    public BeeworksNaviBaseAction mLeftAction;

    @SerializedName("rightActions")
    public List<BeeworksNaviBaseAction> mRightActions;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBackgroundColor);
        dest.writeString(this.mFontColor);
        dest.writeString(this.mTitle);
        dest.writeString(this.mLayout);
        dest.writeParcelable(this.mLeftAction, flags);
        dest.writeList(this.mRightActions);
    }

    public BeeWorksNavigation() {
    }

    protected BeeWorksNavigation(Parcel in) {
        this.mBackgroundColor = in.readString();
        this.mFontColor = in.readString();
        this.mTitle = in.readString();
        this.mLayout = in.readString();
        this.mLeftAction = in.readParcelable(BeeworksNaviBaseAction.class.getClassLoader());
        this.mRightActions = new ArrayList<BeeworksNaviBaseAction>();
        in.readList(this.mRightActions, BeeworksNaviBaseAction.class.getClassLoader());
    }

    public static final Creator<BeeWorksNavigation> CREATOR = new Creator<BeeWorksNavigation>() {
        @Override
        public BeeWorksNavigation createFromParcel(Parcel source) {
            return new BeeWorksNavigation(source);
        }

        @Override
        public BeeWorksNavigation[] newArray(int size) {
            return new BeeWorksNavigation[size];
        }
    };
}
