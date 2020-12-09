package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/5/18.
 */

public class GetUserFilePathRequest {

    public final static String FILE = "file";

    public final static String DROPBOX = "dropbox";

    public final static String EMAIL_ATTACHMENT = "email_attachment";

    @SerializedName("system")
    public String mSystem;

    @SerializedName("custom")
    public String mCustom;
}
