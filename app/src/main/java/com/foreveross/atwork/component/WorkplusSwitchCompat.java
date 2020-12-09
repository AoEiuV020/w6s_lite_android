package com.foreveross.atwork.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.SwitchCompat;

/**
 * Created by dasunsy on 2017/12/5.
 */

public class WorkplusSwitchCompat extends SwitchCompat {

    private OnClickNotPerformToggle mOnClickNotPerformToggle;

    public WorkplusSwitchCompat(Context context) {
        super(context);
        initUI();
        registerListener();
    }

    public WorkplusSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
        registerListener();
    }

    private void initUI() {

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_enabled},
                new int[] {android.R.attr.state_checked},
                new int[] {-android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                0xff8eccff,
                0xff1A98FF,
                0xffffffff,
        };

        int[] trackColors = new int[] {
                0x4c8eccff,
                0x4c1A98FF,
                0x4cdcdcdc,

        };

        ColorStateList colorStateListThumb = new ColorStateList(states, thumbColors);
        ColorStateList colorStateListTrack = new ColorStateList(states, trackColors);

        setThumbTintList(colorStateListThumb);
        setTrackTintList(colorStateListTrack);

        getThumbTintList();
    }


    private void registerListener() {
        setOnTouchListener((v, event) -> {


            int action = event.getAction();


            if (MotionEvent.ACTION_MOVE == action) {
                return true;
            }

            return false;
        });
    }

    public void setOnClickNotPerformToggle(OnClickNotPerformToggle onClickNotPerformToggle) {
        this.mOnClickNotPerformToggle = onClickNotPerformToggle;
    }

    @Override
    public boolean performClick() {
        if (null != mOnClickNotPerformToggle) {
            mOnClickNotPerformToggle.onClick();
        }
        return true;
    }

    ;

    public interface OnClickNotPerformToggle {
        void onClick();
    }


}
