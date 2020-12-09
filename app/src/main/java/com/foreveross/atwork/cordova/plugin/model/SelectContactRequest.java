package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.model.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/10/23.
 */

public class SelectContactRequest {

    @SerializedName("filterSenior")
    public int mFilterSenior = 1;

    @SerializedName("maxCount")
    public int mMax = -1;

    @SerializedName("selectedUsers")
    public List<User> mSelectedUserList = new ArrayList<>();


    @SerializedName("selectedEmployees")
    public List<User> mSelectedEmployeeList = new ArrayList<>();

}
