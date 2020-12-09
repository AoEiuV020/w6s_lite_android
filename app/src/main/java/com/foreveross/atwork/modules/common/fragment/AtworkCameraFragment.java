package com.foreveross.atwork.modules.common.fragment;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.camera.CameraPreview;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.modules.common.activity.AtworkCamera;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by reyzhang22 on 15/11/11.
 */
public class AtworkCameraFragment extends BackHandledFragment {

    private static final String TAG = AtworkCamera.class.getSimpleName();


    private Activity mActivity;

    private CameraPreview mCameraPreview;

    private Camera mCamera;

    private SurfaceView mSurfaceView;

    private FrameLayout mLayout;

    private Button mTakePhoto;



    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if(success){
                mCamera.setOneShotPreviewCallback(null);
                return;
            }

        }
    };

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    private Camera.PictureCallback mRawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_atwork_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                mCamera = Camera.open(0);
                setDisplay(mCamera.getParameters(), mCamera);
                mCamera.startPreview();
                mCamera.autoFocus(mAutoFocusCallback);
                mCameraPreview.setCamera(mCamera);
            } catch (RuntimeException ex){
                AtworkToast.showToast(getString(R.string.camera_not_found));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mCamera != null) {
            mCamera.stopPreview();
            mCameraPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }


    @Override
    protected void findViews(View view) {
        mSurfaceView = view.findViewById(R.id.surfaceView);
        mTakePhoto = view.findViewById(R.id.take_photo);
        mCameraPreview = new CameraPreview(mActivity, mSurfaceView);
        mLayout = view.findViewById(R.id.camera_layout);
        mLayout.addView(mCameraPreview);
        mCameraPreview.setKeepScreenOn(true);
    }

    private void registerListener() {
        mTakePhoto.setOnClickListener(v -> mCamera.takePicture(mShutterCallback, mRawCallback, mJpegCallback));
    }

    private void resetCam() {
        mCamera.startPreview();
        mCamera.autoFocus(mAutoFocusCallback);
        mCameraPreview.setCamera(mCamera);
    }



    /**
     * 刷新系统相册
     * @param file
     */
    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        mActivity.sendBroadcast(mediaScanIntent);
//        previewPhoto(file);
    }

//    private void previewPhoto(File file) {
//        Intent intent = PhotoPreviewActivity.getSelectIntent(mActivity, file.getAbsolutePath());
//        startActivityForResult(intent, REQUEST_CODE_PHOTO_IMAGE);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_PHOTO_IMAGE && resultCode == Activity.RESULT_CANCELED) {
//            return;
//        }
//        if (requestCode == REQUEST_CODE_PHOTO_IMAGE && resultCode == Activity.RESULT_OK) {
//            mActivity.setResult(Activity.RESULT_OK, data);
//            mActivity.finish();
//        }
//    }

    /**
     * 相机展示方向
     * @param parameters
     * @param camera
     */
    private void setDisplay(Camera.Parameters parameters, Camera camera)
    {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8){
            setDisplayOrientation(camera,90);
        }
        else{
            parameters.setRotation(90);
        }

    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try{
            downPolymorphic=camera.getClass().getMethod("setDisplayOrientation", int.class);
            if(downPolymorphic!=null) {
                downPolymorphic.invoke(camera, i);
            }
        }
        catch(Exception e){
            Log.e("Came_e", "图像出错");
        }
    }




    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            try {
                File dir = new File (AtWorkDirUtils.getInstance().getImageDir(mActivity));

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }



    }
}
