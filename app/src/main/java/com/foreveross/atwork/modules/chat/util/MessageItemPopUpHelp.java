package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.file.service.FileTransferService;

import java.util.ArrayList;
import java.util.List;


public class MessageItemPopUpHelp {


    //======POP VIEW ITEM定义=========
    //复制
    public static String COPY_ITEM;
    //使用听筒
    public static String VOICE_PHONE;
    //使用杨声器
    public static String VOICE_SPEAK;
    //语音转换成文字
    public static String VOICE_TRANSLATE;
    //语音已转换成的文字收起文字
    public static String VOICE_SHOW_ORIGINAL;
    //转发
    public static String FORWARDING_ITEM ;
    //分享
    public static String SHARE_DROPBOX;
    //文字翻译
    public static String TEXT_TRANSLATE;
    //已翻译的文字显示原文
    public static String TEXT_SHOW_ORIGINAL;
    //查看未读已读
    public static String CHECK_UNREAD_READ ;
    //引用回复
    public static String MESSAGE_REFERENCE;
    //未读
    public static String USER_UN_READ;
    //已读
    public static String USER_READ;
    //撤回消息
    public static String CHAT_UNDO;
    //删除
    public static String DELETE_ITEM;
    //更多
    public static String MORE_ITEM ;

    //重发
    public static String RESEND ;

    //调试使用, 克隆一定数量消息
    public static String DEBUG_TEST_CLONE_MESSAGE;

    //调试使用, 当前会话消息总数量
    public static String DEBUG_TEST_QUERY_MESSAGE_COUNT;

    public static String SAVE_TO_DOCS;
    //======POP VIEW ITEM定义结束=====

    static {
        refreshViewItemText();
    }


    public static void refreshViewItemText() {
        //复制
        COPY_ITEM = AtworkApplicationLike.getResourceString(R.string.copy_item);
        //使用听筒
        VOICE_PHONE = AtworkApplicationLike.getResourceString(R.string.voice_phone);
        //使用杨声器
        VOICE_SPEAK = AtworkApplicationLike.getResourceString(R.string.voice_speak);
        //语音转换成文字
        VOICE_TRANSLATE = AtworkApplicationLike.getResourceString(R.string.voice_translate);
        //已转换成文字的语音收起文字
        VOICE_SHOW_ORIGINAL = AtworkApplicationLike.getResourceString(R.string.minimize);
        //转发
        FORWARDING_ITEM = AtworkApplicationLike.getResourceString(R.string.forwarding_item);
        //分享
        SHARE_DROPBOX = AtworkApplicationLike.getResourceString(R.string.share);
        //文字翻译
        TEXT_TRANSLATE = AtworkApplicationLike.getResourceString(R.string.text_translate);
        //已翻译的文字显示原文
        TEXT_SHOW_ORIGINAL = AtworkApplicationLike.getResourceString(R.string.text_show_original);
        //查看未读已读
        CHECK_UNREAD_READ = AtworkApplicationLike.getResourceString(R.string.check_unread_read);
        //回复引用
        MESSAGE_REFERENCE = AtworkApplicationLike.getResourceString(R.string.message_quote);
        //未读
        USER_UN_READ = AtworkApplicationLike.getResourceString(R.string.user_un_read);
        //已读
        USER_READ = AtworkApplicationLike.getResourceString(R.string.user_read);
        //撤回消息
        CHAT_UNDO = AtworkApplicationLike.getResourceString(R.string.undo);
        //删除
        DELETE_ITEM = AtworkApplicationLike.getResourceString(R.string.delete_item);
        //更多
        MORE_ITEM = AtworkApplicationLike.getResourceString(R.string.more_item);
        //重发
        RESEND = AtworkApplicationLike.getResourceString(R.string.resend);

        SAVE_TO_DOCS = AtworkApplicationLike.getResourceString(R.string.save_to_doc_center);

        DEBUG_TEST_CLONE_MESSAGE = "克隆10000条消息";
        DEBUG_TEST_QUERY_MESSAGE_COUNT = "查询当前会话消息总数";

    }

    /**
     * 单聊中的弹出
     *
     * @param chatPostMessage
     * @return
     */
    public static String[] getP2PPopItems(Context context, final ChatPostMessage chatPostMessage, final ReceiptMessage receiptMessage, ChatDetailFragment.ChatModel chatMode) {
        List<String> values = new ArrayList<>();

        if (!chatPostMessage.isBurn()) {
            if (isAudioVoiceEarphoneMode(context, chatPostMessage)) {
                values.add(VOICE_SPEAK);
            }

            if (isAudioVoiceSpeakerMode(context, chatPostMessage)) {
                values.add(VOICE_PHONE);
            }



            if (hasCopyItem(chatPostMessage)) {
                values.add(COPY_ITEM);
            }

            //如果是语音消息 , 名片, 组织要去 不转发
            if (canForward(chatPostMessage, true, chatMode == ChatDetailFragment.ChatModel.SELECT)) {
                values.add(FORWARDING_ITEM);

            }

            if (isCanDropboxShare(chatPostMessage)) {
                values.add(SHARE_DROPBOX);
            }

            if (hasRead(chatPostMessage, receiptMessage)) {
                values.add(USER_READ);
            }

            if (hasUnRead(chatPostMessage, receiptMessage)) {
                values.add(USER_UN_READ);
            }

            if (ReferenceMessage.supportReference(chatPostMessage)) {
                values.add(MESSAGE_REFERENCE);
            }

            addVoiceTranslate(chatPostMessage, values);

            addTextTranslate(chatPostMessage, values);


        }





        if (isEnableUndo(chatPostMessage)) {
            values.add(CHAT_UNDO);

        }


        values.add(DELETE_ITEM);

        values.add(MORE_ITEM);


        if(AtworkConfig.TEST_MODE_CONFIG.isTestMode()) {
            values.add(DEBUG_TEST_CLONE_MESSAGE);
            values.add(DEBUG_TEST_QUERY_MESSAGE_COUNT);
        }

        String[] items = values.toArray(new String[values.size()]);
        return items;
    }

    private static boolean isEnableUndo(ChatPostMessage chatPostMessage) {
        if (FileTransferService.INSTANCE.needVariation(chatPostMessage)) {
            return false;
        }

        return ChatSendType.SENDER.equals(chatPostMessage.chatSendType)
                && ChatStatus.Sended.equals(chatPostMessage.chatStatus);
    }

    private static void addVoiceTranslate(ChatPostMessage chatPostMessage, List<String> values) {
        if(AtworkConfig.openVoiceTranslate() && chatPostMessage instanceof VoiceChatMessage) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) chatPostMessage;
            if(!StringUtils.isEmpty(voiceChatMessage.getTranslatedResult()) && voiceChatMessage.isTranslateStatusVisible()){
                values.add(VOICE_SHOW_ORIGINAL);
            }else{
                values.add(VOICE_TRANSLATE);
            }

        }
    }

    /**
     * Description:1.设置中允许翻译；2.是文本；3.（翻译结果为空或者没有显示翻译；显示翻译；修改为：）
     * @param chatPostMessage
     * @param values
     */
    private static void addTextTranslate(ChatPostMessage chatPostMessage, List<String> values) {
        if(AtworkConfig.openTextTranslate() && chatPostMessage instanceof TextChatMessage) {
            TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;
            if(!StringUtils.isEmpty(textChatMessage.getTranslatedResult())
                    && textChatMessage.isTranslateStatusVisible()) {

                values.add(TEXT_SHOW_ORIGINAL);
                values.add(TEXT_TRANSLATE);
            } else {
                values.add(TEXT_TRANSLATE);

            }
        }
    }

    public static boolean canForward(ChatPostMessage chatPostMessage, boolean isItemByItem, boolean isSelectMode) {
        boolean canForward = true;
        if(chatPostMessage.isBurn()) {
            return false;
        }

        
        if (chatPostMessage instanceof VoiceChatMessage && isSelectMode && isItemByItem) {
            return false;
        }
        if (chatPostMessage instanceof ShareChatMessage) {
            ShareChatMessage shareChatMessage = (ShareChatMessage) chatPostMessage;
            if (ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                return false;
            }
        }
        if (chatPostMessage instanceof VoipChatMessage) {
            return false;
        }

        if(chatPostMessage instanceof ImageChatMessage) {
            if(chatPostMessage.notSent()) {
                return false;
            }

            if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return false;
            }
        }


        if(chatPostMessage instanceof AnnoImageChatMessage) {
            if(chatPostMessage.notSent()) {
                return false;
            }

            if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return false;
            }
        }

        if(chatPostMessage instanceof MicroVideoChatMessage) {
            if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return false;
            }
        }

        if (chatPostMessage instanceof FileTransferChatMessage) {

            if(chatPostMessage.notSent()) {
                return false;
            }

            if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return false;
            }

            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage)chatPostMessage;
            return !TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), fileTransferChatMessage.expiredTime);
        }

        if(chatPostMessage instanceof MultipartChatMessage) {

            if(((MultipartChatMessage)chatPostMessage).hasMedias()) {

                if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                    return false;
                }
            }

            //引用的合并消息又可以? 是否矛盾
            if(null != chatPostMessage.parentReferenceMessage) {
                return true;
            }

            if(!isItemByItem) {
                return false;
            }
        }

        if(chatPostMessage instanceof MeetingNoticeChatMessage) {
            return false;
        }

        if(chatPostMessage instanceof ReferenceMessage) {
            ReferenceMessage referenceMessage = (ReferenceMessage) chatPostMessage;

            //引用消息需要递归, 直到被引用的消息是"非引用"类型
            return canForward(referenceMessage.mReferencingMessage, isItemByItem, isSelectMode);
        }


        return canForward;
    }

    public static boolean isCanDropboxShare(ChatPostMessage chatPostMessage) {
        if(chatPostMessage instanceof ImageChatMessage) {
            if(chatPostMessage.notSent()) {
                return false;
            }

            if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return false;
            }
            return true;
        }

        if(chatPostMessage instanceof MicroVideoChatMessage) {
            if(chatPostMessage.notSent()) {
                return false;
            }
            if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return false;
            }
            return true;
        }

        if (chatPostMessage instanceof FileTransferChatMessage) {

            if(chatPostMessage.notSent()) {
                return false;
            }

            if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return false;
            }

            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage)chatPostMessage;
            return !TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), fileTransferChatMessage.expiredTime);
        }

        return false;
    }


    /**
     * Description：判断是否能重发
     * @param chatPostMessage
     * @return
     */
    private static boolean isEnableResend(ChatPostMessage chatPostMessage) {
        if(User.isYou(BaseApplicationLike.baseContext, chatPostMessage.from)){
            return true;
        }
        return false;

    }

    /**Description:判断是否是投票链接
     * @param chatPostMessage
     * @return
     */
    public static boolean isVoteLink(ChatPostMessage chatPostMessage) {
        if (chatPostMessage instanceof ShareChatMessage) {
            ShareChatMessage shareChatMessage = (ShareChatMessage) chatPostMessage;
            return shareChatMessage.isVoteUrl();
        }


        return false;
    }


    public static String[] getDiscussionPopItems(Context context, Discussion discussion, final ChatPostMessage chatPostMessage, ChatDetailFragment.ChatModel chatModel) {
        List<String> values = new ArrayList<>();
       if(isVoteLink(chatPostMessage)){
//            if (isEnableResend(chatPostMessage)) {
//                values.add(RESEND);
//            }
           values.add(RESEND);

            values.add(DELETE_ITEM);

            if (isEnableUndo(chatPostMessage)) {
                values.add(CHAT_UNDO);
            }

            String[] items = values.toArray(new String[values.size()]);
            return items;

        }

        if (isAudioVoiceEarphoneMode(context, chatPostMessage)) {
            values.add(VOICE_SPEAK);
        }

        if (isAudioVoiceSpeakerMode(context, chatPostMessage)) {
            values.add(VOICE_PHONE);
        }


        if (hasCopyItem(chatPostMessage)) {
            values.add(COPY_ITEM);
        }


        //如果是语音消息 , 名片, 组织要要去除转发
        if (canForward(chatPostMessage, true, chatModel == ChatDetailFragment.ChatModel.SELECT)) {
            values.add(FORWARDING_ITEM);
        }

        if (isCanDropboxShare(chatPostMessage)) {
            values.add(SHARE_DROPBOX);
        }

        if (isEnableUndo(chatPostMessage)) {
            values.add(CHAT_UNDO);
        }

        if (isEnableDiscussionReadUnread(chatPostMessage)) {
            values.add(CHECK_UNREAD_READ);
        }

        if (ReferenceMessage.supportReference(chatPostMessage)) {
            values.add(MESSAGE_REFERENCE);
        }

        addVoiceTranslate(chatPostMessage, values);

        addTextTranslate(chatPostMessage, values);





        values.add(DELETE_ITEM);


        values.add(MORE_ITEM);


        if(AtworkConfig.TEST_MODE_CONFIG.isTestMode()) {
            values.add(DEBUG_TEST_CLONE_MESSAGE);
            values.add(DEBUG_TEST_QUERY_MESSAGE_COUNT);
        }

        String[] items = values.toArray(new String[values.size()]);
        return items;
    }

    public static String[] getMultipartMessagePopupItems() {
        List<String> values = new ArrayList<>();
        values.add(DELETE_ITEM);
        return values.toArray(new String[values.size()]);
    }

    private static boolean isEnableDiscussionReadUnread(ChatPostMessage chatPostMessage) {
        if (FileTransferService.INSTANCE.needVariation(chatPostMessage)) {
            return false;
        }


        if(3 * 24 * 60 * 60 * 1000L < TimeUtil.getCurrentTimeInMillis() - chatPostMessage.deliveryTime) {
            return false;
        }

        return ChatSendType.SENDER.equals(chatPostMessage.chatSendType) && ChatStatus.Sended.equals(chatPostMessage.chatStatus);
    }

    /**
     * "虚拟"出来的消息的弹出选型, 例如语音会议消息, 必应消息
     * */
    public static String[] getVirtualMsgPopItems() {
        List<String> values = new ArrayList<>();

        values.add(DELETE_ITEM);
        values.add(MORE_ITEM);

        String[] items = values.toArray(new String[values.size()]);

        return items;
    }

    public static String[] getDocPopItem() {
        List<String> values = new ArrayList<>();

        values.add(FORWARDING_ITEM);
        values.add(DELETE_ITEM);
        values.add(MORE_ITEM);

        String[] items = values.toArray(new String[values.size()]);

        return items;
    }


    private static void appendStyle(SpannableString spannableString) {
        int begin = spannableString.toString().indexOf("(");
        int end = spannableString.toString().indexOf(")") + 1;
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), begin, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), begin, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    private static boolean hasRead(final ChatPostMessage chatPostMessage, final ReceiptMessage receiptMessage) {
        if (FileTransferService.INSTANCE.needVariation(chatPostMessage)) {
            return false;
        }

        return receiptMessage != null && chatPostMessage.chatSendType.equals(ChatSendType.SENDER) && ChatStatus.Sended.equals(chatPostMessage.chatStatus);
    }

    private static boolean hasUnRead(final ChatPostMessage chatPostMessage, final ReceiptMessage receiptMessage) {
        if (FileTransferService.INSTANCE.needVariation(chatPostMessage)) {
            return false;
        }

        return receiptMessage == null && chatPostMessage.chatSendType.equals(ChatSendType.SENDER) && ChatStatus.Sended.equals(chatPostMessage.chatStatus);
    }

    private static boolean hasCopyItem(final ChatPostMessage chatPostMessage) {
        return chatPostMessage instanceof TextChatMessage;
    }

    private static boolean commonSaveFileCheck(final ChatPostMessage chatPostMessage) {
        if (chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage message = (FileTransferChatMessage) chatPostMessage;
            return !(FileStatus.SEND_CANCEL.equals(message.fileStatus) || FileStatus.SEND_FAIL.equals(message.fileStatus) ||
                    FileStatus.SENDING.equals(message.fileStatus) || TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), message.expiredTime));
        }
        return false;
    }



    public static boolean canFavorite(ChatPostMessage chatPostMessage, ChatDetailFragment.ChatModel chatModel) {

        if (ChatStatus.Not_Send.equals(chatPostMessage.chatStatus) || ChatStatus.Sending.equals(chatPostMessage.chatStatus)) {
            return false;
        }
        if (ChatDetailFragment.ChatModel.COMMON.equals(chatModel) && chatPostMessage instanceof StickerChatMessage) {
            return false;
        }
        if (ChatDetailFragment.ChatModel.SELECT.equals(chatModel) && chatPostMessage instanceof MultipartChatMessage) {
            return false;
        }
        if (chatPostMessage.isBurn()
                || chatPostMessage instanceof VoipChatMessage
                || chatPostMessage instanceof MeetingNoticeChatMessage
        ) {
            return false;
        }


        if (chatPostMessage instanceof FileTransferChatMessage) {
            if ( ((FileTransferChatMessage)chatPostMessage).expiredTime == -1) {
                return true;
            }
            return !TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), ((FileTransferChatMessage)chatPostMessage).expiredTime);
        }
        if (chatPostMessage instanceof ShareChatMessage) {
            if (ChatDetailFragment.ChatModel.COMMON.equals(chatModel) && ShareChatMessage.ShareType.OrgInviteBody.toString().equalsIgnoreCase(((ShareChatMessage)chatPostMessage).getShareType())) {
                return false;
            }
            return !ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(((ShareChatMessage)chatPostMessage).getShareType());
        }

        return true;
    }

    private static boolean isAudioVoiceEarphoneMode(Context context, final ChatPostMessage chatPostMessage) {
        if (FileTransferService.INSTANCE.needVariation(chatPostMessage)) {
            return false;
        }

        //如果是在听筒模式下对话框就显示使用扬声器项
        return chatPostMessage instanceof VoiceChatMessage && !PersonalShareInfo.getInstance().isAudioPlaySpeakerMode(context);
    }

    private static boolean isAudioVoiceSpeakerMode(Context context, final ChatPostMessage chatPostMessage) {
        if (FileTransferService.INSTANCE.needVariation(chatPostMessage)) {
            return false;
        }

        //如果是在扬声器模式下对话框就显示使用听筒项
        return chatPostMessage instanceof VoiceChatMessage && PersonalShareInfo.getInstance().isAudioPlaySpeakerMode(context);
    }

}
