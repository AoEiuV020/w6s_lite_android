package com.foreveross.atwork.api.sdk.discussion.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/6/12.
 */
public class QueryDiscussionResponseJson extends BasicResponseJSON {
    @SerializedName("result")
    public Discussion discussion;
}
