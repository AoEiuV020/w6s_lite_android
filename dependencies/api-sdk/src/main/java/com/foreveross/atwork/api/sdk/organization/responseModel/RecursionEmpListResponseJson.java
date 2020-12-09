package com.foreveross.atwork.api.sdk.organization.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/6/12.
 */
public class RecursionEmpListResponseJson extends BasicResponseJSON{
    @SerializedName("result")
    public List<Employee> empList = new ArrayList<>();
}
