package com.foreveross.atwork.api.sdk.robot.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.robot.RobotData
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class GetInstructResponse: BasicResponseJSON() {
    @SerializedName("result")
    var data: Data? = null
    class Data {
        @SerializedName("total_count")
        var totalCount = StringUtils.EMPTY
        @SerializedName("records")
        var records: ArrayList<RobotData> = ArrayList()
        @SerializedName("deletes")
        var deletes: ArrayList<String> = ArrayList()
    }
}