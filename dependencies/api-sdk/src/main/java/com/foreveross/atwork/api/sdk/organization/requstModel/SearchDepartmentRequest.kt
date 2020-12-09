package com.foreveross.atwork.api.sdk.organization.requstModel

import com.google.gson.annotations.SerializedName

class SearchDepartmentRequest {

    @SerializedName("fetch_full_name_path")
    var fullNamePath = true

    @SerializedName("query")
    var query: String = ""

    @SerializedName("org_codes")
    var orgCodes = mutableListOf<String>()
}