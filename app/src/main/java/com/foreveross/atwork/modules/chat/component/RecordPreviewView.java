package com.foreveross.atwork.modules.chat.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.utils.AtworkToast;

import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameRecorder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import sz.itguy.wxlikevideo.PreviewEventListener;
import sz.itguy.wxlikevideo.R;
import sz.itguy.wxlikevideo.camera.CameraHelper;
import sz.itguy.wxlikevideo.recorder.VideoRecorder;

/**
 * 相机预览视图
 *
 * @author Martin
 */
public class RecordPreviewView extends FrameLayout {

    public static final String TAG = "CameraPreviewView";

    final int maxCount = AtworkConfig.MICRO_MAX_TIME / 1000;

    private List<PreviewEventListener> mPreviewEventListenerList = new ArrayList<>();

    private Activity mActivity;
    public Camera mCamera;
    private int mCameraId;

    // 对焦动画视图
    private ImageView mFocusAnimationView;
    private Animation mFocusAnimation;

    // 真实相机预览视图
    private CameraPreviewSurface mCameraPreviewSurface;

    private OnRecordListener mOnRecordListener;// 录制完成回调接口

    private File mVideoRecordingFile = null;

    private VideoRecorder mVideoRecorder;

    private int mTimeCount = 0;// 时间计数
    private ScheduledExecutorService mTimerService = Executors.newScheduledThreadPool(1);// 计时器
    private ScheduledFuture mScheduledFuture;


    public RecordPreviewView(Context context) {
        this(context, null);
    }

    public RecordPreviewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFocusView(context);
    }

    public void init() {
        mVideoRecorder = new VideoRecorder();
        mVideoRecorder.setOutputSize(AtworkConfig.MICRO_TARGET_WIDTH, AtworkConfig.MICRO_TARGET_HEIGHT);
        refreshLayout();
        if (mCameraPreviewSurface != null)
            removeView(mCameraPreviewSurface);

        addView((mCameraPreviewSurface = new CameraPreviewSurface(getContext())), 0);

        initCamera(mActivity);
    }

    public void initCamera(Activity activity) {
        if (mCamera != null) {
            freeCameraResource();
        }

        mCameraId = CameraHelper.getDefaultCameraID();
        try {
            mCamera = CameraHelper.getCameraInstance(mCameraId);

        } catch (RuntimeException e) {
            e.printStackTrace();
            mOnRecordListener.onRecordFail();
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }

        if (null == mCamera) {
            return;
        }

        mVideoRecorder.setCamera(mCamera);
        CameraHelper.setCameraDisplayOrientation(activity, mCameraId, mCamera);

        addPreviewEventListener(mVideoRecorder);
    }

    private void initFocusView(Context context) {
        // 添加对焦动画视图
        mFocusAnimationView = new ImageView(context);
        mFocusAnimationView.setVisibility(INVISIBLE);
        mFocusAnimationView.setImageResource(R.drawable.ms_video_focus_icon);

        addView(mFocusAnimationView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        // 定义对焦动画
        mFocusAnimation = AnimationUtils.loadAnimation(context, R.anim.focus_animation);
        mFocusAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFocusAnimationView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startRecord() {

        try {
            createRecordFile();

            mVideoRecorder.startRecording(mVideoRecordingFile.getAbsolutePath());

            mOnRecordListener.onRecordStart();

            mTimeCount = 0;
            mScheduledFuture = mTimerService.scheduleAtFixedRate(() -> {
                mTimeCount++;
                if (maxCount == mTimeCount) {// 达到指定时间，停止拍摄

                    if (null != getHandler()) {
                        getHandler().post(() -> endRecord());

                    }
                }
            }, 1000, 1000, TimeUnit.MILLISECONDS);

        } catch (FrameFilter.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
            AtworkToast.showToast("录制出错");

            Logger.e("recording_error", e.getMessage());
            if (getVideoRecordingFile() != null) {
                getVideoRecordingFile().delete();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endRecord() {
        mScheduledFuture.cancel(true);

        stopRecord();

        if (1 <= mTimeCount) {
            mOnRecordListener.onRecordFinish();

        } else if (0 == mTimeCount) {

            mOnRecordListener.onRecordTooShort();
        }

        mTimeCount = -1;// 防止重复触发上面的逻辑

    }

    public void stopRecord() {
        try {
            if (mVideoRecorder.isRecording()) {
                mVideoRecorder.stopRecording();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取相机
     *
     * @return
     */
    public Camera getCamera() {
        return mCamera;
    }

    /**
     * 获取CameraId
     *
     * @return
     */
    public int getCameraId() {
        return mCameraId;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }


    private void startPreview() {
        try {
            if (null != mCamera) {
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        try {
            if (null != mCamera) {
                mCamera.stopPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void freeResource() {
        freeCameraResource();
        freeRecorderResource();
    }


    /**
     * 释放摄像头资源
     */
    public void freeCameraResource() {
        try {
            if (null != mCamera) {
                mCamera.stopPreview();
//                mCamera.lock();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void freeRecorderResource() {
        if (null != mVideoRecorder) {
            mVideoRecorder.releaseRecorder();
            mVideoRecorder.releaseFrameFilter();
        }
    }

    /**
     * 添加预览事件监听器
     *
     * @param previewEventListener
     */
    public void addPreviewEventListener(PreviewEventListener previewEventListener) {
        mPreviewEventListenerList.add(previewEventListener);
    }

    public void refreshLayout() {
        requestLayout();
    }

    public void setOnRecordListener(final OnRecordListener onRecordListener) {
        this.mOnRecordListener = onRecordListener;
    }

    private void createRecordFile() {
        String dir;
        if(AtworkConfig.OPEN_DISK_ENCRYPTION) {
            dir = AtWorkDirUtils.getInstance().getTmpFilesCachePath();

        } else {
            dir = AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(mActivity);
        }

        // 创建文件
        try {
            mVideoRecordingFile = new File(dir + System.currentTimeMillis() + "recording.mp4");//mp4格式
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getVideoRecordingFile() {
        return mVideoRecordingFile;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * (1f * AtworkConfig.MICRO_TARGET_HEIGHT / AtworkConfig.MICRO_TARGET_WIDTH));
        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(wms, hms);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPreviewEventListenerList.clear();
    }

    /**
     * 实际相机预览视图
     *
     * @author Martin
     */
    private class CameraPreviewSurface extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

        private static final String TAG = "RealCameraPreviewView";

        // 用于判断双击事件的两次按下事件的间隔
        private static final long DOUBLE_CLICK_INTERVAL = 200;

        private long mLastTouchDownTime;

        private ZoomRunnable mZoomRunnable;

        private int focusAreaSize;
        private Matrix matrix;

        public CameraPreviewSurface(Context context) {
            super(context);
            focusAreaSize = getResources().getDimensionPixelSize(R.dimen.camera_focus_area_size);
            matrix = new Matrix();

            getHolder().addCallback(this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (null != mCamera) {
                Camera.Size size = mCamera.getParameters().getPreviewSize();
                float ratio = 1f * size.height / size.width;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) (width / ratio);
                int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                int hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                super.onMeasure(wms, hms);

            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }


        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(null != mActivity && null == mCamera) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
//            getHolder().removeCallback(this);
            try {
                mCamera.setPreviewDisplay(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            if (holder.getSurface() == null) {
                return;
            }

            handleSurfaceChanged(holder, w, h);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(null != mCamera) {
                        long downTime = System.currentTimeMillis();
                        if (mCamera.getParameters().isZoomSupported()
                                && downTime - mLastTouchDownTime <= DOUBLE_CLICK_INTERVAL) {
                            zoomPreview();
                        }
                        mLastTouchDownTime = downTime;
                        focusOnTouch(event.getX(), event.getY());
                    }


                    break;
            }
            return super.onTouchEvent(event);
        }

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            for (PreviewEventListener previewEventListener : mPreviewEventListenerList)
                previewEventListener.onAutoFocusComplete(success);

            // 设置对焦方式为视频连续对焦
            CameraHelper.setCameraFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, mCamera);
        }

        private void handleSurfaceChanged(SurfaceHolder holder, int w, int h) {
            if(null == mCamera) {
                return;
            }

            try {
                stopPreview();
                // set preview size and make any resize, rotate or
                // reformatting changes here
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = CameraHelper.getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), Math.min(w, h));
                parameters.setPreviewSize(size.width, size.height);
                mCamera.setParameters(parameters);
                // 预览尺寸改变，请求重新布局、计算宽高
                requestLayout();

                for (PreviewEventListener previewEventListener : mPreviewEventListenerList)
                    previewEventListener.onPrePreviewStart();

                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
//                mCamera.unlock();

                for (PreviewEventListener previewEventListener : mPreviewEventListenerList)
                    previewEventListener.onPreviewStarted();

                focusOnTouch(RecordPreviewView.this.getWidth() / 2f, RecordPreviewView.this.getHeight() / 2f);

                mVideoRecorder.init();
            } catch (RuntimeException e) {
                e.printStackTrace();

                mOnRecordListener.onRecordFail();

            } catch (Exception e) {
                for (PreviewEventListener previewEventListener : mPreviewEventListenerList)
                    previewEventListener.onPreviewFailed();
            }
        }

        /**
         * 放大预览视图
         */
        private void zoomPreview() {
            Camera.Parameters parameters = mCamera.getParameters();
            int currentZoom = parameters.getZoom();
            int maxZoom = (int) (parameters.getMaxZoom() / 2f + 0.5);
            int destZoom = 0 == currentZoom ? maxZoom : 0;
            if (parameters.isSmoothZoomSupported()) {
                mCamera.stopSmoothZoom();
                mCamera.startSmoothZoom(destZoom);
            } else {
                Handler handler = getHandler();
                if (null == handler)
                    return;
                handler.removeCallbacks(mZoomRunnable);
                handler.post(mZoomRunnable = new ZoomRunnable(destZoom, currentZoom, mCamera));
            }
        }

        /**
         * On each tap event we will calculate focus area and metering area.
         * <p>
         * Metering area is slightly larger as it should contain more info for exposure calculation.
         * As it is very easy to over/under expose
         */
        private void focusOnTouch(final float x, final float y) {
            //cancel previous actions
            mCamera.cancelAutoFocus();
            // 设置对焦方式为自动对焦
            CameraHelper.setCameraFocusMode(Camera.Parameters.FOCUS_MODE_AUTO, mCamera);
//            if (SystemVersionUtil.hasICS()) {
//                // 计算对焦区域
//                Rect focusRect = calculateTapArea(x, y, 1f);
//                List<Camera.Area> focusAreas = new ArrayList<>();
//                focusAreas.add(new Camera.Area(focusRect, 1000));
//
//                Rect meteringRect = calculateTapArea(x, y, 1.5f);
//                List<Camera.Area> meteringAreas = new ArrayList<>();
//                meteringAreas.add(new Camera.Area(meteringRect, 1000));
//                // 设置对焦区域
//                Camera.Parameters parameters = mCamera.getParameters();
//                parameters.setFocusAreas(focusAreas);
//                if (parameters.getMaxNumMeteringAreas() > 0) {
//                    parameters.setMeteringAreas(meteringAreas);
//                }
//                mCamera.setParameters(parameters);
//            }

            mCamera.autoFocus(this);

            mFocusAnimation.cancel();
            mFocusAnimationView.clearAnimation();
            int left = (int) (x - mFocusAnimationView.getWidth() / 2f);
            int top = (int) (y - mFocusAnimationView.getHeight() / 2f);
            int right = left + mFocusAnimationView.getWidth();
            int bottom = top + mFocusAnimationView.getHeight();
            mFocusAnimationView.layout(left, top, right, bottom);
            mFocusAnimationView.setVisibility(VISIBLE);
            mFocusAnimationView.startAnimation(mFocusAnimation);
        }

        /**
         * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
         * <p>
         * Rotate, scale and translate touch rectangle using matrix configured in
         * {@link SurfaceHolder.Callback#surfaceChanged(SurfaceHolder, int, int, int)}
         */
        private Rect calculateTapArea(float x, float y, float coefficient) {
            int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

            int left = clamp((int) x - areaSize / 2, 0, getWidth() - areaSize);
            int top = clamp((int) y - areaSize / 2, 0, getHeight() - areaSize);

            RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
            matrix.mapRect(rectF);

            return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
        }

        private int clamp(int x, int min, int max) {
            if (x > max) {
                return max;
            }
            if (x < min) {
                return min;
            }
            return x;
        }

    }

    /**
     * 放大预览视图任务
     *
     * @author Martin
     */
    private static class ZoomRunnable implements Runnable {

        int destZoom, currentZoom;
        WeakReference<Camera> cameraWeakRef;

        public ZoomRunnable(int destZoom, int currentZoom, Camera camera) {
            this.destZoom = destZoom;
            this.currentZoom = currentZoom;
            cameraWeakRef = new WeakReference<>(camera);
        }

        @Override
        public void run() {
            Camera camera = cameraWeakRef.get();
            if (null == camera)
                return;

            boolean zoomUp = destZoom > currentZoom;
            for (int i = currentZoom; zoomUp ? i <= destZoom : i >= destZoom; i = (zoomUp ? ++i : --i)) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setZoom(i);
                camera.setParameters(parameters);
            }
        }
    }

    /**
     * 录制完成回调接口
     */
    public interface OnRecordListener {
        void onRecordStart();

        void onRecordTooShort();

        void onRecordFinish();

        void onRecordFail();
    }

}
