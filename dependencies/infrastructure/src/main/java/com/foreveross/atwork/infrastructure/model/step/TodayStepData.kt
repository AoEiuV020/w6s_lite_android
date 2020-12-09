package com.foreveross.atwork.infrastructure.model.step

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class TodayStepData {

    @SerializedName("sportDate")
    var sportDate = StringUtils.EMPTY

    @SerializedName("stepNum")
    var stepNum = 0L

    @SerializedName("km")
    var km = StringUtils.EMPTY

    @SerializedName("kaluli")
    var kaluli = StringUtils.EMPTY

    @SerializedName("today")
    var today = StringUtils.EMPTY
}