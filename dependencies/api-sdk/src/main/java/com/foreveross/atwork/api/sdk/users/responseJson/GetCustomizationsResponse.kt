package com.foreveross.atwork.api.sdk.users.responseJson

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class GetCustomizationsResponse : BasicResponseJSON() {

    @SerializedName("result")
    var result: Result? = null

    class Result {
        @SerializedName("CHAT_FILE_WHITELIST")
        var isInChatFileWhitelist = false
        @SerializedName("FAVORITES_WHITELIST")
        var isFavoriteWhiteList = false
    }
}