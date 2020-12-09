package com.foreveross.atwork.modules.chat.inter;

/**
 * Created by lingen on 15/3/27.
 * Description:
 */
public interface ChatDetailInputListener {

    /**
     * 打开更多功能窗口
     */
    void moreFunctionShow();


    void moreFunctionHidden();


    void voiceShow();

    /**
     * 显示表情输入框
     */
    void emoticonsShow();

    /**
     * 弹出输入键盘
     * */
    void inputShow();


    /**
     * 回调系统发出的action_cancel事件
     */
    void onSystemCancel();

    /**
     * 录音
     */
    void record();

    /**
     * 录音结束
     */
    void recordEnd();

    void recordReadyCancel();

    void recordCancel();

    /**
     * 遇到意外需要及时杀掉录音
     * */
    void recordKill();

    boolean shouldForcedShowSend();

    void handleForcedSend();

    /**
     * 发送文本消息
     */
    void sendText(String text);


    void inputAt();

    void recordNotCancel();

    void voiceMode();

    /**
     * 阅后即焚模式
     * */
    void clickLeftConnerFunView();

    boolean getRecordViewStatus();

    void onEditTextEmpty();

    void onTyping();

}
