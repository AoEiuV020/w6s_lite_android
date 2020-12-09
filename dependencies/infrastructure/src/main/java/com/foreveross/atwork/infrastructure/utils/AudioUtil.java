package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by dasunsy on 16/4/3.
 */
public class AudioUtil {

    /**
     * 是否正在响铃以及通话状态
     *
     * @param context
     * */
    public static boolean isPhoneCalling(Context context) {

        TelephonyManager telephonymanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonymanager.getCallState())
        {
            case TelephonyManager.CALL_STATE_OFFHOOK:
            case TelephonyManager.CALL_STATE_RINGING:
                return true;
            default:
            {
                return false;
            }
        }
    }

    public static void setMode(Context context, int mode) {
        if (!isPhoneCalling(context) && !isHeadSetOn(context)) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(mode);
        }
    }

    public static int getCurrentMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getMode();
    }

    public static void setSpeakerphoneOn(Context context, boolean on) {
        if (!isPhoneCalling(context) && !isHeadSetOn(context)) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(on);
        }
    }

    public static boolean isHeadSetOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        return audioManager.isWiredHeadsetOn();
    }


    public static boolean isSpeakModel(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        return audioManager.isSpeakerphoneOn();
    }

    public static boolean isEarphoneModel(Context context) {
        return !isSpeakModel(context);
    }


    /**
     * 杨声器模式
     */
    public static void speakerModel(Context context) {
        setSpeakerphoneOn(context, true);
        setMode(context, AudioManager.MODE_NORMAL);
    }


    /**
     * 听筒模式
     */
    public static void earphoneModel(Context context) {
        setSpeakerphoneOn(context, false);
        if(21 <= Build.VERSION.SDK_INT) {
            setMode(context, AudioManager.MODE_IN_COMMUNICATION);

        } else {
            setMode(context, AudioManager.MODE_IN_CALL);

        }
    }

}
