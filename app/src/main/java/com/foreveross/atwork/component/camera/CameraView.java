package com.foreveross.atwork.component.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CameraView
extends ViewGroup
implements Camera.PictureCallback,
		   AutoFocusCallback {
	private Size mPreviewSize;
	private Camera mCamera;
	private CameraHost mCameraHost;
	private PreviewStrategy mPreviewStrategy;
	private int mCameraId;
	private Camera.Parameters mPreviewParameters;
	private OnOrientationChangeListener onOrientationChangeListener;
	private int mDisplayOrientation = -1;
	private int mOutputOrientation = -1;
	private int mLastPictureOrientation = -1;
	private boolean mIsInPreviewMode;
	private boolean mIsFaceDetectionEnabled;
	private boolean mIsAutoFocusing;
	private boolean mIsBitmapNeeded;
	private boolean mIsByteArrayNeeded;
	private MediaRecorder mRecorder;
	private OnPictureTakeListener mOnPictureTakeListener;
    private boolean mIsCameraFlashOpen;
	private Context mContext;
	
	public CameraView(Context context) {
		super(context);
		mContext = context;
		onOrientationChangeListener = new OnOrientationChangeListener(context);
	}
	


	public CameraHost getHost() {
		return mCameraHost;
	}
	
	public void setOnPictureTakeListener(OnPictureTakeListener onPictureTakeListener) {
		mOnPictureTakeListener = onPictureTakeListener;
	}
	
	public void setHost(CameraHost cameraHost, SurfaceView sv) {
		mCameraHost = cameraHost;
		if(mCameraHost.getDeviceProfile().isTextureViewUsed()) {
			throw new UnsupportedOperationException("Preview with texture view is not supported");
		}
		
		mPreviewStrategy = new SurfacePreviewStrategy(this, sv);
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void onResume() {
//		addView(mPreviewStrategy.getWidget());
		if(mCamera == null) {
			mCameraId = getHost().getCameraId();
			if(mCameraId >= 0) {

				try {
					mCamera = Camera.open(mCameraId);
                    setCameraParameters();
					if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
						onOrientationChangeListener.enable();
					}
					setCameraDisplayOrientation(mCameraId, mCamera);
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && getHost() instanceof Camera.FaceDetectionListener) {
						mCamera.setFaceDetectionListener((Camera.FaceDetectionListener)getHost());
					}
                    previewCreated();
                    startPreview();
				} catch(Exception e) {
					getHost().onCameraFailed(CameraHost.FailureReason.UNKNOWN);
				}
			} else {
				getHost().onCameraFailed(CameraHost.FailureReason.NO_CAMERAS_ROUND);
			}
		}
	}
	
	public void onPause() {
		if(mCamera != null) {
			previewDestroyed();
			removeView(mPreviewStrategy.getWidget());
		}
	}

    private void setCameraParameters() {
        if (getHost() == null) {
            return ;
        }
        SimpleCameraHost host = (SimpleCameraHost)getHost();
        if (host.isUsingFrontFacingCamera()) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (mIsCameraFlashOpen) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }

        mCamera.setParameters(parameters);
    }

    /**
     * 闪光灯切换
     * @param isCameraFlashOpen
     */
    public void openCameraFlash(boolean isCameraFlashOpen) {

        mIsCameraFlashOpen = isCameraFlashOpen;
        Camera.Parameters parameters = mCamera.getParameters();
        if (isCameraFlashOpen) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
        mCamera.setParameters(parameters);
    }

    /**
     * 摄像头切换
     */
    public boolean onCameraSwitch(boolean isFrontCamera) {

        if (getHost() == null) {
            return false;
        }
        SimpleCameraHost host = (SimpleCameraHost)getHost();
        host.setUsingFrontFacingCamera(isFrontCamera);
        previewDestroyed();

        mCameraId = host.getCameraId();
        if(mCameraId >= 0) {
            try {
                mCamera = Camera.open(mCameraId);
                setCameraParameters();
                if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    onOrientationChangeListener.enable();
                }
                setCameraDisplayOrientation(mCameraId, mCamera);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && getHost() instanceof Camera.FaceDetectionListener) {
                    mCamera.setFaceDetectionListener((Camera.FaceDetectionListener)getHost());
                }
                previewCreated();
                startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(width, height);

		if(width > 0 && height > 0) {
			if(mCamera != null) {
				Camera.Size newSize = null;
				try {
					if(getHost().getRecordingSupportMode() != CameraHost.RecordingSupportMode.STILL_ONLY) {
						Camera.Size deviceHint = CameraDeviceProfile.getInstance().getPrefereredPreviewSizeForVideo(getDisplayOrientation(), width, height, mCamera.getParameters());
						newSize = getHost().getPreferredPreviewSizeForVideo(getDisplayOrientation(), width, height, mCamera.getParameters(), deviceHint);
					}

					if(newSize == null || newSize.width*newSize.height < 65536) {
						newSize = getHost().getPreviewSize(getDisplayOrientation(), width, height, mCamera.getParameters());
					}
				} catch(Exception e) {
				}

				if(newSize != null) {
					if(mPreviewSize == null) {
						mPreviewSize = newSize;
					} else if(mPreviewSize.width != newSize.width || mPreviewSize.height != newSize.height) {
						if(mIsInPreviewMode) {
							stopPreview();
						}

						mPreviewSize = newSize;
						initPreview(width, height, false);
					}
				}
			}
		}
	}


    public boolean onTouchEventForFragment(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    private float mDist;
    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }


    @Override
	protected void onLayout(boolean isChanged, int left, int top, int right, int bottom) {
		if(isChanged && getChildCount() > 0) {
			final View child = getChildAt(0);
			final int width = right - left;
			final int height = bottom - top;
			int previewWidth = width;
			int previewHeight = height;
			
			if(mPreviewSize != null) {
				if(getDisplayOrientation() == 90 || getDisplayOrientation() == 270) {
					previewWidth = mPreviewSize.height;
					previewHeight = mPreviewSize.width;
				} else {
					previewWidth = mPreviewSize.width;
					previewHeight = mPreviewSize.height;
				}
			}
			
			boolean isFirstStrategyUsed = (width*previewHeight > height*previewWidth);
			boolean isFullBleedUsed = getHost().isFullBleedPreviewUsed();
			if((isFirstStrategyUsed && !isFullBleedUsed) || (!isFirstStrategyUsed && isFullBleedUsed)) {
				final int scaledChildWidth = previewWidth*height/previewHeight;
				child.layout((width - scaledChildWidth)/2, 0, (width + scaledChildWidth)/2, height);
			} else {
				final int scaledChildHeight = previewHeight*width/previewWidth;
				child.layout(0, (height - scaledChildHeight)/2, width, (height + scaledChildHeight)/2);
			}
		}
	}
	
	public int getDisplayOrientation() {
		return (mDisplayOrientation);
	}
	
	public void lockToLandscape(boolean isEnabled) {
		if(isEnabled) {
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			onOrientationChangeListener.enable();
		} else {
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			onOrientationChangeListener.disable();
		}
		
		post(new Runnable() {
			@Override
			public void run() {
				setCameraDisplayOrientation(mCameraId, mCamera);
			}
		});
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		mCamera.setParameters(mPreviewParameters);
		if(data != null) {
			new ImageCleanupTask(mContext, data, mCameraId, getHost(), getContext().getCacheDir(), mIsBitmapNeeded, mIsByteArrayNeeded, mDisplayOrientation, mOnPictureTakeListener).start();
		}
		
		if(!getHost().isSingleShotMode()) {
			startPreview();
		}
	}
	
	public void restartPreview() {
		if(!mIsInPreviewMode) {
			startPreview();
		}
	}
	
	public void takePicture(boolean isBitmapNeeded, boolean isByteArrayNeeded) {
		if(mIsInPreviewMode) {
			if(mIsAutoFocusing) {
				throw new IllegalStateException("Camera cannot take a picture while auto-focusing");
			} else {
				mIsBitmapNeeded = isBitmapNeeded;
				mIsByteArrayNeeded = isByteArrayNeeded;
				
				mPreviewParameters = mCamera.getParameters();
				Camera.Parameters pictureParameters = mCamera.getParameters();
				Camera.Size pictureSize = getHost().getPictureSize(pictureParameters);
				pictureParameters.setPictureSize(pictureSize.width, pictureSize.height);
				
				setCameraPictureOrientation();
				
				mCamera.takePicture(getHost().getShutterCallback(), null, this);
				mIsInPreviewMode = false;
			}
		}
	}
	
	public void startRecording()
	throws Exception {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			throw new UnsupportedOperationException("Recording is not supported in this version of Android system");
		}
		
		setCameraPictureOrientation();
		stopPreview();
		mCamera.unlock();
		
		try {
			mRecorder = new MediaRecorder();
			mRecorder.setCamera(mCamera);
			getHost().configureRecorderAudio(mCameraId, mRecorder);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			getHost().configureRecorderProfile(mCameraId, mRecorder);
			getHost().configureRecorderOutput(mCameraId, mRecorder);
			mRecorder.setOrientationHint(mOutputOrientation);
			mPreviewStrategy.attach(mRecorder);
			mRecorder.prepare();
			mRecorder.start();
		} catch(IOException ioe) {
			mRecorder.release();
			mRecorder = null;
			throw ioe;
		}
	}
	
	public void stopRecording()
	throws IOException {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			throw new UnsupportedOperationException("Video recording is not supported in this version of Android system");
		}
		
		MediaRecorder recorder = mRecorder;
		mRecorder = null;
		recorder.stop();
		recorder.release();
		mCamera.reconnect();
	}
	
	public void startAutoFocus() {
		if(mIsInPreviewMode) {
			mCamera.autoFocus(this);
			mIsAutoFocusing = true;
		}
	}
	
	public void cancelAutoFocus() {
		mCamera.cancelAutoFocus();
	}
	
	public boolean isAutoFocusAvailable() {
		return mIsInPreviewMode;
	}
	
	@Override
	public void onAutoFocus(boolean isSuccess, Camera camera) {
		mIsAutoFocusing = false;
		if(getHost() instanceof AutoFocusCallback) {
			getHost().onAutoFocus(isSuccess, camera);
		}
	}
	
	public String getFlashMode() {
		return (mCamera.getParameters().getFlashMode());
	}
	
	public ZoomTransaction zoomTo(int level) {
		if(mCamera == null) {
			throw new IllegalStateException("No camera is found");
		} else {
			Camera.Parameters parameters = mCamera.getParameters();
			if(level >= 0 && level <= parameters.getMaxZoom()) {
				return new ZoomTransaction(mCamera, level);
			} else {
				throw new IllegalArgumentException(String.format("Zoom level (%d) exceeds limit (%d)", level, parameters.getMaxZoom()));
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void startFaceDetection() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && mCamera != null 
				&& !mIsFaceDetectionEnabled && mCamera.getParameters().getMaxNumDetectedFaces() > 0) {
			mCamera.startFaceDetection();
			mIsFaceDetectionEnabled = true;
		}
	}
	
	public void stopFaceDetection() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && mCamera != null && mIsFaceDetectionEnabled) {
			mCamera.stopFaceDetection();
			mIsFaceDetectionEnabled = false;
		}
	}
	
	public boolean isZoomSupported() {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(mCameraId, cameraInfo);
		
		return (getHost().getDeviceProfile().isZoomSupported(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT));
	}
	
	public void previewCreated() {
		if(mCamera != null) {
			try {
				mPreviewStrategy.attach(mCamera);
			} catch(IOException ioe) {
				getHost().handleException(ioe);
			}
		}
	}
	
	public void previewDestroyed() {
		if(mCamera != null) {
			previewStopped();
			mCamera.release();
			mCamera = null;
		}
	}
	
	public void previewReset(int width, int height) {
		previewStopped();
		initPreview(width, height);
	}
	
	private void previewStopped() {
		if(mIsInPreviewMode) {
			stopPreview();
		}
	}
	
	public void initPreview(int width, int height) {
		initPreview(width, height, true);
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void initPreview(int width, int height, boolean isFirstRun) {
		if(mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				parameters.setRecordingHint(getHost().getRecordingSupportMode() != CameraHost.RecordingSupportMode.STILL_ONLY);
			}
			
			requestLayout();
			
			mCamera.setParameters(getHost().adjustPreviewParameters(parameters));
			startPreview();
		}
	}
	
	private void startPreview() {
		mCamera.startPreview();
		mIsInPreviewMode = true;
		getHost().setAutoFocusEnabled(true);
	}
	
	private void stopPreview() {
		mIsInPreviewMode = false;
		getHost().setAutoFocusEnabled(false);
		mCamera.stopPreview();
	}
	
	private void setCameraDisplayOrientation(int cameraId, Camera camera) {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, cameraInfo);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int degrees = 0;
		switch(getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
			
		case Surface.ROTATION_90:
			degrees = 90;
			break;
			
		case Surface.ROTATION_180:
			degrees = 180;
			break;
			
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		
		if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
			mDisplayOrientation = (cameraInfo.orientation + degrees)%360;
			mDisplayOrientation = (360 - mDisplayOrientation)%360;
		} else {
			mDisplayOrientation = (cameraInfo.orientation - degrees + 360)%360;
		}
		
		boolean wasInPreviewMode = mIsInPreviewMode;
		if(mIsInPreviewMode) {
			stopPreview();
		}
		
		mCamera.setDisplayOrientation(mDisplayOrientation);
		if(wasInPreviewMode) {
			startPreview();
		}
	}
	
	private void setCameraPictureOrientation() {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		
		Camera.getCameraInfo(mCameraId, cameraInfo);
		if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
			mOutputOrientation = getCameraPictureRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation());
		} else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			mOutputOrientation = (360 - mDisplayOrientation)%360;
		} else {
			mOutputOrientation = mDisplayOrientation;
		}
		
		if(mLastPictureOrientation != mOutputOrientation) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setRotation(mOutputOrientation);
			mCamera.setParameters(parameters);
			mLastPictureOrientation = mOutputOrientation;
		}
	}
	
	private int getCameraPictureRotation(int orientation) {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(mCameraId, cameraInfo);
		int rotation = 0;
		
		orientation = (orientation + 45)/90*90;
		
		if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			rotation = (cameraInfo.orientation - orientation + 360)%360;
		} else {
			rotation = (cameraInfo.orientation + orientation)%360;
		}
		
		return rotation;
	}
	
	private Activity getActivity() {
		return (Activity)getContext();
	}
	
	private class OnOrientationChangeListener
	extends OrientationEventListener {
		public OnOrientationChangeListener(Context context) {
			super(context);
			disable();
		}
		
		@Override
		public void onOrientationChanged(int orientation) {
            try {
                if(mCamera != null && orientation != ORIENTATION_UNKNOWN) {
                    int newOutputOrientation = getCameraPictureRotation(orientation);
                    if(newOutputOrientation != mOutputOrientation) {
                        mOutputOrientation = newOutputOrientation;
                        Camera.Parameters parameters = mCamera.getParameters();
                        parameters.setRotation(mOutputOrientation);
                        mCamera.setParameters(parameters);
                        mLastPictureOrientation = mOutputOrientation;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

		}
	}
	
	public interface OnPictureTakeListener {
		void onPictureTake(File file);
	}


    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
