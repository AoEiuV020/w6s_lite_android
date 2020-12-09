package com.foreveross.atwork.api.sdk.voip.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

public class QueryZoomTypeMeetingStatusResponse extends BasicResponseJSON {

    @SerializedName("result")
    public boolean result;
}
