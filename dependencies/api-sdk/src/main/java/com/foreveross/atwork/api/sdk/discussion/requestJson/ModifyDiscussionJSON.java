package com.foreveross.atwork.api.sdk.discussion.requestJson;

import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ModifyDiscussionJSON {

    @SerializedName("name")
    public String name;

    @SerializedName("intro")
    public String detailInfo;

    @SerializedName("avatar")
    public String avatar;

    @SerializedName("notice")
    public String notice;

    @SerializedName("more_info")
    public Map<String,String> moreInfo;


    /**
     * Discussion
     * @param discussion
     *
     * @return
     */
    public static ModifyDiscussionJSON createModifyJSON(Discussion discussion) {
        ModifyDiscussionJSON groupModifyJSON = new ModifyDiscussionJSON();
        groupModifyJSON.name = discussion.mName;
        groupModifyJSON.detailInfo = discussion.mIntro;
        groupModifyJSON.notice = discussion.mNotice;
        groupModifyJSON.moreInfo = new HashMap<>();
        groupModifyJSON.avatar = discussion.mAvatar;
        groupModifyJSON.moreInfo.put("time", String.valueOf(TimeUtil.getCurrentTimeInMillis()));
        return groupModifyJSON;
    }

}
