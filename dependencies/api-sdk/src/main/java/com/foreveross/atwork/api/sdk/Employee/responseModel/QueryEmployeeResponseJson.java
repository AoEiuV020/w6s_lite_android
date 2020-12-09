package com.foreveross.atwork.api.sdk.Employee.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/6/15.
 */
public class QueryEmployeeResponseJson extends BasicResponseJSON{
    @SerializedName("result")
    public Employee mEmployee;
}
