package com.foreveross.atwork.api.sdk.message.model

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class QuerySessionListResponse(

    @SerializedName("result")
    val result: List<QuerySessionItemData>?

): BasicResponseJSON() {

    fun parse() {
        result?.forEach {
            it.parseLastMessage()
        }
    }
}

class QuerySessionItemData (
        @SerializedName("conversation_id")
        val conversationId: String,

        @SerializedName("last_delivery_id")
        val lastDeliveryId: String,

        @SerializedName("last_delivery_time")
        val lastDeliveryTime: Long,

        @SerializedName("last_message")
        val lastMessageRawJson: JsonObject?,

        var lastMessage: PostTypeMessage?

) {

    fun parseLastMessage() {
        lastMessageRawJson?.let {
            lastMessage = MessageCovertUtil.covertJsonToMessage(it.toString())
        }


        val lastMessage = lastMessage?:return

        if (LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext) == lastMessage.from) {
            lastMessage.chatSendType = ChatSendType.SENDER
        } else {
            lastMessage.chatSendType = ChatSendType.RECEIVER
        }


        if (lastMessage is FileTransferChatMessage) {
            val fileTransferChatMessage = lastMessage
            if (LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext) == lastMessage.from) {
                fileTransferChatMessage.fileStatus = FileStatus.SENDED
            } else {
                fileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD
            }

        } else if (lastMessage is VoiceChatMessage) {
            val voiceChatMessage = lastMessage
            voiceChatMessage.play = true
        }

        lastMessage.setChatStatus(ChatStatus.Sended)
    }


}