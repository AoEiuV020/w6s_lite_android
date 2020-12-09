package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserSchemaSettingItem(
        @SerializedName("property")
        val property: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("alias")
        val alias: String,

        @SerializedName("visible")
        private var _visible: Boolean = true,

        @SerializedName("modifiable")
        private var _modifiable: Boolean = true,

        @SerializedName("sort_order")
        val sortOrder: Int


) : Parcelable, Comparable<UserSchemaSettingItem> {

    @IgnoredOnParcel
    @SerializedName("_modifiable")
    var modifiable = true
    get() {
        if("qr_code" == property) {
            return true
        }

        if("avatar" == property
            && !AtworkConfig.LOGIN_AVATAR_CAN_MODIFY) {
            return false

        }

        return _modifiable
    }

    @IgnoredOnParcel
    @SerializedName("_visible")
    var visible = true
    get() {

        if("birthday" == property
            && !AtworkConfig.USER_INFO_VIEW_CONFIG.showBirthday) {
            return false
        }


        if("gender" == property
            && !AtworkConfig.USER_INFO_VIEW_CONFIG.showGender) {
            return false
        }

        return _visible
    }


    override fun compareTo(other: UserSchemaSettingItem): Int {
        return this.sortOrder - other.sortOrder
    }

}


