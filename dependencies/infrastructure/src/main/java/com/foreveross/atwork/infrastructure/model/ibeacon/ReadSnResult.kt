package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

class ReadSnResult {
    /**sn_id*/
    @SerializedName("sn_id")
    var snId: String? = null
    /**sn_value*/
    @SerializedName("sn_value")
    var snValue: String? = null
}