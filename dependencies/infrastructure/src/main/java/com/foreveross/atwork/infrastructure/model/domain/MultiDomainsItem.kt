package com.foreveross.atwork.infrastructure.model.domain

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class MultiDomainsItem {

    @SerializedName("domain_id")
    val domainId: String = StringUtils.EMPTY

    @SerializedName("domain_name")
    val domainName: String? = StringUtils.EMPTY

}