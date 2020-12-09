package com.foreveross.atwork.infrastructure.model.app;

import android.os.Parcel;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * 原生应用，改造by reyzhang22 on 16/4/11
 * Created by lingen on 15/4/3.
 * Description:
 */
public class NativeApp extends App {

    /**
     * SCHEME 形式打开忽略包名配置
     * */
    public final static String SCHEME_JUMP_IGNORE_PACKAGE_NAME = "scheme_jump_ignore_package_name";

    @SerializedName("pkg_id")
    public String mPackageId;

    @SerializedName("pkg_no")
    public String mPackageNo;

    @SerializedName("pkg_name")
    public String mPackageName;

    @SerializedName("pkg_signature")
    public String mPackageSignature;

    public int mDownloadStatus;

    public SessionType mType = SessionType.NativeApp;

    public String getApkName() {
        if(StringUtils.isEmpty(mVersion)) {
            return getTitleI18n(BaseApplicationLike.baseContext) + ".apk";
        }

        return getTitleI18n(BaseApplicationLike.baseContext) + "_v" + mVersion + ".apk";
    }

    public class DownLoadStatus {
        public static final int UNINSTALL = 0;
        public static final int DOWNLOADING = 1;
        public static final int INSTALLED = 2;
    }

    @Override
    public boolean supportLightNotice() {
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mPackageId);
        dest.writeString(this.mPackageNo);
        dest.writeString(this.mPackageName);
        dest.writeString(this.mPackageSignature);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
    }

    public NativeApp() {
    }

    protected NativeApp(Parcel in) {
        super(in);
        this.mPackageId = in.readString();
        this.mPackageNo = in.readString();
        this.mPackageName = in.readString();
        this.mPackageSignature = in.readString();
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : SessionType.values()[tmpMType];
    }

    public static final Creator<NativeApp> CREATOR = new Creator<NativeApp>() {
        @Override
        public NativeApp createFromParcel(Parcel source) {
            return new NativeApp(source);
        }

        @Override
        public NativeApp[] newArray(int size) {
            return new NativeApp[size];
        }
    };
}
