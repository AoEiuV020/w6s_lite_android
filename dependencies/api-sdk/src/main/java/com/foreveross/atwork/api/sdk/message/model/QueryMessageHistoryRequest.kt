package com.foreveross.atwork.api.sdk.message.model

class QueryMessageHistoryRequest(

        var skip: Int,

        var limit: Int,

        var orgCode: String,

        var appId: String,

        var messageType: String,

        var tagId: String?,

        var keyword: String

)