package com.foreveross.atwork.component.camera;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaRecorder;

public interface CameraHost
extends Camera.AutoFocusCallback {
	enum RecordingSupportMode {
		STILL_ONLY, VIDEO_ONLY, ANY
	}
	
	enum FailureReason {
		NO_CAMERAS_ROUND(1), UNKNOWN(2);
		
		private int code;
		FailureReason(int code) {
			this.code = code;
		}
	}
	
	Camera.Parameters adjustPictureParameters(Camera.Parameters parameters);
	Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters);
	
	void setAutoFocusEnabled(boolean isEnabled);
	
	void configureRecorderAudio(int cameraId, MediaRecorder recorder);
	void configureRecorderOutput(int cameraId, MediaRecorder recorder);
	void configureRecorderProfile(int cameraId, MediaRecorder recorder);
	
	int getCameraId();
	CameraDeviceProfile getDeviceProfile();
	
	Camera.Size getPictureSize(Camera.Parameters parameters);
	Camera.Size getPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters);
	Camera.Size getPreferredPreviewSizeForVideo(int displayOrientation, int width, int height, Camera.Parameters parameters, Camera.Size deviceHint);
	
	Camera.ShutterCallback getShutterCallback();
	
	void handleException(Exception e);
	
	boolean isFrontFaceCameraMirrored();
	
	boolean isExifBasedRotation();
	
	void saveImage(Bitmap bitmap);
	void saveImage(byte[] bytes, CameraView.OnPictureTakeListener listener);
	
	boolean isSingleShotMode();
	
	RecordingSupportMode getRecordingSupportMode();
	
	void onCameraFailed(FailureReason failureReason);
	
	boolean isFullBleedPreviewUsed();
}
