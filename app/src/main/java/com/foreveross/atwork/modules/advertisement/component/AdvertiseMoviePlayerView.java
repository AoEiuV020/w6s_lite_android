package com.foreveross.atwork.modules.advertisement.component;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.foreveross.atwork.component.WorkplusSurfaceView;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 视频播放控件
 */
public class AdvertiseMoviePlayerView extends WorkplusSurfaceView {

    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mPlayer;
    private boolean mHasSurfaceHolder = false;
    private onSurfaceReadyListener mOnSurfaceReadyListener;
    private ExecutorService mExecutors;
    private Context mContext;
    private int mCurrentPos = -1;
    private String mFileName;
    private OnPlayCompletionListener mOnPlayCompletionListener;

    public AdvertiseMoviePlayerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        createSurface();
        mExecutors = Executors.newSingleThreadExecutor();
    }

    private void createSurface() {
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(mSurfaceHolderCallback); // holder加入回调接口
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// setType必须设置，要不出错.
    }

    public void setOnSurfaceReadyListener(onSurfaceReadyListener listener) {
        mOnSurfaceReadyListener = listener;
    }

    /**
     * 播放视频
     *
     * @param fileName 文件路径
     */
    public void play(final String fileName, final OnPlayCompletionListener completionListener) {
        mFileName = fileName;
        mOnPlayCompletionListener = completionListener;
        play();
    }

    private void play() {
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mPlayer.setDisplay(mSurfaceHolder); // 定义一个SurfaceView播放它

            mPlayer.setOnCompletionListener(arg0 -> {
                stop();
                if (mOnPlayCompletionListener != null)
                    mOnPlayCompletionListener.onPlayCompletion();
                // canvas.drawColor(Color.TRANSPARENT,
                // PorterDuff.Mode.CLEAR);
            });


            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            fixedScale();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPlayer.setOnPreparedListener(mp -> {
            mPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            if (mCurrentPos != -1) {
                mPlayer.seekTo(mCurrentPos);
            }
            mPlayer.start();
        });
    }


    private void stop() {
        release();
    }

    /**
     * 页面从前台到后台会执行 onPause ->onStop 此时Surface会被销毁，
     * 再一次从后台 到前台时需要 重新创建Surface
     */
    public void restart() {
        if (mCurrentPos == -1) {
            return;
        }
        if (!mHasSurfaceHolder) {
            createSurface();
        }

    }

    public void onResume() {
        if (mCurrentPos == -1) {
            return;
        }
        play();
    }


    /**
     * 暂停
     */
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mCurrentPos = mPlayer.getCurrentPosition();
            release();
        }

    }

    /**
     * 释放资源
     */
    public void release() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void fixedScale() {
        int screenHeight = ScreenUtils.getScreenHeight(mContext);
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        int myVideoWidth = mPlayer.getVideoWidth();
        //视频高度
        int myVideoHight = mPlayer.getVideoHeight();
        int width = 0;
        int height = 0;
        if (myVideoWidth < screenWidth + 1) {
            width = screenWidth;
            height = screenHeight;
        } else {
            width = screenHeight * screenWidth / (int) (myVideoHight * 2);
            height = screenHeight * screenWidth / (int) (myVideoWidth * 2);
        }

        if (width < screenWidth && (screenWidth + 1) < 1000) {
            width = screenWidth;
            height = screenHeight;
        }

        mSurfaceHolder.setFixedSize(width, height);
        this.setMeasure(width, height);
        this.requestLayout();
    }

    private Callback mSurfaceHolderCallback = new Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            mHasSurfaceHolder = false;
            if (mPlayer != null && mPlayer.isPlaying()) {
                mCurrentPos = mPlayer.getCurrentPosition();
                mPlayer.stop();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            mHasSurfaceHolder = true;
            if (mOnSurfaceReadyListener != null) {
                mOnSurfaceReadyListener.onSurfaceReady();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            mSurfaceHolder = holder;
            mHasSurfaceHolder = true;

        }

    };

    /**
     * 播放成功回调
     */
    public interface OnPlayCompletionListener {
        void onPlayCompletion();
    }

    public interface onSurfaceReadyListener {
        void onSurfaceReady();
    }

}
