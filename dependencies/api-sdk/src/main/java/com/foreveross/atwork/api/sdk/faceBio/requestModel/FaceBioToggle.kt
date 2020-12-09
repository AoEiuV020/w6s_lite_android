package com.foreveross.atwork.api.sdk.faceBio.requestModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FaceBioToggle (
        @SerializedName("face_id")
        var faceId: String
) {
    @Expose(serialize = false)
    var toggleEnable: Boolean = false;
    @Expose(serialize = false)
    var avatar: String = ""
    @Expose(serialize = false)
    var photoPath: String = ""
    @Expose(serialize = false)
    @SerializedName("additional_face_id")
    var additionalFaceId: AdditionalFaceId = AdditionalFaceId()

    inner class AdditionalFaceId {
        @SerializedName("biz_token")
        var bizToken: String = ""
    }
}


