package com.foreveross.atwork.api.sdk.faceBio.responseModel

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SetFaceBioFilmResult :  BasicResponseJSON() {

    @SerializedName("result")
    lateinit var result: Result

    inner class Result {

        @SerializedName("face_id")
        var faceId = ""

        @SerializedName("avatar")
        var avatar = ""
    }
}