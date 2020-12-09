package com.foreveross.atwork.api.sdk.discussion.requestJson;

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/3/24.
 */

public class TransferOrBecomeOwnerRequestJson {

    @SerializedName("ops")
    public Options mOps;

    @SerializedName("owner")
    public UserHandleInfo mNewOwner;



    public TransferOrBecomeOwnerRequestJson(UserHandleInfo mNewOwner) {
        mOps = Options.TRANSFER;
        this.mNewOwner = mNewOwner;
    }

    public TransferOrBecomeOwnerRequestJson() {
    }

    public enum Options {
        /**
         * 自己成为群主
         * */
        OWNER,

        /**
         * 转移群主
         * */
        TRANSFER
    }
}
