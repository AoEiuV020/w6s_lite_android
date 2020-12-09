package com.foreveross.atwork.component.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;

import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCleanupTask 
extends Thread {
	private byte[] mData;
	private Bitmap mWorkingCopy;
	private int mCameraId;
	private CameraHost mHost;
	private File mCacheDir;
	private boolean mIsBitmapNeeded;
	private boolean mIsByteArrayNeeded;
	private int mDisplayOrientation;
    private CameraView.OnPictureTakeListener mOnPictureTakeListener;
	private Context mContext;

	
	public ImageCleanupTask(Context context, byte[] data, int cameraId, CameraHost host, File cacheDir, boolean isBitmapNeeded, boolean isByteArrayNeeded, int displayOrientation, CameraView.OnPictureTakeListener onPictureTakeListener) {
		mData = data;
		mCameraId = cameraId;
		mHost = host;
		mCacheDir = cacheDir;
		mIsBitmapNeeded = isBitmapNeeded;
		mIsByteArrayNeeded = isByteArrayNeeded;
		mDisplayOrientation = displayOrientation;
        mOnPictureTakeListener = onPictureTakeListener;
		mContext = context;
	}
	
	@Override
	public void run() {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(mCameraId, cameraInfo);
		if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			if(mHost.getDeviceProfile().isFrontFaceCameraPortraitFlipped() && (mDisplayOrientation == 90 || mDisplayOrientation == 270)) {
				applyFlip();
			} else if(mHost.isFrontFaceCameraMirrored()) {
				applyMirror();
			}
		}
		
		if(mHost.isExifBasedRotation() && mHost.getDeviceProfile().isRotationToExifEncoded()) {
            rotateForRealz();
        }
		synchronizeModels(mIsBitmapNeeded, mIsByteArrayNeeded);
		
		if(mIsBitmapNeeded) {
			mHost.saveImage(mWorkingCopy);
		} else if(mWorkingCopy != null) {
			mWorkingCopy.recycle();
		}
		
		if(mIsByteArrayNeeded) {
			mHost.saveImage(mData, mOnPictureTakeListener);
		}


	}
	
	private void applyMirror() {
		synchronizeModels(true, false);
		float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
		Matrix matrix = new Matrix();
		Matrix matrixMirrorY = new Matrix();
		
		matrixMirrorY.setValues(mirrorY);
		matrix.postConcat(matrixMirrorY);
		
		Bitmap mirrored = Bitmap.createBitmap(mWorkingCopy, 0, 0, mWorkingCopy.getWidth(), mWorkingCopy.getHeight(), matrix, true);
		
		mWorkingCopy.recycle();
		mWorkingCopy = mirrored;
		mData = null;
	}
	
	void applyFlip() {
		synchronizeModels(true, false);
		float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
		Matrix matrix = new Matrix();
		Matrix matrixMirrorY = new Matrix();
		
		matrixMirrorY.setValues(mirrorY);
		matrix.preScale(1.0f, -1.0f);
		matrix.postConcat(matrixMirrorY);
		
		Bitmap flipped = Bitmap.createBitmap(mWorkingCopy, 0, 0, mWorkingCopy.getWidth(), mWorkingCopy.getHeight(), matrix, true);
		
		mWorkingCopy.recycle();
		mWorkingCopy = flipped;
		mData = null;
	}
	
	private void rotateForRealz() {
		try {
			synchronizeModels(true, true);
            File dir = new File (AtWorkDirUtils.getInstance().getImageDir(mContext));

            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File photo = new File(dir, fileName);
			if(photo.exists()) {
				photo.delete();
			}
			
			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(photo.getPath());
				outStream.write(mData);
				ExifInterface exif = new ExifInterface(photo.getAbsolutePath());
				Bitmap rotated = null;
				mData = null;
				try {
					if("6".equals(exif.getAttribute(ExifInterface.TAG_ORIENTATION)) || (mDisplayOrientation == 90 && !((SimpleCameraHost)mHost).isUsingFrontFacingCamera())) {
						rotated = rotate(mWorkingCopy, 90);
					} else if("8".equals(exif.getAttribute(ExifInterface.TAG_ORIENTATION)) || (mDisplayOrientation == 90 && ((SimpleCameraHost)mHost).isUsingFrontFacingCamera())) {
						rotated = rotate(mWorkingCopy, 270);
					} else if("3".equals(exif.getAttribute(ExifInterface.TAG_ORIENTATION))) {
						rotated = rotate(mWorkingCopy, 180);
					}
					
					if(rotated != null) {
						mWorkingCopy.recycle();
						mWorkingCopy = rotated;
					}
				} catch(OutOfMemoryError oome) {
				}
			} catch(IOException ioe) {
			} finally {
				if(outStream != null) {
					try {
						outStream.close();
					} catch(IOException ioe) {
					}
				}


			}
		} catch(OutOfMemoryError oome) {
		}
	}


	private static Bitmap rotate(Bitmap bitmap, int degree) {
		Matrix matrix = new Matrix();
		matrix.setRotate(degree);
		return (Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
	}
	
	private void synchronizeModels(boolean isBitmapNeeded, boolean isByteArrayNeeded) {
		if(mData == null && isByteArrayNeeded) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream(mWorkingCopy.getWidth()*mWorkingCopy.getHeight());
			mWorkingCopy.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			mData = outStream.toByteArray();
			
			try {
				outStream.close();
			} catch(IOException ioe) {
			}
		}
		
		if(mWorkingCopy == null && isBitmapNeeded) {
			mWorkingCopy = BitmapFactory.decodeByteArray(mData, 0, mData.length);
		}
		
		if(!isBitmapNeeded && mWorkingCopy != null) {
			mWorkingCopy.recycle();
			mWorkingCopy = null;
		}
		
		System.gc();
	}
}
