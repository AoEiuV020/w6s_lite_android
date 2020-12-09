package com.foreveross.atwork.component;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.chat.util.AudioRecord;

/**
 * Created by lingen on 15/4/9.
 * Description:
 * 录音Dialog
 */
public class RecordDialog extends RelativeLayout {

    private static final int POLL_INTERVAL = 300;

    private ImageView mVolumeImageView;

    private View mRecordingView;

    private View mRecordCancelView;

    private View mRecordTooShortView;

    private Handler mHandler = new Handler();
    private int[] volumeImage = new int[]{
            R.mipmap.amp1,
            R.mipmap.amp2,
            R.mipmap.amp3,
            R.mipmap.amp4,
            R.mipmap.amp5,
            R.mipmap.amp6,
            R.mipmap.amp7,
            R.mipmap.amp8,
            R.mipmap.amp9
    };

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = AudioRecord.getAmplitude();
            if (amp != -1) {
                updateVolume(amp);
                mHandler.postDelayed(mPollTask, POLL_INTERVAL);
            }
        }
    };

    public RecordDialog(Context context) {
        super(context);
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_record, this);
        mVolumeImageView = view.findViewById(R.id.item_record_pop_volume_img);
        mRecordingView = view.findViewById(R.id.item_record_pop_recoding_view);
        mRecordCancelView = view.findViewById(R.id.item_record_pop_cancel_view);
        mRecordTooShortView = view.findViewById(R.id.item_record_pop_too_short_view);
        mHandler.post(mPollTask);
    }

    public void recordingModel() {
        mRecordingView.setVisibility(VISIBLE);
        mRecordCancelView.setVisibility(GONE);
        mRecordTooShortView.setVisibility(GONE);
    }

    public void recordCancelModel() {
        mRecordCancelView.setVisibility(VISIBLE);
        mRecordingView.setVisibility(View.GONE);
        mRecordTooShortView.setVisibility(GONE);
    }

    public void recordTooShort(){
        mRecordTooShortView.setVisibility(VISIBLE);
        mRecordingView.setVisibility(GONE);
        mRecordCancelView.setVisibility(GONE);
    }

    public boolean getViewStatus(){
        return mRecordCancelView.isShown() || mRecordingView.isShown();
    }

    public void stopHandler(){
        mHandler.removeCallbacks(mPollTask);
    }

    private void updateVolume(double signalEMA) {
        int volume = (int) signalEMA;
        if (volume - 1 < 0) {
            mVolumeImageView.setImageResource(volumeImage[0]);
        } else if (volume > 8) {
            mVolumeImageView.setImageResource(volumeImage[8]);
        } else {
            mVolumeImageView.setImageResource(volumeImage[volume]);
        }
    }

}
