package com.foreveross.atwork.infrastructure.model.app;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DistributeMode;
import com.foreveross.atwork.infrastructure.model.app.appEnum.RecommendMode;
import com.foreveross.atwork.infrastructure.model.app.componentMode.AppComponentMode;
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo;
import com.foreveross.atwork.infrastructure.utils.AppWrapHelper;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 拆分contact应用数据，不继承contact，独立管理
 * update on 16/4/9 by reyzhang22
 * Created by lingen on 15/4/2.
 * Description: 应用
 */
public class App implements Parcelable , ShowListItem {


    @SerializedName("app_bundle_version")
    public String mVersion;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("app_type")
    public AppType mAppType;

    @SerializedName("avatar")
    public String mAvatar = StringUtils.EMPTY;

    @SerializedName("org_id")
    public String mOrgId;

    @SerializedName("category_id")
    public String mCategoryId;

    @SerializedName("category_name")
    public String mCategoryName;

    @SerializedName("category_en_name")
    public String mCategoryEnName;

    @SerializedName("category_tw_name")
    public String mCategoryTwName;

    @SerializedName("category_sort")
    public int mCategorySort;

    @SerializedName("category_create_time")
    public long mCategoryCreateTime;

    @SerializedName("category_refresh_time")
    public long mCategoryRefreshTime = -1;

    @SerializedName("category_pinyin")
    public String mCategoryPinYin;

    @SerializedName("category_initial")
    public String mCategoryInitial;

    @SerializedName("app_id")
    public String mAppId;

    @SerializedName("app_name")
    public String mAppName;

    @SerializedName("app_en_name")
    public String mAppEnName;

    @SerializedName("app_tw_name")
    public String mAppTwName;

    @SerializedName("app_kind")
    public AppKind mAppKind;

    @SerializedName("app_intro")
    public String mAppIntro;

    @SerializedName("app_initial")
    public String mAppInitial;

    @SerializedName("app_pinyin")
    public String mAppPinYin;

    @SerializedName("app_biological_auth")
    public String mAppBiologicalAuth;

    @SerializedName("app_params")
    public HashMap<String, String> mAppParams;

    @SerializedName("app_sort")
    public int mAppSort = 0;

    @SerializedName("app_recommend_mode")
    public RecommendMode mAppRecommendMode;

    @SerializedName("app_distribute_mode")
    public DistributeMode mAppDistributeMode;

    @SerializedName("app_create_time")
    public long mAppCreateTime;

    @SerializedName("app_refresh_time")
    public long mAppRefreshTime = -1;

    @SerializedName("bundles")
    public List<AppBundles> mBundles = new ArrayList<>();

    @SerializedName("app_hide_mode")
    public int mHideMode = 0;

    @SerializedName("app_stick_mode")
    public int mTop = 0;

    @SerializedName("shot_cut")
    public Shortcut mShortcut;

    @SerializedName("new_version_notice")
    public boolean mNewVersionNotice = false;

    @SerializedName("app_component_mode")
    public int mComponentMode = -1;

    @SerializedName("is_domain_app")
    public boolean mIsDomainApp = false;

    @SerializedName("searchable")
    public String mSearchable = "";

    @SerializedName("android_bundle_id")
    public String mRealMainBundleId;

    public App() {
    }


    @androidx.annotation.Nullable
    public AppBundles getMainBundle() {
        if(ListUtil.isEmpty(mBundles)) {
            return null;
        }


        for(AppBundles appBundle: mBundles) {
            if(appBundle.isMainBundle()) {
                return appBundle;
            }

            if(getMainBundleId().equals(appBundle.mBundleId)) {
                return appBundle;
            }
        }

        return null;
    }

    public String getMainBundleId() {
        if(!StringUtils.isEmpty(mRealMainBundleId)) {
            return mRealMainBundleId;
        }

        if(!ListUtil.isEmpty(mBundles)) {
            return mBundles.get(0).mBundleId;
        }
        return StringUtils.EMPTY;
    }

    public AppComponentMode getAppComponentMode() {
        switch (mComponentMode) {
            case 1:
                return AppComponentMode.ANNOUNCE;
        }

        return AppComponentMode.DEFAULT;
    }


    public boolean isShowInMarket() {
        if(null == mShortcut) {
            return isShowInMarketBasic();
        }

        return isShowInMarketBasic() && mShortcut.mShowInMarket;
    }

    public boolean isShowInMarketBasic() {
        return 0 == mHideMode;
    }

    public boolean isNaiveAppSchemaUrl() {
        if (this instanceof NativeApp && !ListUtil.isEmpty(mBundles)) {
            AppBundles appBundles = mBundles.get(0);
            return appBundles.isSchemaUri();

        }

        return false;

    }

    public boolean supportLightNotice() {
        return false;
    }




    public static int makeCompareWith(Context context, Object thisObj, Object anotherObj) {

        boolean thisObjIllegal = thisObj == null || !(thisObj instanceof App);
        boolean anotherObjIllegal = anotherObj == null || !(anotherObj instanceof App);

        if (thisObjIllegal && anotherObjIllegal) {
            return 0;
        }

        if(thisObjIllegal) {
            return 1;
        }

        if(anotherObjIllegal) {
            return -1;
        }


        App thisApp = (App) thisObj;
        App anotherApp = (App) anotherObj;

        int sort = thisApp.mAppSort - anotherApp.mAppSort;

        if(0 == sort) {
            String lhsPinyin = FirstLetterUtil.getFullLetter(thisApp.getTitleI18n(context));
            String rhsPinyin = FirstLetterUtil.getFullLetter(anotherApp.getTitleI18n(context));
            sort = lhsPinyin.compareTo(rhsPinyin);
        }

        return sort;
    }

    @Override
    public String getTitle() {
        return mAppName;
    }

    @Override
    public String getTitleI18n(Context context) {


        return I18nInfo.getNameI18n(context, new I18nInfo() {
            @Nullable
            @Override
            public String getStringName() {
                return mAppName;
            }

            @Nullable
            @Override
            public String getStringTwName() {
                return mAppTwName;
            }

            @Nullable
            @Override
            public String getStringEnName() {
                return mAppEnName;
            }
        });

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


    public AppI18nInfo getI18nInfo() {
        AppI18nInfo appI18nInfo = new AppI18nInfo();
        appI18nInfo.setName(mAppName);
        appI18nInfo.setEnName(mAppEnName);
        appI18nInfo.setTwName(mAppTwName);
        appI18nInfo.setCategoryName(mCategoryName);
        appI18nInfo.setCategoryEnName(mCategoryEnName);
        appI18nInfo.setCategoryTwName(mCategoryTwName);

        return appI18nInfo;

    }
    @Override
    public String getTitlePinyin() {
        return mAppPinYin;
    }

    @Override
    public String getParticipantTitle() {
        return mAppName;
    }



    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getAvatar() {
        if(StringUtils.isEmpty(mAvatar)) {
            if(!ListUtil.isEmpty(mBundles)) {
                mAvatar = mBundles.get(0).mIcon;
            }
        }

        return mAvatar;
    }

    @Override
    public String getId() {
        return mAppId;
    }

    @Override
    public String getDomainId() {
        return mDomainId;
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


    public boolean isStickTop() {
        return 1 == mTop;
    }

    public boolean isNeedBioAuthProtect() {
        return "enabled".equalsIgnoreCase(mAppBiologicalAuth);
    }

    /**
     * 批量转换 assessType 类型的 app
     * @see #transferKind()
     * */
    public static List<App> batchTransferKindAccessType(List<App> appList) {
        List<App> appWrappedList = new ArrayList<>();

        for(App app : appList) {
            app.transferKind();
            app.transferAccessType();
            AppBundles.wrapAppBundleData(app);
            appWrappedList.add(AppWrapHelper.transferApp(app));
        }

        return appWrappedList;
    }


    /**
     * 批量转换 adminType 类型的 app
     * @see #transferKind()
     * */
    public static List<App> batchTransferKindAdminType(List<App> appList) {
        List<App> appWrappedList = new ArrayList<>();

        for(App app : appList) {
            app.transferKind();
            app.transferAdminType();

            appWrappedList.add(AppWrapHelper.transferApp(app));
        }

        return appWrappedList;

    }

    public static List<String> toAppIdList(List<App> appList) {
        List<String> appIdList = new ArrayList<>();
        for(App app : appList) {
            appIdList.add(app.mAppId);
        }

        return appIdList;
    }

    /**
     *  重网络拿到的都需要转换 kind 类型
     * */
    public void transferKind() {
        if(AppKind.NativeApp.equals(mAppKind) && !ListUtil.isEmpty(mBundles)) {
            if(BundleType.System.equals(mBundles.get(0).mBundleType) && LocalApp.EMAIL_PREFIX.equalsIgnoreCase(mBundles.get(0).mTargetUrl)) {
                mAppKind = AppKind.NativeEmail;
            }
        }
    }

    public void transferAccessType() {
        mAppType = AppType.Access;
    }

    public void transferAdminType() {
        mAppType = AppType.Admin;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        App app = (App) o;

        return mAppId.equals(app.mAppId);

    }

    @Override
    public int hashCode() {
        return mAppId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mVersion);
        dest.writeString(this.mDomainId);
        dest.writeInt(this.mAppType == null ? -1 : this.mAppType.ordinal());
        dest.writeString(this.mAvatar);
        dest.writeString(this.mOrgId);
        dest.writeString(this.mCategoryId);
        dest.writeString(this.mCategoryName);
        dest.writeString(this.mCategoryEnName);
        dest.writeString(this.mCategoryTwName);
        dest.writeInt(this.mCategorySort);
        dest.writeLong(this.mCategoryCreateTime);
        dest.writeLong(this.mCategoryRefreshTime);
        dest.writeString(this.mCategoryPinYin);
        dest.writeString(this.mCategoryInitial);
        dest.writeString(this.mAppId);
        dest.writeString(this.mAppName);
        dest.writeString(this.mAppEnName);
        dest.writeString(this.mAppTwName);
        dest.writeInt(this.mAppKind == null ? -1 : this.mAppKind.ordinal());
        dest.writeString(this.mAppIntro);
        dest.writeString(this.mAppInitial);
        dest.writeString(this.mAppPinYin);
        dest.writeString(this.mAppBiologicalAuth);
        dest.writeSerializable(this.mAppParams);
        dest.writeInt(this.mAppSort);
        dest.writeInt(this.mAppRecommendMode == null ? -1 : this.mAppRecommendMode.ordinal());
        dest.writeInt(this.mAppDistributeMode == null ? -1 : this.mAppDistributeMode.ordinal());
        dest.writeLong(this.mAppCreateTime);
        dest.writeLong(this.mAppRefreshTime);
        dest.writeTypedList(this.mBundles);
        dest.writeInt(this.mHideMode);
        dest.writeInt(this.mTop);
        dest.writeParcelable(this.mShortcut, flags);
        dest.writeByte(this.mNewVersionNotice ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mComponentMode);
        dest.writeByte(this.mIsDomainApp ? (byte) 1 : (byte) 0);
        dest.writeString(this.mSearchable);
        dest.writeString(this.mRealMainBundleId);
    }

    protected App(Parcel in) {
        this.mVersion = in.readString();
        this.mDomainId = in.readString();
        int tmpMAppType = in.readInt();
        this.mAppType = tmpMAppType == -1 ? null : AppType.values()[tmpMAppType];
        this.mAvatar = in.readString();
        this.mOrgId = in.readString();
        this.mCategoryId = in.readString();
        this.mCategoryName = in.readString();
        this.mCategoryEnName = in.readString();
        this.mCategoryTwName = in.readString();
        this.mCategorySort = in.readInt();
        this.mCategoryCreateTime = in.readLong();
        this.mCategoryRefreshTime = in.readLong();
        this.mCategoryPinYin = in.readString();
        this.mCategoryInitial = in.readString();
        this.mAppId = in.readString();
        this.mAppName = in.readString();
        this.mAppEnName = in.readString();
        this.mAppTwName = in.readString();
        int tmpMAppKind = in.readInt();
        this.mAppKind = tmpMAppKind == -1 ? null : AppKind.values()[tmpMAppKind];
        this.mAppIntro = in.readString();
        this.mAppInitial = in.readString();
        this.mAppPinYin = in.readString();
        this.mAppBiologicalAuth = in.readString();
        this.mAppParams = (HashMap<String, String>) in.readSerializable();
        this.mAppSort = in.readInt();
        int tmpMAppRecommendMode = in.readInt();
        this.mAppRecommendMode = tmpMAppRecommendMode == -1 ? null : RecommendMode.values()[tmpMAppRecommendMode];
        int tmpMAppDistributeMode = in.readInt();
        this.mAppDistributeMode = tmpMAppDistributeMode == -1 ? null : DistributeMode.values()[tmpMAppDistributeMode];
        this.mAppCreateTime = in.readLong();
        this.mAppRefreshTime = in.readLong();
        this.mBundles = in.createTypedArrayList(AppBundles.CREATOR);
        this.mHideMode = in.readInt();
        this.mTop = in.readInt();
        this.mShortcut = in.readParcelable(Shortcut.class.getClassLoader());
        this.mNewVersionNotice = in.readByte() != 0;
        this.mComponentMode = in.readInt();
        this.mIsDomainApp = in.readByte() != 0;
        this.mSearchable = in.readString();
        this.mRealMainBundleId = in.readString();
    }

    public static final Creator<App> CREATOR = new Creator<App>() {
        @Override
        public App createFromParcel(Parcel source) {
            return new App(source);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };
}
