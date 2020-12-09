package com.foreveross.atwork.modules.chat.util;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.translate.TextTranslateSdkType;
import com.foreveross.atwork.infrastructure.model.translate.TextTranslateStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;

/**
 * Created by dasunsy on 2017/6/5.
 */

public class TextTranslateHelper {

    /**
     * Description:更新文本消息状态并且同步至数据库
     * */
    public static void updateTranslateResultAndUpdateDb(TextChatMessage textChatMessage, String result) {
        updateTranslateResultAndUpdateDb(textChatMessage, result, "");
    }

    public static void updateTranslateResultAndUpdateDb(TextChatMessage textChatMessage, String result, String language) {
        updateTranslateResultAndUpdateDb(textChatMessage, result, language, false);
    }

    public static void updateTranslateResultAndUpdateDb(TextChatMessage textChatMessage, String result, String language, boolean isFromMultipartMessage) {
        setTranslateStatus(textChatMessage, false, true, result, language);
        if(isFromMultipartMessage){
            ChatDetailExposeBroadcastSender.refreshMultipartMessageViewUI();
        }else{
            ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, textChatMessage);
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        }

    }

    public static void showTranslateStatusAndUpdateDb(TextChatMessage textChatMessage, boolean visible) {
        showTranslateStatusAndUpdateDb(textChatMessage, visible, false);
    }
    public static void showTranslateStatusAndUpdateDb(TextChatMessage textChatMessage, boolean visible, boolean isFromMultipartMessage) {
        textChatMessage.showTranslateStatus(visible);
        if(isFromMultipartMessage){
            ChatDetailExposeBroadcastSender.refreshMultipartMessageViewUI();
        }else {
            ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, textChatMessage);
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        }

    }

    public static void setTranslating(TextChatMessage textChatMessage, boolean isTranslating) {
        setTranslating(textChatMessage, isTranslating, false);
    }

    public static void setTranslating(TextChatMessage textChatMessage, boolean isTranslating, boolean isFromMultipartMessage) {
        boolean visible;
        if(isTranslating) {
            visible = true;
        } else {
            visible = false;
        }
        setTranslateStatus(textChatMessage, isTranslating, visible, null);
        if(isFromMultipartMessage){
            ChatDetailExposeBroadcastSender.refreshMultipartMessageViewUI();
        }else{
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        }
    }

    public static String getSource(TextChatMessage textChatMessage) {
        if(null != textChatMessage.mTextTranslate) {

            if(TextTranslateSdkType.YOUDAO == textChatMessage.mTextTranslate.mTranslateSdk) {
                return AtworkApplicationLike.getResourceString(R.string.source_youdao);
            }

            if(TextTranslateSdkType.GOOGLE == textChatMessage.mTextTranslate.mTranslateSdk) {
                return AtworkApplicationLike.getResourceString(R.string.source_google);
            }

            if(!StringUtils.isEmpty(textChatMessage.mTextTranslate.mTranslateSdkLabel)){
                return textChatMessage.mTextTranslate.mTranslateSdkLabel;
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * Description:设置翻译信息
     * @param isTranslating
     * @param result
     * */
    private static void setTranslateStatus(TextChatMessage textChatMessage, boolean isTranslating, boolean visible, String result) {
        setTranslateStatus(textChatMessage, isTranslating, visible, result, "");
    }

    public static void setTranslateStatus(TextChatMessage textChatMessage, boolean isTranslating, boolean visible, String result, String language) {
        if(null == textChatMessage.mTextTranslate) {
            textChatMessage.mTextTranslate = new TextTranslateStatus();
            textChatMessage.mTextTranslate.mTranslateSdk = AtworkConfig.TEXT_TRANSLATE_SDK.getSdkType();
            if(TextTranslateSdkType.YOUDAO == textChatMessage.mTextTranslate.mTranslateSdk) {
                textChatMessage.mTextTranslate.mTranslateSdkLabel = AtworkApplicationLike.getResourceString(R.string.source_youdao);

            } else if(TextTranslateSdkType.GOOGLE == textChatMessage.mTextTranslate.mTranslateSdk) {
                textChatMessage.mTextTranslate.mTranslateSdkLabel = AtworkApplicationLike.getResourceString(R.string.source_google);

            }
        }

        textChatMessage.mTextTranslate.mVisible = visible;
        textChatMessage.mTextTranslate.mTranslating = isTranslating;
        textChatMessage.mTextTranslate.mResult = result;
        textChatMessage.mTextTranslate.mTranslationLanguage = language;

    }
}
