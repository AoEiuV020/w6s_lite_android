package com.foreveross.atwork.modules.pointNumStore.model

import com.google.gson.annotations.SerializedName

class PointNumStoreLoginAction {

    @SerializedName("appId")
    var appId: String? = null

    @SerializedName("appKey")
    var appKey: String? = null

    @SerializedName("nickname")
    var nickname:String? = null

    @SerializedName("username")
    var username: String? = null

    @SerializedName("userid")
    var userid: String? = null

    @SerializedName("mobile")
    var mobile: String? = null

    @SerializedName("pointnum")
    var pointnum: Long? = null

    @SerializedName("version")
    var version: String? = null


}