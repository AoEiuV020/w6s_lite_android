package com.foreveross.atwork.api.sdk.auth.model

import android.os.Parcelable
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class LoginDeviceNeedAuthResponse (

        @SerializedName("result")
        var result: LoginDeviceNeedAuthResult?

) : BasicResponseJSON()

@Parcelize
class LoginDeviceNeedAuthResult (
        @SerializedName("user_id")
        var userId: String?,

        @SerializedName("user_phone")
        var userPhone: String?,

        @SerializedName("username")
        var username: String?,

        @SerializedName("name")
        var name: String?,

        var preInputPwd: String
) : Parcelable

