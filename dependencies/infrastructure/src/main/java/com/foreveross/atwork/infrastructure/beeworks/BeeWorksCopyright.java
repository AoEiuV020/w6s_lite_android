package com.foreveross.atwork.infrastructure.beeworks;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by lingen on 15/12/21.
 */
public class BeeWorksCopyright implements Parcelable {

    @SerializedName("companyName")
    public String companyName = "";

    @SerializedName("companyCopyright")
    public String companyCopyright = "";

    @SerializedName("companyCopyright_en")
    public String companyCopyrightEn = "";

    @SerializedName("companyCopyright_hant")
    public String companyCopyrightHant = "";

    @SerializedName("companyUrl")
    public String companyUrl = "";

    @SerializedName("companyLoginIcon")
    public String companyLoginIcon = "";

    @SerializedName("companyAboutIcon")
    public String companyAboutIcon = "";

    @SerializedName("companyLaunchIcon")
    public CompanyLaunchIcon companyLaunchIcon;

    @SerializedName("contactInfo")
    public String contactInfo;


    public static BeeWorksCopyright createInstance(JSONObject jsonObject){
        BeeWorksCopyright beeWorksCopyright = new Gson().fromJson(jsonObject.toString(), BeeWorksCopyright.class);
        return beeWorksCopyright;
    }

    public String getCompanyCopyright(Context context) {

        String companyCopyright;
        switch (LanguageUtil.getWorkplusLocaleTag(context)) {
            case "zh-cn" :
                companyCopyright = this.companyCopyright;
                break;

            case "zh-rtw":
                companyCopyright = companyCopyrightHant;
                break;

            default:
                companyCopyright = companyCopyrightEn;
        }

        if(StringUtils.isEmpty(companyCopyright)) {
            companyCopyright = this.companyCopyright;
        }

        return companyCopyright;
    }


    public static class CompanyLaunchIcon implements Parcelable {

        @SerializedName("local")
        public String mLocal;

        @SerializedName("mediaId")
        public String mMediaId;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mLocal);
            dest.writeString(this.mMediaId);
        }

        public CompanyLaunchIcon() {
        }

        protected CompanyLaunchIcon(Parcel in) {
            this.mLocal = in.readString();
            this.mMediaId = in.readString();
        }

        public static final Parcelable.Creator<CompanyLaunchIcon> CREATOR = new Parcelable.Creator<CompanyLaunchIcon>() {
            @Override
            public CompanyLaunchIcon createFromParcel(Parcel source) {
                return new CompanyLaunchIcon(source);
            }

            @Override
            public CompanyLaunchIcon[] newArray(int size) {
                return new CompanyLaunchIcon[size];
            }
        };
    }

    public BeeWorksCopyright() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.companyName);
        dest.writeString(this.companyCopyright);
        dest.writeString(this.companyCopyrightEn);
        dest.writeString(this.companyCopyrightHant);
        dest.writeString(this.companyUrl);
        dest.writeString(this.companyLoginIcon);
        dest.writeString(this.companyAboutIcon);
        dest.writeParcelable(this.companyLaunchIcon, flags);
        dest.writeString(this.contactInfo);
    }

    protected BeeWorksCopyright(Parcel in) {
        this.companyName = in.readString();
        this.companyCopyright = in.readString();
        this.companyCopyrightEn = in.readString();
        this.companyCopyrightHant = in.readString();
        this.companyUrl = in.readString();
        this.companyLoginIcon = in.readString();
        this.companyAboutIcon = in.readString();
        this.companyLaunchIcon = in.readParcelable(CompanyLaunchIcon.class.getClassLoader());
        this.contactInfo = in.readString();
    }

    public static final Creator<BeeWorksCopyright> CREATOR = new Creator<BeeWorksCopyright>() {
        @Override
        public BeeWorksCopyright createFromParcel(Parcel source) {
            return new BeeWorksCopyright(source);
        }

        @Override
        public BeeWorksCopyright[] newArray(int size) {
            return new BeeWorksCopyright[size];
        }
    };
}
