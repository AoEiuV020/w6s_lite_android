package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/10/23.
 */

public class SelectContactsResponse {

    @SerializedName("user")
    public List<User> mUsers = new ArrayList<>();

    @SerializedName("employee")
    public List<Employee> mEmployees = new ArrayList<>();
}
