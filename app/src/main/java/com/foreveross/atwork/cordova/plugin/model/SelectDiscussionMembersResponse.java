package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/11/29.
 */

public class SelectDiscussionMembersResponse {

    @SerializedName("members")
    public List<Employee> mSelectedList = new ArrayList<>();
}
