package com.foreveross.atwork.api.sdk.message.model

import com.foreveross.atwork.api.sdk.model.BasicWithAuthRequest

class QueryMessagesOnSessionRequest (

        val remoteConversionId: String,

        val sessionId: String,

        val begin: Long,

        val end: Long,

        val limit: Int

): BasicWithAuthRequest() {
    override fun toString(): String {
        return "QueryMessagesOnSessionRequest(remoteConversionId='$remoteConversionId', sessionId='$sessionId', begin=$begin, end=$end, limit=$limit)"
    }
}