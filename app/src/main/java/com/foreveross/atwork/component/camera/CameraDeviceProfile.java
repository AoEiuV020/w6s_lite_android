package com.foreveross.atwork.component.camera;

import android.hardware.Camera;
import android.os.Build;

public class CameraDeviceProfile {
	private static CameraDeviceProfile instance;
	
	static public void initConfiguration() {
		if("occam".equals(Build.PRODUCT)) {
			instance = new Nexus4DeviceProfile();
		} else if("m7".equals(Build.PRODUCT) && "HTC".equalsIgnoreCase(Build.MANUFACTURER)) {
			instance = new HtcOneDeviceProfile();
		} else if(("d2att".equals(Build.PRODUCT) || "d2spr".equalsIgnoreCase(Build.MANUFACTURER)) && "samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
			instance = new SamsungGalaxyS3DeviceProfile();
		} else if("jflteuc".equals(Build.PRODUCT)) {
			instance = new SamsungGalaxySGHI337DeviceProfile();
		} else if("gd1wifiue".equals(Build.PRODUCT)) {
			instance = new SamsungGalaxyCameraDeviceProfile();
		} else if("espressowifiue".equals(Build.PRODUCT)) {
			instance = new SamsungGalaxyTab2DeviceProfile();
		} else if("loganub".equals(Build.PRODUCT)) {
			instance = new SamsungGalaxyAce3DeviceProfile();
		} else if("samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
			instance = new SamsungDeviceProfile();
		} else if("motorola".equalsIgnoreCase(Build.MANUFACTURER)) {
			if("XT890_rtgb".equals(Build.PRODUCT)) {
				instance = new MotorolaRazrIDeviceProfile();
			} else {
				instance = new MotorolaDeviceProfile();
			}
		} else if("htc_vivow".equalsIgnoreCase(Build.PRODUCT)) {
			instance = new DroidIncredible2DeviceProfile();
		} else if("C1505_1271-7585".equalsIgnoreCase(Build.PRODUCT)) {
			instance = new SonyXperiaEDeviceProfile();
		} else {
			instance = new CameraDeviceProfile();
		}
	}
	
	public static CameraDeviceProfile getInstance() {
		return instance;
	}
	
	public CameraDeviceProfile() {
	}
	
	public boolean isTextureViewUsed() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isCyanogenSupported());
	}
	
	public boolean isRotationToExifEncoded() {
		return false;
	}
	
	public boolean isExifBasedRotation() {
		return false;
	}
	
	public boolean isFrontFaceCameraPortraitFlipped() {
		return false;
	}
	
	public int getMinPictureHeight() {
		return 0;
	}
	
	public int getMaxPictureHeight() {
		return Integer.MAX_VALUE;
	}
	
	public Camera.Size getPrefereredPreviewSizeForVideo(int displayOrientation, int width, int height, Camera.Parameters parameters) {
		return null;
	}
	
	public boolean isZoomSupported(boolean isFrontFaceCamera) {
		return true;
	}
	
	private boolean isCyanogenSupported() {
		return (System.getProperty("os.version").contains("cyanogenmod") || Build.HOST.contains("cyanogenmod"));
	}
	
	static private class HtcOneDeviceProfile
	extends CameraDeviceProfile {
		@Override
		public int getMaxPictureHeight() {
			return 1400;
		}
	}
	
	static private class Nexus4DeviceProfile
	extends CameraDeviceProfile {
		@Override
		public int getMaxPictureHeight() {
			return 720;
		}
	}
	
	static private class SamsungGalaxyTab2DeviceProfile
	extends CameraDeviceProfile {
		@Override
		public int getMaxPictureHeight() {
			return 1104;
		}
	}
	
	static private class SamsungGalaxyAce3DeviceProfile
	extends CameraDeviceProfile {
	}
	
	static private class SamsungGalaxySGHI337DeviceProfile
	extends CameraDeviceProfile {
		@Override
		public int getMaxPictureHeight() {
			return 2448;
		}
	}
	
	static private class FullExifFixupDeviceProfile
	extends CameraDeviceProfile {
		@Override
		public boolean isRotationToExifEncoded() {
			return true;
		}
		
		@Override
		public boolean isExifBasedRotation() {
			return true;
		}
	}
	
	static private class SamsungDeviceProfile 
	extends FullExifFixupDeviceProfile {
	}
	
	static private class SamsungGalaxyS3DeviceProfile
	extends SamsungDeviceProfile {
		@Override
		public int getMaxPictureHeight() {
			return 1836;
		}
	}
	
	static private class SamsungGalaxyCameraDeviceProfile
	extends SamsungDeviceProfile {
		@Override
		public int getMinPictureHeight() {
			return 3072;
		}
	}
	
	static private class MotorolaDeviceProfile
	extends FullExifFixupDeviceProfile {
		@Override
		public boolean isFrontFaceCameraPortraitFlipped() {
			return true;
		}
	}
	
	static private class MotorolaRazrIDeviceProfile
	extends MotorolaDeviceProfile {
		@Override
		public boolean isZoomSupported(boolean isFrontFaceCamera) {
			return !isFrontFaceCamera;
		}
	}
	
	static private class DroidIncredible2DeviceProfile
	extends CameraDeviceProfile {
		@Override
		public boolean isFrontFaceCameraPortraitFlipped() {
			return true;
		}
		
		@Override
		public int getMaxPictureHeight() {
			return 1952;
		}
	}
	
	static private class SonyXperiaEDeviceProfile
	extends FullExifFixupDeviceProfile {
	}
}
