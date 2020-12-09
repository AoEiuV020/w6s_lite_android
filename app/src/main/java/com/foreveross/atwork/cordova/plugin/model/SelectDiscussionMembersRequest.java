package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.model.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/11/29.
 */

public class SelectDiscussionMembersRequest {

    @SerializedName("discussion_id")
    public String mDiscussionId;

    @SerializedName("members_selected")
    public List<User> mSelectedList = new ArrayList<>();
}
