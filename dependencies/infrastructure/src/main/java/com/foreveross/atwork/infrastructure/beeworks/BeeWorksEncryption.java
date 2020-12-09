package com.foreveross.atwork.infrastructure.beeworks;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by dasunsy on 2017/3/29.
 */

public class BeeWorksEncryption implements Parcelable{

    public static final int TK_TYPE_HEADER = 1;
    public static final int TK_TYPE_DEFAULT = TK_TYPE_HEADER;

    @SerializedName("db")
    public boolean mDb = false;

    @SerializedName("file")
    public boolean mDisk = false;

    @SerializedName("voip")
    public boolean mVoip = false;

    @SerializedName("im")
    public boolean mIm = false;

    @SerializedName("login")
    public boolean mLogin = false;

    @SerializedName("tkType")
    public int mTkType = TK_TYPE_DEFAULT;

    public static BeeWorksEncryption createInstance(JSONObject jsonObject){
        if (jsonObject == null) {
            return new BeeWorksEncryption();
        }

        return JsonUtil.fromJson(jsonObject.toString(), BeeWorksEncryption.class);
    }

    public BeeWorksEncryption() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mDb ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mDisk ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mVoip ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIm ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mLogin ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mTkType);
    }

    protected BeeWorksEncryption(Parcel in) {
        this.mDb = in.readByte() != 0;
        this.mDisk = in.readByte() != 0;
        this.mVoip = in.readByte() != 0;
        this.mIm = in.readByte() != 0;
        this.mLogin = in.readByte() != 0;
        this.mTkType = in.readInt();
    }

    public static final Creator<BeeWorksEncryption> CREATOR = new Creator<BeeWorksEncryption>() {
        @Override
        public BeeWorksEncryption createFromParcel(Parcel source) {
            return new BeeWorksEncryption(source);
        }

        @Override
        public BeeWorksEncryption[] newArray(int size) {
            return new BeeWorksEncryption[size];
        }
    };
}
