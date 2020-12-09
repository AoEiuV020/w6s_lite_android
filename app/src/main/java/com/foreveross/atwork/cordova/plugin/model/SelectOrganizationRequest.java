package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wuzejie on 2019/10/11.
 */

public class SelectOrganizationRequest {

    @SerializedName("orgCode")
    public String orgCode = StringUtils.EMPTY;

    @SerializedName("orgId")
    public String orgId = StringUtils.EMPTY;

}