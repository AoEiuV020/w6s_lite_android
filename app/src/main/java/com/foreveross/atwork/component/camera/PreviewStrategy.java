package com.foreveross.atwork.component.camera;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.View;

import java.io.IOException;

public interface PreviewStrategy {
	void attach(Camera camera)
	throws IOException;
	
	void attach(MediaRecorder recorder);
	
	View getWidget();
}
