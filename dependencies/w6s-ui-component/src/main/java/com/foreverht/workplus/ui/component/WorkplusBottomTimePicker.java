package com.foreverht.workplus.ui.component;
/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;

import java.util.Calendar;


public class WorkplusBottomTimePicker extends DialogFragment {

    private static final String TAG = WorkplusBottomTimePicker.class.getSimpleName();

    private TextView mCancel;
    private TextView mOk;
    private TextView mTitle;
    private DatePicker mDpPicker;

    private DateDialogOnClickListener mListener;

    public void setDataPickListener(DateDialogOnClickListener listener) {
        this.mListener = listener;
    }
    private long currentTime = 0;
    private long maxTime = 0;

    public WorkplusBottomTimePicker(long time, long maxTime) {

        super();
        currentTime = time;
        this.maxTime = maxTime;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //该方法需要放在onViewCreated比较合适, 若在 onStart 在部分机型(如:小米3)会出现闪烁的情况
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent_70);
        StatusBarUtil.setTransparentFullScreen(getDialog().getWindow());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_bottom_time_picker, null);
        initView(view);
        registerListener(view);
        setCancelable(false);
        return view;
    }

    private void initView(View view) {
        mCancel = view.findViewById(R.id.tv_cancel);
        mOk = view.findViewById(R.id.tv_ok);
        mTitle = view.findViewById(R.id.tv_until);
        mDpPicker = view.findViewById(R.id.dpPicker);
        String value = TimeUtil.getStringForMillis( currentTime, TimeUtil.YYYY_MM_DD);
        String[] dateStrArray = value.split("-");
        int year = Integer.valueOf(dateStrArray[0]);
        int month = Integer.valueOf(dateStrArray[1]);
        int day = Integer.valueOf(dateStrArray[2]);
        mDpPicker.setMinDate(TimeUtil.getCurrentTimeInMillis() - 1000);
        if (maxTime != 0) {
            mDpPicker.setMaxDate(maxTime);
        }
        mDpPicker.init(year, month - 1, day, (view1, year1, monthOfYear, dayOfMonth) -> {

        });

    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    private void registerListener(View view) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        });
        mCancel.setOnClickListener(v -> dismiss());
        mOk.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(mDpPicker.getYear(), mDpPicker.getMonth(), mDpPicker.getDayOfMonth() );
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 0);
            if (mListener != null) {
                mListener.ok(calendar.getTimeInMillis());
            }
        });

    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface DateDialogOnClickListener {
        void ok(long date);
    }
}
