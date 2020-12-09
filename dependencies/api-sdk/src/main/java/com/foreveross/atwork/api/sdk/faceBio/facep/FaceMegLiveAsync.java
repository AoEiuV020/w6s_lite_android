package com.foreveross.atwork.api.sdk.faceBio.facep;

import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.faceBio.facep.requestModel.BizVerifyRequest;
import com.foreveross.atwork.api.sdk.faceBio.facep.responseModel.BizTokenResponse;
import com.foreveross.atwork.api.sdk.faceBio.facep.responseModel.BizVerifiedResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.infrastructure.model.face.FaceBizInfo;
import com.foreveross.atwork.infrastructure.model.face.FaceBizVerifiedInfo;

public class FaceMegLiveAsync {

    private static final FaceMegLiveAsync sInstance = new FaceMegLiveAsync();

    public static FaceMegLiveAsync getInstance() {
        return sInstance;
    }

    public void getBizToken(final Context context, final OnFaceBizInfoListener listener) {
        new AsyncTask<Void, Void, BizTokenResponse>() {
            @Override
            protected BizTokenResponse doInBackground(Void... voids) {
                HttpResult result = FaceMegLiveSync.getInstance().getBizToken(context, "face");
                if (result == null) {
                    return null;
                }
                return (BizTokenResponse) result.resultResponse;
            }

            @Override
            protected void onPostExecute(BizTokenResponse response) {
                if (listener == null) {
                    return;
                }
                if (response == null) {
                    listener.networkFail(-1, "net error");
                    return;
                }
                if (response.status != 0) {
                    listener.networkFail(response.status, response.message);
                    return;
                }
                listener.success(response.result);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void verify(final Context context, final BizVerifyRequest request, final OnFaceVerifyListener listener) {
        new AsyncTask<Void, Void, BizVerifiedResponse>() {
            @Override
            protected BizVerifiedResponse doInBackground(Void... voids) {
                HttpResult result = FaceMegLiveSync.getInstance().verify(context, request, "face");
                if (result == null) {
                    return null;
                }
                return (BizVerifiedResponse)result.resultResponse;
            }

            @Override
            protected void onPostExecute(BizVerifiedResponse bizVerifiedResponse) {
                if (listener == null) {
                    return;
                }
                if (bizVerifiedResponse == null || bizVerifiedResponse.result == null) {
                    listener.networkFail( -1, "network error");
                    return;
                }
                if (bizVerifiedResponse.status != 0) {
                    listener.networkFail(bizVerifiedResponse.status, bizVerifiedResponse.result.mResultMsg);
                    return;
                }
                listener.success(bizVerifiedResponse.result);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public interface  OnFaceBizInfoListener extends NetWorkFailListener {
        void success(FaceBizInfo info);
    }

    public interface OnFaceVerifyListener extends NetWorkFailListener {
        void success(FaceBizVerifiedInfo verifiedInfo);
    }
}
