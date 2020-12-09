package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * 视频播放控件
 */
public class MoviePlayerView extends SurfaceView {

    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mPlayer;
    private boolean mHasSurfaceHolder = false;
    private onSurfaceReadyListener mOnSurfaceReadyListener;


    public MoviePlayerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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
    public void play(final String fileName, final OnMoviePlayListener listener) {

        if (null != mPlayer) {
            mPlayer.start();
            return;
        }

        mPlayer = new MediaPlayer();

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnCompletionListener(arg0 -> {
//            stop();
            if (listener != null)
                listener.onPlayCompletion();
            // canvas.drawColor(Color.TRANSPARENT,
            // PorterDuff.Mode.CLEAR);
        });

        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }

        mPlayer.setOnPreparedListener(mp -> {
            try {
                if (listener != null) {
                    listener.onPrepareListener(mPlayer.getDuration());
                }
                mPlayer.setDisplay(mSurfaceHolder); // 定义一个SurfaceView播放它
                mPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    private void stop() {
        release();
    }

    public boolean isPlayBefore() {
        return null != mPlayer;
    }

    public boolean isPlaying() {
        if (null != mPlayer) {
            return mPlayer.isPlaying();
        }

        return false;

    }

    public void resume() {
        if (mPlayer != null) {
            mPlayer.start();
        }

    }


    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
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

    public void seekTo(int progress) {
        if (mPlayer == null) {
            return;
        }
        mPlayer.seekTo(progress);
    }

    public int getCurrentSeekPos() {
        if (mPlayer == null) {
            return 0;
        }
        return mPlayer.getCurrentPosition();
    }

    private Callback mSurfaceHolderCallback = new Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            mHasSurfaceHolder = false;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            mHasSurfaceHolder = true;
            if (mOnSurfaceReadyListener != null) {
                mOnSurfaceReadyListener.onSurfaceReady();
            }

            if (isPlayBefore()) {
                mPlayer.setDisplay(mSurfaceHolder);
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
     * 播放回调
     */
    public interface OnMoviePlayListener {
        void onPlayCompletion();
        void onPrepareListener(int duration);
    }

    public interface onSurfaceReadyListener {
        void onSurfaceReady();
    }

}
