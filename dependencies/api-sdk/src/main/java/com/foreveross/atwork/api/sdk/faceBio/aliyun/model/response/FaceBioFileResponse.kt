package com.foreveross.atwork.api.sdk.faceBio.aliyun.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class FaceBioFileResponse: BasicResponseJSON() {

    @SerializedName("media_id")
    val mediaId: String = ""

}