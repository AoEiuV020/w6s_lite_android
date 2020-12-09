package com.foreveross.atwork.modules.chat.inter;

import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;

/**
 * Created by lingen on 15/5/7.
 * Description:
 */
public interface ChatItemLongClickListener {

    void fileLongClick(FileTransferChatMessage fileTransferChatMessage, AnchorInfo anchorInfo);

    void textLongClick(ChatPostMessage textChatMessage, AnchorInfo anchorInfo);

    void stickerLongClick(StickerChatMessage stickerMessage, AnchorInfo anchorInfo);

    void imageLongClick(ChatPostMessage imageChatMessage, AnchorInfo anchorInfo);

    void annoImageLongClick(ChatPostMessage imageChatMessage, AnchorInfo anchorInfo);

    void voiceLongClick(VoiceChatMessage voiceChatMessage, AnchorInfo anchorInfo);

    void shareLongClick(ShareChatMessage shareChatMessage, AnchorInfo anchorInfo);

    void microVideoLongClick(MicroVideoChatMessage microVideoChatMessage, AnchorInfo anchorInfo);

    void voipLongClick(VoipChatMessage voipChatMessage, AnchorInfo anchorInfo);

    void referenceLongClick(ReferenceMessage referenceMessage, AnchorInfo anchorInfo);

    void burnLongClick(ChatPostMessage chatPostMessage, AnchorInfo anchorInfo);

    void multipartLongClick(MultipartChatMessage multipartChatMessage, AnchorInfo anchorInfo);

    void meetingLongClick(MeetingNoticeChatMessage meetingNoticeChatMessage, AnchorInfo anchorInfo);

    void showDeleteLongClick(ChatPostMessage chatPostMessage, AnchorInfo anchorInfo);

}
