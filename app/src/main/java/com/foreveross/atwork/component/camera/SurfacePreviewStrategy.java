package com.foreveross.atwork.component.camera;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;


public class SurfacePreviewStrategy
implements PreviewStrategy,
		   SurfaceHolder.Callback {
	private final CameraView mCameraView;
	private SurfaceView mPreview;
	private SurfaceHolder mPreviewHolder;
	
	SurfacePreviewStrategy(CameraView cameraView, SurfaceView sv) {
		mCameraView = cameraView;
        mPreview = sv;
//		mPreview = new SurfaceView(mCameraView.getContext());
		mPreviewHolder = mPreview.getHolder();
		//mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mPreviewHolder.addCallback(this);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolde) {
		mCameraView.previewCreated();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		mCameraView.initPreview(width, height);
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		mCameraView.previewDestroyed();
	}
	
	@Override
	public void attach(Camera camera)
	throws IOException {
		camera.setPreviewDisplay(mPreviewHolder);
	}
	
	@Override
	public void attach(MediaRecorder mediaRecorder) {
		mediaRecorder.setPreviewDisplay(mPreviewHolder.getSurface());
	}
	
	@Override
	public View getWidget() {
		return mPreview;
	}
}
