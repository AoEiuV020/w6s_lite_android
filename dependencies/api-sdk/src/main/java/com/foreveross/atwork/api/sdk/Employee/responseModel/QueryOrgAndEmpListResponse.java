package com.foreveross.atwork.api.sdk.Employee.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 16/5/25.
 */
public class QueryOrgAndEmpListResponse extends BasicResponseJSON {
    @SerializedName("result")
    public List<Result> mResultList;

    public class Result {
        @SerializedName("org")
        public Organization org;

        @SerializedName("employee")
        public Employee emp;
    }
}
