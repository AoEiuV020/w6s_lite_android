package com.foreveross.atwork.modules.chat.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.MultipartMessageDetailBasicView
import com.foreveross.atwork.modules.chat.component.multipart.item.*
import com.foreveross.atwork.modules.chat.component.multipart.item.reference.*

class MultipartMessageAdapterV2(val dataList: List<ChatPostMessage>, private val strTranslationShortName: String?): BaseQuickAdapter<ChatPostMessage, MultipartMessageItemViewHolder>(dataList) {

    companion object {
        private const val VIEW_TYPE_UNKNOWN = -1
        private const val VIEW_TYPE_TEXT = 0
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_GIF = 2
        private const val VIEW_TYPE_STICKER = 3
        private const val VIEW_TYPE_VOICE = 4
        private const val VIEW_TYPE_FILE = 5
        private const val VIEW_TYPE_MICRO_VIDEO = 6
        private const val VIEW_TYPE_SHARE_LINK = 7
        private const val VIEW_TYPE_ORG_INVITE = 8
        private const val VIEW_TYPE_LOCATION = 9
        private const val VIEW_TYPE_REFERENCE_TEXT = 10
        private const val VIEW_TYPE_REFERENCE_IMAGE = 11
        private const val VIEW_TYPE_REFERENCE_GIF = 12
        private const val VIEW_TYPE_REFERENCE_STICKER = 13
        private const val VIEW_TYPE_REFERENCE_FILE = 14
        private const val VIEW_TYPE_REFERENCE_VOICE = 15
        private const val VIEW_TYPE_REFERENCE_MICRO_VIDEO = 16
        private const val VIEW_TYPE_REFERENCE_BUSINESS_CARD = 17
        private const val VIEW_TYPE_REFERENCE_LINK_SHARE = 18
        private const val VIEW_TYPE_ANNO_FILE = 19
        private const val VIEW_TYPE_REFERENCE_ANNO_FILE = 20
        private const val VIEW_TYPE_ANNO_IMAGE = 21
        private const val VIEW_TYPE_REFERENCE_ANNO_IMAGE = 22
        private const val VIEW_TYPE_REFERENCE_MULTIPART = 23
        private const val VIEW_TYPE_REFERENCE_LOCATION = 24

    }

    override fun getDefItemViewType(position: Int): Int {
        return when (val message = getItem(position)) {
            is TextChatMessage -> VIEW_TYPE_TEXT
            is StickerChatMessage -> VIEW_TYPE_STICKER
            is VoiceChatMessage -> VIEW_TYPE_VOICE
            is AnnoFileTransferChatMessage -> VIEW_TYPE_ANNO_FILE
            is AnnoImageChatMessage -> VIEW_TYPE_ANNO_IMAGE
            is FileTransferChatMessage -> VIEW_TYPE_FILE
            is MicroVideoChatMessage -> VIEW_TYPE_MICRO_VIDEO
            is ShareChatMessage -> {
                when {
                    ShareChatMessage.ShareType.Link.toString().equals(message.shareType, ignoreCase = true) -> VIEW_TYPE_SHARE_LINK
                    ShareChatMessage.ShareType.OrgInviteBody.toString().equals(message.shareType, ignoreCase = true) -> VIEW_TYPE_ORG_INVITE
                    ShareChatMessage.ShareType.Loc.toString().equals(message.shareType, ignoreCase = true) -> VIEW_TYPE_LOCATION
                    else -> VIEW_TYPE_UNKNOWN
                }
            }
            is ImageChatMessage -> {

                if (message.isGif) {
                    VIEW_TYPE_GIF
                } else {
                    VIEW_TYPE_IMAGE
                }
            }

            is ReferenceMessage -> {
                when(message.mReferencingMessage) {
                    is TextChatMessage -> VIEW_TYPE_REFERENCE_TEXT
                    is ReferenceMessage -> VIEW_TYPE_REFERENCE_TEXT
                    is StickerChatMessage -> VIEW_TYPE_REFERENCE_STICKER
                    is MicroVideoChatMessage -> VIEW_TYPE_REFERENCE_MICRO_VIDEO
                    is AnnoFileTransferChatMessage -> VIEW_TYPE_REFERENCE_ANNO_FILE
                    is FileTransferChatMessage -> VIEW_TYPE_REFERENCE_FILE
                    is VoiceChatMessage -> VIEW_TYPE_REFERENCE_VOICE
                    is ImageChatMessage -> {
                        if ((message.mReferencingMessage as ImageChatMessage).isGif) {
                            VIEW_TYPE_REFERENCE_GIF
                        } else {
                            VIEW_TYPE_REFERENCE_IMAGE
                        }
                    }
                    is AnnoImageChatMessage -> VIEW_TYPE_REFERENCE_ANNO_IMAGE
                    is ShareChatMessage -> {
                        val shareChatMessage = message.mReferencingMessage as ShareChatMessage
                        when {
                            ShareChatMessage.ShareType.BusinessCard.toString().equals(shareChatMessage.shareType, ignoreCase = true) -> VIEW_TYPE_REFERENCE_BUSINESS_CARD
                            ShareChatMessage.ShareType.Link.toString().equals(shareChatMessage.shareType, ignoreCase = true) -> VIEW_TYPE_REFERENCE_LINK_SHARE
                            ShareChatMessage.ShareType.Loc.toString().equals(shareChatMessage.shareType, ignoreCase = true) -> VIEW_TYPE_REFERENCE_LOCATION
                            else -> VIEW_TYPE_UNKNOWN
                        }
                    }
                    is MultipartChatMessage -> VIEW_TYPE_REFERENCE_MULTIPART
                    else -> VIEW_TYPE_UNKNOWN
                }
            }

            else -> VIEW_TYPE_UNKNOWN
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): MultipartMessageItemViewHolder {
        val messageItemView = when (viewType) {
            VIEW_TYPE_TEXT -> MultipartMessageDetailTextView(mContext, strTranslationShortName)
            VIEW_TYPE_IMAGE -> MultipartMessageDetailImageView(mContext)
            VIEW_TYPE_REFERENCE_ANNO_IMAGE -> MultipartMessageDetailReferenceAnnoImageView(mContext)
            VIEW_TYPE_GIF -> MultipartMessageDetailGifView(mContext)
            VIEW_TYPE_STICKER -> MultipartMessageDetailStickerView(mContext)
            VIEW_TYPE_VOICE -> MultipartMessageDetailVoiceView(mContext)
            VIEW_TYPE_FILE -> MultipartMessageDetailFileView(mContext)
            VIEW_TYPE_ANNO_FILE -> MultipartMessageDetailAnnoFileView(mContext)
            VIEW_TYPE_ANNO_IMAGE -> MultipartMessageDetailAnnoImageView(mContext)
            VIEW_TYPE_MICRO_VIDEO -> MultipartMessageDetailMicroVideoView(mContext)
            VIEW_TYPE_SHARE_LINK -> MultipartMessageDetailShareLinkView(mContext)
            VIEW_TYPE_LOCATION -> MultipartMessageDetailShareLocationView(mContext)
            VIEW_TYPE_ORG_INVITE -> MultipartMessageDetailOrgInviteView(mContext)
            VIEW_TYPE_REFERENCE_TEXT -> MultipartMessageDetaiReferenceTextView(mContext)
            VIEW_TYPE_REFERENCE_IMAGE -> MultipartMessageDetailReferenceImageView(mContext)
            VIEW_TYPE_REFERENCE_GIF -> MultipartMessageDetailReferenceGifView(mContext)
            VIEW_TYPE_REFERENCE_STICKER -> MultipartMessageDetailReferenceStickerView(mContext)
            VIEW_TYPE_REFERENCE_MICRO_VIDEO -> MultipartMessageDetailReferenceMicroVideoView(mContext)
            VIEW_TYPE_REFERENCE_ANNO_FILE -> MultipartMessageDetailReferenceAnnoFileView(mContext)
            VIEW_TYPE_REFERENCE_FILE -> MultipartMessageDetailReferenceFileView(mContext)
            VIEW_TYPE_REFERENCE_VOICE -> MultipartMessageDetailReferenceVoiceView(mContext)
            VIEW_TYPE_REFERENCE_BUSINESS_CARD-> MultipartMessageDetailReferenceBusinessCardView(mContext)
            VIEW_TYPE_REFERENCE_LINK_SHARE-> MultipartMessageDetailReferenceShareLinkView(mContext)
            VIEW_TYPE_REFERENCE_MULTIPART -> MultipartMessageDetaiReferenceMultipartView(mContext)
            VIEW_TYPE_REFERENCE_LOCATION -> MultipartMessageDetaiReferenceLocationView(mContext)
            else -> MultipartMessageDetailUnknownView(mContext)
        }

        return MultipartMessageItemViewHolder(messageItemView)
    }


    override fun convert(helper: MultipartMessageItemViewHolder, item: ChatPostMessage) {
        val multipartMessageDetailRefreshView = helper.messageItemView as MultipartMessageDetailBasicView<ChatPostMessage>
        multipartMessageDetailRefreshView.refreshItemView(item, helper.realPosition, dataList)
    }


}

class MultipartMessageItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var messageItemView = itemView as MultipartMessageDetailBasicView<*>
}
