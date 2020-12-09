package com.foreveross.atwork.infrastructure.model.wallet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/4.
 */

public class RedEnvelopeGrabbedInfo implements Comparable<RedEnvelopeGrabbedInfo>{

    @SerializedName("user_id")
    public String mUserId;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("amount")
    public long mAmount;

    @SerializedName("create_time")
    public long mGrabTime;

    @SerializedName("optimum")
    public boolean mOptimum;

    @Override
    public int compareTo(RedEnvelopeGrabbedInfo another) {
        int result;

        long resultLong = another.mGrabTime - this.mGrabTime;

        if (0 < resultLong) {
            result = 1;
        } else if (0 == resultLong) {
            result = 0;
        } else {
            result = -1;
        }

        return result;
    }
}
