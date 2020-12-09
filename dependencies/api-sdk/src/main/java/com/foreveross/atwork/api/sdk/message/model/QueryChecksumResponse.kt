package com.foreveross.atwork.api.sdk.message.model

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

class QueryChecksumResponse(

        @SerializedName("result")
        val result: Result

) : BasicResponseJSON() {

    class Result(
            @SerializedName("count")
            val count: Int,

            @SerializedName("checksum")
            val checksum: String)
}