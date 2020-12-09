package com.foreveross.atwork.api.sdk.app.requestJson

import com.foreveross.atwork.infrastructure.utils.StringUtils

class QueryAppListByAdminRequest (
        val orgCode: String,

        val skip: Int,

        val limit: Int
)