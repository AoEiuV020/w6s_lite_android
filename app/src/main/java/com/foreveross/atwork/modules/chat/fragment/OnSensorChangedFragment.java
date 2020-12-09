package com.foreveross.atwork.modules.chat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.AudioUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.modules.chat.util.AudioPlayHelper;
import com.foreveross.atwork.modules.chat.util.AudioRecord;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;

/**
 * Created by dasunsy on 2017/5/4.
 */

public abstract class OnSensorChangedFragment extends BackHandledFragment implements SensorEventListener {

    public static final String REGISTER_SENSOR = "speakerModel";

    public static final String UNREGISTER_SENSOR = "earphoneModel";

    private Sensor mSensor;
    //判断距离感应监听器是否已经注册  防止多次重复注册
    private boolean mUnRegisterListener = true;
    private SensorManager mSensorManager;

    private float mLastRange;


    private BroadcastReceiver mHandleRegisterBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (REGISTER_SENSOR.equals(action)) {
                playAudioStart(context);

            } else if (UNREGISTER_SENSOR.equals(action)) {
                playAudioEnd(context);

            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSensorManager = (SensorManager) BaseApplicationLike.baseContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcast();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcast();
        //注销距离感应器监听器
        playAudioEnd(BaseApplicationLike.baseContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销距离感应器监听器
        playAudioEnd(BaseApplicationLike.baseContext);
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if(mLastRange == range) {
            return;
        }

        mLastRange = range;

        LogUtil.e("audio", "audio range -> " + range);

        LogUtil.e("audio", "audio range max -> " + mSensor.getMaximumRange());

        boolean lastPlayStatus = AudioUtil.isSpeakModel(getActivity());

        if (range >= mSensor.getMaximumRange()) {

            hideMaskLayer();

            AudioPlayHelper.getInstance().useSavedAudioMode(getActivity());

        } else {
            //听筒模式
            AudioPlayHelper.getInstance().earphoneModel(getActivity());

            showMaskLayer();


        }

        boolean currentSpeakerStatus = AudioUtil.isSpeakModel(getActivity());
        if (lastPlayStatus != currentSpeakerStatus) {
            replay();

            if (currentSpeakerStatus) {
                AtworkToast.showResToast(R.string.switch_speaker_mode);

            } else {
                AtworkToast.showResToast(R.string.switch_earphone_mode);

            }
        }

    }

    protected void replay() {
        AudioRecord.replay();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REGISTER_SENSOR);
        intentFilter.addAction(UNREGISTER_SENSOR);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mHandleRegisterBroadcast, intentFilter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mHandleRegisterBroadcast);
    }


    protected void hideMaskLayer() {
        WindowManager.LayoutParams lAttrs = getActivity().getWindow().getAttributes();
        lAttrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setAttributes(lAttrs);

    }

    protected void showMaskLayer() {
        WindowManager.LayoutParams lAttrs = getActivity().getWindow().getAttributes();
        lAttrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getActivity().getWindow().setAttributes(lAttrs);
    }

    protected void playAudioStart(Context context) {
        if (!AudioUtil.isHeadSetOn(context)) {
            registerSensorListener();
        }

        if (AudioUtil.isEarphoneModel(context)) {
            AtworkToast.showResToast(R.string.now_earphone_mode);
        }
    }

    protected void playAudioEnd(Context context) {
        unregisterSensorListener();

        hideMaskLayer();

        if (!VoipHelper.isHandlingVoipCall()) {
            AudioPlayHelper.getInstance().revertAudioMode(context);
        }

        AudioPlayHelper.muteAudioFocus(BaseApplicationLike.baseContext, false);

        AudioRecord.sPlayingVoiceMedia = null;

    }


    private void registerSensorListener() {
        if (mUnRegisterListener && null != mSensorManager) {
            //注册距离感应器监听器
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mUnRegisterListener = false;

        }
    }

    private void unregisterSensorListener() {
        if (null != mSensorManager) {
            if (null != mSensor) {
                mSensorManager.unregisterListener(this, mSensor);

            } else {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                mSensorManager.unregisterListener(this, mSensor);
            }
        }

        mUnRegisterListener = true;
    }

    /**
     * 发送广播 注册距离感应监听器
     */
    public static void registerSensor() {
        Intent intent = new Intent(REGISTER_SENSOR);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    /**
     * 发送广播 注销距离感应监听器
     */
    public static void unregisterSensor() {
        Intent intent = new Intent(UNREGISTER_SENSOR);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }
}
