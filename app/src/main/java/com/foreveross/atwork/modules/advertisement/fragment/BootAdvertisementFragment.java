package com.foreveross.atwork.modules.advertisement.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementEvent;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementOpsType;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementType;
import com.foreveross.atwork.modules.advertisement.manager.AdvertisementManager;
import com.foreveross.atwork.modules.advertisement.manager.BootAdvertisementManager;
import com.foreveross.atwork.modules.main.fragment.SplashFragment;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by reyzhang22 on 17/9/18.
 */

public class BootAdvertisementFragment extends BackHandledFragment {

    public static final String KEY_AD_ID = "KEY_AD_ID";
    public static final String KEY_AD_NAME = "KEY_AD_NAME";
    public static final String KEY_AD_TYPE = "KEY_AD_TYPE";
    public static final String KEY_AD_PATH = "KEY_AD_PATH";
    public static final String KEY_LINK_URL = "KEY_LINK_URL";
    public static final String KEY_AD_SKIP_TIME = "KEY_AD_SKIP_TIME";
    public static final String KEY_AD_ORG_ID = "KEY_AD_ORG_ID";

    private Activity mActivity;

    private ScalableVideoView mVideoPlayer;
    private ImageView mImageView;
    private View mLlSkip;
    private TextView mTvSkipLabel;
    private TextView mTvSkipCounter;

    private String mAdvertisementId;
    private String mAdvertisementName;
    private String mLinkUrl;
    private String mType;
    private String mPath;
    private int mSkipTime = 0;
    private String mOrgId;

    private Timer mTimer;
    private TimerTask mTask;

    private int mPausePosition = -1;

    private boolean mWasPaused = false;

    public void shutDownTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTask = null;
        mTimer = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advertisement_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();
    }

    @Override
    protected void findViews(View view) {
        mVideoPlayer = view.findViewById(R.id.ad_video_view);
        mImageView = view.findViewById(R.id.ad_image_view);
        mLlSkip = view.findViewById(R.id.ll_skip);
        mTvSkipLabel = view.findViewById(R.id.tv_jump_label);
        mTvSkipCounter = view.findViewById(R.id.skip_second);

        mTvSkipLabel.setText(getStrings(R.string.over_jump) + " ");
    }

    private void registerListener() {
        mVideoPlayer.setOnClickListener(view -> {
            nextToWeb();
        });

        mImageView.setOnClickListener(view ->{
            nextToWeb();
        });

        mLlSkip.setOnClickListener(view ->{
            postAdvertisementEvent(AdvertisementOpsType.Skip);
            mActivity.setResult(Activity.RESULT_OK);
            onFinish();
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        mType = bundle.getString(KEY_AD_TYPE);
        mPath = bundle.getString(KEY_AD_PATH);
        mSkipTime = bundle.getInt(KEY_AD_SKIP_TIME);
        mLinkUrl = bundle.getString(KEY_LINK_URL);
        mAdvertisementId = bundle.getString(KEY_AD_ID);
        mAdvertisementName = bundle.getString(KEY_AD_NAME);
        mOrgId = bundle.getString(KEY_AD_ORG_ID);
        File file = new File(mPath);
        if (!file.exists()) {
            onFinish();
            return;
        }
        BootAdvertisementManager.getInstance().setLatestViewAdTime(mActivity, mOrgId);
        postAdvertisementEvent(AdvertisementOpsType.Display);
        if (AdvertisementType.Video.valueOfString().equalsIgnoreCase(mType)) {
            playAdMovie();
            return;
        }
        playAdImage();

    }

    /**
     * 提交统计数据
     * @param opsType
     */
    private void postAdvertisementEvent(AdvertisementOpsType opsType) {

        AdvertisementEvent advertisementEvent = new AdvertisementEvent();
        advertisementEvent.orgId = mOrgId;
        advertisementEvent.advertisementId = mAdvertisementId;
        advertisementEvent.advertisementName = mAdvertisementName;
        advertisementEvent.type = mType;
        advertisementEvent.opsType = opsType.valueOfString();

        AdvertisementManager.INSTANCE.postAdvertisementEvent(advertisementEvent, null);
    }

    /**
     * 倒计时
     */
    private void prepareCountDown() {
        if (mSkipTime == 0) {
            mTvSkipCounter.setVisibility(View.GONE);
            return;
        }
        mTimer  = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(() -> {
                    mSkipTime--;
                    mTvSkipCounter.setText("" + mSkipTime);
                    if(mSkipTime < 1) {
                        mTvSkipCounter.setVisibility(View.GONE);
                        mActivity.setResult(Activity.RESULT_OK);
                        onFinish();
                    }
                });
            }
        };

        mTvSkipCounter.setText("" + mSkipTime);
        mTimer.schedule(mTask, 1000, 1000);
    }

    private void playAdMovie() {
        mVideoPlayer.setVisibility(View.VISIBLE);
        try {
            mVideoPlayer.setDataSource(mPath);
            mVideoPlayer.setScalableType(ScalableType.CENTER_CROP);
            mVideoPlayer.invalidate();
            mVideoPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
                playAdFail();
                return false;
            });
            mVideoPlayer.prepare(mediaPlayer -> {
                mediaPlayer.start();
            });

        } catch (IOException e) {
            e.printStackTrace();
            playAdFail();
        }
    }

    private void playAdFail() {
        mActivity.runOnUiThread(() -> {
            if (!mWasPaused) {
                toast(R.string.play_ad_fail);
            }
            deleteFile();
            onFinish();
        });
    }

    private void deleteFile() {
        AsyncTaskThreadPool.getInstance().execute(() -> {
            File file = new File(mPath);
            if (file.exists()) {
                file.delete();
            }
        });
    }

    private void playAdImage() {
        mImageView.setVisibility(View.VISIBLE);
//        mImageView.setImageBitmap(BitmapFactory.decodeFile(mPath));
        ImageCacheHelper.displayImage(mPath, mImageView, ImageCacheHelper.getNotCacheOptions(), new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {

            }

            @Override
            public void onImageLoadedFail() {
                playAdFail();
            }
        });
    }

    private void nextToWeb() {
        postAdvertisementEvent(AdvertisementOpsType.Click);
        if (TextUtils.isEmpty(mLinkUrl)) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(SplashFragment.RESULT_KEY_WEB_URL, mLinkUrl);
        mActivity.setResult(SplashFragment.RESULT_CODE_TO_WEB, intent);
        onFinish();
    }

    private void onFinish() {
        //界面切换效果
        shutDownTimer();

        mVideoPlayer = null;
        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        mActivity.finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        mWasPaused = true;

        try {
            if (mVideoPlayer != null) {
                if (mVideoPlayer.isPlaying()) {
                    mPausePosition = mVideoPlayer.getCurrentPosition();
                    mVideoPlayer.pause();
                }
            }
            if (mTimer != null) {
                shutDownTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mWasPaused = true;

        if (AdvertisementType.Image.equals(mType)) {
            return;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        prepareCountDown();
        if (AdvertisementType.Image.equals(mType)) {
            return;
        }
        if (mPausePosition != -1) {
            mVideoPlayer.seekTo(mPausePosition);
            mVideoPlayer.start();
        }
    }
}
