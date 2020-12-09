package com.w6s.model.incomingCall

import android.os.Parcel
import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.employee.Position
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *  create by reyzhang22 at 2019-08-19
 */
@Parcelize
data class IncomingCaller(

        @Expose
        @SerializedName("mobile")
        var mobile: String = "",

        @Expose
        @SerializedName("name")
        var name: String = "",

        @Expose
        @SerializedName("root_org_name")
        var rootOrgName: String = "",

        @SerializedName("modify_time")
        var modifyTime: Long = -1,

        @SerializedName("positions")
        var positions: List<Position> = mutableListOf()
) : Parcelable {

        var jobTitle = ""

        var orgName = ""

        var corpName = ""

        var fullNamePath = ""
}