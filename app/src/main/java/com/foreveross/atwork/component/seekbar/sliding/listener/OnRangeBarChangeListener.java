package com.foreveross.atwork.component.seekbar.sliding.listener;


import com.foreveross.atwork.component.seekbar.BaseSeekBar;

/**
 * Created by tangyx on 16/8/25.
 */
public interface OnRangeBarChangeListener {
    void onIndexChangeListener(BaseSeekBar rangeBar, int leftThumbIndex, int rightThumbIndex);
}
