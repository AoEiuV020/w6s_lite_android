package com.foreveross.atwork.api.sdk.qrcode.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/2/19.
 */
public class GetDiscussionJoinQrcodeResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result result;

    public class Result {
        @SerializedName("survivalSeconds")
        private Long survivalSeconds = 0L;

        @SerializedName("survival_seconds")
        private Long survival_seconds;

        @SerializedName("content")
        public String content;


        public long getSurvivalSeconds() {
            if(null == survival_seconds) {
                return survivalSeconds;
            }

            return survival_seconds;
        }

    }
}
