package com.foreveross.atwork.api.sdk.util;

import android.content.Context;

import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.colleague.ColleagueAsyncNetService;
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

import java.util.HashSet;


public class LightNoticeHelper {

    private static String getLoadingNoticeUrlKey(Context context, String noticeUrl) {
        return noticeUrl + "_" + LoginUserInfo.getInstance().getLoginUserId(context);
    }

    //todo 回调太深了
    public static void loadLightNotice(final String noticeUrl, final Context context, final LightNoticeListener listener) {

        final String loadingNoticeUrlKey = getLoadingNoticeUrlKey(context, noticeUrl);
        if(!RequestRemoteInterceptor.INSTANCE.checkLegal(loadingNoticeUrlKey)) {

            LogUtil.e("is loading -> " + noticeUrl);
            return;
        }

        LogUtil.e("start loading -> " + noticeUrl);


        RequestRemoteInterceptor.INSTANCE.addInterceptRequestId(loadingNoticeUrlKey);


        CordovaAsyncNetService.getUserTicket(context, null, 5 * 1000, new CordovaAsyncNetService.GetUserTicketListener() {
            @Override
            public void getUserTicketSuccess(String userTicket) {
                try {
                    ColleagueAsyncNetService.getInstance().getLightNotice(context, noticeUrl, userTicket, new ColleagueAsyncNetService.OnLightNoticeListener() {
                        @Override
                        public void onSuccess(LightNoticeData lightNoticeJson) {
                            RequestRemoteInterceptor.INSTANCE.removeInterceptRequestId(loadingNoticeUrlKey);
                            listener.success(lightNoticeJson);
                        }

                        @Override
                        public void networkFail(int errorCode, String errorMsg) {
                            RequestRemoteInterceptor.INSTANCE.removeInterceptRequestId(loadingNoticeUrlKey);

                            listener.fail();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                RequestRemoteInterceptor.INSTANCE.removeInterceptRequestId(loadingNoticeUrlKey);

                listener.fail();

            }
        });
    }

    public interface LightNoticeListener {
        void success(LightNoticeData lightNoticeJson);
        void fail();
    }


}
