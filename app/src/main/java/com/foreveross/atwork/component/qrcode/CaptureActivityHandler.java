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

package com.foreveross.atwork.component.qrcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.foreveross.atwork.component.qrcode.view.ViewfinderResultPointCallback;
import com.foreveross.atwork.modules.qrcode.fragment.QrcodeScanFragment;
import com.foreveross.atwork.qrcode.zxing.ScanConstants;
import com.foreveross.atwork.qrcode.zxing.camera.CameraManager;
import com.foreveross.atwork.qrcode.zxing.decode.DecodeThread;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.util.Collection;
import java.util.Map;



/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class
			.getSimpleName();

	private final QrcodeScanFragment fragment;

	/**
	 * 真正负责扫描任务的核心线程
	 */
	private final DecodeThread decodeThread;

	private State state;

	private final CameraManager cameraManager;

	/**
	 * 当前扫描的状态
	 */
	private enum State {
		/**
		 * 预览
		 */
		PREVIEW,
		/**
		 * 扫描成功
		 */
		SUCCESS,
		/**
		 * 结束扫描
		 */
		DONE
	}

	public CaptureActivityHandler(QrcodeScanFragment fragment,
								  Collection<BarcodeFormat> decodeFormats,
								  Map<DecodeHintType, ?> baseHints, String characterSet,
								  CameraManager cameraManager) {
		this.fragment = fragment;

		// 启动扫描线程
		decodeThread = new DecodeThread(fragment, decodeFormats, baseHints,
				characterSet, new ViewfinderResultPointCallback(
						fragment.getViewfinderView()));
		decodeThread.start();

		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		this.cameraManager = cameraManager;

		// 开启相机预览界面
		cameraManager.startPreview();

		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
			case ScanConstants.RESTART_PREVIEW: // 准备进行下一次扫描
				Log.d(TAG, "Got restart preview message");
				restartPreviewAndDecode();
				break;
			case ScanConstants.DECODE_SUCCEEDED:
				Log.d(TAG, "Got decode succeeded message");
				state = State.SUCCESS;
				Bundle bundle = message.getData();
				Bitmap barcode = null;
				float scaleFactor = 1.0f;
				if (bundle != null) {
					byte[] compressedBitmap = bundle
							.getByteArray(DecodeThread.BARCODE_BITMAP);
					if (compressedBitmap != null) {
						barcode = BitmapFactory.decodeByteArray(
								compressedBitmap, 0, compressedBitmap.length,
								null);
						// Mutable copy:
						barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
					}
					scaleFactor = bundle
							.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
				}
				fragment.handleDecode((Result) message.obj, barcode);
				break;
			case ScanConstants.DECODE_FAILED:
				// We're decoding as fast as possible, so when one decode fails,
				// start another.
				state = State.PREVIEW;
				cameraManager.requestPreviewFrame(decodeThread.getHandler(),
						ScanConstants.DECODE);
				break;
			case ScanConstants.RETURN_SCAN_RESULT:
				Log.d(TAG, "Got return scan result message");
//				fragment.setResult(Activity.RESULT_OK, (Intent) message.obj);
//				fragment.finish();
				break;
			case ScanConstants.LAUNCH_PRODUCT_QUERY:

				break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), ScanConstants.QUIT);
		quit.sendToTarget();

		try {
			// Wait at most half a second; should be enough time, and onPause()
			// will timeout quickly
			decodeThread.join(500L);
		}
		catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(ScanConstants.DECODE_SUCCEEDED);
		removeMessages(ScanConstants.DECODE_FAILED);
	}

	/**
	 * 完成一次扫描后，只需要再调用此方法即可
	 */
	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;

			// 向decodeThread绑定的handler（DecodeHandler)发送解码消息
			cameraManager.requestPreviewFrame(decodeThread.getHandler(),
					ScanConstants.DECODE);
			fragment.drawViewfinder();
		}
	}

}
