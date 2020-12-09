package com.foreveross.atwork.modules.meeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingJoinRequest;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.modules.meeting.service.UmeetingReflectService;

/**
 * Created by dasunsy on 2017/12/24.
 */

public class JoinUmeetingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (StringUtils.isEmpty(LoginUserInfo.getInstance().getLoginUserAccessToken(this))) {
            HandleLoginService.toLoginHandle(this, getIntent(),false);
            finish();
            return;
        }

        if (!(Intent.ACTION_VIEW.equals(getIntent().getAction()))) {
            finish();
            return;
        }

        Uri uri = getIntent().getData();

        if (null == uri) {
            finish();
            return;
        }


        String type = uri.getQueryParameter("type");

        if (!"umeeting".equalsIgnoreCase(type)) {
            finish();
            return;
        }

        if (UmeetingReflectService.isInitialized()) {
            joinMeeting(uri);
            return;
        }


        UmeetingReflectService.initialize(this, AtworkConfig.ZOOM_CONFIG, (errorCode, internalErrorCode) -> joinMeeting(uri));

    }

    private void joinMeeting(Uri uri) {
        runOnUiThread(() -> {
            String name = LoginUserInfo.getInstance().getLoginUserName(this);

            String meetingId = uri.getQueryParameter("id");
            UmeetingJoinRequest umeetingJoinRequest = UmeetingJoinRequest.newInstance()
                    .setContext(this)
                    .setMeetingNo(meetingId)
                    .setDisplayName(name);

            UmeetingReflectService.joinMeeting(umeetingJoinRequest);


            finish();
        });
    }
}
