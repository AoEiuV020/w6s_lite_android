package com.foreveross.atwork.api.sdk.message.model

import com.foreveross.atwork.api.sdk.model.BasicWithAuthRequest

class QuerySessionListRequest (

        val timestamp: Long,

        val limit: Int

): BasicWithAuthRequest()