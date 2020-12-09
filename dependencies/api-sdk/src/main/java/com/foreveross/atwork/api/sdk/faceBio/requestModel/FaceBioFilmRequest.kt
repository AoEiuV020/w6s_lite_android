package com.foreveross.atwork.api.sdk.faceBio.requestModel

import com.google.gson.annotations.SerializedName

data class FaceBioFilmRequest(
        @SerializedName("provider")
        val provider: String) {

    @SerializedName("face_id")
    var faceId = ""

    @SerializedName("password")
    var password = ""
}