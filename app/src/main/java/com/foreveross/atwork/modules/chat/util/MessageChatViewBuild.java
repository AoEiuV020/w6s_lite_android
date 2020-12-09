package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ESpaceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.modules.chat.component.LeftStickerChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.HistoryDividerView;
import com.foreveross.atwork.modules.chat.component.chat.LeftBurnChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftBusinessCardShareChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftESpaceChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftFileChatDetailView;
import com.foreveross.atwork.modules.chat.component.chat.LeftGifChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftImageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftLinkShareChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftMeetingNoticeChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftMicroVideoChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftMultipartChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftOrgInviteShareChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftShareLocationView;
import com.foreveross.atwork.modules.chat.component.chat.LeftTextChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftVoiceChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftVoipChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.MeetingNoticeItemView;
import com.foreveross.atwork.modules.chat.component.chat.MultiImageArticleItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightBurnChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightBusinessCardShareChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightESpaceChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightFileChatDetailItem;
import com.foreveross.atwork.modules.chat.component.chat.RightGifChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightImageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightLinkShareChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightMeetingNoticeChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightMicroVideoChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightMultipartChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightOrgInviteShareChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightShareLocationView;
import com.foreveross.atwork.modules.chat.component.chat.RightStickerChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightTextChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightVoiceChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.RightVoipChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.SingleImageArticleItemView;
import com.foreveross.atwork.modules.chat.component.chat.SystemChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.TemplateMessageView;
import com.foreveross.atwork.modules.chat.component.chat.UndoMessageItemView;
import com.foreveross.atwork.modules.chat.component.chat.anno.LeftAnnoFileChatDetailView;
import com.foreveross.atwork.modules.chat.component.chat.anno.LeftAnnoImageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.anno.RightAnnoFileChatDetailItem;
import com.foreveross.atwork.modules.chat.component.chat.anno.RightAnnoImageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedAnnoFileMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedAnnoImageMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedBusinessCardMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedFileMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedGifMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedImageMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedLocationMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedMicroVideoMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedMultipartMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedShareLinkMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedStickerMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedTextMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.LeftReferencedVoiceMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedAnnoFileMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedAnnoImageMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedBusinessCardMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedFileMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedGifMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedImageMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedLocationMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedMicroVideoMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedMultipartMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedShareLinkMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedStickerMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedTextMessageChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.reference.RightReferencedVoiceMessageChatItemView;
import com.foreveross.atwork.utils.ChatMessageHelper;


public class MessageChatViewBuild {


    public static int LEFT_TEXT = 0;

    public static int RIGHT_TEXT = 1;

    public static int LEFT_IMAGE = 2;

    public static int RIGHT_IMAGE = 3;

    public static int LEFT_VOICE = 4;

    public static int RIGHT_VOICE = 5;

    public static int LEFT_FILE = 6;

    public static int RIGHT_FILE = 7;

    public static int SINGLE_ARTICLES = 8;

    public static int MULTI_ARTICLES = 9;

    public static int SYSTEM = 10;

    public static int HISTORY_DIVIDER = 11;

    public static int LEFT_GIF = 12;

    public static int RIGHT_GIF = 13;

    public static int LEFT_SHARE_LINK = 14;

    public static int RIGHT_SHARE_LINK = 15;

    public static int LEFT_MICRO_VIDEO = 16;

    public static int RIGHT_MICRO_VIDEO = 17;

    public static int LEFT_AUDIO_CONF = 18;

    public static int RIGHT_AUDIO_CONF = 19;

    public static int LEFT_SHARE_CARD = 20;

    public static int RIGHT_SHARE_CARE = 21;

    public static int LEFT_SHARE_ORG = 22;

    public static int RIGHT_SHARE_ORG = 23;

    public static int RIGHT_VOIP_MEETING = 24;

    public static int LEFT_VOIP_MEETING = 25;

    public static int LEFT_BURN = 26;

    public static int RIGHT_BURN = 27;

    public static int LEFT_MULTIPART = 28;

    public static int RIGHT_MULTIPART = 29;

    public static int TEMPLATE = 30;

    public static int LEFT_BING_TEXT = 31;

    public static int LEFT_BING_VOICE = 32;

    public static int RIGHT_BING_TEXT = 33;

    public static int RIGHT_BING_VOICE = 34;

    public static int MEETING_NOTICE = 35;

    public static int LEFT_MEETING_NOTICE = 36;

    public static int RIGHT_MEETING_NOTICE = 37;

    public static int LEFT_RED_ENVELOPE = 38;

    public static int RIGHT_RED_ENVELOPE = 39;

    public static int RED_ENVELOPE_NOTICE = 40;

    public static int RED_ENVELOPE_ROLLBACK_NOTICE = 41;


    public static int LEFT_STICKER = 42;

    public static int RIGHT_STICKER = 43;

    public static int LEFT_SHARE_LOCATION = 44;

    public static int RIGHT_SHARE_LOCATION = 45;

    public static int LEFT_REFERENCED_TEXT = 46;

    public static int RIGHT_REFERENCED_TEXT = 47;

    public static int LEFT_REFERENCED_REFERENCED = 48;

    public static int RIGHT_REFERENCED_REFERENCED = 49;

    public static int LEFT_REFERENCED_IMAGE = 50;

    public static int RIGHT_REFERENCED_IMAGE = 51;

    public static int LEFT_REFERENCED_GIF = 52;

    public static int RIGHT_REFERENCED_GIF = 53;

    public static int LEFT_REFERENCED_VOICE = 54;

    public static int RIGHT_REFERENCED_VOICE = 55;

    public static int LEFT_REFERENCED_FILE = 56;

    public static int RIGHT_REFERENCED_FILE = 57;

    public static int LEFT_REFERENCED_BUSINESS_CARD = 58;

    public static int RIGHT_REFERENCED_BUSINESS_CARD = 59;

    public static int LEFT_REFERENCED_SHARE_LINK = 60;

    public static int RIGHT_REFERENCED_SHARE_LINK = 61;

    public static int LEFT_REFERENCED_MICRO_VIDEO = 62;

    public static int RIGHT_REFERENCED_MICRO_VIDEO = 63;

    public static int LEFT_REFERENCED_MULTIPART = 64;

    public static int RIGHT_REFERENCED_MULTIPART = 65;

    public static int LEFT_REFERENCED_STICKER = 66;

    public static int RIGHT_REFERENCED_STICKER = 67;

    public static int LEFT_REFERENCED_LOCATION = 68;

    public static int RIGHT_REFERENCED_LOCATION = 69;

    public static int LEFT_FILE_ANNO = 70;

    public static int RIGHT_FILE_ANNO = 71;

    public static int LEFT_REFERENCED_FILE_ANNO = 72;

    public static int RIGHT_REFERENCED_FILE_ANNO = 73;

    public static int LEFT_IMAGE_ANNO = 74;

    public static int RIGHT_IMAGE_ANNO = 75;

    public static int LEFT_REFERENCED_IMAGE_ANNO = 76;

    public static int RIGHT_REFERENCED_IMAGE_ANNO = 77;

    public static int UNDO = 78;

    public static int MESSAGE_VIEW_TYPE_COUNT = 79;


    @Deprecated
    public static View getMsgChatView(Context context, Bundle savedInstanceState, ChatPostMessage message, Session session) {
        //最高级别->撤销消息
        if(message.isUndo()) {
            return new UndoMessageItemView(context);
        }


        if(message.isBurn()) {
            if (isRightView(message)) {
                return new RightBurnChatItemView(context);
            }
            if (isLeftView(message)) {
                return new LeftBurnChatItemView(context);
            }

        }

        //文件传输
        if (message.getChatType().equals(ChatPostMessage.ChatType.File)) {
            if (isRightView(message)) {
                return new RightFileChatDetailItem(context);
            }
            if (isLeftView(message)) {
                return new LeftFileChatDetailView(context);
            }
        }

        //文本消息
        if (message.getChatType().equals(ChatPostMessage.ChatType.Text)) {
            if (isLeftView(message)) {
                LeftTextChatItemView view = new LeftTextChatItemView(context);
                view.setSessionId(session.identifier);
                return view;
            }
            if (isRightView(message)) {
                RightTextChatItemView view = new RightTextChatItemView(context);
                view.setSessionId(session.identifier);
                return view;
            }

        }

        //图片消息
        if (message.getChatType().equals(ChatPostMessage.ChatType.Image)) {
            if (isRightView(message)) {
                if(isGif(message)) {
                    return new RightGifChatItemView(context);
                }
                return new RightImageChatItemView(context);
            }
            if (isLeftView(message)) {
                if(isGif(message)) {
                    return new LeftGifChatItemView(context);
                }
                return new LeftImageChatItemView(context);
            }
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.STICKER)) {
            if (isRightView(message)) {
                return new RightStickerChatItemView(context);
            }
            return new LeftStickerChatItemView(context);
        }

        //系统消息
        if (message.getChatType().equals(ChatPostMessage.ChatType.System)) {
            return new SystemChatItemView(context);
        }

        if(message.getChatType().equals(ChatPostMessage.ChatType.UNKNOWN)) {
            if (isRightView(message)) {
                return new RightTextChatItemView(context);
            } else if (isLeftView(message)) {
                return new LeftTextChatItemView(context);
            }
        }

        //历史记录分割线
        if (message.getChatType().equals(ChatPostMessage.ChatType.HistoryDivider)) {
            return new HistoryDividerView(context);
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.Voice)) {
            if (isRightView(message)) {
                return new RightVoiceChatItemView(context);
            }
            if (isLeftView(message)) {
                return new LeftVoiceChatItemView(context);
            }
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.Article)) {
            ArticleChatMessage articleChatMessage = (ArticleChatMessage) message;
            if (articleChatMessage.articles.size() == 1) {
                return new SingleImageArticleItemView(context, session);
            }
            if (articleChatMessage.articles.size() > 1) {
                return new MultiImageArticleItemView(context, session);
            }
            return new SystemChatItemView(context);
        }

        //分享类型
        if (message.getChatType().equals(ChatPostMessage.ChatType.Share)) {
            ShareChatMessage shareChatMessage = (ShareChatMessage) message;
            if (isRightView(message)) {
                if (ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new RightLinkShareChatItemView(context);
                }
                if (ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new RightBusinessCardShareChatItemView(context);
                }
                if (ShareChatMessage.ShareType.OrgInviteBody.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new RightOrgInviteShareChatItemView(context);
                }
                if (ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new RightShareLocationView(context);
                }

            }
            if (isLeftView(message)) {

                if (ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new LeftLinkShareChatItemView(context);
                }

                if (ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new LeftBusinessCardShareChatItemView(context);
                }

                if (ShareChatMessage.ShareType.OrgInviteBody.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new LeftOrgInviteShareChatItemView(context);
                }
                if (ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return new LeftShareLocationView(context, savedInstanceState);
                }
            }
        }

        //小视频
        if (message.getChatType().equals(ChatPostMessage.ChatType.MicroVideo)) {
            if (isRightView(message)){
                return new RightMicroVideoChatItemView(context);
            }
            if (isLeftView(message)){
                return new LeftMicroVideoChatItemView(context);
            }
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.AUDIOMEETING)) {
            ESpaceChatMessage eSpaceChatMessage = (ESpaceChatMessage)message;
            if (!eSpaceChatMessage.mIsActivity) {
                return new SystemChatItemView(context);
            }
            if (isRightView(message)) {
                return new RightESpaceChatItemView(context);
            }
            return new LeftESpaceChatItemView(context);
        }


        //语音会议
        if(message.getChatType().equals(ChatPostMessage.ChatType.VOIP)) {
            if (isRightView(message)){
                return new RightVoipChatItemView(context);
            }
            if (isLeftView(message)){
                return new LeftVoipChatItemView(context);
            }

        }

        //语音会议
        if(message.getChatType().equals(ChatPostMessage.ChatType.MULTIPART)) {
            if (isRightView(message)){
                return new RightMultipartChatItemView(context);
            }
            if (isLeftView(message)){
                return new LeftMultipartChatItemView(context);
            }

        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.TEMPLATE)) {
            return new TemplateMessageView(context, session);
        }


        //带comment 的文件消息
        if(message.getChatType().equals(ChatPostMessage.ChatType.ANNO_FILE)) {
            if (isRightView(message)){
                return new RightAnnoFileChatDetailItem(context);
            }
            if (isLeftView(message)){
                return new LeftAnnoFileChatDetailView(context);
            }
        }


        //带comment 的图片消息
        if(message.getChatType().equals(ChatPostMessage.ChatType.ANNO_IMAGE)) {
            if (isRightView(message)){
                return new RightAnnoImageChatItemView(context);
            }
            if (isLeftView(message)){
                return new LeftAnnoImageChatItemView(context);
            }
        }

        if(message.getChatType().equals(ChatPostMessage.ChatType.QUOTED)) {

            ReferenceMessage referenceMessage = (ReferenceMessage) message;
            if(null != referenceMessage.mReferencingMessage) {

                if(referenceMessage.mReferencingMessage instanceof TextChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedTextMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedTextMessageChatItemView(context);
                    }
                }


                if(referenceMessage.mReferencingMessage instanceof ReferenceMessage) {
                    if (isRightView(message)){
                        return new RightReferencedTextMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedTextMessageChatItemView(context);
                    }
                }


                if(referenceMessage.mReferencingMessage instanceof ImageChatMessage) {

                    if(isGif(referenceMessage.mReferencingMessage)) {
                        if (isRightView(message)){
                            return new RightReferencedGifMessageChatItemView(context);
                        }
                        if (isLeftView(message)){
                            return new LeftReferencedGifMessageChatItemView(context);
                        }
                    }

                    if (isRightView(message)){
                        return new RightReferencedImageMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedImageMessageChatItemView(context);
                    }
                }

                if(referenceMessage.mReferencingMessage instanceof AnnoImageChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedAnnoImageMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedAnnoImageMessageChatItemView(context);
                    }
                }

                if(referenceMessage.mReferencingMessage instanceof StickerChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedStickerMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedStickerMessageChatItemView(context);
                    }
                }

                if(referenceMessage.mReferencingMessage instanceof VoiceChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedVoiceMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedVoiceMessageChatItemView(context);
                    }
                }

                if(referenceMessage.mReferencingMessage instanceof AnnoFileTransferChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedAnnoFileMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedAnnoFileMessageChatItemView(context);
                    }
                }

                if(referenceMessage.mReferencingMessage instanceof FileTransferChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedFileMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedFileMessageChatItemView(context);
                    }
                }


                if(referenceMessage.mReferencingMessage instanceof ShareChatMessage) {
                    ShareChatMessage shareChatMessage = (ShareChatMessage) referenceMessage.mReferencingMessage;
                    if(ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        if (isRightView(message)){
                            return new RightReferencedBusinessCardMessageChatItemView(context);
                        }
                        if (isLeftView(message)){
                            return new LeftReferencedBusinessCardMessageChatItemView(context);
                        }
                    }


                    if(ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        if (isRightView(message)){
                            return new RightReferencedShareLinkMessageChatItemView(context);
                        }
                        if (isLeftView(message)){
                            return new LeftReferencedShareLinkMessageChatItemView(context);
                        }
                    }


                    if(ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        if (isRightView(message)){
                            return new RightReferencedLocationMessageChatItemView(context);
                        }
                        if (isLeftView(message)){
                            return new LeftReferencedLocationMessageChatItemView(context);
                        }
                    }
                }


                if(referenceMessage.mReferencingMessage instanceof MicroVideoChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedMicroVideoMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedMicroVideoMessageChatItemView(context);
                    }

                }


                if(referenceMessage.mReferencingMessage instanceof MultipartChatMessage) {
                    if (isRightView(message)){
                        return new RightReferencedMultipartMessageChatItemView(context);
                    }
                    if (isLeftView(message)){
                        return new LeftReferencedMultipartMessageChatItemView(context);
                    }

                }


            }

        }


        if(message.getChatType().equals(ChatPostMessage.ChatType.MEETING_NOTICE)) {
            MeetingNoticeChatMessage meetingNoticeChatMessage = (MeetingNoticeChatMessage) message;
            if(meetingNoticeChatMessage.isToDiscussion()) {
                if (isLeftView(message)) {
                    return new LeftMeetingNoticeChatItemView(context);
                }
                return new RightMeetingNoticeChatItemView(context);
            }


            return new MeetingNoticeItemView(context);

        }


        Logger.e("msg error", "null view? -> " + JsonUtil.toJson(message));

        return null;
    }

    public static int getMsgChatViewType(ChatPostMessage message) {
        if(message.isUndo()) {
            return UNDO;
        }

        //文件传输
        if (message.isBurn()) {
            if (isRightView(message)) {
                return RIGHT_BURN;
            }
            if (isLeftView(message)) {
                return LEFT_BURN;
            }
        }

        //文件传输
        if (message.getChatType().equals(ChatPostMessage.ChatType.File)) {
            if (isRightView(message)) {
                return RIGHT_FILE;
            }
            if (isLeftView(message)) {
                return LEFT_FILE;
            }
        }

        //文本消息
        if (message.getChatType().equals(ChatPostMessage.ChatType.Text)) {
            if (isLeftView(message)) {
                return LEFT_TEXT;
            }
            if (isRightView(message)) {
                return RIGHT_TEXT;
            }
        }

        //图片消息
        if (message.getChatType().equals(ChatPostMessage.ChatType.Image)) {
            if (isRightView(message)) {
                if(isGif(message)) {
                    return RIGHT_GIF;
                }
                return RIGHT_IMAGE;

            }
            if (isLeftView(message)) {
                if(isGif(message)) {
                    return LEFT_GIF;
                }
                return LEFT_IMAGE;
            }
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.System)) {
            return SYSTEM;
        }

        //历史记录分割线
        if (message.getChatType().equals(ChatPostMessage.ChatType.HistoryDivider)) {
            return HISTORY_DIVIDER;
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.Voice)) {
            if (isRightView(message)) {
                return RIGHT_VOICE;
            }
            if (isLeftView(message)) {
                return LEFT_VOICE;
            }
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.Article)) {
            ArticleChatMessage articleChatMessage = (ArticleChatMessage) message;
            if (articleChatMessage.articles.size() == 1) {
                return SINGLE_ARTICLES;
            }
            if (articleChatMessage.articles.size() > 1) {
                return MULTI_ARTICLES;
            }
            return SYSTEM;
        }

        //文本消息
        if (message.getChatType().equals(ChatPostMessage.ChatType.Share)) {
            ShareChatMessage shareChatMessage = (ShareChatMessage)message;
            if (isLeftView(message)) {
                if (ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                    return LEFT_SHARE_LINK;
                }

                if (ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                    return LEFT_SHARE_CARD;
                }

                if (ShareChatMessage.ShareType.OrgInviteBody.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                    return LEFT_SHARE_ORG;
                }

                if (ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return LEFT_SHARE_LOCATION;
                }

            }
            if (isRightView(message)) {
                if (ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                    return RIGHT_SHARE_LINK;
                }

                if (ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                    return RIGHT_SHARE_CARE;
                }

                if (ShareChatMessage.ShareType.OrgInviteBody.toString().equalsIgnoreCase(shareChatMessage.getShareType())){
                    return RIGHT_SHARE_ORG;
                }

                if (ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                    return RIGHT_SHARE_LOCATION;
                }
            }
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.MicroVideo)) {
            if (isLeftView(message)) {
                return LEFT_MICRO_VIDEO;
            }
            return RIGHT_MICRO_VIDEO;
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.VOIP)) {
            if (isLeftView(message)) {
                return LEFT_VOIP_MEETING;
            }
            return RIGHT_VOIP_MEETING;
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.MULTIPART)) {
            if (isLeftView(message)) {
                return LEFT_MULTIPART;
            }
            return RIGHT_MULTIPART;
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.AUDIOMEETING)) {
            ESpaceChatMessage eSpaceChatMessage = (ESpaceChatMessage)message;
            if (!eSpaceChatMessage.mIsActivity) {
                return SYSTEM;
            }
            if (isLeftView(message)) {
                return LEFT_AUDIO_CONF;
            }
            return RIGHT_AUDIO_CONF;
        }

        if (message.getChatType().equals(ChatPostMessage.ChatType.TEMPLATE)) {
            return TEMPLATE;
        }

        if(message.getChatType().equals(ChatPostMessage.ChatType.BING_TEXT)) {
            if (isLeftView(message)) {
                return LEFT_BING_TEXT;
            }
            return RIGHT_BING_TEXT;
        }

        if(message.getChatType().equals(ChatPostMessage.ChatType.BING_VOICE)) {
            if (isLeftView(message)) {
                return LEFT_BING_VOICE;
            }
            return RIGHT_BING_VOICE;
        }

        if(message.getChatType().equals(ChatPostMessage.ChatType.MEETING_NOTICE)) {
            MeetingNoticeChatMessage meetingNoticeChatMessage = (MeetingNoticeChatMessage) message;
            if(meetingNoticeChatMessage.isToDiscussion()) {
                if (isLeftView(message)) {
                    return LEFT_MEETING_NOTICE;
                }
                return RIGHT_MEETING_NOTICE;
            }


            return MEETING_NOTICE;

        }


        if(message.getChatType().equals(ChatPostMessage.ChatType.STICKER)) {
            if(isLeftView(message)) {
                return LEFT_STICKER;
            }

            return RIGHT_STICKER;
        }

        if(message.getChatType().equals(ChatPostMessage.ChatType.ANNO_FILE)) {
            if(isLeftView(message)) {
                return LEFT_FILE_ANNO;
            }

            return RIGHT_FILE_ANNO;
        }

        if(message.getChatType().equals(ChatPostMessage.ChatType.ANNO_IMAGE)) {
            if(isLeftView(message)) {
                return LEFT_IMAGE_ANNO;
            }

            return RIGHT_IMAGE_ANNO;
        }


        if(message.getChatType().equals(ChatPostMessage.ChatType.QUOTED)) {
            ReferenceMessage referenceMessage = (ReferenceMessage) message;

            if(null != referenceMessage.mReferencingMessage) {

                if(referenceMessage.mReferencingMessage instanceof TextChatMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_TEXT;
                    }

                    return RIGHT_REFERENCED_TEXT;
                }


                if(referenceMessage.mReferencingMessage instanceof ReferenceMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_REFERENCED;
                    }

                    return RIGHT_REFERENCED_REFERENCED;
                }

                if(referenceMessage.mReferencingMessage instanceof ImageChatMessage) {

                    if(isGif(referenceMessage.mReferencingMessage)) {

                        if(isLeftView(message)) {
                            return LEFT_REFERENCED_GIF;
                        }

                        return RIGHT_REFERENCED_GIF;
                    }

                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_IMAGE;
                    }

                    return RIGHT_REFERENCED_IMAGE;
                }


                if(referenceMessage.mReferencingMessage instanceof AnnoImageChatMessage) {

                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_IMAGE_ANNO;
                    }

                    return RIGHT_REFERENCED_IMAGE_ANNO;

                }

                if(referenceMessage.mReferencingMessage instanceof StickerChatMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_STICKER;
                    }

                    return RIGHT_REFERENCED_STICKER;
                }

                if(referenceMessage.mReferencingMessage instanceof VoiceChatMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_VOICE;
                    }

                    return RIGHT_REFERENCED_VOICE;
                }

                if(referenceMessage.mReferencingMessage instanceof AnnoFileTransferChatMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_FILE_ANNO;
                    }

                    return RIGHT_REFERENCED_FILE_ANNO;
                }


                if(referenceMessage.mReferencingMessage instanceof FileTransferChatMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_FILE;
                    }

                    return RIGHT_REFERENCED_FILE;
                }


                if(referenceMessage.mReferencingMessage instanceof ShareChatMessage) {
                    ShareChatMessage shareChatMessage = (ShareChatMessage) referenceMessage.mReferencingMessage;
                    if(ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        if(isLeftView(message)) {
                            return LEFT_REFERENCED_BUSINESS_CARD;
                        }

                        return RIGHT_REFERENCED_BUSINESS_CARD;
                    }


                    if(ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        if(isLeftView(message)) {
                            return LEFT_REFERENCED_SHARE_LINK;
                        }

                        return RIGHT_REFERENCED_SHARE_LINK;
                    }


                    if(ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                        if(isLeftView(message)) {
                            return LEFT_REFERENCED_LOCATION;
                        }

                        return RIGHT_REFERENCED_LOCATION;
                    }


                }


                if(referenceMessage.mReferencingMessage instanceof MicroVideoChatMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_MICRO_VIDEO;
                    }

                    return RIGHT_REFERENCED_MICRO_VIDEO;
                }


                if(referenceMessage.mReferencingMessage instanceof MultipartChatMessage) {
                    if(isLeftView(message)) {
                        return LEFT_REFERENCED_MULTIPART;
                    }

                    return RIGHT_REFERENCED_MULTIPART;
                }
            }

        }

        return -1;
    }
    private static boolean isGif(ChatPostMessage chatPostMessage) {
        if(chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            return imageChatMessage.isGif;
        }
        return false;
    }

    public static boolean isLeftView(ChatPostMessage message){
        return ChatMessageHelper.isReceiver(message);
    }

    public static boolean isRightView(ChatPostMessage message){
        return ChatMessageHelper.isSender(message);

    }

}
