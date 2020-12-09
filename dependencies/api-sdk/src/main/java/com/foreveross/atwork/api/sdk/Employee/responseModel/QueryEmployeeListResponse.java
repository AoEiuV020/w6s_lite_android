package com.foreveross.atwork.api.sdk.Employee.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 16/5/11.
 */
public class QueryEmployeeListResponse extends BasicResponseJSON{
    @SerializedName("result")
    public List<Employee> employeeList;
}
