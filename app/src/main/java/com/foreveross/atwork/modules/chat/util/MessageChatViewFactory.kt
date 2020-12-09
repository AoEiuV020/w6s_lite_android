@file: JvmName("MessageChatViewFactory")

package com.foreveross.atwork.modules.chat.util


import android.content.Context
import android.os.Bundle
import android.view.View
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage
import com.foreveross.atwork.modules.chat.component.LeftStickerChatItemView
import com.foreveross.atwork.modules.chat.component.chat.*
import com.foreveross.atwork.modules.chat.component.chat.anno.LeftAnnoFileChatDetailView
import com.foreveross.atwork.modules.chat.component.chat.anno.LeftAnnoImageChatItemView
import com.foreveross.atwork.modules.chat.component.chat.anno.RightAnnoFileChatDetailItem
import com.foreveross.atwork.modules.chat.component.chat.anno.RightAnnoImageChatItemView
import com.foreveross.atwork.modules.chat.component.chat.reference.*
import com.foreveross.atwork.modules.chat.util.MessageChatViewBuild.*

fun getMsgChatView(context: Context, savedInstanceState: Bundle?,  session: Session, viewType: Int): View =
        when(viewType) {
            UNDO -> UndoMessageItemView(context)

            RIGHT_BURN -> RightBurnChatItemView(context)
            LEFT_BURN -> LeftBurnChatItemView(context)

            RIGHT_FILE -> RightFileChatDetailItem(context)
            LEFT_FILE -> LeftFileChatDetailView(context)

            RIGHT_TEXT -> {
                val rightTextChatItemView = RightTextChatItemView(context)
                rightTextChatItemView.setSessionId(session.identifier);
                rightTextChatItemView
            }

            LEFT_TEXT -> {
                val leftTextChatItemView = LeftTextChatItemView(context)
                leftTextChatItemView.setSessionId(session.identifier);
                leftTextChatItemView
            }

            RIGHT_IMAGE -> RightImageChatItemView(context)
            LEFT_IMAGE -> LeftImageChatItemView(context)

            RIGHT_GIF -> RightGifChatItemView(context)
            LEFT_GIF -> LeftGifChatItemView(context)

            RIGHT_STICKER -> RightStickerChatItemView(context)
            LEFT_STICKER -> LeftStickerChatItemView(context)

            SYSTEM -> SystemChatItemView(context)

            HISTORY_DIVIDER -> HistoryDividerView(context)

            RIGHT_VOICE -> RightVoiceChatItemView(context)
            LEFT_VOICE -> LeftVoiceChatItemView(context)

            SINGLE_ARTICLES -> SingleImageArticleItemView(context, session)
            MULTI_ARTICLES -> MultiImageArticleItemView(context, session)

            RIGHT_SHARE_LINK -> RightLinkShareChatItemView(context)
            LEFT_SHARE_LINK -> LeftLinkShareChatItemView(context)

            RIGHT_SHARE_CARE -> RightBusinessCardShareChatItemView(context)
            LEFT_SHARE_CARD -> LeftBusinessCardShareChatItemView(context)

            RIGHT_SHARE_ORG -> RightOrgInviteShareChatItemView(context)
            LEFT_SHARE_ORG -> LeftOrgInviteShareChatItemView(context)

            RIGHT_SHARE_LOCATION -> RightShareLocationView(context)
            LEFT_SHARE_LOCATION -> LeftShareLocationView(context, savedInstanceState)

            RIGHT_MICRO_VIDEO -> RightMicroVideoChatItemView(context)
            LEFT_MICRO_VIDEO -> LeftMicroVideoChatItemView(context)

            RIGHT_AUDIO_CONF -> RightESpaceChatItemView(context)
            LEFT_AUDIO_CONF -> LeftESpaceChatItemView(context)

            RIGHT_VOIP_MEETING -> RightVoipChatItemView(context)
            LEFT_VOIP_MEETING -> LeftVoipChatItemView(context)

            RIGHT_MULTIPART -> RightMultipartChatItemView(context)
            LEFT_MULTIPART -> LeftMultipartChatItemView(context)

            TEMPLATE -> TemplateMessageView(context, session)

            RIGHT_FILE_ANNO -> RightAnnoFileChatDetailItem(context)
            LEFT_FILE_ANNO -> LeftAnnoFileChatDetailView(context)

            RIGHT_IMAGE_ANNO -> RightAnnoImageChatItemView(context)
            LEFT_IMAGE_ANNO -> LeftAnnoImageChatItemView(context)

            RIGHT_REFERENCED_TEXT -> RightReferencedTextMessageChatItemView(context)
            LEFT_REFERENCED_TEXT -> LeftReferencedTextMessageChatItemView(context)

            RIGHT_REFERENCED_REFERENCED -> RightReferencedTextMessageChatItemView(context)
            LEFT_REFERENCED_REFERENCED -> LeftReferencedTextMessageChatItemView(context)

            RIGHT_REFERENCED_GIF -> RightReferencedGifMessageChatItemView(context)
            LEFT_REFERENCED_GIF -> LeftReferencedGifMessageChatItemView(context)

            RIGHT_REFERENCED_IMAGE -> RightReferencedImageMessageChatItemView(context)
            LEFT_REFERENCED_IMAGE -> LeftReferencedImageMessageChatItemView(context)

            RIGHT_REFERENCED_IMAGE_ANNO -> RightReferencedAnnoImageMessageChatItemView(context)
            LEFT_REFERENCED_IMAGE_ANNO -> LeftReferencedAnnoImageMessageChatItemView(context)

            RIGHT_REFERENCED_STICKER -> RightReferencedStickerMessageChatItemView(context)
            LEFT_REFERENCED_STICKER -> LeftReferencedStickerMessageChatItemView(context)

            RIGHT_REFERENCED_VOICE -> RightReferencedVoiceMessageChatItemView(context)
            LEFT_REFERENCED_VOICE -> LeftReferencedVoiceMessageChatItemView(context)

            RIGHT_REFERENCED_FILE_ANNO -> RightReferencedAnnoFileMessageChatItemView(context)
            LEFT_REFERENCED_FILE_ANNO -> RightReferencedAnnoFileMessageChatItemView(context)

            RIGHT_REFERENCED_FILE -> RightReferencedFileMessageChatItemView(context)
            LEFT_REFERENCED_FILE -> LeftReferencedFileMessageChatItemView(context)


            RIGHT_REFERENCED_SHARE_LINK -> RightReferencedShareLinkMessageChatItemView(context)
            LEFT_REFERENCED_SHARE_LINK -> LeftReferencedShareLinkMessageChatItemView(context)

            RIGHT_REFERENCED_BUSINESS_CARD -> RightReferencedBusinessCardMessageChatItemView(context)
            LEFT_REFERENCED_BUSINESS_CARD -> LeftReferencedBusinessCardMessageChatItemView(context)

            RIGHT_REFERENCED_LOCATION -> RightReferencedLocationMessageChatItemView(context)
            LEFT_REFERENCED_LOCATION -> LeftReferencedLocationMessageChatItemView(context)

            RIGHT_REFERENCED_MICRO_VIDEO -> RightReferencedMicroVideoMessageChatItemView(context)
            LEFT_REFERENCED_MICRO_VIDEO -> LeftReferencedMicroVideoMessageChatItemView(context)

            RIGHT_REFERENCED_MULTIPART -> RightReferencedMultipartMessageChatItemView(context)
            LEFT_REFERENCED_MULTIPART -> LeftReferencedMultipartMessageChatItemView(context)

            MEETING_NOTICE -> MeetingNoticeItemView(context)
            RIGHT_MEETING_NOTICE -> RightMeetingNoticeChatItemView(context)
            LEFT_MEETING_NOTICE -> LeftMeetingNoticeChatItemView(context)


            else -> SystemChatItemView(context)

        }
