package com.foreveross.atwork.api.sdk.qrcode.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/2/18.
 */
public class GetQrCodeJoinInfoResponse extends BasicResponseJSON {
    @SerializedName("result")
    public Result result;

    public class Result {
        @SerializedName("id")
        public String id;

        @SerializedName("addresser")
        public String addresser;

        @SerializedName("issued_time")
        public long issuedTime;

        @SerializedName("expire_time")
        public long expireTime;

        @SerializedName("props")
        public WorkplusQrCodeInfo props;
    }
}
