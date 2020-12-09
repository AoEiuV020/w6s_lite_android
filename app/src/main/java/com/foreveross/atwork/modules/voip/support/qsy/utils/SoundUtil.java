package com.foreveross.atwork.modules.voip.support.qsy.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.foreveross.atwork.R;

/**
 * Created by RocXu on 2016/3/9.
 */
public class SoundUtil {
    private static final String TAG = "SoundUtil";
    public static final int KEY_RING = 1;
    public static final int KEY_BUSYTONE = 2;
    public static final int KEY_RINGIN = 3;
    private Context mContext;
    private SoundPool mSoundPool;
    private SparseArray<Integer> mSoundsArr;
    private static SoundUtil mInstance;
    private int loopPlayStreamID;
    private boolean isInited;

    public static SoundUtil getInstance() {
        if (mInstance == null) {
            mInstance = new SoundUtil();
        }
        return mInstance;
    }

    private SoundUtil() {
    }

    public void init(Context context) {
        if(isInited){
            return;
        }
        this.mContext = context;
        Log.e("init", "LOAD START-> " + System.currentTimeMillis());
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundsArr = new SparseArray<>();
        mSoundsArr.put(1, mSoundPool.load(mContext, R.raw.tangsdkui_ring, 1));
        mSoundsArr.put(2, mSoundPool.load(mContext, R.raw.tangsdkui_busytone, 1));
        mSoundsArr.put(3, mSoundPool.load(mContext, R.raw.tangsdkui_ringin, 1));
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.e("onLoadComplete", "LOAD COMPLETE-> "+ System.currentTimeMillis());
                isInited = true;
            }
        });

    }

    public void playSound(final int sound, final int loop) {
        if(mSoundPool == null || mSoundsArr == null){
            return;
        }

        // stop loop playing sound first
        stopLoopPlay();
        AudioManager mgr = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final float volume = streamVolumeCurrent / streamVolumeMax;
        // 播放响铃时延迟
        if (sound == KEY_RING) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mSoundPool==null || mSoundsArr == null){
                        return;
                    }

                    int streamID = mSoundPool.play(mSoundsArr.get(sound), volume, volume, 1, loop, 1f);
                    if (sound == KEY_RING) {
                        loopPlayStreamID = streamID;
                    }
                }
            }, 500);
        } else {
            mSoundPool.play(mSoundsArr.get(sound), volume, volume, 1, loop, 1f);
        }

    }


    public void stopLoopPlay() {
        if (loopPlayStreamID > 0) {
            mSoundPool.stop(loopPlayStreamID);
            loopPlayStreamID = 0;
        }
    }

    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
        if (mSoundsArr != null) {
            mSoundsArr.clear();
            mSoundsArr = null;
        }
        loopPlayStreamID = 0;
        isInited = false;
    }

}
