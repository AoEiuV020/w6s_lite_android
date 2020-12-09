package com.foreveross.atwork.infrastructure.model.wallet;

import android.content.Context;

/**
 * Created by dasunsy on 2018/1/15.
 */

public interface IRedEnvelopeDetailInterface {

    String getTransactionId();

    boolean isFromDiscussionChat();

    String getSenderId();

    String getSenderDomainId();

    String getReceiverId();

    long getGrabbedMoney(Context context);

    String getRemark();

    RedEnvelopeRule getRedEnvelopeRule();
}
