package com.foreveross.atwork.api.sdk.domain.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.domain.MultiDomainsItem
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class QueryMultiDomainsResponse: BasicResponseJSON() {


    @SerializedName("result")
    val result: List<MultiDomainsItem>? = null

}

