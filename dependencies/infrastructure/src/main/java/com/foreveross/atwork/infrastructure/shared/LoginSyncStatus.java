package com.foreveross.atwork.infrastructure.shared;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

/**
 * Created by dasunsy on 2017/1/16.
 */

public class LoginSyncStatus {

    @SerializedName("discussion_result")
    public boolean mDiscussionResult = false;

    @SerializedName("star_contact_result")
    public boolean mStarContactResult = false;

    @SerializedName("organization_result")
    public boolean mOrganizationResult = false;

    @SerializedName("conversation_setting_result")
    public boolean mConversationSettingResult = false;

    @SerializedName("user_setting_result")
    public boolean mUserSettingResult = false;

    @SerializedName("app_result")
    public Set<String> mAppOrgCodesResult = null;

}
