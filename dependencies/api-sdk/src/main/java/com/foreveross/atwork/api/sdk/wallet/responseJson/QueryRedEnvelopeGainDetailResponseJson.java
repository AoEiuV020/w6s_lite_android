package com.foreveross.atwork.api.sdk.wallet.responseJson;

import android.content.Context;
import androidx.annotation.Nullable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.wallet.IRedEnvelopeDetailInterface;
import com.foreveross.atwork.infrastructure.model.wallet.RedEnvelopeGrabbedInfo;
import com.foreveross.atwork.infrastructure.model.wallet.RedEnvelopeRule;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2018/1/4.
 */

public class QueryRedEnvelopeGainDetailResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result implements IRedEnvelopeDetailInterface{

        @SerializedName("count")
        public int mCount;

        @SerializedName("amount")
        public long mAmount;

        @SerializedName("user_id")
        public String mSenderId;

        @SerializedName("domain_id")
        public String mSenderDomain;

        @SerializedName("type")
        public String mRedEnvelopeRule;

        @SerializedName("to")
        public String mReceiverId;

        @SerializedName("to_type")
        public String mReceiverType;

        @SerializedName("id")
        public String mTransactionId;

        @SerializedName("remark")
        public String mRemark;

        @SerializedName("status")
        public String mStatus;

        @SerializedName("members")
        public List<RedEnvelopeGrabbedInfo> mGrabbedInfoList = new ArrayList<>();


        @Nullable
        public RedEnvelopeGrabbedInfo findMyGrabbedInfo(Context context) {
            for(RedEnvelopeGrabbedInfo grabbedInfo : mGrabbedInfoList) {
                if(User.isYou(context, grabbedInfo.mUserId)) {
                    return grabbedInfo;
                }
            }

            return null;
        }

        public boolean isExpired() {
            return "FINISHED".equals(mStatus);
        }

        @Override
        public String getTransactionId() {
            return mTransactionId;
        }

        @Override
        public boolean isFromDiscussionChat() {
            return ParticipantType.Discussion == ParticipantType.toParticipantType(mReceiverType);
        }

        @Override
        public String getSenderId() {
            return mSenderId;
        }

        @Override
        public String getSenderDomainId() {
            return mSenderDomain;
        }

        @Override
        public String getReceiverId() {
            return mReceiverId;
        }

        @Override
        public long getGrabbedMoney(Context context) {
            RedEnvelopeGrabbedInfo myGrabbedInfo = findMyGrabbedInfo(context);
            if(null != myGrabbedInfo) {
                return myGrabbedInfo.mAmount;
            }
            return 0;
        }

        @Override
        public String getRemark() {
            return mRemark;
        }

        @Override
        public RedEnvelopeRule getRedEnvelopeRule() {
            return RedEnvelopeRule.valueOf(mRedEnvelopeRule);
        }
    }
}
