package com.foreveross.atwork.modules.voip.support.agora.interfaces;

import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.PhoneState;

/**
 * Created by quanshi on 2015/12/7.
 */
public interface OnVoipStatusListener {
    /**
     * 通知用户列表刷新
     */
    void onUsersProfileRefresh();

    /**
     * toast提醒
     * */
    void onTipToast(String tip);

    /**
     * 通知免提状态变化
     */
    void onLoudSpeakerStatusChanged(boolean bLoudSpeaker);

    /**
     * 通知说话人变化
     */
    void onIsSpeakingChanged(String strSpeakingNames);

    /**
     * 通知视频实例列表变化
     */
    void onVideoItemAdded(String userId);

    void onVideoItemDeleted(String userId);

    /**
     * 通知Attach Surface attach, 返回的Surface必须是New出来的，不然无法显示
     */
    Object onVideoItemAttachSurface(String userId);

    /**
     * 通知Attach Surface detach
     */
    void onVideoItemDetachSurface(String userId, Object videoSurface);

    /**
     * 通知视频首帧显示
     */
    void onVideoItemShowed(String userId, String domainId);

    /**
     * 通知视频呼叫结束了
     */
    void onVideoCallClosed();

    /**
     * 通知呼叫状态变化
     */
    void onCallStateChanged(CallState callState);

    /**
     * 通知呼叫时间状态变化
     */
    void onCallingTimeElpased(long nSeconds);

    /**
     * 通知VOIP质量变化
     */
    void onVOIPQualityIsBad();

    /**
     * 通知电话呼叫结果通知
     */
    void onPhoneCallResult(boolean bSucceeded);

    /**
     * 通知电话呼叫状态变化
     */
    void onPhoneStateChanged(PhoneState phoneState);


}
