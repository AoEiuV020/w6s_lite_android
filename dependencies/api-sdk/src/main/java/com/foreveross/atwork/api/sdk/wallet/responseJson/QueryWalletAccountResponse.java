package com.foreveross.atwork.api.sdk.wallet.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.wallet.WalletAccount;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/12/29.
 */

public class QueryWalletAccountResponse extends BasicResponseJSON {

    @SerializedName("result")
    public WalletAccount mWalletAccount;

}

