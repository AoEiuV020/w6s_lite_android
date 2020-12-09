package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.domain.EnvSettings;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/7/13.
 */
public class OrganizationSettings implements Parcelable {

    @SerializedName("org_code")
    public String mOrgCode;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("view_type")
    public String mViewType;

    @SerializedName("counting")
    public boolean mCounting = true;

    @SerializedName("level")
    public int mLevel;

    @SerializedName("corp_alias")
    public String mCorpAlias;

    @SerializedName("dept_alias")
    public String mDeptAlias;

    @SerializedName("node_select")
    public String mNodeSelect;

    @SerializedName("create_time")
    public long mCreateTime;

    @SerializedName("modify_time")
    public long mModifyTime = -1;

    @SerializedName("shake_url")
    public String mShakeUrl;

    @SerializedName("theme_settings")
    public ThemeSettings mThemeSettings;

    @SerializedName("moments_settings")
    private MomentsSettings mMomentsSettings;

    @SerializedName("cycle_settings")
    private MomentsSettings mCycleSettings;


    //    @SerializedName("white_lists")
    public String mWhiteLists;

    @SerializedName("email_settings")
    public List<EmailSettings> mEmailSettings;

    @SerializedName("vpn_enabled")
    public boolean mVpnEnable = false;

    @SerializedName("view_enabled")
    public boolean mViewEnable = true;

    @SerializedName("app_customization_enabled")
    public boolean mAppCustomizationEnabled = false;

    @SerializedName("vpn_settings")
    public List<VpnSettings> mVpnSettings;

    @SerializedName("voip_enabled")
    public boolean mVoipEnable = false;

    @SerializedName("watermark_settings")
    public WatermarkSettings mWatermarkSettings;

    @SerializedName("discussion_settings")
    public DiscussionSettings mDiscussionSettings;

    @SerializedName("senior_settings")
    public SeniorSettings mSeniorSettings;

    @SerializedName("ad_settings")
    public AdvertisementSettings mAdvertisementSettings;

    @SerializedName("environment_variables")
    public Map<String, String> mEnvironmentVariables;

    @SerializedName("customization_scopes")
    public List<CustomizationScope>mCustomizationScopes;

    @SerializedName("envs")
    public  List<EnvSettings> mEnvsSettings;

    @SerializedName("vas_enabled")
    public  boolean vasEnabled;

    @SerializedName("vas_settings")
    public List<VasSettings> vasSettings;


    /**
     * {@link #mMomentsSettings} 为旧数据, {@link #mCycleSettings} 为新数据, 此处为兼容处理, 在获取不了
     * {@link #mCycleSettings}时, 使用旧数据处理
     * */
    public MomentsSettings getMomentsSettings() {
        if(null != mCycleSettings) {
            return mCycleSettings;
        }

        return mMomentsSettings;
    }

    public OrganizationSettings() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOrgCode);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mViewType);
        dest.writeByte(this.mCounting ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mLevel);
        dest.writeString(this.mCorpAlias);
        dest.writeString(this.mDeptAlias);
        dest.writeString(this.mNodeSelect);
        dest.writeLong(this.mCreateTime);
        dest.writeLong(this.mModifyTime);
        dest.writeString(this.mShakeUrl);
        dest.writeParcelable(this.mThemeSettings, flags);
        dest.writeParcelable(this.mMomentsSettings, flags);
        dest.writeParcelable(this.mCycleSettings, flags);
        dest.writeString(this.mWhiteLists);
        dest.writeTypedList(this.mEmailSettings);
        dest.writeByte(this.mVpnEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mViewEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mAppCustomizationEnabled ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.mVpnSettings);
        dest.writeByte(this.mVoipEnable ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mWatermarkSettings, flags);
        dest.writeParcelable(this.mDiscussionSettings, flags);
        dest.writeParcelable(this.mSeniorSettings, flags);
        dest.writeParcelable(this.mAdvertisementSettings, flags);
        dest.writeInt(this.mEnvironmentVariables.size());
        for (Map.Entry<String, String> entry : this.mEnvironmentVariables.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeTypedList(this.mCustomizationScopes);
        dest.writeTypedList(this.mEnvsSettings);
        dest.writeByte(this.vasEnabled ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.vasSettings);

    }

    protected OrganizationSettings(Parcel in) {
        this.mOrgCode = in.readString();
        this.mDomainId = in.readString();
        this.mViewType = in.readString();
        this.mCounting = in.readByte() != 0;
        this.mLevel = in.readInt();
        this.mCorpAlias = in.readString();
        this.mDeptAlias = in.readString();
        this.mNodeSelect = in.readString();
        this.mCreateTime = in.readLong();
        this.mModifyTime = in.readLong();
        this.mShakeUrl = in.readString();
        this.mThemeSettings = in.readParcelable(ThemeSettings.class.getClassLoader());
        this.mMomentsSettings = in.readParcelable(MomentsSettings.class.getClassLoader());
        this.mCycleSettings = in.readParcelable(MomentsSettings.class.getClassLoader());
        this.mWhiteLists = in.readString();
        this.mEmailSettings = in.createTypedArrayList(EmailSettings.CREATOR);
        this.mVpnEnable = in.readByte() != 0;
        this.mViewEnable = in.readByte() != 0;
        this.mAppCustomizationEnabled = in.readByte() != 0;
        this.mVpnSettings = in.createTypedArrayList(VpnSettings.CREATOR);
        this.mVoipEnable = in.readByte() != 0;
        this.mWatermarkSettings = in.readParcelable(WatermarkSettings.class.getClassLoader());
        this.mDiscussionSettings = in.readParcelable(DiscussionSettings.class.getClassLoader());
        this.mSeniorSettings = in.readParcelable(SeniorSettings.class.getClassLoader());
        this.mAdvertisementSettings = in.readParcelable(AdvertisementSettings.class.getClassLoader());
        int mEnvironmentVariablesSize = in.readInt();
        this.mEnvironmentVariables = new HashMap<String, String>(mEnvironmentVariablesSize);
        for (int i = 0; i < mEnvironmentVariablesSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.mEnvironmentVariables.put(key, value);
        }
        this.mCustomizationScopes = in.createTypedArrayList(CustomizationScope.CREATOR);
        this.mEnvsSettings = in.readParcelable(EnvSettings.class.getClassLoader());
        this.vasEnabled = in.readByte() !=0;
        this.vasSettings = in.readParcelable(VasSettings.class.getClassLoader());
    }

    public static final Creator<OrganizationSettings> CREATOR = new Creator<OrganizationSettings>() {
        @Override
        public OrganizationSettings createFromParcel(Parcel source) {
            return new OrganizationSettings(source);
        }

        @Override
        public OrganizationSettings[] newArray(int size) {
            return new OrganizationSettings[size];
        }
    };

    public boolean isAgoraEnabled(){
        if(this.vasEnabled){
            for (VasSettings vasSettings : this.vasSettings){
                if(vasSettings.isAgoraEnabled()){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isZoomEnabled(){
        if(this.vasEnabled){
            for (VasSettings vasSettings : this.vasSettings){
                if(vasSettings.isZoomEnabled()){
                    return true;
                }
            }
        }
        return false;
    }

    public VasSettings getAgoraSetting(){
        if(this.vasEnabled){
            for (VasSettings vasSettings : this.vasSettings){
                if(vasSettings.isAgoraEnabled()){
                    return vasSettings;
                }
            }
        }
        return null;
    }

    public VasSettings getZoomSetting(){
        if(this.vasEnabled){
            for (VasSettings vasSettings : this.vasSettings){
                if(vasSettings.isZoomEnabled()){
                    return vasSettings;
                }
            }
        }
        return null;
    }
}