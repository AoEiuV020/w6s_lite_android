package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.foreveross.atwork.api.sdk.wallet.model.Receiver;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.wallet.RedEnvelopeRule;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class GiveMultiDiscussionRedEnvelopeRequestJson extends GiveRedEnvelopeRequestJson {


    @SerializedName("receivers")
    public List<Receiver> mReceiverList;

    public static GiveMultiDiscussionRedEnvelopeRequestJson newInstance() {
        return new GiveMultiDiscussionRedEnvelopeRequestJson();
    }

    public GiveMultiDiscussionRedEnvelopeRequestJson setDiscussionList(List<Discussion> discussionList) {
        List<Receiver> receiverList = new ArrayList<>();
        for(Discussion discussion : discussionList) {
            Receiver receiver = Receiver.newReceiver()
                    .setToType(ParticipantType.DISCUSSION)
                    .setTo(discussion.getId())
                    .setDomainId(discussion.getDomainId())
                    .setName(discussion.mName)
                    .setOrgId(discussion.getOrgCodeCompatible())
                    .setAvatar(discussion.mAvatar);

            receiverList.add(receiver);
        }


        return setReceiverList(receiverList);
    }

    public String findReceiverName(String to) {
        Receiver receiver = findReceiver(to);
        if(null !=receiver) {
            return receiver.mName;
        }

        return StringUtils.EMPTY;

    }

    public String findReceiverOrgId(String to) {
        Receiver receiver = findReceiver(to);
        if(null !=receiver) {
            return receiver.mOrgId;
        }

        return StringUtils.EMPTY;

    }

    public String findReceiverAvatar(String to) {
        Receiver receiver = findReceiver(to);
        if(null !=receiver) {
            return receiver.mAvatar;
        }

        return StringUtils.EMPTY;

    }

    public Receiver findReceiver(String to) {
        for(Receiver receiver : mReceiverList) {
            if(to.equals(receiver.mTo)) {
                return receiver;
            }
        }

        return null;
    }


    public GiveMultiDiscussionRedEnvelopeRequestJson setReceiverList(List<Receiver> receiverList) {
        mReceiverList = receiverList;
        return this;
    }

    public GiveMultiDiscussionRedEnvelopeRequestJson setRedEnvelopeRule(RedEnvelopeRule redEnvelopeRule) {
        mRedEnvelopeRule = redEnvelopeRule;
        return this;
    }

    public GiveMultiDiscussionRedEnvelopeRequestJson setRemark(String remark) {
        mRemark = remark;
        return this;
    }

    public GiveMultiDiscussionRedEnvelopeRequestJson setPwd(String pwd) {
        mPwd = pwd;
        return this;
    }

    public GiveMultiDiscussionRedEnvelopeRequestJson setTotalNum(int totalNum) {
        mTotalNum = totalNum;
        return this;
    }

    public GiveMultiDiscussionRedEnvelopeRequestJson setTotalMoney(long totalMoney) {
        mTotalMoney = totalMoney;
        return this;
    }

}
