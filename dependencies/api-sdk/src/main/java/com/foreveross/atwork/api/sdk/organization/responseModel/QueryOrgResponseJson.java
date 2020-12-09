package com.foreveross.atwork.api.sdk.organization.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/6/8.
 */
public class QueryOrgResponseJson extends BasicResponseJSON {
    @SerializedName("result")
    public Organization organization;
}
