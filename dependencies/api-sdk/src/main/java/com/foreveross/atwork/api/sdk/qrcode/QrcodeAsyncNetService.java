package com.foreveross.atwork.api.sdk.qrcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.qrcode.requestModel.QrLoginParamsRequestJson;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.GetQrCodeJoinInfoResponse;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.PersonalQrcodeResponseJson;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.google.gson.Gson;

/**
 * Created by dasunsy on 16/2/18.
 */
public class QrcodeAsyncNetService {
    public static final int TYPE_GROUP_JOIN = 1;

    private static final Object sLock = new Object();
    private static QrcodeAsyncNetService sInstance;

    private QrcodeAsyncNetService() {

    }

    public static QrcodeAsyncNetService getInstance() {
        synchronized (sLock) {
            if (null == sInstance) {
                sInstance = new QrcodeAsyncNetService();
            }

            return sInstance;
        }
    }

    /**
     * 根据扫描得到的 id pin 去后台拿二维码对应的信息
     * @param context
     * @param id
     * @param addresser
     * @param listener
     * */
    @SuppressLint("StaticFieldLeak")
    public void getQrcodeRelativeInfo(final Context context, final String id, final String addresser, final OnGetQrcodeRelativeInfoListener listener) {

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return QrcodeSyncNetService.getInstance().getQrcodeRelativeInfo(context, id, addresser);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    GetQrCodeJoinInfoResponse getQrCodeJoinInfoResponse = (GetQrCodeJoinInfoResponse) httpResult.resultResponse;
                    listener.success(getQrCodeJoinInfoResponse);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }


            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * 扫码登录
     * @param context
     * @param qrCode
     * @param action
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void qrLogin(final Context context, final String qrCode, final String action, final OnQrLoginListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                QrLoginParamsRequestJson qrLoginParamsRequestJson = new QrLoginParamsRequestJson();
                qrLoginParamsRequestJson.mAction = action;
                qrLoginParamsRequestJson.mCode = qrCode;
                qrLoginParamsRequestJson.mOrgId = PersonalShareInfo.getInstance().getCurrentOrg(context);
                return QrcodeSyncNetService.getInstance().qrLogin(context, new Gson().toJson(qrLoginParamsRequestJson));
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 获取个人分享二维码
     *
     * @param userId
     */
    @SuppressLint("StaticFieldLeak")
    public void fetchPersonalQrcode(final Context context, final String userId, final OnGetQrcodeListener listener) {

        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                return QrcodeSyncNetService.getInstance().getPersonalQrcode(context, userId);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if(httpResult.isRequestSuccess()) {
                    PersonalQrcodeResponseJson responseJson = (PersonalQrcodeResponseJson) httpResult.resultResponse;
                    Bitmap bitmap = BitmapUtil.strToBitmap(responseJson.mResult.mContent);
                    if (bitmap != null) {
                        listener.success(bitmap, responseJson.mResult.getSurvivalSeconds());
                        return;
                    }
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 获取组织分享url
     * @param context
     * @param orgId
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void fetchOrgQrUrl(final Context context, final String orgId, final onOrgQrUrlListener listener) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return QrcodeSyncNetService.getInstance().getOrgQrUrl(context, orgId);
            }

            @Override
            protected void onPostExecute(String result) {
                if (listener != null) {
                    listener.onQrUrlSuccess(result);
                }
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public interface OnGetQrcodeListener extends NetWorkFailListener {
        void success(Bitmap qrcodeBitmap, long effectTime);
    }

    public interface OnGetQrcodeRelativeInfoListener extends NetWorkFailListener {
        void success(GetQrCodeJoinInfoResponse response);
    }

    public interface OnQrLoginListener extends NetWorkFailListener {
        void success();
    }

    public interface onOrgQrUrlListener extends NetWorkFailListener {
        void onQrUrlSuccess(String url);
    }
}
