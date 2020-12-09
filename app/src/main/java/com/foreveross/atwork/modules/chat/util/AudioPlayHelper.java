package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.media.AudioManager;

import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.AudioUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

/**
 * Created by dasunsy on 2017/1/6.
 */

public class AudioPlayHelper {

    private final static Object sLock = new Object();

    private static AudioPlayHelper sInstance;

    private int mLastAudioMode = -1;

    private Boolean mLastAudioStatus = null;


    public static AudioPlayHelper getInstance() {
        /**
         * double check
         * */
        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new AudioPlayHelper();
                }

            }
        }

        return sInstance;

    }

    public void speakerModel(Context context) {
        mLastAudioStatus = false;
        mLastAudioMode = AudioUtil.getCurrentMode(context);

        AudioUtil.speakerModel(context);
    }

    public void earphoneModel(Context context) {
        mLastAudioStatus = true;
        mLastAudioMode = AudioUtil.getCurrentMode(context);

        AudioUtil.earphoneModel(context);
    }

    public void revertAudioMode(Context context) {
        if(-1 != mLastAudioMode && null != mLastAudioStatus) {
            AudioUtil.setSpeakerphoneOn(context, mLastAudioStatus);
            AudioUtil.setMode(context, AudioManager.MODE_NORMAL);

        }

        clear();
    }

    public void useSavedAudioMode(Context context) {

        if(PersonalShareInfo.getInstance().isAudioPlaySpeakerMode(context)) {
            speakerModel(context);
        } else {

            earphoneModel(context);
        }
    }

    public static boolean muteAudioFocus(Context context, boolean mute) {
        boolean resultBool;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (mute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            resultBool = (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);

        } else {
            int result = am.abandonAudioFocus(null);
            resultBool = (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
        }

        LogUtil.e("audio", "pauseMusic mute=" + mute + " result=" + resultBool);

        return resultBool;
    }




    public void clear() {
        mLastAudioMode = -1;
        mLastAudioStatus = null;
    }


}
