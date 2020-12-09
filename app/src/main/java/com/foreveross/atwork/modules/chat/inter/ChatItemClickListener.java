package com.foreveross.atwork.modules.chat.inter;

import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageContentInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;

/**
 * Created by lingen on 15/4/24.
 */
public interface ChatItemClickListener {


    void selectClick(ChatPostMessage message);

    /**
     * 点击文本触发的时间
     */
    void textClick(TextChatMessage textChatMessage, String mViewDoubleClickTag);


    /**
     * 点击图片触发的事件
     */
    void imageClick(ChatPostMessage imageChatMessage);

    void stickerClick(ChatPostMessage stickerMesage);

    /**
     * 点击文件触发的事件
     */
    void fileClick(FileTransferChatMessage fileTransferChatMessage);


    void avatarClick(String identifier, String domainId);

    /**
     * 长按头像触发的事件
     * */
    void avatarLongClick(String identifier, String domainId);

    void hideAll();

    void microVideoClick(MicroVideoChatMessage microVideoChatMessage);

    /**
     * 单击语音会议消息
     * */
    void voipClick(VoipChatMessage voipChatMessage);


    /**
     * 点击引用消息
     */
    void referenceClick(ReferenceMessage referenceMessage);


    /**
     * 点击 anno image
     */
    void annoImageClick(AnnoImageChatMessage annoImageChatMessage, ImageContentInfo targetImageContentInfo);

    /**
     * 阅后即焚点击事件
     * */
    void burnClick(ChatPostMessage chatPostMessage);

    /**
     * （右边的）合并消息点击事件
     **/
    void RightMultipartClick(MultipartChatMessage multipartChatMessage);
    /**
     * （左边的）合并消息点击事件
     **/
    void LeftMultipartClick(MultipartChatMessage multipartChatMessage);

    /**
     * 语音会议(umeeting/qsy)点击事件
     * */
    void meetingClick(MeetingNoticeChatMessage meetingNoticeChatMessage);


    void reEditUndoClick(ChatPostMessage chatPostMessage);

    /**
     * 位置信息
     */
    void locationClick(ShareChatMessage shareChatMessage);

}
