package com.foreveross.atwork.infrastructure.plugin.face;

import android.app.Activity;

import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;

public class NewLandFaceBioPlugin {

    public interface INewLandFaceBio extends WorkplusPlugin {
         void detect(Activity activity, int cameraMode, OnFaceBioDetectListener listener);
    }
}
