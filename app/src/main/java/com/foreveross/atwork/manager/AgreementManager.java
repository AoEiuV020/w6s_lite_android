package com.foreveross.atwork.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.agreement.UserLoginAgreementSyncService;
import com.foreveross.atwork.api.sdk.agreement.responseJson.AgreementStatusResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.manager.listener.BaseQueryListener;

/**
 * Created by dasunsy on 2017/7/22.
 */

public class AgreementManager {

    public static boolean SHOULD_CHECK_AGREEMENT = true;

    public static void  isUserLoginAgreementConfirmed(Context context, BaseQueryListener<AgreementStatus> listener) {
        new AsyncTask<Void, Void, AgreementStatus>() {
            @Override
            protected AgreementStatus doInBackground(Void... params) {
                PersonalShareInfo.getInstance().setNeedCheckSignedAgreement(context, true);

                return isUserLoginAgreementConfirmedSync(context);
            }

            @Override
            protected void onPostExecute(AgreementStatus agreementStatus) {
                listener.onSuccess(agreementStatus);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static AgreementStatus isUserLoginAgreementConfirmedSync(Context context) {
        if(!DomainSettingsManager.getInstance().handleLoginAgreementEnable()) {
            PersonalShareInfo.getInstance().setNeedCheckSignedAgreement(context, false);
            return AgreementStatus.NOT_SURE;
        }

        if(PersonalShareInfo.getInstance().isLoginSignedAgreementConfirmed(context)) {
            PersonalShareInfo.getInstance().setNeedCheckSignedAgreement(context, false);
            return AgreementStatus.CONFIRMED;
        }


        HttpResult httpResult = UserLoginAgreementSyncService.getAgreementStatus(context);
        if(httpResult.isRequestSuccess()) {

            PersonalShareInfo.getInstance().setNeedCheckSignedAgreement(context, false);

            AgreementStatusResponse agreementStatusResponse = (AgreementStatusResponse)httpResult.resultResponse;
            //save status to local
            PersonalShareInfo.getInstance().setLoginSignedAgreementConfirmed(context, agreementStatusResponse.mResult.mAgreementConfirmed);

            if(agreementStatusResponse.mResult.mAgreementConfirmed) {
                return AgreementStatus.CONFIRMED;

            } else {
                return AgreementStatus.NOT_CONFIRMED;

            }

        }

        return AgreementStatus.NOT_SURE;

    }

    public enum AgreementStatus {

        CONFIRMED,

        NOT_SURE,

        NOT_CONFIRMED
    }
}
