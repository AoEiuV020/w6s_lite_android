package com.foreveross.atwork.infrastructure.beeworks

import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class BeeWorksEmail {

    @SerializedName("mailSignature")
    var mailSignature: String = StringUtils.EMPTY
    get() {
        return BeeWorksHelper.getString(BaseApplicationLike.baseContext, field)
    }


}