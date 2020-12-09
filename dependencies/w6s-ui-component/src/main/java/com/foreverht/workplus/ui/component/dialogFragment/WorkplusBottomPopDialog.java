package com.foreverht.workplus.ui.component.dialogFragment;
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
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.foreverht.workplus.ui.component.R;
import com.foreverht.workplus.ui.component.item.PopupDialogItemView;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;


public class WorkplusBottomPopDialog extends DialogFragment {

    private static final String TAG = WorkplusBottomPopDialog.class.getSimpleName();

    private TextView mCancel;

    private LinearLayout mContentLayout;

    private TextView mTvPopDialogTitle;

    private View mTvPopDialogTitleLine;

    private String mTitle;

    private String[] mDataArray;

    private SparseIntArray mColorArray = new SparseIntArray();

    private String mColorValue = "";
    private int mColor = -1;

    private SparseIntArray mDrawableArray = new SparseIntArray();

    private BottomPopDialogOnClickListener mListener;
    private BottomPopDialogOnCancelListener mCancelListener;

    public WorkplusBottomPopDialog() {
        super();
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
        View view = inflater.inflate(R.layout.view_user_info_more, null);
        initView(view);
        registerListener(view);
        setCancelable(true);
        return view;
    }

    private void initView(View view) {

        mTvPopDialogTitle = view.findViewById(R.id.tv_pop_dialog_title);
        mTvPopDialogTitleLine = view.findViewById(R.id.tv_pop_dialog_title_line);
        mContentLayout = view.findViewById(R.id.pop_dialog_content_layout);
        mCancel = view.findViewById(R.id.cancel);

        if(!StringUtils.isEmpty(mTitle)) {
            mTvPopDialogTitle.setText(mTitle);
            mTvPopDialogTitle.setVisibility(View.VISIBLE);
            mTvPopDialogTitleLine.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < mDataArray.length; i++) {
            PopupDialogItemView dialogItem = new PopupDialogItemView(getContext());
            dialogItem.refreshData(mDataArray[i]);

            if (i == mDataArray.length - 1) {
                dialogItem.hideLine();
            }

            if (mColorArray.size() != 0 && mColorArray.get(i) != 0) {
                dialogItem.setTextColor(mColorArray.get(i));
            }

            if (mColor != -1 && !TextUtils.isEmpty(mColorValue)) {
                if (mDataArray[i].equalsIgnoreCase(mColorValue)) {
                    dialogItem.setTextColor(mColor);
                }
            }


            if (mDrawableArray.size() != 0 && mDrawableArray.get(i) != 0) {
                dialogItem.setDrawableLeft(mDrawableArray.get(i), 0, 0, 0);
            }


            mContentLayout.addView(dialogItem);

            dialogItem.setOnClickListener(v -> mListener.onDialogOnClick(dialogItem.getItemContent()));
        }

    }


    public void refreshData(String[] dataArray) {
        mDataArray = dataArray;
    }

    public void setItemOnListener(BottomPopDialogOnClickListener listener) {
        mListener = listener;
    }

    public void setOnCancelListener(BottomPopDialogOnCancelListener listener) {
        mCancelListener = listener;
    }



    /**
     * 设置全部 item 颜色, 需要在{@link #refreshData(String[])}后调用
     * */
    public void setAllItemTextColor(int color) {

        if(null != mDataArray) {
            for(int i = 0; i < mDataArray.length; i++) {
                setItemTextColor(i, color);
            }
        }
    }

    /**
     * 设置字体颜色
     */
    public void setItemTextColor(int index, int color) {
        mColorArray.put(index, color);
    }

    public void setItemTextValueColor(String colorValue, int color) {
        mColorValue = colorValue;
        mColor = color;
    }


    /**
     * 设置小图标
     * */
    public void setItemTextDrawable(int index, int drawableRes) {
        mDrawableArray.put(index, drawableRes);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    private void registerListener(View view) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mCancelListener != null) {
                    mCancelListener.onCancel();
                }
                dismiss();
            }
            return false;
        });
        mCancel.setOnClickListener(v -> {
            if (mCancelListener != null) {
                mCancelListener.onCancel();
            }
            dismiss();

        });
    }


    public interface BottomPopDialogOnClickListener {
        void onDialogOnClick(String tag);
    }

    public interface BottomPopDialogOnCancelListener {
        void onCancel();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
