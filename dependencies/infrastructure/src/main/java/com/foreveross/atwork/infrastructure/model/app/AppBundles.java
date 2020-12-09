package com.foreveross.atwork.infrastructure.model.app;/**
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


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.foreveross.atwork.infrastructure.model.app.componentMode.AppComponentMode;
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by reyzhang22 on 16/4/9.
 */
public class AppBundles implements Parcelable, ShowListItem {

    private static final String DEFAULT_CAS_TICKET_URL = "http://cas.kedachina.com.cn/cas/v1/tickets";

    @SerializedName("bundle_id")
    public String mBundleId;

    @SerializedName("app_id")
    public String appId;

    @SerializedName("domain_id")
    public String appDomainId;

    @SerializedName("name")
    public String mBundleName = "";

    @SerializedName("tw_name")
    public String mBundleTwName = "";

    @SerializedName("en_name")
    public String mBundleEnName = "";

    @SerializedName("pinyin")
    public String mBundlePy = "";

    @SerializedName("initial")
    public String mBundleInitial = "";

    @SerializedName("app_type")
    public AppType mAppType;

    @SerializedName("app_kind")
    public AppKind mAppKind;

    @SerializedName("main_bundle_id")
    public String mMainBundleId;

    @SerializedName("bundle_type")
    public BundleType mBundleType;

    @SerializedName("bundle_version")
    public String mBundleVersion;

    @SerializedName("bundle_remark")
    public String mBundleRemark;

    @SerializedName("bundle_platform")
    public String mBundlePlatform;

    @SerializedName("bundle_params")
    public HashMap<String, String> mBundleParams;

    @SerializedName("icon")
    public String mIcon;

    @SerializedName("upload_time")
    public long mUploadTime;

    @SerializedName("hidden")
    public boolean mHidden = false;

    // 以下参数当为原生应用时有效
    @SerializedName("pkg_id")
    public String mPackageId;

    @SerializedName("pkg_no")
    public String mPackageNo;

    @SerializedName("pkg_name")
    public String mPackageName;

    @SerializedName("pkg_signature")
    public String mPackageSignature;

    // 以下参数当为轻应用时有效

    @SerializedName("notify_url")
    public String mNoticeUrl;

    @SerializedName("admin_endpoints")
    public HashMap<String, String> mAdminEndPoints;

    @SerializedName("access_endpoints")
    public HashMap<String, String> mAccessEndPoints;


    @SerializedName("target_url")
    public String mTargetUrl;

    @SerializedName("init_url")
    public String mInitUrl;

    // 以下参数当为轻应用时有效
    /**横屏与竖屏, 旧版本字段,3.0.2 开始 都迁移至 {@link #mSettings}*/
    @Deprecated
    @SerializedName("show_mode")
    public String mShowMode;

    /**全屏与非全屏, 旧版本字段,3.0.2 开始 都迁移至 {@link #mSettings}*/
    @Deprecated
    @SerializedName("screen_mode")
    public DisplayMode mScreenMode;

    @SerializedName("settings")
    public Settings mSettings = new Settings();

    @SerializedName("new_version_notice")
    public boolean mNewVersionNotice = false;

    @SerializedName("bio_auth")
    public String mAppBiologicalAuth = "disable";

    @SerializedName("sort")
    public int mSort;

    @SerializedName("shortcut")
    public boolean mShortcut;

    @SerializedName("linked")
    public boolean mLinked = false;

    @SerializedName("category_name")
    public String mCategoryName = "";

    @SerializedName("category_en_name")
    public String mCategoryEnName = "";

    @SerializedName("category_tw_name")
    public String mCategoryTwName = "";

    @SerializedName("org_id")
    public String mOrgId = "";

    public int mDownloadStatus;

    public String mRouteUrl;

    public int mTop = -1;

    public boolean isNeedBioAuthProtect() {
        return "enabled".equalsIgnoreCase(mAppBiologicalAuth);
    }

    public AppBundles() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppBundles that = (AppBundles) o;
        return Objects.equals(mBundleId, that.mBundleId) &&
                Objects.equals(appId, that.appId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mBundleId, appId);
    }

    public boolean isSchemaUri() {
        if(StringUtils.isEmpty(mInitUrl)) {
            return false;
        }

        try {

            return mInitUrl.contains(":") || mInitUrl.contains("/");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<String> toAppBundleIdList(List<AppBundles> appBundleList) {
        List<String> appIdList = new ArrayList<>();
        for(AppBundles app : appBundleList) {
            appIdList.add(app.mBundleId);
        }

        return appIdList;
    }

    @Override
    public String getTitle() {
        return mBundleName;
    }

    @Override
    public String getTitleI18n(Context context) {
        return I18nInfo.getNameI18n(context, new I18nInfo() {
            @Nullable
            @Override
            public String getStringName() {
                return mBundleName;
            }

            @Nullable
            @Override
            public String getStringTwName() {
                return mBundleTwName;
            }

            @Nullable
            @Override
            public String getStringEnName() {
                return mBundleEnName;
            }
        });
    }

    @Override
    public String getTitlePinyin() {
        return mBundlePy;
    }

    @Override
    public String getParticipantTitle() {
        return mBundleName;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getAvatar() {
        return mIcon;
    }

    @Override
    public String getId() {
        return mBundleId;
    }

    @Override
    public String getDomainId() {
        return appDomainId;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return false;
    }

    @Override
    public void select(boolean isSelect) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    public static int sort(Context context, Object thisObj, Object anotherObj) {

        boolean thisObjIllegal = thisObj == null || !(thisObj instanceof AppBundles);
        boolean anotherObjIllegal = anotherObj == null || !(anotherObj instanceof AppBundles);

        if (thisObjIllegal && anotherObjIllegal) {
            return 0;
        }

        if(thisObjIllegal) {
            return 1;
        }

        if(anotherObjIllegal) {
            return -1;
        }


        AppBundles thisBundle = (AppBundles) thisObj;
        AppBundles anotherBundle = (AppBundles) anotherObj;

        int sort = thisBundle.mSort - anotherBundle.mSort;

        if(0 == sort) {
            String lhsPinyin = FirstLetterUtil.getFullLetter(thisBundle.getTitleI18n(context));
            String rhsPinyin = FirstLetterUtil.getFullLetter(anotherBundle.getTitleI18n(context));
            sort = lhsPinyin.compareTo(rhsPinyin);
        }

        return sort;
    }

    public String getCategoryNameI18n(Context context) {

        return I18nInfo.getNameI18n(context, new I18nInfo() {
            @Nullable
            @Override
            public String getStringName() {
                return mCategoryName;
            }

            @Nullable
            @Override
            public String getStringTwName() {
                return mCategoryTwName;
            }

            @Nullable
            @Override
            public String getStringEnName() {
                return mCategoryEnName;
            }
        });

    }

    public static void wrapAppBundleData(App app) {
        if (!ListUtil.isEmpty(app.mBundles)) {
            for (AppBundles appBundle : app.mBundles) {
                if (TextUtils.isEmpty(appBundle.mBundleName)) {
                    appBundle.mBundleName = app.mAppName;
                }
                if (TextUtils.isEmpty(appBundle.mBundleEnName)) {
                    appBundle.mBundleEnName = app.mAppEnName;
                }
                if (TextUtils.isEmpty(appBundle.mBundleTwName)) {
                    appBundle.mBundleTwName = app.mAppTwName;
                }
                appBundle.appDomainId = app.mDomainId;
                appBundle.appId = app.mAppId;
                appBundle.mOrgId = app.mOrgId;
                appBundle.mAppType = app.mAppType;
                appBundle.mAppKind = app.mAppKind;
                appBundle.mCategoryEnName = app.mCategoryEnName;
                appBundle.mCategoryName = app.mCategoryName;
                appBundle.mCategoryTwName = app.mCategoryTwName;
                appBundle.mMainBundleId = app.getMainBundleId();
                appBundle.mTop = app.mTop;
                if (app.mComponentMode == 1) {
                    appBundle.mHidden = true;
                }
            }
        }
    }

    public boolean isMainBundle() {
        return this.mBundleId.equalsIgnoreCase(this.mMainBundleId);
    }

    public String getNativeAppName() {
        if(StringUtils.isEmpty(mBundleVersion)) {
            return getTitleI18n(BaseApplicationLike.baseContext) + ".apk";
        }

        return getTitleI18n(BaseApplicationLike.baseContext) + "_v" + mBundleVersion + ".apk";
    }

    public boolean isNativeAppBundle() {
        return AppKind.NativeApp.equals(this.mAppKind);
    }


    public boolean isLightAppBundle() {
        return AppKind.LightApp.equals(this.mAppKind);
    }

    public boolean isServiceAppBundle() {
        return AppKind.ServeNo.equals(this.mAppKind);
    }

    public boolean useOfflinePackage() {
        return isLightAppBundle() && !TextUtils.isEmpty(mSettings.mMobileBehaviour.mRelease);
    }

    public String getTicketUrl() {

        if (MapUtil.isEmpty(mBundleParams)) {
            return DEFAULT_CAS_TICKET_URL;
        }

        for (Map.Entry<String, String> entry : mBundleParams.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("ticket_url")) {

                if(!StringUtils.isEmpty(entry.getValue())) {
                    return entry.getValue();
                }

            }
        }

        return DEFAULT_CAS_TICKET_URL;

    }

    public boolean needCasLogin() {

        if (null == mBundleParams) {
            return false;
        }

        for (Map.Entry<String, String> entry : mBundleParams.entrySet()) {
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBundleId);
        dest.writeString(this.appId);
        dest.writeString(this.appDomainId);
        dest.writeString(this.mBundleName);
        dest.writeString(this.mBundleTwName);
        dest.writeString(this.mBundleEnName);
        dest.writeString(this.mBundlePy);
        dest.writeString(this.mBundleInitial);
        dest.writeInt(this.mAppType == null ? -1 : this.mAppType.ordinal());
        dest.writeInt(this.mAppKind == null ? -1 : this.mAppKind.ordinal());
        dest.writeString(this.mMainBundleId);
        dest.writeInt(this.mBundleType == null ? -1 : this.mBundleType.ordinal());
        dest.writeString(this.mBundleVersion);
        dest.writeString(this.mBundleRemark);
        dest.writeString(this.mBundlePlatform);
        dest.writeSerializable(this.mBundleParams);
        dest.writeString(this.mIcon);
        dest.writeLong(this.mUploadTime);
        dest.writeByte(this.mHidden ? (byte) 1 : (byte) 0);
        dest.writeString(this.mPackageId);
        dest.writeString(this.mPackageNo);
        dest.writeString(this.mPackageName);
        dest.writeString(this.mPackageSignature);
        dest.writeString(this.mNoticeUrl);
        dest.writeSerializable(this.mAdminEndPoints);
        dest.writeSerializable(this.mAccessEndPoints);
        dest.writeString(this.mTargetUrl);
        dest.writeString(this.mInitUrl);
        dest.writeString(this.mShowMode);
        dest.writeInt(this.mScreenMode == null ? -1 : this.mScreenMode.ordinal());
        dest.writeParcelable(this.mSettings, flags);
        dest.writeByte(this.mNewVersionNotice ? (byte) 1 : (byte) 0);
        dest.writeString(this.mAppBiologicalAuth);
        dest.writeInt(this.mSort);
        dest.writeByte(this.mShortcut ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mLinked ? (byte) 1 : (byte) 0);
        dest.writeString(this.mCategoryName);
        dest.writeString(this.mCategoryEnName);
        dest.writeString(this.mCategoryTwName);
        dest.writeString(this.mOrgId);
        dest.writeInt(this.mDownloadStatus);
        dest.writeString(this.mRouteUrl);
        dest.writeInt(this.mTop);
    }

    protected AppBundles(Parcel in) {
        this.mBundleId = in.readString();
        this.appId = in.readString();
        this.appDomainId = in.readString();
        this.mBundleName = in.readString();
        this.mBundleTwName = in.readString();
        this.mBundleEnName = in.readString();
        this.mBundlePy = in.readString();
        this.mBundleInitial = in.readString();
        int tmpMAppType = in.readInt();
        this.mAppType = tmpMAppType == -1 ? null : AppType.values()[tmpMAppType];
        int tmpMAppKind = in.readInt();
        this.mAppKind = tmpMAppKind == -1 ? null : AppKind.values()[tmpMAppKind];
        this.mMainBundleId = in.readString();
        int tmpMBundleType = in.readInt();
        this.mBundleType = tmpMBundleType == -1 ? null : BundleType.values()[tmpMBundleType];
        this.mBundleVersion = in.readString();
        this.mBundleRemark = in.readString();
        this.mBundlePlatform = in.readString();
        this.mBundleParams = (HashMap<String, String>) in.readSerializable();
        this.mIcon = in.readString();
        this.mUploadTime = in.readLong();
        this.mHidden = in.readByte() != 0;
        this.mPackageId = in.readString();
        this.mPackageNo = in.readString();
        this.mPackageName = in.readString();
        this.mPackageSignature = in.readString();
        this.mNoticeUrl = in.readString();
        this.mAdminEndPoints = (HashMap<String, String>) in.readSerializable();
        this.mAccessEndPoints = (HashMap<String, String>) in.readSerializable();
        this.mTargetUrl = in.readString();
        this.mInitUrl = in.readString();
        this.mShowMode = in.readString();
        int tmpMScreenMode = in.readInt();
        this.mScreenMode = tmpMScreenMode == -1 ? null : DisplayMode.values()[tmpMScreenMode];
        this.mSettings = in.readParcelable(Settings.class.getClassLoader());
        this.mNewVersionNotice = in.readByte() != 0;
        this.mAppBiologicalAuth = in.readString();
        this.mSort = in.readInt();
        this.mShortcut = in.readByte() != 0;
        this.mLinked = in.readByte() != 0;
        this.mCategoryName = in.readString();
        this.mCategoryEnName = in.readString();
        this.mCategoryTwName = in.readString();
        this.mOrgId = in.readString();
        this.mDownloadStatus = in.readInt();
        this.mRouteUrl = in.readString();
        this.mTop = in.readInt();
    }

    public static final Creator<AppBundles> CREATOR = new Creator<AppBundles>() {
        @Override
        public AppBundles createFromParcel(Parcel source) {
            return new AppBundles(source);
        }

        @Override
        public AppBundles[] newArray(int size) {
            return new AppBundles[size];
        }
    };
}
