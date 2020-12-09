package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/7/6.
 */
@Deprecated
public class BasicErrorResponseJson {

    public BasicErrorResponseJson(String message) {
        if(null == message) {
            message = StringUtils.EMPTY;
        }

        this.message = message;
    }

    @SerializedName("message")
    public String message;
}
