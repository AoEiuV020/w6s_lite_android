/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreveross.atwork.qrcode.zxing.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;

import java.io.IOException;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding. <br/>
 * <br/>
 *
 * 该类封装了相机的所有服务并且是该app中唯一与相机打交道的类
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

	private static final int MIN_FRAME_WIDTH = 240;

	private static final int MAX_FRAME_WIDTH = 700; // = 5/8 * 1920

	private final Context context;

	private final CameraConfigurationManager configManager;

	private Camera camera;

	private AutoFocusManager autoFocusManager;

	private Rect framingRect;

	private Rect framingRectInPreview;

	private boolean initialized;

	private boolean previewing;

	private int requestedFramingRectWidth;

	private int requestedFramingRectHeight;

	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final PreviewCallback previewCallback;

	private boolean mIsSquare = true;

	public void setFramingRect(boolean isSquare) {
		mIsSquare = isSquare;
		framingRect = null;
	}

	public static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
	static {
		int sdkInt;
		try {
			sdkInt = Integer.parseInt(Build.VERSION.SDK);
		} catch (NumberFormatException nfe) {
			// Just to be safe
			sdkInt = 10000;
		}
		SDK_INT = sdkInt;
	}

	public CameraManager(Context context) {
		this.context = context;
		this.configManager = new CameraConfigurationManager(context);
		previewCallback = new PreviewCallback(configManager);
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 *
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public synchronized void openDriver(SurfaceHolder holder)
			throws IOException {
		Camera theCamera = camera;
		if (theCamera == null) {
			// 获取手机背面的摄像头
			theCamera = OpenCameraInterface.open();
			if (theCamera == null) {
				throw new IOException();
			}
			camera = theCamera;
		}

		// 设置摄像头预览view
		theCamera.setPreviewDisplay(holder);

		if (!initialized) {
			initialized = true;
			configManager.initFromCameraParameters(theCamera);
			if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
				setManualFramingRect(requestedFramingRectWidth,
						requestedFramingRectHeight);
				requestedFramingRectWidth = 0;
				requestedFramingRectHeight = 0;
			}
		}

		Camera.Parameters parameters = theCamera.getParameters();
		String parametersFlattened = parameters == null ? null : parameters
				.flatten(); // Save
							// these,
							// temporarily
		try {
			configManager.setDesiredCameraParameters(theCamera, false);
		}
		catch (RuntimeException re) {
			// Driver failed
			Log.w(TAG,
					"Camera rejected parameters. Setting only minimal safe-mode parameters");
			Log.i(TAG, "Resetting to saved camera params: "
					+ parametersFlattened);
			// Reset:
			if (parametersFlattened != null) {
				parameters = theCamera.getParameters();
				parameters.unflatten(parametersFlattened);
				try {
					theCamera.setParameters(parameters);
					configManager.setDesiredCameraParameters(theCamera, true);
				}
				catch (RuntimeException re2) {
					// Well, darn. Give up
					Log.w(TAG,
							"Camera rejected even safe-mode parameters! No configuration");
				}
			}
		}

	}

	public Camera getCamera() {
		return camera;
	}

	public synchronized boolean isOpen() {
		return camera != null;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
			// Make sure to clear these each time we close the camera, so that
			// any scanning rect
			// requested by intent is forgotten.
			framingRect = null;
			framingRectInPreview = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public synchronized void startPreview() {
		Camera theCamera = camera;
		if (theCamera != null && !previewing) {
			// Starts capturing and drawing preview frames to the screen
			// Preview will not actually start until a surface is supplied with
			// setPreviewDisplay(SurfaceHolder) or
			// setPreviewTexture(SurfaceTexture).
			theCamera.startPreview();

			previewing = true;
			autoFocusManager = new AutoFocusManager(context, camera);

            autoFocusManager.start();
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public synchronized void stopPreview() {
		if (autoFocusManager != null) {
			autoFocusManager.stop();
			autoFocusManager = null;
		}
		if (camera != null && previewing) {
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			previewing = false;
		}
	}


	public synchronized void setTorch(boolean newSetting) {
		if (newSetting != configManager.getTorchState(camera)) {
			if (camera != null) {
				if (autoFocusManager != null) {
					autoFocusManager.stop();
				}
				configManager.setTorch(camera, newSetting);
				if (autoFocusManager != null) {
					autoFocusManager.start();
				}
			}
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively. <br/>
	 *
	 * 两个绑定操作：<br/>
	 * 1：将handler与回调函数绑定；<br/>
	 * 2：将相机与回调函数绑定<br/>
	 * 综上，该函数的作用是当相机的预览界面准备就绪后就会调用hander向其发送传入的message
	 *
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public synchronized void requestPreviewFrame(Handler handler, int message) {
		Camera theCamera = camera;
		if (theCamera != null && previewing) {
			previewCallback.setHandler(handler, message);

			// 绑定相机回调函数，当预览界面准备就绪后会回调Camera.PreviewCallback.onPreviewFrame
			theCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	public synchronized void requestLightUpPreviewFrame(Handler handler, int message) {
		Camera theCamera = camera;
		if (theCamera != null && previewing) {
			previewCallback.setLightHandler(handler, message);

			// 绑定相机回调函数，当预览界面准备就绪后会回调Camera.PreviewCallback.onPreviewFrame
			theCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the barcode. This target helps with alignment as well as
	 * forces the user to hold the device far enough away to ensure the image
	 * will be in focus.
	 *
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public synchronized Rect getFramingRect() {
		if (framingRect == null) {
			if (camera == null) {
				return null;
			}
			Point screenResolution = configManager.getScreenResolution();
			if (screenResolution == null) {
				// Called early, before init even finished
				return null;
			}

			int width = findDesiredDimensionInRange(screenResolution.x,
					MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
			// 将扫描框设置成一个正方形

			int height;
			int topOffset;
			if (mIsSquare) {
				height = width;
				topOffset = (screenResolution.y - height) / 2 - 120;
			} else {
				height = (int)(width * 1.7);
				topOffset = (screenResolution.y - height) / 2 - 60;
			}

			int leftOffset = (screenResolution.x - width) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);

			Log.d(TAG, "Calculated framing rect: " + framingRect);
		}

		return framingRect;
	}


	/**
	 * Target 3/4 of each dimension<br/>
	 * 计算结果在hardMin~hardMax之间
	 *
	 * @param resolution
	 * @param hardMin
	 * @param hardMax
	 * @return
	 */
	private static int findDesiredDimensionInRange(int resolution, int hardMin,
			int hardMax) {
		int dim = (resolution >> 1) + (resolution >> 2);
		if (dim < hardMin) {
			return hardMin;
		}
		if (dim > hardMax) {
			return hardMax;
		}
		return dim;
	}

	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview
	 * frame, not UI / screen.
	 */
	public synchronized Rect getFramingRectInPreview() {
			Rect framingRect = getFramingRect();
			if (framingRect == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			Point cameraResolution = configManager.getCameraResolution();
			Point screenResolution = configManager.getScreenResolution();
			if (cameraResolution == null || screenResolution == null) {
				// Called early, before init even finished
				return null;
			}
			rect.left = rect.left * cameraResolution.y / screenResolution.x;
			rect.right = rect.right * cameraResolution.y / screenResolution.x;
			rect.top = rect.top * cameraResolution.x / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
			framingRectInPreview = rect;

			Log.d(TAG, "Calculated framingRectInPreview rect: "
					+ framingRectInPreview);
			Log.d(TAG, "cameraResolution: " + cameraResolution);
			Log.d(TAG, "screenResolution: " + screenResolution);

		return framingRectInPreview;
	}

	public synchronized Rect getFramingRectInPreview2() {
			Rect framingRect = getFramingRect();
			if (framingRect == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			Point cameraResolution = configManager.getCameraResolution();
			Point screenResolution = configManager.getScreenResolution();
			if (cameraResolution == null || screenResolution == null) {
				// Called early, before init even finished
				return null;
			}
			rect.left = rect.left * cameraResolution.x / screenResolution.x;
			rect.right = rect.right * cameraResolution.x / screenResolution.x;
			rect.top = rect.top * cameraResolution.y / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
			framingRectInPreview = rect;

			Log.d(TAG, "Calculated framingRectInPreview rect: "
					+ framingRectInPreview);
			Log.d(TAG, "cameraResolution: " + cameraResolution);
			Log.d(TAG, "screenResolution: " + screenResolution);

		return framingRectInPreview;
	}

	/**
	 * Allows third party apps to specify the scanning rectangle dimensions,
	 * rather than determine them automatically based on screen resolution.
	 *
	 * @param width
	 *            The width in pixels to scan.
	 * @param height
	 *            The height in pixels to scan.
	 */
	public synchronized void setManualFramingRect(int width, int height) {
		if (initialized) {
			Point screenResolution = configManager.getScreenResolution();
			if (width > screenResolution.x) {
				width = screenResolution.x;
			}
			if (height > screenResolution.y) {
				height = screenResolution.y;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.d(TAG, "Calculated manual framing rect: " + framingRect);
			framingRectInPreview = null;
		}
		else {
			requestedFramingRectWidth = width;
			requestedFramingRectHeight = height;
		}
	}

	/**
	 * A factory method to build the appropriate LuminanceSource object based on
	 * the format of the preview buffers, as described by Camera.Parameters.
	 *
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,
			int width, int height)  {
		Rect rect = getFramingRectInPreview();
		if (rect == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.

//		Log.e("qrcode", "width -> " + width);
//		Log.e("qrcode", "height -> " + height);
//		Log.e("qrcode", "rect.left -> " + rect.left);
//		Log.e("qrcode", "rect.top -> " + rect.top);
//		Log.e("qrcode", "rect.width() -> " + rect.width());
//		Log.e("qrcode", "rect.height() -> " + rect.height());
		try {
			return new PlanarYUVLuminanceSource(data, width, height, 0,
					0, width, height, false);
		} catch (Exception e) {
			throw new IllegalArgumentException("not working");
		}

	}

	public PlanarYUVLuminanceSource buildLuminanceSource2(byte[] data,
														 int width, int height)  {
		Rect rect = getFramingRectInPreview2();
		if (rect == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.

//		Log.e("qrcode", "width -> " + width);
//		Log.e("qrcode", "height -> " + height);
//		Log.e("qrcode", "rect.left -> " + rect.left);
//		Log.e("qrcode", "rect.top -> " + rect.top);
//		Log.e("qrcode", "rect.width() -> " + rect.width());
//		Log.e("qrcode", "rect.height() -> " + rect.height());
		try {
			return new PlanarYUVLuminanceSource(data, width, height, 0,
					0, width, height, false);
		} catch (Exception e) {
			throw new IllegalArgumentException("not working");
		}

	}

	/**
	 * 焦点放小
	 */
	public void zoomOut() {
		if (camera != null && camera.getParameters().isZoomSupported()) {

			Camera.Parameters parameters = camera.getParameters();
			if (parameters.getZoom() <= 0) {
				return;
			}

			parameters.setZoom(parameters.getZoom() - 1);
			camera.setParameters(parameters);

		}
	}

	/**
	 * 焦点放大
	 */
	public void zoomIn() {
		if (camera != null && camera.getParameters().isZoomSupported()) {

			Camera.Parameters parameters = camera.getParameters();
			if (parameters.getZoom() >= parameters.getMaxZoom()) {
				return;
			}

			parameters.setZoom(parameters.getZoom() + 1);
			camera.setParameters(parameters);

		}
	}

	/*
	 * 缩放
	 *
	 * @param scale
	 */
	public void setCameraZoom(int scale) {
		if (camera != null && camera.getParameters().isZoomSupported()
				&& scale <= camera.getParameters().getMaxZoom() && scale >= 0) {

			Camera.Parameters parameters = camera.getParameters();

			parameters.setZoom(scale);
			camera.setParameters(parameters);

		}
	}

	public void release() {
		if (previewCallback != null) {
			previewCallback.release();
		}
	}


	public void setCameraDisplayOrientation(Activity activity, int cameraId) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
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
			default:
				break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}
}
