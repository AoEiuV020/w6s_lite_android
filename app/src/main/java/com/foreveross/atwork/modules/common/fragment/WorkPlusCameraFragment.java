package com.foreveross.atwork.modules.common.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.camera.CameraDeviceProfile;
import com.foreveross.atwork.component.camera.CameraHost;
import com.foreveross.atwork.component.camera.CameraHostProvider;
import com.foreveross.atwork.component.camera.CameraView;
import com.foreveross.atwork.component.camera.SimpleCameraHost;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.modules.common.activity.PhotoPreviewActivity;
import com.foreveross.atwork.modules.common.activity.WorkPlusCameraActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;

import java.io.File;

/**
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

/**
 * Created by reyzhang22 on 15/11/12.
 */
public class WorkPlusCameraFragment extends BackHandledFragment implements
        CameraView.OnPictureTakeListener, CameraHostProvider, WorkPlusCameraActivity.onTouchForFragmentListener {

    private static final String TAG = WorkPlusCameraFragment.class.getSimpleName();

    private static final int REQUEST_CODE_PHOTO_IMAGE = 0x15;

    private Activity mActivity;

    private CameraHost mCameraHost;

    private CameraView mCameraView;
    private ImageView mBtnTakePhoto;

    private TextView mCancel;

    private SurfaceView mSurfaceView;

    private LinearLayout mLighterLayout;

    private TextView mLighterStatus;

    private ImageView mCameraSwitcher;

    private boolean mIsCameraFlash = false;

    private boolean mIsUsingFrontCamera = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCameraHost = new SimpleCameraHost(activity, new File(AtWorkDirUtils.getInstance().getImageDir(mActivity)));
        mActivity= activity;
        ((WorkPlusCameraActivity)mActivity).setOnTouchListener(this);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }


    @Override
    public void onPictureTake(File file) {
        mBtnTakePhoto.setClickable(true);
        refreshGallery(file);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO_IMAGE && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_CODE_PHOTO_IMAGE && resultCode == Activity.RESULT_OK) {
            mActivity.setResult(Activity.RESULT_OK, data);
            mActivity.finish();
        }
    }

    /**
     * 刷新系统相册
     * @param file
     */
    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        BaseApplicationLike.baseContext.sendBroadcast(mediaScanIntent);
        previewPhoto(file);
    }

    private void previewPhoto(File file) {
        Intent intent = PhotoPreviewActivity.getIntent(mActivity, file.getAbsolutePath());
        startActivityForResult(intent, REQUEST_CODE_PHOTO_IMAGE);
    }




    @Override
    protected void findViews(View view) {
        mSurfaceView = view.findViewById(R.id.sv);
        mCameraView = new CameraView(mActivity);
        mCameraView.setHost(new WorkPlusCameraHost(mActivity), mSurfaceView);
        mCameraView.setOnPictureTakeListener(this);

        FrameLayout viewGroup = view.findViewById(R.id.panel_camera_preview);
        viewGroup.addView(mCameraView);

        mBtnTakePhoto = view.findViewById(R.id.button_take_photo);
        mCancel = view.findViewById(R.id.close_camera);

        mLighterLayout = view.findViewById(R.id.camera_lighter_switcher);
        mLighterStatus = view.findViewById(R.id.camera_lighter_status);
        mCameraSwitcher = view.findViewById(R.id.camera_switcher);
    }

    private void registerListener() {
        mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraView == null) {
                    return;
                }
                if (CommonUtil.isFastClick(2000)) {
                    return;
                }
                mCameraView.restartPreview();
                mCameraView.takePicture(false, true);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });


        mLighterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraView.openCameraFlash(mIsCameraFlash);
                if (mIsCameraFlash) {
                    mIsCameraFlash = false;
                    mLighterStatus.setText(getString(R.string.open));

                } else {
                    mIsCameraFlash = true;
                    mLighterStatus.setText(getString(R.string.close));
                }
            }
        });

        mCameraSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsUsingFrontCamera = !mIsUsingFrontCamera;
                boolean result = mCameraView.onCameraSwitch(mIsUsingFrontCamera);
                if (!result) {
                    AtworkToast.showToast(getString(R.string.switch_camera_fail));
                }
            }
        });
    }


    @Override
    public CameraHost getCameraHost() {
        return mCameraHost;
    }

    @Override
    public void onTouchEventForFragment(MotionEvent event) {
        mCameraView.onTouchEventForFragment(event);
    }

    private class WorkPlusCameraHost extends SimpleCameraHost {

        public WorkPlusCameraHost(Context context) {
            super(context, new File(AtWorkDirUtils.getInstance().getImageDir(mActivity)));
            CameraDeviceProfile.initConfiguration();
            setUsingFrontFacingCamera(mIsUsingFrontCamera);
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            return super.adjustPreviewParameters(parameters);
        }
    }


}
