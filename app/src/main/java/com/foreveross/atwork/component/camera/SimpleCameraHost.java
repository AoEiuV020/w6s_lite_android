package com.foreveross.atwork.component.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaActionSound;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;

import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SimpleCameraHost 
implements CameraHost {
	private static final String[] SCAN_TYPES = { "image/jpeg" };
	private Context context;
	private File photoFolder;
    private int mLastCameraId = -100;
    private boolean mUsingFrontFacingCamera = false;
	
	public SimpleCameraHost(Context context, File photoFolder) {
		this.context = context;
		this.photoFolder = photoFolder;
	}
	
	@Override
	public Camera.Parameters adjustPictureParameters(Camera.Parameters parameters) {
		return parameters;
	}
	
	@Override
	public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
		return parameters;
	}
	
	@Override
	public void configureRecorderAudio(int cameraId, MediaRecorder mediaRecorder) {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	}
	
	@Override
	public void configureRecorderOutput(int cameraId, MediaRecorder mediaRecorder) {
		mediaRecorder.setOutputFile(getVideoPath().getAbsolutePath());
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void configureRecorderProfile(int cameraId, MediaRecorder mediaRecorder) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_HIGH)) {
			mediaRecorder.setProfile(CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH));
		} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_LOW)) {
			mediaRecorder.setProfile(CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW));
		} else {
			throw new IllegalStateException("Cannot find valid camcorder profile");
		}
	}
	
	@Override
	public int getCameraId() {
		int cameraCount = Camera.getNumberOfCameras();
		int cameraId = -1;
		if(cameraCount > 0) {
			cameraId = 0;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			for(int index = 0; index < cameraCount; index++) {
				Camera.getCameraInfo(index, cameraInfo);
				if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK && !isUsingFrontFacingCamera() ) {
					cameraId = index;
                    mLastCameraId = index;
					break;
				} else if(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT && isUsingFrontFacingCamera()) {
					cameraId = index;
                    mLastCameraId = index;
					break;
				} else {
                    cameraId = index;
                }
			}
		}
		
		return cameraId;
	}
	
	@Override
	public CameraDeviceProfile getDeviceProfile() {
		return CameraDeviceProfile.getInstance();
	}
	
	@Override
	public Camera.Size getPictureSize(Camera.Parameters parameters) {
		CameraDeviceProfile cameraDeviceProfile = getDeviceProfile();
		return (CameraUtils.getLargestPictureSize(cameraDeviceProfile.getMinPictureHeight(), cameraDeviceProfile.getMaxPictureHeight(), parameters));
	}
	
	@Override
	public Camera.Size getPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
		return CameraUtils.getBestAspectPreviewSize(displayOrientation, width, height, parameters);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public Camera.Size getPreferredPreviewSizeForVideo(int displayOrientation, int width, int height, Camera.Parameters parameters, Camera.Size deviceHint) {
		Camera.Size size = null;
		if(deviceHint != null) {
			size = deviceHint;
		} else {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				size = parameters.getPreferredPreviewSizeForVideo();
			}
		}
		
		return size;
	}
	
	@Override
	public Camera.ShutterCallback getShutterCallback() {
		return null;
	}
	
	@Override
	public void handleException(Exception e) {
	}
	
	@Override
	public boolean isFrontFaceCameraMirrored() {
		return false;
	}
	
	@Override
	public void saveImage(Bitmap bitmap) {
	}
	
	@Override
	public void saveImage(byte[] bytes, CameraView.OnPictureTakeListener listener) {
        File dir = new File (AtWorkDirUtils.getInstance().getImageDir(context));

        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File photo = new File(dir, fileName);
		if(photo.exists()) {
			photo.delete();
		}
		
		FileOutputStream fileOutStream = null;
		BufferedOutputStream bufferedOutStream = null;
		try {
			fileOutStream = new FileOutputStream(photo.getPath());
			bufferedOutStream = new BufferedOutputStream(fileOutStream);
			bufferedOutStream.write(bytes);
			bufferedOutStream.flush();
			fileOutStream.getFD().sync();
			
			if(isSavedImageScanEnabled()) {
				MediaScannerConnection.scanFile(context, new String[] { photo.getPath() }, SCAN_TYPES, null);
			}
		} catch(IOException ioe) {
			handleException(ioe);
		} finally {
			if(bufferedOutStream != null) {
				try {
					bufferedOutStream.close();
				} catch(IOException ioe) {
				}
			}
			if(fileOutStream != null) {
				try {
					fileOutStream.close();
				} catch(IOException ioe) {
				}
			}
            if(listener != null) {
                listener.onPictureTake(photo);
            }
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onAutoFocus(boolean isSuccess, Camera camera) {
		if(isSuccess && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			new MediaActionSound().play(MediaActionSound.FOCUS_COMPLETE);
		}
	}
	
	@Override
	public boolean isSingleShotMode() {
		return false;
	}
	
	@Override
	public void setAutoFocusEnabled(boolean isEnabled) {
	}

	@Override
	public boolean isExifBasedRotation() {
		return true;
	}
	
	@Override
	public RecordingSupportMode getRecordingSupportMode() {
		return RecordingSupportMode.ANY;
	}
	
	@Override
	public void onCameraFailed(FailureReason reason) {
	}
	
	@Override
	public boolean isFullBleedPreviewUsed() {
		return false;
	}
	
	protected File getPhotoPath() {	
		return (new File(photoFolder, getPhotoFilename()));
	}
	
	protected String getPhotoFilename() {
		return String.format("Photo_%s.jpg", new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date()));
	}
	
	protected File getVideoPath() {
		File directory = getVideoDirectory();
		directory.mkdirs();
		
		return (new File(directory, getVideoFilename()));
	}
	
	protected File getVideoDirectory() {
		return (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
	}
	
	protected String getVideoFilename() {
		return String.format("Video_%s.mp4", new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()));
	}
	
	protected boolean isUsingFrontFacingCamera() {
		return mUsingFrontFacingCamera;
	}

    public void setUsingFrontFacingCamera(boolean usingFrontFacingCamera) {
        this.mUsingFrontFacingCamera = usingFrontFacingCamera;
    }

	protected boolean isSavedImageScanEnabled() {
		return true;
	}
}