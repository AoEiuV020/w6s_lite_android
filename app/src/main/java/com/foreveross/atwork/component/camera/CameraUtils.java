package com.foreveross.atwork.component.camera;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtils {
	static private final double ASPECT_TOLERANCE = 0.1;
	
	static public Camera.Size getOptimalPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
		Camera.Size optimalSize = null;
		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
		int targetHeight = height;
		double minDiff = Double.MAX_VALUE;
		double targetRatio = (double)width/height;
		if(displayOrientation == 90 || displayOrientation == 270) {
			targetRatio = (double)height/width;
		}
		
		for(Size size : sizes) {
			double ratio = (double)size.width/size.height;
			if(Math.abs(ratio - targetRatio) <= ASPECT_TOLERANCE) {
				if(Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		
		if(optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for(Size size : sizes) {
				if(Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		
		return optimalSize;
	}
	
	static public Camera.Size getBestAspectPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
		return getBestAspectPreviewSize(displayOrientation, width, height, parameters, 0.0d);
	}
	
	static public Camera.Size getBestAspectPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters, double threshold) {
		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		double targetRatio = (double)width/height;
		if(displayOrientation == 90 || displayOrientation == 270) {
			targetRatio = (double)height/width;
		}
		
		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
		Collections.sort(sizes, Collections.reverseOrder(new SizeComparator()));
		for(Size size : sizes) {
			double ratio = (double)size.width/size.height;
			if(Math.abs(ratio - targetRatio) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(ratio - targetRatio);
			}
			
			if(minDiff < threshold) {
				break;
			}
		}
		
		return optimalSize;
	}
	
	static public Camera.Size getLargestPictureSize(int minHeight, int maxHeight, Camera.Parameters parameters) {
		List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
		Camera.Size largestSize = null;
		int offset = 0;		
		while(offset < sizes.size()) {
			largestSize = sizes.get(offset);
			if(largestSize.height <= maxHeight && largestSize.height >= minHeight) {
				break;
			}
		}
		Camera.Size size = null;
		for(int index = offset + 1; index < sizes.size(); index++) {
			size = sizes.get(index);
			if(size.height <= maxHeight && size.height > minHeight) {
				int area1 = largestSize.width*largestSize.height;
				int area2 = size.width*size.height;
				if(area2 > area1) {
					largestSize = size;
				}
			}
		}
		
		return largestSize;
	}

	static public Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
		Camera.Size smallSize = sizes.get(0);
		for(int index = 1; index < sizes.size(); index++) {
			int area1 = smallSize.width*smallSize.height;
			int area2 = sizes.get(index).width*sizes.get(index).height;
			if(area2 < area1) {
				smallSize = sizes.get(index);
			}
		}
		return smallSize;
	}
	
	private static class SizeComparator
	implements Comparator<Camera.Size> {
		@Override
		public int compare(Size size1, Size size2) {
			int area1 = size1.width*size1.height;
			int area2 = size2.width*size2.height;
			
			return (area1 == area2 ? 0 : (area1 < area2 ? -1 : 1));
		}
	}
}
