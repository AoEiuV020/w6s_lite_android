package com.foreveross.atwork.infrastructure.model.device

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LoginDeviceInfo(

        @SerializedName("id")
        var id: String,

        @SerializedName("domain_id")
        var domainId: String,

        @SerializedName("device_id")
        var deviceId: String?,

        @SerializedName("device_name")
        var deviceName: String?,

        @SerializedName("device_system_info")
        var deviceSystemInfo: String?,

        @SerializedName("user_id")
        var userId: String?,

        @SerializedName("username")
        var username: String?,

        @SerializedName("name")
        var name: String?,

        @SerializedName("authenticated")
        var authenticated: Boolean = false,

        @SerializedName("authenticated_time")
        var authenticatedTime: Long,

        @SerializedName("last_login_time")
        var lastLoginTime: Long,

        @SerializedName("system_version")
        var systemVersion: String?,

        @SerializedName("system_model")
        var systemModel: String?,

        @SerializedName("product_version")
        var productVersion: String?,

        var currentAuthenticatedDeviceIds: List<String> = ArrayList()


) : Parcelable {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginDeviceInfo

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}