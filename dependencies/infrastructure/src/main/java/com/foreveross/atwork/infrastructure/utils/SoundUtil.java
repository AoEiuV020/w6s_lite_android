package com.foreveross.atwork.infrastructure.utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by dasunsy on 16/9/22.
 */

public class SoundUtil {
    public static SoundPool getSoundCompat(int streamType) {
        SoundPool soundPool = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setLegacyStreamType(streamType).build();

            soundPool = builder.setMaxStreams(1).setAudioAttributes(audioAttributes).build();

        } else {
            soundPool = new SoundPool(1, streamType, 0);

        }

        return soundPool;
    }
}
