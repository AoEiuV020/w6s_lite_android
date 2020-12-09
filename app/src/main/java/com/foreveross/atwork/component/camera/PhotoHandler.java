package com.foreveross.atwork.component.camera;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class PhotoHandler
implements PictureCallback {
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
//		File pictureFolder = new File(AtWorkDirUtils.getInstance().getImageDir());
//		if(!pictureFolder.mkdir()) {
//		}
	}
}
