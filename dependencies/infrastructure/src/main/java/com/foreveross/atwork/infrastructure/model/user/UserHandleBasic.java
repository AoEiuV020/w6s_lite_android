package com.foreveross.atwork.infrastructure.model.user;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/5/6.
 */
public class UserHandleBasic {

    @Expose
    @SerializedName("user_id")
    public String mUserId = StringUtils.EMPTY;


    @Expose
    @SerializedName("domain_id")
    public String mDomainId = StringUtils.EMPTY;


    public String getPrimaryKey() {
        return mUserId + "@" + mDomainId;
    }

}
