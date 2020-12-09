package com.foreveross.atwork.modules.image.component;

import android.graphics.Bitmap;
import android.graphics.Matrix;

//用于查看大图的缓存bitmap
public class ItemRotateBitmap{
	public static final String TAG = "RotateBitmap";
	private Bitmap mBitmap;
	private int mRotation;
	
	public ItemRotateBitmap(Bitmap bitmap) {
	    mBitmap = bitmap;
	    mRotation = 0;
	}
	
	public ItemRotateBitmap(Bitmap bitmap, int rotation) {
	    mBitmap = bitmap;
	    mRotation = rotation % 360;
	}
	
	public void setRotation(int rotation) {
	    mRotation = rotation;
	}
	
	public int getRotation() {
	    return mRotation;
	}
	
	public Bitmap getBitmap() {
	    return mBitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
	    mBitmap = bitmap;
	}
	
	public Matrix getRotateMatrix() {
	    // By default this is an identity matrix.
	    Matrix matrix = new Matrix();
	    if (mRotation != 0) {
	        // We want to do the rotation at origin, but since the bounding
	        // rectangle will be changed after rotation, so the delta values
	        // are based on old & new width/height respectively.
	        int cx = mBitmap.getWidth() / 2;
	        int cy = mBitmap.getHeight() / 2;
	        matrix.preTranslate(-cx, -cy);
	        matrix.postRotate(mRotation);
	        matrix.postTranslate(getWidth() / 2, getHeight() / 2);
	    }
	    return matrix;
	}
	
	public boolean isOrientationChanged() {
	    return (mRotation / 90) % 2 != 0;
	}
	
	public int getHeight() {
	    if (isOrientationChanged()) {
	        return mBitmap.getWidth();
	    } else {
	        return mBitmap.getHeight();
	    }
	}
	
	public int getWidth() {
	    if (isOrientationChanged()) {
	        return mBitmap.getHeight();
	    } else {
	        return mBitmap.getWidth();
	    }
	}
}

