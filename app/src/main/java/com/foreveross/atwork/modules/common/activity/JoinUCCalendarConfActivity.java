package com.foreveross.atwork.modules.common.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.foreverht.cache.UCCalendarCache;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.uccalendar.UCCalendarToken;
import com.foreveross.atwork.infrastructure.plugin.UCCalendarPlugin;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UCCalendarManager;
import com.foreveross.atwork.support.BaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ContactQueryHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

/**
 * Created by reyzhang22 on 2017/12/19.
 */

public class JoinUCCalendarConfActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       checkScheme(this);
       finish();
    }

    /**
     * 检查schema启动
     * @param context
     */
    public void checkScheme(Activity context) {
        if (Intent.ACTION_VIEW.equals(context.getIntent().getAction())) {
            Uri calendarUri = context.getIntent().getData();
            long eventId = ContentUris.parseId(calendarUri);

            String username = LoginUserInfo.getInstance().getLoginUserRealUserName(context);
            if (TextUtils.isEmpty(username)) {
                return;
            }
            UCCalendarToken ucCalendarToken = UCCalendarCache.getInstance().getUCCalendarCache(username);
            if (ucCalendarToken != null && ucCalendarToken.mIsLogin) {

                return;
            }
            UCCalendarManager.getInstance().getUCCalendarToken(context, new UCCalendarPlugin.OnUCCalendarTokenListener() {
                @Override
                public void onUCCalendarTokenSuccess(UCCalendarToken token) {

                    ContactQueryHelper.getCurrentContactMobile(mobile -> {

                        if(StringUtils.isEmpty(mobile)) {
                            AtworkToast.showResToast(R.string.qsy_cannot_into_meeting_by_phone);
                        } else {
                            UCCalendarManager.getInstance().setPhoneNumber(mobile);
                        }

                        doLoginUCCalendar(context, token, eventId);


                    });

                }

                @Override
                public void onUCCalendarTokenFail(int error) {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.QsyCalendar, error, StringUtils.EMPTY);
                }
            });

        }
    }


    private void doLoginUCCalendar(Context context, UCCalendarToken token, long eventId) {
        UCCalendarManager.getInstance().onLoginUCCalendar(token.mSerialNo, token.mToken, new UCCalendarPlugin.OnUCCalendarLoginListener() {
            @Override
            public void onUCCalendarLoginSuccess() {
                UCCalendarManager.getInstance().onJoinConf(context, eventId);
            }

            @Override
            public void onUCCalendarLoginFail(String reason) {
                AtworkToast.showToast("login fail:" + reason);
            }
        });

    }
}
