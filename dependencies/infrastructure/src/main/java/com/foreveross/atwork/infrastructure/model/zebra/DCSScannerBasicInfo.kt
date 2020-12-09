package com.foreveross.atwork.infrastructure.model.zebra

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class DCSScannerBasicInfo (

    @SerializedName("scanner_id")
    var scannerId: Int = -1,

    @SerializedName("scanner_name")
    var scannerName: String = StringUtils.EMPTY

)