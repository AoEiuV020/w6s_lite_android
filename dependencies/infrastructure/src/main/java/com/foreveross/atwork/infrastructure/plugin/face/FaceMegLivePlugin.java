package com.foreveross.atwork.infrastructure.plugin.face;

import android.app.Activity;
import android.content.Context;

import com.foreveross.atwork.infrastructure.model.face.FaceBizInfo;
import com.foreveross.atwork.infrastructure.model.face.FaceBizVerifiedInfo;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;

public class FaceMegLivePlugin {

    public interface IFaceMegLive extends WorkplusPlugin {

        void init(Context context);

        void detect(Activity activity, String bizToken, FacePlusDetectListener listener);
    }

    public interface FacePlusDetectListener {

        void onDetectSuccess(String token, String detectedData);

        void onDetectFailure(int errorCode, String errorMsg);
    }

}
