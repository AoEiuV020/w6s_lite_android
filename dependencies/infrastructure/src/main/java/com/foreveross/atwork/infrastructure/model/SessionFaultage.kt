package com.foreveross.atwork.infrastructure.model

import java.util.*


/**
 *
 * 下面为session 断层示意说明
 *
 * ------------------
 * 1. message
 * 2. message
 * 3. message
 * ...
 *
 * sessionLastMessage
 *
 * ...
 *
 * ----- 可能有断层 -----
 * ...
 *
 * beginTimeInSyncMessages
 *
 * ...
 *
 * endTimeInSyncMessages
 * --------------------
 * */
class SessionFaultage (

        val id: String = UUID.randomUUID().toString(),

        val sessionId: String,

        val sessionLastMessageTime: Long,

        val beginTimeInSyncMessages: Long

) {
    override fun toString(): String {
        return "SessionFaultage(id='$id', sessionId='$sessionId', sessionLastMessageTime=$sessionLastMessageTime, beginTimeInSyncMessages=$beginTimeInSyncMessages)"
    }
}