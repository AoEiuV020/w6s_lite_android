package com.foreveross.atwork.api.sdk.auth.model

import com.google.gson.annotations.SerializedName

class LoginWithFaceBioRequest : LoginTokenJSON() {

    init {
        grantType = "face_id"
    }

    var saveToken = true

    var additionalFaceId  = AdditionalFaceId()

    inner class AdditionalFaceId {
        @SerializedName("biz_token")
        var bizToken: String = ""
    }
}