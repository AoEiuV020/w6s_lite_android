package com.foreveross.atwork.infrastructure.model.email;

import android.os.Parcel;
import android.os.Parcelable;

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
 * K9往来邮件联系人
 * Created by reyzhang22 on 16/7/5.
 */
public class K9Contacts implements Parcelable{

    /**
     * 当前登录使用的邮箱地址
     */
    public String mMailBoxId;

    /**
     * 往来联系人地址
     */
    public String mContactAddr;

    /**
     * 往来联系人名称
     */
    public String mContactName;

    public String mId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMailBoxId);
        dest.writeString(this.mContactAddr);
        dest.writeString(this.mContactName);
    }

    public K9Contacts() {
    }

    protected K9Contacts(Parcel in) {
        this.mMailBoxId = in.readString();
        this.mContactAddr = in.readString();
        this.mContactName = in.readString();
    }

    public static final Creator<K9Contacts> CREATOR = new Creator<K9Contacts>() {
        @Override
        public K9Contacts createFromParcel(Parcel source) {
            return new K9Contacts(source);
        }

        @Override
        public K9Contacts[] newArray(int size) {
            return new K9Contacts[size];
        }
    };
}
