package com.foreveross.atwork.api.sdk.discussion.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class QueryReadOrUnreadResponse extends BasicResponseJSON {


    @SerializedName("result")
    public List<QueryReadUnreadResult> resultList;


    public static class QueryReadUnreadResult implements Serializable{
        @SerializedName("from_domain")
        public String fromDomain;

        @SerializedName("from_type")
        public String fromType;

        @SerializedName("from")
        public String from;

        @SerializedName("to_domain")
        public String toDomain;

        @SerializedName("to_type")
        public String toType;

        @SerializedName("to")
        public String to;

        @SerializedName("status")
        public String status;

        @SerializedName("evp_id")
        public String evpId;

        @SerializedName("receipt_time")
        public long receiptTime;

    }

}
