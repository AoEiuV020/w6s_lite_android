package com.foreveross.atwork.api.sdk.users.responseJson

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class CheckSpecialViewResponse: BasicResponseJSON() {

    @SerializedName("result")
    var result = false
}