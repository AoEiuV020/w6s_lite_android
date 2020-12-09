package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetRunningAppsResponse {
    @SerializedName("package_names")
    public List<String> mPackageNameList = new ArrayList<>();
}
