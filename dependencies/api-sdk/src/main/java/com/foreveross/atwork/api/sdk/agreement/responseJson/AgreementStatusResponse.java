package com.foreveross.atwork.api.sdk.agreement.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/7/22.
 */

public class AgreementStatusResponse extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result {
        @SerializedName("agreement_confirmed")
        public boolean mAgreementConfirmed;

    }
}
