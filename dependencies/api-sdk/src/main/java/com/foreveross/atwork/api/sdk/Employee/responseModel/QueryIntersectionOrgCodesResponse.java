package com.foreveross.atwork.api.sdk.Employee.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QueryIntersectionOrgCodesResponse extends BasicResponseJSON {

    @SerializedName("result")
    public List<String> orgCodes;
}
