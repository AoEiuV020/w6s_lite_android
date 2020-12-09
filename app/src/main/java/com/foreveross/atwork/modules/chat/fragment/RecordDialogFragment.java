package com.foreveross.atwork.modules.chat.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.RecordDialog;

/**
 * Created by lingen on 15/4/9.
 * Description:
 */
public class RecordDialogFragment extends DialogFragment {

    public RecordDialog recordDialog;
    private Mode mode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //必须在 onCreateView 之前
        setStyle(STYLE_NO_FRAME, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        recordDialog = new RecordDialog(getActivity());
        recordDialog.setBackgroundResource(R.drawable.bg_record);
        //初始化dialog背景，不再依赖switchMode时改变背景
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        return recordDialog;
    }

    public void switchMode(Mode mode) {

        if (null == recordDialog) {
            return;
        }
        this.mode = mode;
        if (Mode.CANCEL.equals(mode)) {
            recordDialog.recordCancelModel();
        } else if (Mode.RECORDING.equals(mode)) {
            recordDialog.recordingModel();
        } else if (Mode.TOO_SHORT.equals(mode)) {
            recordDialog.recordTooShort();
        }
        //切换 mode 之后才从透明转变成黑色底, 优化体验
//        recordDialog.setBackgroundResource(R.drawable.bg_record);
    }

    public boolean getRecordViewStatus() {
        return recordDialog.getViewStatus();
    }

    @Override
    public void show(FragmentManager manager, String tag) {


        try {
//            getFragmentManager().executePendingTransactions();
            if (!this.isAdded()) {
                super.show(manager, tag);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {

        if (recordDialog == null) {
            Log.e("Audio","recordDialog null");
//            super.dismiss();
            return;
        }

        if (!isAdded()){
            return;
        }

        try {
            recordDialog.stopHandler();

            //若最后是录音时间太短的警告, 则让dialog 逗留久一点
            if (Mode.TOO_SHORT.equals(mode) && null != recordDialog && null != recordDialog.getHandler()) {

                recordDialog.getHandler().postDelayed(() -> RecordDialogFragment.super.dismiss(), 1000);

            } else {
                super.dismiss();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    public enum Mode {

        RECORDING,

        CANCEL,

        TOO_SHORT
    }
}
