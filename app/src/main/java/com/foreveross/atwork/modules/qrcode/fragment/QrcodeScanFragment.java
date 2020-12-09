package com.foreveross.atwork.modules.qrcode.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.qrcode.CaptureActivityHandler;
import com.foreveross.atwork.component.qrcode.view.ViewfinderView;
import com.foreveross.atwork.cordova.plugin.WorkPlusBarcodeScannerPlugin;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.modules.image.activity.MediaSelectActivity;
import com.foreveross.atwork.modules.qrcode.service.QrcodeManager;
import com.foreveross.atwork.qrcode.zxing.InactivityTimer;
import com.foreveross.atwork.qrcode.zxing.ScanConstants;
import com.foreveross.atwork.qrcode.zxing.ZxingQrcodeInterface;
import com.foreveross.atwork.qrcode.zxing.camera.CameraManager;
import com.foreveross.atwork.qrcode.zxing.decode.BitmapQrcodeDecoder;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ImageViewUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static com.foreveross.atwork.modules.qrcode.service.QrcodeManager.OCT_RESULT_PREFIX;

/**
 * Created by dasunsy on 15/12/9.
 */
public class QrcodeScanFragment extends BackHandledFragment implements SurfaceHolder.Callback, ZxingQrcodeInterface {

    private static final String TAG = QrcodeScanFragment.class.getSimpleName();

    private static final int REQUEST_SCAN_CODE = 10020;
    private static final int REQUEST_SCAN_PHOTO_CODE = 10021;

    private CameraManager mCameraManager;
    private Handler mRestartScanHandler = new Handler();
    private CaptureActivityHandler mHandler;
    private ViewfinderView mViewfinderView;
    private RelativeLayout mView;
    private SurfaceView mSurfaceView;
    private TextView mTvScanTip;
    private ImageView mIvScanTip;
    private View mFakeStatusBar;
    private ImageView mIvBack;

    private Vector<BarcodeFormat> mDecodeFormats;
    private InactivityTimer mInactivityTimer;

    private String mCharacterSet;
    private boolean mHasSurface;

    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private boolean mVibrate;

    private boolean mFromCordovaRequest;
    private boolean mIsNativeAction;
    private AudioManager mAudioManager;
    private Activity mActivity;

    private TextView mLightUpTv;
    private boolean mIsLighting = false;
    private ImageView mVerticalLightUp;

    private TextView mQrAndBar;
    private TextView mBarCode;
    private boolean mIsBarCodeSelected = false;

    private TextView mPhotoAlbumTv;

    private BitmapQrcodeDecoder mQrcodeDecoder;

    private boolean mIsRecognizing = false;

    private Handler mLightingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int isDark = msg.arg1;
            showLightStatus(isDark == 1 ? true : false);
        }

    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mQrcodeDecoder = new BitmapQrcodeDecoder(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode_scan, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFromCordovaRequest = bundle.getBoolean(WorkPlusBarcodeScannerPlugin.DATA_FROM_CORDOVA, false);
            mIsNativeAction = bundle.getBoolean(WorkPlusBarcodeScannerPlugin.DATA_IS_NATIVE_ACTION, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
        changeViewStatus();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adjustTvScanTip();

//        CameraManager.initData(mActivity);


        mHasSurface = false;
        mInactivityTimer = new InactivityTimer(mActivity);

        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public void onResume() {
        super.onResume();

        // 相机初始化的动作需要开启相机并测量屏幕大小，这些操作
        // 不建议放到onCreate中，因为如果在onCreate中加上首次启动展示帮助信息的代码的 话，
        // 会导致扫描窗口的尺寸计算有误的bug
        mCameraManager = new CameraManager(BaseApplicationLike.baseContext);
        mViewfinderView.setCameraManager(mCameraManager);

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats = null;
        mCharacterSet = null;


        mPlayBeep = true;
        AudioManager audioService = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        mCameraManager.closeDriver();
//        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        mInactivityTimer.shutdown();
        //remove all runnable
        if (null != mRestartScanHandler) {
            mRestartScanHandler.removeCallbacksAndMessages(null);
        }
        if (null != mLightingHandler) {
            mLightingHandler.removeCallbacksAndMessages(null);
            mLightingHandler = null;
        }
        if (mCameraManager != null){
            mCameraManager.release();
        }
        super.onDestroy();
    }

    @Override
    protected void findViews(View view) {
        mView = view.findViewById(R.id.qr_camera_view);
        mViewfinderView = view.findViewById(R.id.viewfinder_view);
        mSurfaceView = view.findViewById(R.id.preview_view);
        mTvScanTip = view.findViewById(R.id.tv_scan_tip);
        mFakeStatusBar = view.findViewById(R.id.v_fake_statusbar);
        mIvBack = view.findViewById(R.id.back_btn);
        mLightUpTv = view.findViewById(R.id.turn_light);
        mVerticalLightUp = view.findViewById(R.id. barcode_vertical_light);
        mQrAndBar = view.findViewById(R.id.q_and_b);
        mBarCode = view.findViewById(R.id.barcode);
        mPhotoAlbumTv = view.findViewById(R.id.photo_album);
        mIvScanTip = view.findViewById(R.id.barcode_vertical_tip);
    }


    @Override
    public View getFakeStatusBar() {
        return mFakeStatusBar;
    }

    public void initBundle(boolean cordovaRequest, boolean scannedType) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(WorkPlusBarcodeScannerPlugin.DATA_FROM_CORDOVA, cordovaRequest);
        bundle.putBoolean(WorkPlusBarcodeScannerPlugin.DATA_IS_NATIVE_ACTION, scannedType);
        setArguments(bundle);
    }

    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        LogUtil.e("result -> " + "handleDecode ~~~~");


        if (mIsRecognizing) {
            return;
        }
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();

        LogUtil.e("result -> " + resultString);

        if (!mIsNativeAction && (mFromCordovaRequest && !resultString.startsWith(OCT_RESULT_PREFIX))) {
            Intent intent = new Intent();
            intent.putExtra("result", resultString);
            getActivity().setResult(Activity.RESULT_OK, intent);
            //结束当前这个Activity对象的生命
            getActivity().finish();
            return;
        }

        QrcodeManager.getInstance().handleSelfProtocol(mActivity, resultString, this::restartQrScan);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_SCAN_CODE && resultCode == Activity.RESULT_OK) {
            finish();
        }
        if (requestCode == REQUEST_SCAN_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            final List<MediaItem> selectedList = (List<MediaItem>) data.getSerializableExtra(MediaSelectActivity.RESULT_SELECT_IMAGE_INTENT);
            if (ListUtil.isEmpty(selectedList)) {
                return;
            }
            MediaItem mediaItem = selectedList.get(0);
            mTvScanTip.setText(getText(R.string.recognizing));
            mIsRecognizing = true;
            getQrCodeResult(mediaItem, new OnLocalBitmapDecodeListener() {
                @Override
                public void onDecodeSuccess(Result result, Bitmap bitmap) {
                    mActivity.runOnUiThread(() -> {
                        mIsRecognizing = false;
                        handleDecode(result, bitmap);
                    });
                }

                @Override
                public void onDecodeFail() {
                    mActivity.runOnUiThread(() -> {
                        mIsRecognizing = false;
                        mTvScanTip.setText(getText(R.string.scan_text));
                        AtworkToast.showResToast(R.string.recognize_fail);

                    });
                }
            });
        }
    }

    public void restartQrScan() {
        //防止太频繁扫描二维码
        mRestartScanHandler.postDelayed(() -> {

            Handler handler = getHandler();
            if(null != handler) {
                handler.sendEmptyMessage(ScanConstants.RESTART_PREVIEW);
            }

        }, 100);

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            mCameraManager.openDriver(surfaceHolder);

            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, mDecodeFormats, null,
                        mCharacterSet, mCameraManager);
            }
            mCameraManager.requestLightUpPreviewFrame(mLightingHandler, 1011);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            popAuthAlert();
        }

    }



    private void popAuthAlert() {
        final AtworkAlertDialog atworkAlertDialog = new AtworkAlertDialog(mActivity, AtworkAlertDialog.Type.SIMPLE)
                .setContent(R.string.tip_camera_fail_no_auth)
                .hideDeadBtn();

        atworkAlertDialog.setClickBrightColorListener(dialog -> {
            atworkAlertDialog.dismiss();
            mActivity.finish();
        });

        atworkAlertDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (KeyEvent.KEYCODE_BACK == keyCode) {
                atworkAlertDialog.dismiss();
                mActivity.finish();

                return true;
            }
            return false;
        });

        atworkAlertDialog.show();
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();

    }



    private void adjustTvScanTip() {
        mTvScanTip.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTvScanTip.getLayoutParams();
                int marginLength = (ScreenUtils.getScreenHeight(mActivity) >> 2) - mTvScanTip.getHeight() + 120;
                lp.setMargins(0, 0, 0, marginLength);
                mTvScanTip.setLayoutParams(lp);
                mTvScanTip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());

        mLightUpTv.setOnClickListener(view ->{
            turnLight();
        });

        mVerticalLightUp.setOnClickListener(view -> {
            turnLight();
        });

        mBarCode.setOnClickListener(view -> {
            mIsBarCodeSelected = true;
            mCameraManager.setFramingRect(false);
            mViewfinderView.invalidateTransform(false);
            changeViewStatus();

        });

        mQrAndBar.setOnClickListener(view ->{
            mIsBarCodeSelected = false;
            mCameraManager.setFramingRect(true);
            mViewfinderView.invalidateTransform(true);
            changeViewStatus();
        });

        mPhotoAlbumTv.setOnClickListener(view -> {
            AndPermission
                    .with(mActivity)
                    .runtime()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                    .onGranted(granted -> {
                        Intent getAlbum = MediaSelectActivity.getIntent(BaseApplicationLike.baseContext);
                        getAlbum.putExtra(MediaSelectActivity.DATA_OPEN_FULL_MODE_SELECT, true);
                        getAlbum.setType("image/*");
                        startActivityForResult(getAlbum, REQUEST_SCAN_PHOTO_CODE);
                    })
                    .onDenied(denied ->  AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .start();

        });

        mSurfaceView.setOnTouchListener((v, event) -> {
            Camera camera = mCameraManager.getCamera();
            if(null == camera) {
                return false;
            }

            Camera.Parameters params = camera.getParameters();
            int action = event.getAction();

            if (event.getPointerCount() > 1) {
                // handle multi-touch events
                if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    mDist = getFingerSpacing(event);
                } else if (action == MotionEvent.ACTION_MOVE
                        && params.isZoomSupported()) {
                    camera.cancelAutoFocus();
                    handleZoom(event, params);
                }
            } else {
                // handle single touch events
                if (action == MotionEvent.ACTION_UP) {
                    handleFocus(event, params);
                }
            }
            return true;
        });
    }

    private void turnLight() {
        mIsLighting = !mIsLighting;
        mCameraManager.setTorch(mIsLighting);
        changeLightingViewStatus();
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCameraManager.setCameraDisplayOrientation(mActivity, 1);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;

    }


    @Override
    public Activity getQrActivity() {
        return mActivity;
    }

    @Override
    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    public ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();

                int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setVolume(volume, volume);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = mediaPlayer -> mediaPlayer.seekTo(0);


    private void showLightStatus(boolean lightUp) {
        if (mIsLighting) {
            return;
        }
        if (lightUp) {
            mLightUpTv.setVisibility(View.VISIBLE);
            changeLightingViewStatus();
            return;
        }
        mLightUpTv.setVisibility(View.GONE);
        mVerticalLightUp.setVisibility(View.GONE);
    }

    private void changeViewStatus() {
        try {
            if (mIsBarCodeSelected) {
                mQrAndBar.setCompoundDrawablesWithIntrinsicBounds(0, ImageViewUtil.getResourceInt("q_and_b_unselect"), 0, 0);
                mBarCode.setCompoundDrawablesWithIntrinsicBounds(0, ImageViewUtil.getResourceInt("barcode_selected"), 0, 0);
                mBarCode.setTextColor(mActivity.getResources().getColor(R.color.common_blue_bg));
                mQrAndBar.setTextColor(mActivity.getResources().getColor(R.color.white));
                mTvScanTip.setVisibility(View.GONE);
                mIvScanTip.setVisibility(View.VISIBLE);
                return;
            }
            mQrAndBar.setCompoundDrawablesWithIntrinsicBounds(0, ImageViewUtil.getResourceInt("q_and_b_selected"), 0, 0);
            mBarCode.setCompoundDrawablesWithIntrinsicBounds(0, ImageViewUtil.getResourceInt("barcode_unselect"), 0, 0);
            mBarCode.setTextColor(mActivity.getResources().getColor(R.color.white));
            mQrAndBar.setTextColor(mActivity.getResources().getColor(R.color.common_blue_bg));
            mTvScanTip.setVisibility(View.VISIBLE);
            mIvScanTip.setVisibility(View.GONE);
        } catch (Exception e) {

        }

    }

    private void changeLightingViewStatus() {
        try {
            mLightUpTv.setVisibility(mIsBarCodeSelected ? View.GONE : View.VISIBLE);
            mVerticalLightUp.setVisibility(mIsBarCodeSelected ? View.VISIBLE : View.GONE);
            if (mIsLighting) {
                mVerticalLightUp.setBackgroundResource(R.mipmap.icon_qrcode_close);
                mLightUpTv.setCompoundDrawablesWithIntrinsicBounds(0, ImageViewUtil.getResourceInt("icon_close_camera_light"), 0, 0);
                mLightUpTv.setText(getText(R.string.light_close));
                return;
            }
            mVerticalLightUp.setBackgroundResource(R.mipmap.icon_qrcode_light);
            mLightUpTv.setCompoundDrawablesWithIntrinsicBounds(0, ImageViewUtil.getResourceInt("icon_open_camera_light"), 0, 0);
            mLightUpTv.setText(getText(R.string.light_up));
        } catch (Exception e) {

        }
    }



    private void getQrCodeResult(MediaItem mediaItem, OnLocalBitmapDecodeListener listener) {
        new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... params) {
                Result result = null;

                Bitmap judgeBitmap = BitmapFactory.decodeFile(mediaItem.filePath);
                if (null != judgeBitmap) {
                    byte[] compressBye = ImageShowHelper.compressImageForQrcodeRecognize(BitmapUtil.Bitmap2Bytes(judgeBitmap, false));
                    judgeBitmap = BitmapUtil.Bytes2Bitmap(compressBye);
                }

                if (null != judgeBitmap) {
                    result = mQrcodeDecoder.getRawResult(judgeBitmap);
                }
                if (result == null || judgeBitmap == null) {
                    listener.onDecodeFail();
                    return null;
                }
                listener.onDecodeSuccess(result, judgeBitmap);

                return result;

            }

            @Override
            protected void onPostExecute(Result result) {

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //下面方法尝试去获取是否有二维码result

    }

    private float mDist = 0;

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            // zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            // zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCameraManager.getCamera().setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null
                && supportedFocusModes
                .contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCameraManager.getCamera().autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    interface OnLocalBitmapDecodeListener {
         void onDecodeSuccess(Result result, Bitmap bitmap);
         void onDecodeFail();
    }


}
