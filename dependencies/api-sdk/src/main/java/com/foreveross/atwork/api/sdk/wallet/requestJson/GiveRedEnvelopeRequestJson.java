package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.foreveross.atwork.infrastructure.model.wallet.RedEnvelopeRule;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/3.
 */

public class GiveRedEnvelopeRequestJson {


    @SerializedName("envelope_type")
    public RedEnvelopeRule mRedEnvelopeRule;

    @SerializedName("total_num")
    public int mTotalNum;

    @SerializedName("total_money")
    public long mTotalMoney;

    @SerializedName("to_type")
    public String mToType;

    @SerializedName("to_domain")
    public String mToDomainId;

    @SerializedName("to")
    public String mTo;


    @SerializedName("to_name")
    public String mToName;

    @SerializedName("remark")
    public String mRemark;


    @SerializedName("password")
    public String mPwd;


    public static GiveRedEnvelopeRequestJson newInstance() {
        return new GiveRedEnvelopeRequestJson();
    }

    public GiveRedEnvelopeRequestJson setRedEnvelopeRule(RedEnvelopeRule redEnvelopeRule) {
        mRedEnvelopeRule = redEnvelopeRule;
        return this;
    }

    public GiveRedEnvelopeRequestJson setTotalNum(int totalNum) {
        mTotalNum = totalNum;
        return this;
    }

    public GiveRedEnvelopeRequestJson setTotalMoney(long totalMoney) {
        mTotalMoney = totalMoney;
        return this;
    }

    public GiveRedEnvelopeRequestJson setToType(ParticipantType toType) {
        mToType = toType.stringValue();
        return this;
    }

    public GiveRedEnvelopeRequestJson setToDomainId(String toDomainId) {
        mToDomainId = toDomainId;
        return this;
    }

    public GiveRedEnvelopeRequestJson setTo(String to) {
        mTo = to;
        return this;
    }

    public GiveRedEnvelopeRequestJson setToName(String toName) {
        mToName = toName;
        return this;
    }

    public GiveRedEnvelopeRequestJson setRemark(String remark) {
        mRemark = remark;
        return this;
    }

    public GiveRedEnvelopeRequestJson setPwd(String pwd) {
        mPwd = pwd;
        return this;
    }
}
