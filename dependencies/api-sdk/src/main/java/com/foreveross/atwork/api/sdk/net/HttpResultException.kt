package com.foreveross.atwork.api.sdk.net

class HttpResultException(
        var errorCode: Int = -1,

        var errorMsg: String? = "",

        val httpResult: HttpResult? = null

) : Exception()