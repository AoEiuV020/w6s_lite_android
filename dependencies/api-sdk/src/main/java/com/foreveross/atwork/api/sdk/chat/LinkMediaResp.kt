package com.foreveross.atwork.api.sdk.chat

import android.os.Parcel
import android.os.Parcelable
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *  create by reyzhang22 at 2020/3/5
 */
@Parcelize
class LinkMediaResp : BasicResponseJSON() {

    @SerializedName("result")
    var result = Result()

    @Parcelize
    class Result() : Parcelable {

        @SerializedName("id")
        lateinit var fileId: String

        @SerializedName("link_id")
        lateinit var linkedId: String
    }
}