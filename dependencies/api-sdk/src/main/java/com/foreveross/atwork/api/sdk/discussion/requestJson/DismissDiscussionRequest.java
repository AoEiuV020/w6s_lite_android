package com.foreveross.atwork.api.sdk.discussion.requestJson;

import com.google.gson.annotations.SerializedName;

public class DismissDiscussionRequest {


    @SerializedName("ops")
    private Options mOps = Options.DISMISS;

    public Options getOps() {
        return mOps;
    }


    public enum Options {
        /**
         * 解散群
         * */
        DISMISS


    }
}
