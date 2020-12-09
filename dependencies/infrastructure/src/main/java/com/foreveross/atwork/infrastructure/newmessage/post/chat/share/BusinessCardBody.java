package com.foreveross.atwork.infrastructure.newmessage.post.chat.share;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessCardBody implements Serializable {
    @SerializedName("name")
    @Expose
    public String mName;

    @SerializedName("avatar")
    @Expose
    public String mAvatar;

    @SerializedName("user_id")
    @Expose
    public String mUserId;

    @SerializedName("domain_id")
    @Expose
    public String mDomainId;


    @SerializedName("gender")
    @Expose
    public String mGender;


    @SerializedName("status")
    @Expose
    public String mSignature;

    @SerializedName("job_title")
    @Expose
    public String mJobTitle;

    @SerializedName("job_org")
    @Expose
    public String mJobOrgCode;
}
