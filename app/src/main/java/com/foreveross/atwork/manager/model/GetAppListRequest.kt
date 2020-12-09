package com.foreveross.atwork.manager.model

import android.content.Context
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class GetAppListRequest(
        @SerializedName("strategy")
        var strategy: Int = STRATEGY_DEFAULT,

        @SerializedName("limit")
        var limit: Int = -1,

        @SerializedName("org_id")
        private var orgCode: String? = null
) {

    fun getOrgCode(ctx: Context): String {
        if(StringUtils.isEmpty(orgCode)) {
            return PersonalShareInfo.getInstance().getCurrentOrg(ctx)
        }

        return orgCode!!
    }


    companion object {
        const val STRATEGY_DEFAULT = 0

        const val STRATEGY_VISIT = 1

        const val STRATEGY_CUSTOM = 2
    }
}

