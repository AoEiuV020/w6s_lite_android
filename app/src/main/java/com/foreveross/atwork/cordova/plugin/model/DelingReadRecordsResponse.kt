package com.foreveross.atwork.cordova.plugin.model

import com.google.gson.annotations.SerializedName

class DelingReadRecordsResponse(

        @SerializedName("total_count")
        var totalCount: Int,

        @SerializedName("records")
        var recordListStr: String
)