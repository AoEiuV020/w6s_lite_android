package com.foreveross.atwork.infrastructure.newmessage.post.chat

import android.content.Context
import android.text.TextUtils
import com.foreveross.atwork.infrastructure.R
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.support.StringConstants
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import java.io.File
import java.io.Serializable
import java.util.*

class StickerChatMessage : ChatPostMessage(), Serializable {


    @Expose
    var themeName = ""

    @Expose
    var originalStickerName = ""

    @Expose
    var stickerName = ""

    @Expose
    var isGif = false

    @Expose
    var width = 80

    @Expose
    var height = 80

    @Expose
    var isLocal = true

    @Expose
    var name = ""

    override fun getChatBody(): MutableMap<String, Any> {
        val chatBody = HashMap<String, Any>()
        chatBody[THEME_NAME] = themeName
        chatBody[STICKER_NAME] = stickerName
        chatBody[WIDTH] = width
        chatBody[HEIGHT] = height
        chatBody[IS_GIF] = isGif
        chatBody[IS_LOCAL] = isLocal
        chatBody[NAME] = name
        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody[ChatPostMessage.ORG_ID] = mOrgId
        }
        if (isBurn) {
            chatBody[ChatPostMessage.BURN] = mBurnInfo.chatMapBody
        }
        setBasicChatBody(chatBody)
        return chatBody
    }

    override fun getChatType(): ChatType {
        return ChatType.STICKER
    }

    override fun getSessionShowTitle(): String {
        if (!TextUtils.isEmpty(name) && !"null".equals(name.toLowerCase())) {
            return "[" + name + "]"
        }
        return StringConstants.SESSION_NAME_STICKER
    }

    override fun getSearchAbleString(): String {
        return StringUtils.EMPTY;
    }

    override fun needNotify(): Boolean {
        return true
    }

    override fun needCount(): Boolean {
        return true
    }


    companion object {
        val THEME_NAME = "theme_name"

        val STICKER_NAME = "sticker_name"

        val WIDTH = "width"

        val HEIGHT = "height"

        val IS_GIF = "is_gif"

        val IS_LOCAL = "is_local"

        val NAME = "name"

        fun getStickerChatMessageFromJson(jsonMap: Map<String, Any>): StickerChatMessage {
            val stickerChatMessage = StickerChatMessage()
            stickerChatMessage.initPostTypeMessageValue(jsonMap)
            val bodyMap = jsonMap[PostTypeMessage.BODY] as Map<String, Any>
            stickerChatMessage.initChatTypeMessageValue(bodyMap)

            val themeName = bodyMap[THEME_NAME].toString()
            stickerChatMessage.themeName = themeName
            var stickerName = bodyMap[STICKER_NAME].toString()

            stickerChatMessage.originalStickerName = stickerName

            if (!TextUtils.isEmpty(stickerName) && stickerName.contains(".")) {
                stickerName = stickerName.split(".").get(0)
            }
            stickerChatMessage.stickerName = stickerName
            if (bodyMap.containsKey(WIDTH)) {
                stickerChatMessage.width = (bodyMap[WIDTH] as Double).toInt()
            }
            if (bodyMap.containsKey(HEIGHT)) {
                stickerChatMessage.height = (bodyMap[HEIGHT] as Double).toInt()
            }
            if (bodyMap.containsKey(IS_GIF)) {
                stickerChatMessage.isGif = bodyMap[IS_GIF].toString().toBoolean()
            }
            if (bodyMap.containsKey(IS_LOCAL)) {
                stickerChatMessage.isLocal = bodyMap[IS_LOCAL].toString().toBoolean()
            }

            stickerChatMessage.name = bodyMap[NAME].toString()
//            if (!StringUtils.isEmpty(stickerChatMessage.mDeletionPolicy) && bodyMap.containsKey(ChatPostMessage.BURN)) {
//                stickerChatMessage.mBurnInfo = BurnInfo.parseFromMap(bodyMap[ChatPostMessage.BURN] as Map<String, Any>)
//            }


            return stickerChatMessage
        }

        fun newSendStickerMessage(context: Context, themeName: String, stickerName: String, name: String, stickerWidth: Int, stickerHeight: Int, to: String, toDomain: String,
                                  toType: ParticipantType, orgId: String?, receiverContact: ShowListItem?, burn: Boolean, readTime: Long, deletionOnTime: Long, bingCreatorId: String?): StickerChatMessage {
            var toAvatar: String? = null
            var toName: String? = null
            if (null != receiverContact) {
                toAvatar = receiverContact.avatar
                toName = receiverContact.title
            }
            return newSendStickerMessage(context, themeName, stickerName, name, stickerWidth, stickerHeight, to, toDomain, toType, toAvatar, toName, BodyType.Face, orgId, burn, readTime, deletionOnTime, bingCreatorId)
        }

        fun newSendStickerMessage(context: Context, themeName: String, stickerName: String, name: String, stickerWidth: Int, stickerHeight: Int, to: String, toDomain: String, toType: ParticipantType, toAvatar: String?, toName: String?,
                                  bodyType: BodyType, orgId: String?, isBurn: Boolean, readTime: Long, deletionOnTime: Long, bingCreatorId: String?): StickerChatMessage {

            val stickerChatMessager = StickerChatMessage()
            stickerChatMessager.deliveryId = UUID.randomUUID().toString()
            stickerChatMessager.deliveryTime = TimeUtil.getCurrentTimeInMillis()
            stickerChatMessager.buildSenderInfo(context)
            stickerChatMessager.themeName = themeName
            stickerChatMessager.stickerName = stickerName
            stickerChatMessager.to = to
            stickerChatMessager.mToType = toType
            stickerChatMessager.mToDomain = toDomain
            stickerChatMessager.chatSendType = ChatSendType.SENDER
            stickerChatMessager.chatStatus = ChatStatus.Sending
            stickerChatMessager.mBodyType = bodyType
            stickerChatMessager.mOrgId = orgId
            stickerChatMessager.read = ReadStatus.AbsolutelyRead
            stickerChatMessager.mDisplayAvatar = toAvatar
            stickerChatMessager.mDisplayName = toName
            stickerChatMessager.mBingCreatorId = bingCreatorId
            stickerChatMessager.width = stickerWidth
            stickerChatMessager.height = stickerHeight
            stickerChatMessager.name = name
            return stickerChatMessager
        }

    }

    fun getChatStickerUrl(context: Context, remoteUrl: String): String {

        var stickerThumbnailUri: String
        if(!StringUtils.isEmpty(this.originalStickerName)) {
            stickerThumbnailUri = AtWorkDirUtils.getInstance().getStickerOriginalPath(this.themeName, this.originalStickerName)

            if (File(stickerThumbnailUri).exists()) {
                return "file://$stickerThumbnailUri"
            }
        }



        stickerThumbnailUri = AtWorkDirUtils.getInstance().getStickerOriginalPath(this.themeName, this.stickerName)

        if (File(stickerThumbnailUri).exists()) {
            return "file://$stickerThumbnailUri"
        }



        return AtWorkDirUtils.getInstance().getStickerOriginalUrl(context, remoteUrl, this.themeName, this.stickerName)

    }
}