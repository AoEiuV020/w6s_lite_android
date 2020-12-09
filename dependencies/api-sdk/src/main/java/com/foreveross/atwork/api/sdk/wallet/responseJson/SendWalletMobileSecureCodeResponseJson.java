package com.foreveross.atwork.api.sdk.wallet.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/12/31.
 */

public class SendWalletMobileSecureCodeResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result {

    }
}
