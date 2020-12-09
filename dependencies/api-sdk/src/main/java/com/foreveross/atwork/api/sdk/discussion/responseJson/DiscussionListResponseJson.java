package com.foreveross.atwork.api.sdk.discussion.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/6/12.
 */
public class DiscussionListResponseJson extends BasicResponseJSON {
    @SerializedName("result")
    public List<Discussion> mDiscussionList = new ArrayList<>();
}
