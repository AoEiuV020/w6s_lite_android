package com.foreveross.atwork.api.sdk.organization.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 16/4/26.
 */
public class OrganizationListResponse extends BasicResponseJSON{
    @SerializedName("result")
    public List<Organization> organizationList;
}
