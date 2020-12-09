package com.foreveross.atwork.cordova.plugin.model

import android.content.Context
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class SetAppCustomSortRequest {

    @SerializedName("app_ids_sort")
    var appIdsSort: String = StringUtils.EMPTY

    @SerializedName("org_id")
    private var orgCode: String? = null



    fun getOrgCode(ctx: Context): String {
        if(StringUtils.isEmpty(orgCode)) {
            return PersonalShareInfo.getInstance().getCurrentOrg(ctx)
        }

        return orgCode!!
    }
}