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

package com.foreveross.atwork.qrcode.zxing.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 该类的作用是在预览界面加载好后向ui线程发消息
 */
final class PreviewCallback implements Camera.PreviewCallback {

	private static final String TAG = PreviewCallback.class.getSimpleName();

	private final CameraConfigurationManager configManager;
	private Handler previewHandler;
	private int previewMessage;
	private Handler mLightUpHandler;
	private int mLightUpMessage;


	//上次记录的索引
	private int mDarkIndex = 0;
	//一个历史记录的数组，255是代表亮度最大值
	private long[] mDarkList = new long[]{255, 255, 255, 255};
	//扫描间隔
	private int mWaitScanTime = 300;

	//亮度低的阀值
	private int mDarkValue = 60;

	PreviewCallback(CameraConfigurationManager configManager) {
		this.configManager = configManager;
	}

	/**
	 * 绑定handler，用于发消息到ui线程
	 * 
	 * @param previewHandler
	 * @param previewMessage
	 */
	void setHandler(Handler previewHandler, int previewMessage) {
		this.previewHandler = previewHandler;
		this.previewMessage = previewMessage;
	}

	void setLightHandler(Handler lightUpHandler, int lightUpMessage) {
		this.mLightUpHandler = lightUpHandler;
		this.mLightUpMessage = lightUpMessage;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		try {
			Point cameraResolution = configManager.getCameraResolution();
			Handler thePreviewHandler = previewHandler;
			if (cameraResolution != null) {
				if (thePreviewHandler != null) {
					Message message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x, cameraResolution.y,
							data);
					message.sendToTarget();
					previewHandler = null;
				}

				if (mLightUpHandler == null) {
					return;
				}
				int width = camera.getParameters().getPreviewSize().width;
				int height = camera.getParameters().getPreviewSize().height;
				//像素点的总亮度
				long pixelLightCount = 0L;
				//像素点的总数
				long pixeCount = width * height;
				//采集步长，因为没有必要每个像素点都采集，可以跨一段采集一个，减少计算负担，必须大于等于1。
				int step = 10;
				//data.length - allCount * 1.5f的目的是判断图像格式是不是YUV420格式，只有是这种格式才相等
				//因为int整形与float浮点直接比较会出问题，所以这么比
				if (Math.abs(data.length - pixeCount * 1.5f) < 0.00001f) {
					for (int i = 0; i < pixeCount; i += step) {
						//如果直接加是不行的，因为data[i]记录的是色值并不是数值，byte的范围是+127到—128，
						// 而亮度FFFFFF是11111111是-127，所以这里需要先转为无符号unsigned long参考Byte.toUnsignedLong()
						pixelLightCount += ((long) data[i]) & 0xffL;
					}
					//平均亮度
					long cameraLight = pixelLightCount / (pixeCount / step);
					//更新历史记录
					int lightSize = mDarkList.length;
					mDarkList[mDarkIndex = mDarkIndex % lightSize] = cameraLight;
					mDarkIndex++;
					boolean isDarkEnv = true;
					//判断在时间范围waitScanTime * lightSize内是不是亮度过暗
					for (int i = 0; i < lightSize; i++) {
						if (mDarkList[i] > mDarkValue) {
							isDarkEnv = false;
						}
					}
					Message lightUpMessage = mLightUpHandler.obtainMessage(mLightUpMessage, isDarkEnv ? 1 : 0,
							0);
					lightUpMessage.sendToTarget();

				}


			} else {
				Log.d(TAG, "Got preview callback, but no handler or resolution available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void release( ){
		mLightUpHandler = null;
	}

}
