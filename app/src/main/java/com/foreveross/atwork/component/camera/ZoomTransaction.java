package com.foreveross.atwork.component.camera;

import android.hardware.Camera;
import android.hardware.Camera.OnZoomChangeListener;

public class ZoomTransaction 
implements OnZoomChangeListener {
	private Camera mCamera;
	private int mLevel;
	private Runnable mOnComplete;
	private OnZoomChangeListener mOnZoomChangeListener;
	
	public ZoomTransaction(Camera camera, int level) {
		mCamera = camera;
		mLevel = level;
	}
	
	public ZoomTransaction onComplete(Runnable onComplete) {
		mOnComplete = onComplete;
		
		return this;
	}
	
	public ZoomTransaction onChange(Camera.OnZoomChangeListener onZoomChangeListener) {
		mOnZoomChangeListener = onZoomChangeListener;
		
		return this;
	}
	
	public void go() {
		Camera.Parameters parameters = mCamera.getParameters();
		if(parameters.isSmoothZoomSupported()) {
			mCamera.setZoomChangeListener(this);
			mCamera.startSmoothZoom(mLevel);
		} else {
			parameters.setZoom(mLevel);
			mCamera.setParameters(parameters);
			onZoomChange(mLevel, true, mCamera);			
		}
	}
	
	public void cancel() {
		mCamera.stopSmoothZoom();
	}
	
	@Override
	public void onZoomChange(int zoomValue, boolean isStopped, Camera camera) {
		if(mOnZoomChangeListener != null) {
			mOnZoomChangeListener.onZoomChange(zoomValue, isStopped, camera);
		}
		
		if(isStopped && mOnComplete != null) {
			mOnComplete.run();
		}
	}
}
