package com.foreveross.atwork.api.sdk.users.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 16/6/3.
 */
public class ContactSyncResponse extends BasicResponseJSON {
    @SerializedName("result")
    public List<ContactSyncItemJson> result;
}
