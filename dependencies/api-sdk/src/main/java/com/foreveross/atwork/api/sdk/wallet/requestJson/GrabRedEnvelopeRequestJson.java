package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/2/1.
 */

public class GrabRedEnvelopeRequestJson {

    @SerializedName("envelope_id")
    public String mEnvelopeId;

    @SerializedName("receiver_type")
    public String mReceiverType;

    @SerializedName("receiver_id")
    public String mReceiverId;

    private GrabRedEnvelopeRequestJson(Builder builder) {
        mEnvelopeId = builder.mEnvelopeId;
        mReceiverType = builder.mReceiverType;
        mReceiverId = builder.mReceiverId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String mEnvelopeId;
        private String mReceiverType;
        private String mReceiverId;

        private Builder() {
        }

        public Builder setEnvelopeId(String envelopeId) {
            this.mEnvelopeId = envelopeId;
            return this;
        }

        public Builder setReceiverType(String receiverType) {
            this.mReceiverType = receiverType;
            return this;
        }

        public Builder setReceiverId(String receiverId) {
            this.mReceiverId = receiverId;
            return this;
        }

        public GrabRedEnvelopeRequestJson build() {
            return new GrabRedEnvelopeRequestJson(this);
        }
    }
}
