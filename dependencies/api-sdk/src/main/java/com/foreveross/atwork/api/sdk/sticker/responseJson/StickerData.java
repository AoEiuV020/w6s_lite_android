package com.foreveross.atwork.api.sdk.sticker.responseJson;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSetting;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class StickerData implements Parcelable {

    @SerializedName("id")
    public String mId = StringUtils.EMPTY;

    @SerializedName("index")
    public int mIndex = 1;

    @SerializedName("name")
    public String mName = StringUtils.EMPTY;

    @SerializedName("tw_name")
    public String mTwName = StringUtils.EMPTY;

    @SerializedName("en_name")
    public String mEnName = StringUtils.EMPTY;

    @SerializedName("origin_name")
    public String mOriginName = StringUtils.EMPTY;

    @SerializedName("thumb_name")
    public String mThumbName = StringUtils.EMPTY;

    public String getShowName(Context context) {
        if (context == null) {
            context = BaseApplicationLike.baseContext;
        }
        int currentSetting = CommonShareInfo.getLanguageSetting(context);
        if (LanguageSetting.SIMPLIFIED_CHINESE == currentSetting) {
            return mName;

        } else if (LanguageSetting.TRADITIONAL_CHINESE == currentSetting) {
            if (TextUtils.isEmpty(mTwName)) {
                return mName;
            }
            return mTwName;

        } else if (LanguageSetting.ENGLISH == currentSetting) {
            if (TextUtils.isEmpty(mEnName)) {
                return mName;
            }
            return mEnName;
        }
        return mName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeInt(this.mIndex);
        dest.writeString(this.mName);
        dest.writeString(this.mTwName);
        dest.writeString(this.mEnName);
        dest.writeString(this.mOriginName);
        dest.writeString(this.mThumbName);
    }

    public StickerData() {
    }

    protected StickerData(Parcel in) {
        this.mId = in.readString();
        this.mIndex = in.readInt();
        this.mName = in.readString();
        this.mTwName = in.readString();
        this.mEnName = in.readString();
        this.mOriginName = in.readString();
        this.mThumbName = in.readString();
    }

    public static final Parcelable.Creator<StickerData> CREATOR = new Parcelable.Creator<StickerData>() {
        @Override
        public StickerData createFromParcel(Parcel source) {
            return new StickerData(source);
        }

        @Override
        public StickerData[] newArray(int size) {
            return new StickerData[size];
        }
    };
}
