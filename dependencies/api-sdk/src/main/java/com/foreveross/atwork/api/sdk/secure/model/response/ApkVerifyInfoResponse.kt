package com.foreveross.atwork.api.sdk.secure.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class ApkVerifyInfoResponse: BasicResponseJSON() {

    @SerializedName("result")
    var infoList: List<ApkVerifyInfo> = emptyList()


}