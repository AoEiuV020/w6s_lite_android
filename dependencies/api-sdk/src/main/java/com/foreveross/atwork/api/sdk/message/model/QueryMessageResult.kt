package com.foreveross.atwork.api.sdk.message.model

import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import java.util.*


class QueryMessageResult {

    var postTypeMessages: List<ChatPostMessage> = ArrayList()


    var success: Boolean = false

    var realOfflineMsgSize: Int = 0

    var httpResult: HttpResult? = null


}
