package com.foreveross.atwork.infrastructure.model.wallet;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/12/29.
 */

public class WalletAccount {

    @SerializedName("fortunes")
    public long mFortunes;

    @SerializedName("integration")
    public long mBonusPoints;

    @SerializedName("phone")
    public String mMobile;

    @SerializedName("state")
    public String mState;

    @SerializedName("mass_enabled")
    public boolean mMassEnabled;



    public boolean isBind() {
        return !StringUtils.isEmpty(mMobile);
    }
}
