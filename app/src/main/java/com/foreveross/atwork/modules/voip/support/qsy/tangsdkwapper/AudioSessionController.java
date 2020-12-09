package com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper;

import android.util.Log;

import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.tang.gnettangsdk.CGNetTangSessionErrorInfo;
import com.tang.gnettangsdk.CGNetTangVariant;
import com.tang.gnettangsdk.IGNetTangAudioSession;
import com.tang.gnettangsdk.IGNetTangAudioSessionSink;
import com.tang.gnettangsdk.intArray;
import com.tang.gnettangsdk.phoneCallNumArray;

public class AudioSessionController extends IGNetTangAudioSessionSink {
    public AudioSessionController(IGNetTangAudioSession audioService) {
        m_audioSession = audioService;
        if (m_audioSession != null)
            m_audioSession.setSessionCallback(this);
    }

    public void enableLoudSpeaker() {
        m_audioSession.enableLoudSpeaker();
    }

    public void disableLoudSpeaker() {
        m_audioSession.disableLoudSpeaker();
    }

    public void startVoip() {
        m_audioSession.startVoip();
    }

    public void stopVoip() {
        m_audioSession.stopVoip();
    }

    public void muteUser(intArray nUserID, int nNum) {
        m_audioSession.muteUser(nUserID.cast(), nNum);
        for (int i = 0; i < nNum; i++) {
            Log.e("VOIP", "muteUser, nUserID=" + nUserID.getitem(i));
        }
    }

    public void unMuteUser(intArray nUserID, int nNum) {
        m_audioSession.unMuteUser(nUserID.cast(), nNum);
        for (int i = 0; i < nNum; i++) {
            Log.e("VOIP", "unMuteUser, nUserID=" + nUserID.getitem(i));
        }
    }

    public void call(phoneCallNumArray PhoneCallNum, int nNum) {
        m_audioSession.call(PhoneCallNum.cast(), nNum);
        for (int i = 0; i < nNum; i++) {
            Log.e("VOIP", "PhoneCallNum, nUserID=" + PhoneCallNum.getitem(i).getNUserID());
            Log.e("VOIP", "PhoneCallNum, sPhoneNum=" + PhoneCallNum.getitem(i).getSPhoneNum());
        }
    }

    public void hangUp(intArray nUserID, int nNum) {
        m_audioSession.hangUp(nUserID.cast(), nNum);
        for (int i = 0; i < nNum; i++) {
            Log.e("VOIP", "hangUp, nUserID=" + nUserID.getitem(i));
        }
    }

    public void bind(long nUserId, long nPhoneUserId) {
        m_audioSession.bind(nUserId, nPhoneUserId);
    }

    public int getPropertyValue(String propName, CGNetTangVariant value) {
        return m_audioSession.getPropertyValue(propName, value);
    }

    @Override
    public void onSessionErrorHandle(CGNetTangSessionErrorInfo pErrorInfo) {
        Log.e("VOIP", "MyAudioService::onSessionErrorHandle()");

    }

    @Override
    public void onAudioSessionPropertyChanged(String propName, CGNetTangVariant oldPropValue, CGNetTangVariant newPropValue) {
        try {
            Log.e("VOIP", "MyAudioService::onAudioSessionPropertyChanged(), propName = " + propName);

            if (propName.compareTo("loudSpeakerStatus") == 0) {
                TangSDKInstance.getInstance().onLoudSpeakerStatusChanged(newPropValue.getBoolVal());
            } else if (propName.compareTo("networkQuality") == 0) {
                TangSDKInstance.getInstance().onVOIPQualityChanged((int) newPropValue.getUintVal());
            } else if (propName.compareTo("audioDataReady") == 0) {  //fix bug B160308-001: 蜜蜂SDK 开启呼会议后，3s后才能听到对方说话声
                TangSDKInstance.getInstance().onAudioChannelReady(newPropValue.getBoolVal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private IGNetTangAudioSession m_audioSession = null;
}