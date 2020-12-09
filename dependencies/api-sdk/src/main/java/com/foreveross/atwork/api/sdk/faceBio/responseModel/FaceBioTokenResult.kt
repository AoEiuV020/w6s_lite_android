package com.foreveross.atwork.api.sdk.faceBio.responseModel

import android.os.Parcelable
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *  create by reyzhang22 at 2019-09-20
 */
@Parcelize
class FaceBioTokenResult : BasicResponseJSON(), Parcelable {
    @SerializedName("result")
    lateinit var result: Result

    inner class Result {

        @SerializedName("token")
        var token = ""

        @SerializedName("provider")
        var provider = ""
    }

}