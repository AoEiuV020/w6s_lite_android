package com.foreveross.atwork.api.sdk.wallet.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class GiveMultiDiscussionRedEnvelopeResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public List<Result> mResults;

    public static class Result {
        @SerializedName("id")
        public String mTransactionId;

        @SerializedName("receiver")
        public Receiver mReceiver;
    }


    public class Receiver {

        @SerializedName("id")
        public String mTo;

        @SerializedName("domain_id")
        public String mDomainId;


        @SerializedName("type")
        public String mToType;



    }
}
