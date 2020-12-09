package com.foreveross.atwork.api.sdk.setting.model;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.google.gson.annotations.SerializedName;


public class FeedbackJson {

    @SerializedName("name")
    public String mName;

    @SerializedName("feedback")
    public String mFeedBack;

    @SerializedName("device")
    public String mDevice = android.os.Build.MODEL;

    @SerializedName("system_version")
    public String mSystemVersion = android.os.Build.VERSION.RELEASE;

    @SerializedName("product_version")
    public String mProductVersion;

    @SerializedName("type")
    public String mType;

    @SerializedName("attachment")
    public String mAttachment;

    @SerializedName("start_interval")
    public Long mStartInterval;

    @SerializedName("end_interval")
    public Long mEndInterval;

    @SerializedName("system_model")
    public String mSystemModel = "android";


    public FeedbackJson() {
        mName = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext).mUsername;
        mProductVersion = AppUtil.getVersionName(BaseApplicationLike.baseContext);
    }


    public static FeedbackJson getFeedbackJson(String feedback) {
        FeedbackJson json = new FeedbackJson();
        json.mFeedBack = feedback.trim();
        return json;
    }

}
