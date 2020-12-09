package com.foreveross.atwork.infrastructure.model.zebra

import com.google.gson.annotations.SerializedName

const val EVENT_CONNECTED = 0

const val EVENT_DISCONNECTED = 1

const val EVENT_SCANNED_RESULT = 2



class ZebraEventResult {

    @SerializedName("event")
    var event: Int = -1

    @SerializedName("scanner_id")
    var scannerId: Int?  = null

    @SerializedName("result")
    var data: ZebraEventData? = null

    class ZebraEventData (

        @SerializedName("data")
        var data: String? = null

    )
}



