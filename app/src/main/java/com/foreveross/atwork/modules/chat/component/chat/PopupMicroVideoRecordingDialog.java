package com.foreveross.atwork.modules.chat.component.chat;/**
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


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.LineView;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptHelper;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.chat.component.RecordPreviewView;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.io.File;

/**
 * Created by reyzhang22 on 15/12/23.
 */
public class PopupMicroVideoRecordingDialog extends DialogFragment {

    private static final String TAG = PopupMicroVideoRecordingDialog.class.getSimpleName();

    private View mRoot;
    private RelativeLayout mRlVideoMain;
    private RecordPreviewView mRecordPreviewView;
    private LinearLayout mLlFunctionBottom;
    private ImageView mTakeMovie;
    private ImageView mCancel;
    private ImageView mIvVideoHistory;

    private LineView mProgressBar;
    private Animator mAnimator;
    private int mProgressWidth;

    private TextView mMovieUpTip;

    private boolean mIsFinishView = true;

    private OnMicroVideoTakingListener mMicroVideoTakingListener;

    private boolean mCancelRecord = false;

    private String mNewestVideoName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog view = new Dialog(getActivity(), R.style.micro_video_style);
        view.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.setContentView(R.layout.micro_video_recording_popup_window);
        view.setCanceledOnTouchOutside(true);
        Window window = view.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        initViews(view);

        if(StatusBarUtil.supportStatusBarMode()) {
            StatusBarUtil.setTransparentFullScreen(window);
            StatusBarUtil.setStatusBarMode(window, true);

        }

        return view;
    }


    public void setMicroVideoTakingListener(OnMicroVideoTakingListener listener) {
        mMicroVideoTakingListener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsFinishView = true;

        refreshVideoHistoryCover();

     }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsFinishView = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecordPreviewView.freeResource();
    }


    private void initViews(Dialog view) {
        mRoot = view.findViewById(R.id.ll_root);
        mRlVideoMain = view.findViewById(R.id.rl_record_video);
        mRecordPreviewView = view.findViewById(R.id.movieRecorderView);
        mProgressBar = view.findViewById(R.id.progressBar);
        mMovieUpTip = view.findViewById(R.id.move_up_tip);

        mLlFunctionBottom = view.findViewById(R.id.ll_function_bottom);
        mTakeMovie = view.findViewById(R.id.take_micro_video);
        mCancel = view.findViewById(R.id.cancel);
        mIvVideoHistory = view.findViewById(R.id.iv_video_history);

        getProgressBarWidth();
        registerListener();

        mRecordPreviewView.setActivity(getActivity());
        mRecordPreviewView.init();
    }


    private void getProgressBarWidth() {
        final ViewTreeObserver observer = mProgressBar.getViewTreeObserver();
        observer.addOnPreDrawListener(() -> {
            mProgressWidth = mProgressBar.getMeasuredWidth();
            return true;
        });
    }

    private void refreshVideoHistoryCover() {
        mNewestVideoName = getNewestVideoName();
        if (!StringUtils.isEmpty(mNewestVideoName)) {
            String coverPath = ImageShowHelper.getThumbnailPath(getContext(), mNewestVideoName);
            ImageCacheHelper.displayImage(coverPath, mIvVideoHistory, ImageCacheHelper.getRectOptions(R.mipmap.loading_chat_size), new ImageCacheHelper.ImageLoadedListener() {
                @Override
                public void onImageLoadedComplete(Bitmap bitmap) {

                }

                @Override
                public void onImageLoadedFail() {
                    String path = AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(getContext()) + mNewestVideoName + ".mp4";
                    String originalPath = EncryptHelper.getOriginalPath(path);
                    File file = new File(originalPath);
                    if (!file.exists()) {
                        return;
                    }
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(originalPath, MediaStore.Images.Thumbnails.MINI_KIND);
                    if (bitmap == null) {
                        return;
                    }
                    mIvVideoHistory.setImageBitmap(bitmap);
                }
            });
        } else {
            mIvVideoHistory.setVisibility(View.GONE);
        }
    }

    private void registerListener() {
        setTransparentAreaClickListener();

        mRecordPreviewView.setOnRecordListener(new RecordPreviewView.OnRecordListener() {

            @Override
            public void onRecordStart() {
                startAnimator();
            }

            @Override
            public void onRecordTooShort() {
                if (mRecordPreviewView.getVideoRecordingFile() != null) {
                    mRecordPreviewView.getVideoRecordingFile().delete();
                }
                AtworkToast.showToast(getString(R.string.recording_video_too_short));
                reset();
            }

            @Override
            public void onRecordFinish() {
                handleRecordFinish();

            }

            @Override
            public void onRecordFail() {
                new Handler().postDelayed(() -> popNoAuthAlert(), 150);
            }
        });


        mTakeMovie.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mRecordPreviewView.startRecord();

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mCancelRecord) {
                    mRecordPreviewView.stopRecord();

                    if (mRecordPreviewView.getVideoRecordingFile() != null) {
                        mRecordPreviewView.getVideoRecordingFile().delete();
                    }
                    reset();

                } else {

                    mRecordPreviewView.endRecord();
                }

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (event.getY() < 0f) {
                    moveUpToCancel();
                    mCancelRecord = true;

                } else {
                    moveBack();
                    mCancelRecord = false;
                }
            }
            return true;
        });

        mCancel.setOnClickListener(v -> PopupMicroVideoRecordingDialog.this.dismissAllowingStateLoss());

        mIvVideoHistory.setOnClickListener(v -> {
            if (!StringUtils.isEmpty(mNewestVideoName)) {

                final PopupMicroVideoHistoryDialog dialog = new PopupMicroVideoHistoryDialog();

                dialog.setHeight(mRlVideoMain.getHeight() + mLlFunctionBottom.getHeight());
                dialog.setMicroVideoTakingListener(mMicroVideoTakingListener);
                dialog.setRefreshCoverListener(() -> refreshVideoHistoryCover());
                dialog.show(getFragmentManager(), "TEXT_POP_DIALOG");
            }
        });
    }

    private void setTransparentAreaClickListener() {
        mRoot.setOnClickListener((v) -> {
            PopupMicroVideoRecordingDialog.this.dismissAllowingStateLoss();
        });

        //覆盖该区域的点击事件
        mRlVideoMain.setOnClickListener((v) -> {});
        //覆盖该区域的点击事件
        mLlFunctionBottom.setOnClickListener((v) -> {});
    }

    private void popNoAuthAlert() {
        final AtworkAlertDialog atworkAlertDialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                .setContent(R.string.tip_camera_fail_no_auth)
                .hideDeadBtn();

        atworkAlertDialog.setClickBrightColorListener(dialog -> {
            atworkAlertDialog.dismiss();
            PopupMicroVideoRecordingDialog.this.dismiss();
        });

        atworkAlertDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (KeyEvent.KEYCODE_BACK == keyCode) {
                atworkAlertDialog.dismiss();
                PopupMicroVideoRecordingDialog.this.dismiss();

                return true;
            }
            return false;
        });

        atworkAlertDialog.show();
    }

    private void handleRecordFinish() {
        if (mIsFinishView) {
            mIsFinishView = false;

            if (mMicroVideoTakingListener != null && mRecordPreviewView.getVideoRecordingFile() != null) {
                mMicroVideoTakingListener.onMicroVideoFile(true, mRecordPreviewView.getVideoRecordingFile().getAbsolutePath());
            }

        }

        dismiss();
    }

    public void moveUpToCancel() {
        mMovieUpTip.setCompoundDrawables(null, null, null, null);
        mMovieUpTip.setText(getResources().getText(R.string.release_cancel));
    }

    public void moveBack() {
        mMovieUpTip.setVisibility(View.VISIBLE);
        Drawable tipDrawable = getResources().getDrawable(R.mipmap.icon_move_up_cancle);
        tipDrawable.setBounds(0, 0, tipDrawable.getMinimumWidth(), tipDrawable.getMinimumHeight());
        mMovieUpTip.setCompoundDrawables(tipDrawable, null, null, null);
        mMovieUpTip.setText(getResources().getText(R.string.move_up_cancel));
    }

    private void startAnimator() {
        mAnimator = ObjectAnimator.ofInt(mProgressBar, "layoutWidth", mProgressWidth, 0);
        mAnimator.setDuration(AtworkConfig.MICRO_MAX_TIME);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
    }

    public void reset() {
        mMovieUpTip.setVisibility(View.GONE);
        mAnimator.cancel();

        mProgressBar.revertMaxWidth();
    }


    private String getNewestVideoName() {
        File dir = new File(AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(getContext()));
        File[] fileArray = dir.listFiles();
        File tempFile = null;
        for (File file : fileArray) {
            if (null == tempFile) {
                tempFile = file;
            } else {
                if (tempFile.lastModified() < file.lastModified()) {
                    tempFile = file;

                }

            }
        }

        if (null == tempFile) {
            return "";
        }

        return tempFile.getName().replace(".mp4", "");
    }

    public interface OnMicroVideoTakingListener {
        void onMicroVideoFile(boolean needRename, String filePath);
    }

    public interface OnRefreshCoverListener {
        void onRefresh();
    }

    @Override
    public void dismiss() {
        this.dismissAllowingStateLoss();
    }
}
