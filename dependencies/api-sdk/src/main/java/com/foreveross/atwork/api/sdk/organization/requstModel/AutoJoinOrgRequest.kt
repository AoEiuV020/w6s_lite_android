package com.foreveross.atwork.api.sdk.organization.requstModel

import com.google.gson.annotations.SerializedName

class AutoJoinOrgRequest {

    @SerializedName("query")
    var name:String = ""

    @SerializedName("phone")
    var phone:String = ""

    @SerializedName("avatar")
    var avatar:String = ""

    constructor(name:String,phone:String,avatar:String){
        this.name = name
        this.phone = phone
        this.avatar = avatar
    }



}