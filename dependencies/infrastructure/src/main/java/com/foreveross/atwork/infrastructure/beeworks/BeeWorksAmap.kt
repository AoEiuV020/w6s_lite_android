package com.foreveross.atwork.infrastructure.beeworks

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * Created by reyzhang22 on 17/9/20.
 */

class BeeWorksAmap {

    @SerializedName("android")
    var info: BeeWorksAmapInfo = BeeWorksAmapInfo()




    class BeeWorksAmapInfo {
        @SerializedName("appKey")
        var appKey: String? = ""

        @SerializedName("shareLocation")
        var shareLocation: Boolean ? = true

    }
}
