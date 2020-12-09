package com.foreveross.atwork.infrastructure.newmessage.post.chat

import android.content.Context
import android.text.TextUtils
import com.foreveross.atwork.infrastructure.model.file.FileData
import com.foreveross.atwork.infrastructure.model.file.FileData.getFileTypeFromDb
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.support.StringConstants.SESSION_NAME_DOC
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

const val ID = "id"
const val VOLUME_ID = "volume_id"
const val VOLUME_TYPE = "volume_type"
const val OWNER_CODE = "owner_code"
const val PARENT_ID = "parent_id"
const val NAME_PATH = "name_path"
const val PATH = "path"
const val DOC_NAME = "name"
const val SIZE = "size"
const val TYPE = "type"
const val ITEM_TYPE = "item_type"
const val IS_DIR = "is_dir"
class DocChatMessage : ChatPostMessage() {

    @Expose
    @SerializedName(NAME_PATH)
    var namePath: String = ""

    @Expose
    @SerializedName(PARENT_ID)
    var parentId: String = ""

    @Expose
    var path: String = ""

    @Expose
    @SerializedName(VOLUME_ID)
    var volumeId:String = ""

    @Expose
    var name: String = ""

    @Expose
    var size: Long = 0L


    @Expose
    var id: String = ""

    @Expose
    @SerializedName(OWNER_CODE)
    var ownerCode: String = ""

    @Expose
    @SerializedName(ITEM_TYPE)
    var itemType: String = ""

    @Expose
    @SerializedName(IS_DIR)
    var isDir: Boolean = false

    @Expose
    @SerializedName(TYPE)
    var shareType: String = ""

    @Expose
    @SerializedName(VOLUME_TYPE)
    var volumeType: String = ""

    @Expose
    @SerializedName(PostTypeMessage.MEDIA_ID)
    var mediaId: String = ""

    override fun getChatBody(): MutableMap<String, Any> {
        val chatBody = HashMap<String, Any>()
        chatBody[ID] = id
        chatBody[VOLUME_ID] = volumeId
        chatBody[VOLUME_TYPE] = volumeType
        chatBody[OWNER_CODE] = ownerCode
        chatBody[PARENT_ID] = parentId
        chatBody[NAME_PATH] = namePath
        chatBody[PATH] = path
        chatBody[DOC_NAME] = name
        chatBody[SIZE] = size
        chatBody[TYPE] = shareType
        chatBody[ITEM_TYPE] = itemType
        chatBody[IS_DIR] = isDir
        chatBody[PostTypeMessage.MEDIA_ID] = mediaId
        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody[ORG_ID] = mOrgId
        }

        setBasicChatBody(chatBody)
        return chatBody
    }

    override fun getSearchAbleString(): String {
        return name + name
    }

    override fun needNotify(): Boolean {
        return true
    }

    override fun getChatType(): ChatType {
        return ChatType.DOC
    }

    override fun getSessionShowTitle(): String {
        return SESSION_NAME_DOC
    }

    override fun needCount(): Boolean {
        return true
    }

    companion object {

        fun getDocChatMessageFromJson(jsonMap: Map<String, Any>): DocChatMessage {
            val docChatMessage = DocChatMessage()
            docChatMessage.initPostTypeMessageValue(jsonMap)
            val bodyMap = jsonMap[PostTypeMessage.BODY] as Map<String, Any>
            docChatMessage.initChatTypeMessageValue(bodyMap)

            if (bodyMap.containsKey(ID)) {
                docChatMessage.id = bodyMap[ID].toString()
            }
            if (bodyMap.containsKey(VOLUME_ID)) {
                docChatMessage.volumeId = bodyMap[VOLUME_ID].toString()
            }
            if (bodyMap.containsKey(VOLUME_TYPE)) {
                docChatMessage.volumeType = bodyMap[VOLUME_TYPE].toString()
            }
            if (bodyMap.containsKey(OWNER_CODE)) {
                docChatMessage.ownerCode = bodyMap[OWNER_CODE].toString()
            }
            if (bodyMap.containsKey(PARENT_ID)) {
                docChatMessage.parentId = bodyMap[PARENT_ID].toString()
            }
            if (bodyMap.containsKey(NAME_PATH)) {
                docChatMessage.namePath = bodyMap[NAME_PATH].toString()
            }
            if (bodyMap.containsKey(DOC_NAME)) {
                docChatMessage.name = bodyMap[DOC_NAME].toString()
            }
            if (bodyMap.containsKey(PATH)) {
                docChatMessage.path = bodyMap[PATH].toString()
            }
            if (bodyMap.containsKey(SIZE)) {
                docChatMessage.size = (bodyMap[SIZE] as Double).toLong()
            }
            if (bodyMap.containsKey(TYPE)) {
                docChatMessage.shareType = bodyMap[TYPE].toString()
            }
            if (bodyMap.containsKey(ITEM_TYPE)) {
                docChatMessage.itemType = bodyMap[ITEM_TYPE].toString()
            }
            if (bodyMap.containsKey(IS_DIR)) {
                docChatMessage.isDir = bodyMap[IS_DIR].toString().toBoolean()
            }
            if (bodyMap.containsKey(PostTypeMessage.MEDIA_ID)) {
                docChatMessage.mediaId = bodyMap[PostTypeMessage.MEDIA_ID].toString()
            }

            return docChatMessage
        }

        fun newSendDocMessage(context: Context, id: String, volumeId: String, volumeType: String, ownerCode: String, parentId: String,
                              namePath: String, name: String, path: String, size: Long, shareType: String, itemType: String, isDir: Boolean,
                              to: String, toDomain: String, toType: ParticipantType, toAvatar: String?, toName: String?,
                                  bodyType: BodyType, orgId: String?, isBurn: Boolean, readTime: Long, deletionOnTime: Long, bingCreatorId: String?): DocChatMessage {

            val docChateMessage = DocChatMessage()
            docChateMessage.deliveryId = UUID.randomUUID().toString()
            docChateMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis()
            docChateMessage.buildSenderInfo(context)
            docChateMessage.to = to
            docChateMessage.mToType = toType
            docChateMessage.mToDomain = toDomain
            docChateMessage.chatSendType = ChatSendType.SENDER
            docChateMessage.chatStatus = ChatStatus.Sending
            docChateMessage.mBodyType = bodyType
            docChateMessage.mOrgId = orgId
            docChateMessage.read = ReadStatus.AbsolutelyRead
            docChateMessage.mDisplayAvatar = toAvatar
            docChateMessage.mDisplayName = toName
            docChateMessage.mBingCreatorId = bingCreatorId
            docChateMessage.id = id
            docChateMessage.volumeId = volumeId
            docChateMessage.volumeType = volumeType
            docChateMessage.ownerCode = ownerCode
            docChateMessage.parentId = parentId
            docChateMessage.namePath = namePath
            docChateMessage.name = name
            docChateMessage.path = path
            docChateMessage.size = size
            docChateMessage.shareType = shareType
            docChateMessage.itemType = itemType
            docChateMessage.isDir = isDir
            return docChateMessage
        }


    }

    fun convertToFileTransferMessage(): FileTransferChatMessage {
        return FileTransferChatMessage().also {
            it.deliveryId = this.deliveryId
            it.from = this.from
            it.to = this.to
            it.mFromType = this.mFromType
            it.mToType = this.mToType
            it.mBodyType = BodyType.File
            it.name = this.name
            it.size = this.size
            it.fileType = FileData.getFileType(this.name)
            it.mediaId = this.mediaId
        }
    }

}