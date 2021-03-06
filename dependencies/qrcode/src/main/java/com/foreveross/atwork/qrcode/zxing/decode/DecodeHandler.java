/*
 * Copyright (C) 2010 ZXing authors
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

package com.foreveross.atwork.qrcode.zxing.decode;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.foreveross.atwork.qrcode.zxing.ScanConstants;
import com.foreveross.atwork.qrcode.zxing.ZxingQrcodeInterface;
import com.foreveross.atwork.qrcode.zxing.camera.CameraManager;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.util.Map;


final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final ZxingQrcodeInterface zxingQrcodeInterface;

	private final MultiFormatReader multiFormatReader;

	private boolean running = true;

	DecodeHandler(ZxingQrcodeInterface zxingQrcodeInterface, Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.zxingQrcodeInterface = zxingQrcodeInterface;
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
			case ScanConstants.DECODE:
				decode((byte[]) message.obj, message.arg1, message.arg2);
				break;
			case ScanConstants.QUIT:
				running = false;
				Looper.myLooper().quit();
				break;
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height) {
		long start = System.currentTimeMillis();
		Result rawResult = null;

		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;
		width = height;
		height = tmp;

		PlanarYUVLuminanceSource source = null;

			try {
				source = zxingQrcodeInterface.getCameraManager()
						.buildLuminanceSource(rotatedData, width, height);
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
				// ?????????????????????????????????bitmap???????????????????????????
				rawResult = multiFormatReader.decodeWithState(bitmap);
			}
			catch (ReaderException re) {
				// continue
				try {
					source = zxingQrcodeInterface.getCameraManager().buildLuminanceSource2(data, height, width);
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
					rawResult = multiFormatReader.decodeWithState(bitmap);
				} catch (Exception e) {
				}
			}
			finally {
				multiFormatReader.reset();
			}

		Handler handler = zxingQrcodeInterface.getHandler();
		if (rawResult != null) {
			// Don't log the barcode contents for security.
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode in " + (end - start) + " ms");
			if (handler != null) {
				Message message = Message.obtain(handler,
						ScanConstants.DECODE_SUCCEEDED, rawResult);
				Bundle bundle = new Bundle();
				bundleThumbnail(source, bundle);
				message.setData(bundle);
				message.sendToTarget();
			}
		}
		else {
			if (handler != null) {
				Message message = Message.obtain(handler, ScanConstants.DECODE_FAILED);
				message.sendToTarget();
			}
		}
	}

	private Bitmap rotateBitmap(Bitmap origin, float alpha) {
		if (origin == null) {
			return null;
		}
		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(alpha);
		// ????????????????????????
		Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		if (newBM.equals(origin)) {
			return newBM;
		}
		origin.recycle();
		return newBM;
	}

	private static void bundleThumbnail(PlanarYUVLuminanceSource source,
			Bundle bundle) {
		int[] pixels = source.renderThumbnail();
		int width = source.getThumbnailWidth();
		int height = source.getThumbnailHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height,
				Bitmap.Config.ARGB_8888);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
		bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
		bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width
				/ source.getWidth());
	}

}
