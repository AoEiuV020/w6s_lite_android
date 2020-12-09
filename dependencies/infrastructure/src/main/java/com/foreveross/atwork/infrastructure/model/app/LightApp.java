package com.foreveross.atwork.infrastructure.model.app;

import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.app.appEnum.BannerType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.foreveross.atwork.infrastructure.model.app.appEnum.ProgressBarType;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * 轻应用改造 by reyzhang22 16/4/11
 * Created by lingen on 15/4/3.
 * Description:
 */
public class LightApp extends App {

    public static final String HORIZONTAL = "HORIZONTAL";

    public static final String MOBILE_ENDPOINT = "MOBILE";


    //横屏与竖屏
    @SerializedName("show_mode")
    public String mShowMode;

    @SerializedName("notice_url")
    public String mNoticeUrl;

    @SerializedName("admin_endpoints")
    public HashMap<String, String> mAdminEndPoints;

    @SerializedName("access_endpoints")
    public HashMap<String, String> mAccessEndPoints;

    //全屏与非全屏
    @SerializedName("screen_mode")
    public DisplayMode mScreenMode;

    @SerializedName("banner_type")
    public BannerType mBannerType;

    @SerializedName("banner_prop")
    public String mBannerProp;

    @SerializedName("progress_bar_type")
    public ProgressBarType mProgressBarType;

    @SerializedName("progress_bar_color")
    public String mProgressBarColor;

    @SerializedName("release")
    public String mRelease;

    public String mRouteUrl;

    public LightApp() {
    }

    public boolean needCasLogin() {
        if (null == mBundles) {
            return false;
        }

        if (null == mBundles.get(0)) {
            return false;
        }

        HashMap<String, String> bundleParams = mBundles.get(0).mBundleParams;

        if (null == bundleParams) {
            return false;
        }

        for (Map.Entry<String, String> entry : bundleParams.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("cas")) {
                try {
                    int value = Integer.parseInt(entry.getValue());
                    if (1 == value) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        }

        return false;
    }


    @Override
    public boolean supportLightNotice() {
        return true;
    }


    public boolean useOfflinePackage() {
        return !StringUtils.isEmpty(mRelease);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mShowMode);
        dest.writeString(this.mNoticeUrl);
        dest.writeSerializable(this.mAdminEndPoints);
        dest.writeSerializable(this.mAccessEndPoints);
        dest.writeInt(this.mScreenMode == null ? -1 : this.mScreenMode.ordinal());
        dest.writeInt(this.mBannerType == null ? -1 : this.mBannerType.ordinal());
        dest.writeString(this.mBannerProp);
        dest.writeInt(this.mProgressBarType == null ? -1 : this.mProgressBarType.ordinal());
        dest.writeString(this.mProgressBarColor);
        dest.writeString(this.mRelease);
        dest.writeString(this.mRouteUrl);
    }

    protected LightApp(Parcel in) {
        super(in);
        this.mShowMode = in.readString();
        this.mNoticeUrl = in.readString();
        this.mAdminEndPoints = (HashMap<String, String>) in.readSerializable();
        this.mAccessEndPoints = (HashMap<String, String>) in.readSerializable();
        int tmpMScreenMode = in.readInt();
        this.mScreenMode = tmpMScreenMode == -1 ? null : DisplayMode.values()[tmpMScreenMode];
        int tmpMBannerType = in.readInt();
        this.mBannerType = tmpMBannerType == -1 ? null : BannerType.values()[tmpMBannerType];
        this.mBannerProp = in.readString();
        int tmpMProgressBarType = in.readInt();
        this.mProgressBarType = tmpMProgressBarType == -1 ? null : ProgressBarType.values()[tmpMProgressBarType];
        this.mProgressBarColor = in.readString();
        this.mRelease = in.readString();
        this.mRouteUrl = in.readString();
    }

    public static final Creator<LightApp> CREATOR = new Creator<LightApp>() {
        @Override
        public LightApp createFromParcel(Parcel source) {
            return new LightApp(source);
        }

        @Override
        public LightApp[] newArray(int size) {
            return new LightApp[size];
        }
    };
}
