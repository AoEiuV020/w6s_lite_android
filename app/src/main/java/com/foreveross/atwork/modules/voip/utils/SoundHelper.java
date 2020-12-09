package com.foreveross.atwork.modules.voip.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.SoundUtil;

import java.lang.ref.WeakReference;

/**
 * Created by dasunsy on 2016/9/22.
 */

public class SoundHelper {
    private static SoundHelper sInstance;

    private SoundPool mSoundPool;

    private int mRingId;

    private int mLoopPlayStreamID;

    private Handler mPlayHandler;

    private WeakReference<Runnable> mPostRunnableWeak;


    private SoundHelper() {

    }

    public static SoundHelper getInstance() {
        if (null == sInstance) {
            sInstance = new SoundHelper();
        }

        return sInstance;
    }

    public void init(Context context) {
        mSoundPool = SoundUtil.getSoundCompat(AudioManager.STREAM_MUSIC);
        mRingId = mSoundPool.load(context, R.raw.ring, 1);
    }

    /**
     * 播放铃声
     */
    public void play(Context context) {
        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);

        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        final float volume = streamVolumeCurrent / streamVolumeMax;

        if(null == mPlayHandler) {
            mPlayHandler = new Handler();
        }


        Runnable runnable = () -> {
            {

                if (null == mSoundPool) {
                    mSoundPool = SoundUtil.getSoundCompat(AudioManager.STREAM_MUSIC);
                    mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {

                        mLoopPlayStreamID = mSoundPool.play(mRingId, volume, volume, 1, -1, 1f);

                    });

                    mRingId = mSoundPool.load(context, R.raw.ring, 1);

                } else {
                    mLoopPlayStreamID = mSoundPool.play(mRingId, volume, volume, 1, -1, 1f);

                }

            }
        };
        mPostRunnableWeak = new WeakReference<>(runnable);


        mPlayHandler.postDelayed(runnable , 500);



    }

    public void stop() {
        if (mLoopPlayStreamID > 0) {
            mSoundPool.stop(mLoopPlayStreamID);
            mLoopPlayStreamID = 0;
        }

    }

    public void release() {
        if (null != mSoundPool) {
            mSoundPool.release();
            mSoundPool = null;
        }

        releaseHandler();

        mRingId = 0;
        mLoopPlayStreamID = 0;
    }

    public void releaseHandler() {
        if(null != mPlayHandler && null != mPostRunnableWeak) {
            Runnable runnable = mPostRunnableWeak.get();
            if(null != runnable) {
                mPlayHandler.removeCallbacks(runnable);
            }

            mPostRunnableWeak.clear();
        }

        mPlayHandler = null;
        mPostRunnableWeak = null;
    }


}
