package com.foreveross.atwork.modules.chat.util;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.translate.VoiceTranslateSdkType;
import com.foreveross.atwork.infrastructure.model.translate.VoiceTranslateStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;

/**
 * Created by wuzejie on 2019/9/24
 */

public class VoiceTranslateHelper {

    /**
     * 描述：设置翻译（正在翻译）->setTranslateStatus（）设置翻译的状态，并更新UI
     * * */
    public static void setTranslating(VoiceChatMessage voiceChatMessage, boolean isTranslating) {
        boolean visible;
        if(isTranslating) {
            visible = true;
        } else {
            visible = false;
        }
        setTranslateStatus(voiceChatMessage, isTranslating, visible, null);
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();//更新消息的UI

    }


    /**
     * 设置翻译信息
     * @param visible：是否显示
     * @param isTranslating：是否正在翻译
     * @param result：翻译结果
     * */
    private static void setTranslateStatus(VoiceChatMessage voiceChatMessage, boolean isTranslating, boolean visible, String result) {
        if(null == voiceChatMessage.mVoiceTranslateStatus) {
            voiceChatMessage.mVoiceTranslateStatus = new VoiceTranslateStatus();
            voiceChatMessage.mVoiceTranslateStatus.mTranslateSdk =  VoiceTranslateSdkType.XUNFEI;

        }

        voiceChatMessage.mVoiceTranslateStatus.mVisible = visible;
        voiceChatMessage.mVoiceTranslateStatus.mTranslating = isTranslating;
        voiceChatMessage.mVoiceTranslateStatus.mResult = result;

    }

    private boolean isVoiceTranslateMode(ChatPostMessage chatMessage) {
        return chatMessage instanceof VoiceChatMessage;
    }

    /**
     * 获取翻译结果
     * @param chatMessage：消息体
     * */
    public void  handleResultText(ChatPostMessage chatMessage, Context context) {
        if (isVoiceTranslateMode(chatMessage)) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) chatMessage;
            //如果之前已经有翻译了
            if (voiceChatMessage.hasTranslatedBefore()) {

                showTranslatedResult(voiceChatMessage.getTranslatedResult(),voiceChatMessage);//显示翻译信息


            } else {
                //设置当前的翻译语言
//                if (LanguageUtil.isZhLocal(context)) {
//                    mVoiceTranslateTarget = "zh_cn";
//
//                } else {
//                    mVoiceTranslateTarget = "en_us";
//                }

                startVoiceTranslate();

            }

        } else {


        }
    }
    /**
     * 显示翻译信息
     * @param result：翻译结果
     * */
    private void showTranslatedResult(String result,VoiceChatMessage voiceChatMessage) {
        //mTvResult.setText(result);设置UI

    }
    /**
     * 开始翻译信息
     * */
    private void startVoiceTranslate() {

    }


}
