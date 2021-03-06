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


    //======POP VIEW ITEM??????=========
    //??????
    public static String COPY_ITEM;
    //????????????
    public static String VOICE_PHONE;
    //???????????????
    public static String VOICE_SPEAK;
    //?????????????????????
    public static String VOICE_TRANSLATE;
    //???????????????????????????????????????
    public static String VOICE_SHOW_ORIGINAL;
    //??????
    public static String FORWARDING_ITEM ;
    //??????
    public static String SHARE_DROPBOX;
    //????????????
    public static String TEXT_TRANSLATE;
    //??????????????????????????????
    public static String TEXT_SHOW_ORIGINAL;
    //??????????????????
    public static String CHECK_UNREAD_READ ;
    //????????????
    public static String MESSAGE_REFERENCE;
    //??????
    public static String USER_UN_READ;
    //??????
    public static String USER_READ;
    //????????????
    public static String CHAT_UNDO;
    //??????
    public static String DELETE_ITEM;
    //??????
    public static String MORE_ITEM ;

    //??????
    public static String RESEND ;

    //????????????, ????????????????????????
    public static String DEBUG_TEST_CLONE_MESSAGE;

    //????????????, ???????????????????????????
    public static String DEBUG_TEST_QUERY_MESSAGE_COUNT;

    public static String SAVE_TO_DOCS;
    //======POP VIEW ITEM????????????=====

    static {
        refreshViewItemText();
    }


    public static void refreshViewItemText() {
        //??????
        COPY_ITEM = AtworkApplicationLike.getResourceString(R.string.copy_item);
        //????????????
        VOICE_PHONE = AtworkApplicationLike.getResourceString(R.string.voice_phone);
        //???????????????
        VOICE_SPEAK = AtworkApplicationLike.getResourceString(R.string.voice_speak);
        //?????????????????????
        VOICE_TRANSLATE = AtworkApplicationLike.getResourceString(R.string.voice_translate);
        //???????????????????????????????????????
        VOICE_SHOW_ORIGINAL = AtworkApplicationLike.getResourceString(R.string.minimize);
        //??????
        FORWARDING_ITEM = AtworkApplicationLike.getResourceString(R.string.forwarding_item);
        //??????
        SHARE_DROPBOX = AtworkApplicationLike.getResourceString(R.string.share);
        //????????????
        TEXT_TRANSLATE = AtworkApplicationLike.getResourceString(R.string.text_translate);
        //??????????????????????????????
        TEXT_SHOW_ORIGINAL = AtworkApplicationLike.getResourceString(R.string.text_show_original);
        //??????????????????
        CHECK_UNREAD_READ = AtworkApplicationLike.getResourceString(R.string.check_unread_read);
        //????????????
        MESSAGE_REFERENCE = AtworkApplicationLike.getResourceString(R.string.message_quote);
        //??????
        USER_UN_READ = AtworkApplicationLike.getResourceString(R.string.user_un_read);
        //??????
        USER_READ = AtworkApplicationLike.getResourceString(R.string.user_read);
        //????????????
        CHAT_UNDO = AtworkApplicationLike.getResourceString(R.string.undo);
        //??????
        DELETE_ITEM = AtworkApplicationLike.getResourceString(R.string.delete_item);
        //??????
        MORE_ITEM = AtworkApplicationLike.getResourceString(R.string.more_item);
        //??????
        RESEND = AtworkApplicationLike.getResourceString(R.string.resend);

        SAVE_TO_DOCS = AtworkApplicationLike.getResourceString(R.string.save_to_doc_center);

        DEBUG_TEST_CLONE_MESSAGE = "??????10000?????????";
        DEBUG_TEST_QUERY_MESSAGE_COUNT = "??????????????????????????????";

    }

    /**
     * ??????????????????
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

            //????????????????????? , ??????, ???????????? ?????????
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
     * Description:1.????????????????????????2.????????????3.??????????????????????????????????????????????????????????????????????????????
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

            //??????????????????????????????? ????????????
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

            //????????????????????????, ???????????????????????????"?????????"??????
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
     * Description????????????????????????
     * @param chatPostMessage
     * @return
     */
    private static boolean isEnableResend(ChatPostMessage chatPostMessage) {
        if(User.isYou(BaseApplicationLike.baseContext, chatPostMessage.from)){
            return true;
        }
        return false;

    }

    /**Description:???????????????????????????
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


        //????????????????????? , ??????, ????????????????????????
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
     * "??????"??????????????????????????????, ????????????????????????, ????????????
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

        //???????????????????????????????????????????????????????????????
        return chatPostMessage instanceof VoiceChatMessage && !PersonalShareInfo.getInstance().isAudioPlaySpeakerMode(context);
    }

    private static boolean isAudioVoiceSpeakerMode(Context context, final ChatPostMessage chatPostMessage) {
        if (FileTransferService.INSTANCE.needVariation(chatPostMessage)) {
            return false;
        }

        //???????????????????????????????????????????????????????????????
        return chatPostMessage instanceof VoiceChatMessage && PersonalShareInfo.getInstance().isAudioPlaySpeakerMode(context);
    }

}
