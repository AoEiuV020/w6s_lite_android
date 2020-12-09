package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/3.
 */

public class CheckRedEnvelopeGainDetailRequestJson {


    @SerializedName("envelope_id")
    public String mEnvelopeId;

    public static CheckRedEnvelopeGainDetailRequestJson newInstance() {
        return new CheckRedEnvelopeGainDetailRequestJson();
    }

    public CheckRedEnvelopeGainDetailRequestJson setEnvelopeId(String envelopeId) {
        mEnvelopeId = envelopeId;
        return this;
    }
}
