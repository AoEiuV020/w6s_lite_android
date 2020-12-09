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

/**
 * 用户设置模型
 * Created by reyzhang22 on 16/4/7.
 */
public class UserSettings implements Parcelable{


    /**
     * 是否是开发者模式
     */
    public boolean mIsDevMode;

    /**
     * 开发者模式code
     */
    public String mDevCode;

    /**
     * ip域名切换
     */
    public boolean mIpDomainSwitch;


    public UserSettings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mIsDevMode ? (byte) 1 : (byte) 0);
        dest.writeString(this.mDevCode);
        dest.writeByte(this.mIpDomainSwitch ? (byte) 1 : (byte) 0);
    }

    protected UserSettings(Parcel in) {
        this.mIsDevMode = in.readByte() != 0;
        this.mDevCode = in.readString();
        this.mIpDomainSwitch = in.readByte() != 0;
    }

    public static final Creator<UserSettings> CREATOR = new Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel source) {
            return new UserSettings(source);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new UserSettings[size];
        }
    };
}
