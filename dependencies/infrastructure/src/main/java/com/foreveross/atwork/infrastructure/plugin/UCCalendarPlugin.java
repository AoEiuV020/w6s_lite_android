package com.foreveross.atwork.infrastructure.plugin;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.uccalendar.UCCalendarToken;

import java.util.ArrayList;

/**
 * Created by reyzhang22 on 2017/11/21.
 */

public class UCCalendarPlugin {

    public interface IUCCalendar extends WorkplusPlugin {

        void onCalendarInit(Context context);

        void onCalendarLogin(String uId, String token, OnUCCalendarLoginListener listener);

        void onShowCalendar(Context context);

        void onShowConfDetail(Context context, long eventId);

        void onCreateConf(Context context, ArrayList<String> uIdList);

        void onJoinConf(Context context, long confId);

        void onJoinConfImmediately(Context context, int confId, String username, String confPwd, OnUCCalendarJoinConfListener listener);

        void onCalendarLogout();

        void onUploadLogsWithDescription(String desc, OnUCCalendarCallBack onUCCalendarCallBack);

        void onAddUserStatusListener(OnUCCalendarUserStatusListener listener);

        void setPhoneNumber(String phoneNumber);

    }

    public interface OnUCCalendarCallBack {
        void onSuccess();

        void onFail(int errCode);

        void onProcess();
    }

    public interface OnUCCalendarLoginListener {

        void onUCCalendarLoginSuccess();

        void onUCCalendarLoginFail(String reason);
    }

    public interface OnUCCalendarTokenListener  {

        void onUCCalendarTokenSuccess(UCCalendarToken token);

        void onUCCalendarTokenFail(int error);
    }

    public interface OnUCCalendarJoinConfListener {

        void onJoinConfSuccess();

        void onJoinConfFail();
    }

    public interface OnUCCalendarUserStatusListener {
        void onSessionInvalid();
    }
}
