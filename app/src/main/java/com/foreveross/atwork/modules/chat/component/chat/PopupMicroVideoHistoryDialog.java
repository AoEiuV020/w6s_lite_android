package com.foreveross.atwork.modules.chat.component.chat;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                       __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
                            |__|
 */


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.WhiteClickGridView;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.chat.adapter.VideoHistoryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by reyzhang22 on 15/12/23.
 */
public class PopupMicroVideoHistoryDialog extends DialogFragment {

    private static final String TAG = PopupMicroVideoHistoryDialog.class.getSimpleName();
    private PopupMicroVideoRecordingDialog.OnMicroVideoTakingListener mMicroVideoTakingListener;
    private PopupMicroVideoRecordingDialog.OnRefreshCoverListener mRefreshCoverListener;

    private static final String DATA_HEIGHT = "DATA_HEIGHT";

    private View mVRoot;
    private ImageView mIvCancel;
    private RelativeLayout mRlCancel;
    private TextView mTvEdit;
    private RelativeLayout mRlVideo;
    private LinearLayout mLlMain;
    private WhiteClickGridView mGvVideo;
    private VideoHistoryAdapter mAdapter;
    private List<String> mVideoInfoList = new ArrayList<>();



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int height = getArguments().getInt(DATA_HEIGHT, DensityUtil.dip2px( 400));
        Dialog dialogView = new Dialog(getActivity(), R.style.micro_video_style);
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogView.setContentView(R.layout.micro_video_history_popup_window);
        dialogView.setCanceledOnTouchOutside(true);
        Window window = dialogView.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        initViews(dialogView);

        ViewUtil.setHeight(mLlMain, height);

        registerListener();

        //12dp 转换成 px
        int vLength = (int) (DensityUtil.DP_8_TO_PX * 1.5);
        int paddingLength = getPaddingLength(getContext());
        if(0 <= paddingLength){
            mGvVideo.setPadding(paddingLength, vLength, 0, vLength);
        }else{
            mGvVideo.setPadding(vLength, vLength, vLength, vLength);
        }


        if(StatusBarUtil.supportStatusBarMode()) {
            StatusBarUtil.setTransparentFullScreen(window);
            StatusBarUtil.setStatusBarMode(window, true);

        }

        return dialogView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetchVideoNameList();
        mVideoInfoList.add("plus_button");
        mAdapter = new VideoHistoryAdapter(getContext(), this, mVideoInfoList);
        mAdapter.setMicroVideoTakingListener(mMicroVideoTakingListener);
        mGvVideo.setAdapter(mAdapter);

        getDialog().setOnDismissListener(dialog -> {
            if(null != mRefreshCoverListener) {
                mRefreshCoverListener.onRefresh();
            }

            try {
                PopupMicroVideoHistoryDialog.super.onDismiss(dialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initViews(Dialog view) {
        mVRoot = view.findViewById(R.id.ll_root);
        mRlCancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        mIvCancel = (ImageView) view.findViewById(R.id.iv_cancel);
        mTvEdit = (TextView) view.findViewById(R.id.tv_edit);
        mRlVideo = (RelativeLayout) view.findViewById(R.id.rl_video);
        mGvVideo = (WhiteClickGridView) view.findViewById(R.id.gv_video);
        mLlMain = (LinearLayout) view.findViewById(R.id.ll_main_area);
    }

    private void registerListener() {
        mVRoot.setOnClickListener((v) -> {
            PopupMicroVideoHistoryDialog.this.dismissAllowingStateLoss();
        });


        mRlCancel.setOnClickListener(v -> PopupMicroVideoHistoryDialog.this.dismiss());

        mTvEdit.setOnClickListener(v -> {
            if(mAdapter.getEditMode()) {
                dismiss();

            } else {
                mTvEdit.setText(R.string.done);
                mAdapter.setEditMode(true);
                mAdapter.setSendModePos(-1);
                mAdapter.notifyDataSetChanged();

            }
        });



        mGvVideo.setOnTouchInvalidPositionListener(motionEvent -> {
            mAdapter.setSendModePos(-1);

            if (mAdapter.getEditMode()) {
                mTvEdit.setText(R.string.edit);
                mAdapter.setEditMode(false);

            }

            mAdapter.notifyDataSetChanged();

            return true;
        });

        mRlVideo.setOnClickListener(v -> {
            mAdapter.setSendModePos(-1);

            if (mAdapter.getEditMode()) {
                mTvEdit.setText(R.string.edit);
                mAdapter.setEditMode(false);

            }

            mAdapter.notifyDataSetChanged();
        });


    }

    private void fetchVideoNameList() {
        File dir = new File(AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(getContext()));
        File[] fileArray = dir.listFiles();
        Arrays.sort(fileArray, (lhs, rhs) -> (int) (rhs.lastModified() - lhs.lastModified()));
        for(File file : fileArray) {
            mVideoInfoList.add(file.getName().replace(".mp4", ""));
        }
    }

    public void setHeight(int height) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_HEIGHT, height);
        setArguments(bundle);
    }

    public void setMicroVideoTakingListener(PopupMicroVideoRecordingDialog.OnMicroVideoTakingListener listener) {
        mMicroVideoTakingListener = listener;
    }

    /**
     * 返回列表 paddingLeft 的距离(达到左右对称)
     */
    private int getPaddingLength(Context context) {
        int paddingLength = (ScreenUtils.getScreenWidth(context) - 3 * DensityUtil.dip2px( 122)) / 4;
        return paddingLength;
    }

    public void setRefreshCoverListener(PopupMicroVideoRecordingDialog.OnRefreshCoverListener onRefreshCoverListener) {
        this.mRefreshCoverListener = onRefreshCoverListener;
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
