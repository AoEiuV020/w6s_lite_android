package com.foreveross.atwork.api.sdk.app.requestJson

import com.foreveross.atwork.infrastructure.utils.StringUtils

class QueryAppListInAppStoreRequest (
        val orgCode: String,

        val categoryId: String = StringUtils.EMPTY,

        val kw: String = StringUtils.EMPTY,

        val skip: Int,

        val limit: Int
)