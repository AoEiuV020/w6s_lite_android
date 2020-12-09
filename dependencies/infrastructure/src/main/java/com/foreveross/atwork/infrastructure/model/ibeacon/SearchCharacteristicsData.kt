package com.foreveross.atwork.infrastructure.model.ibeacon

import com.google.gson.annotations.SerializedName

/**
 * 返回当前设备的特征
 */
class SearchCharacteristicsData {
    /**majorId值*/
    @SerializedName("major_id")
    var majorId: String? = null
    /**minorId值*/
    @SerializedName("minor_id")
    var minorId: String? = null
    /**snId值*/
    @SerializedName("sn_id")
    var snId: String? = null
}