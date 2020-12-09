package com.foreveross.atwork.api.sdk.Employee.requestModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 16/5/13.
 */
public class SearchEmployeePost {
    @SerializedName("org_codes")
    public List<String> mOrgCode;

    @SerializedName("query")
    public String query;

    @SerializedName("filter_senior")
    public boolean filterSenior;

    @SerializedName("view_acl")
    public boolean mViewAcl;

    @SerializedName("filter_rank")
    public boolean mFilterRank;

    @SerializedName("rank_view")
    public boolean mRankView;
}
