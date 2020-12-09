package com.foreveross.atwork.modules.meeting.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.List;

/**
 * Created by dasunsy on 2017/11/11.
 */

public abstract class UmeetingInviteBasicActivity extends FragmentActivity {

    //创建预约会畅会议 code
    public static final int INVITE_ZOOM_MEETING_INSTANT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AtworkConfig.ZOOM_CONFIG.getEnabled()) {

            handleZoomInvite();

            return;

        }


        long meetingIdLong = getIntent().getLongExtra("meetingId", 0);
        String meetingId = Long.toString(meetingIdLong);

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(AtworkConfig.ZOOM_CONFIG.getInviteUrl() + "?meetingId=" + meetingId)
                .setHideTitle(true)
                .setNeedShare(false)
                .setUseSystem(true);

        Intent intent = WebViewActivity.getIntent(this, webViewControlAction);
        startActivity(intent);

        finish();
    }

    private void handleZoomInvite() {
        CurrentVoipMeeting currentVoipMeeting = ZoomVoipManager.INSTANCE.getCurrentVoipMeeting();

        if(null != currentVoipMeeting) {
            //处于即时呼叫的模式中


            //非群聊, 直接打开组织架构选人界面
            if (MeetingInfo.Type.DISCUSSION == currentVoipMeeting.mMeetingInfo.mType) {
                Intent intent = ZoomInstantMeetingInviteActivity.getIntent(this);
                startActivity(intent);
                finish();

            } else {
                SelectedContactList.clear();

                UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
//                userSelectControlAction.setSelectedContacts(ZoomVoipManager.INSTANCE.getMemberList());

                Intent intent = UserSelectActivity.getIntent(this, userSelectControlAction);

                startActivityForResult(intent, INVITE_ZOOM_MEETING_INSTANT);

            }
            return;
        }


        String meetingId = StringUtils.EMPTY;
        if (null != ZoomManager.INSTANCE.getCurrentConfId()) {
            meetingId = ZoomManager.INSTANCE.getCurrentConfId();
        }

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(AtworkConfig.ZOOM_CONFIG.getInviteUrl() + "?meetingId=" + meetingId)
                .setHideTitle(false)
                .setNeedShare(false)
                .setUseSystem(true);

        Intent intent = WebViewActivity.getIntent(this, webViewControlAction);
        startActivity(intent);

        finish();
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(INVITE_ZOOM_MEETING_INSTANT == requestCode && RESULT_OK == resultCode) {
            handleInviteZoomMeetingInstant();

        }
    }

    private void handleInviteZoomMeetingInstant() {
        CurrentVoipMeeting currentVoipMeeting = ZoomVoipManager.INSTANCE.getCurrentVoipMeeting();
        if(null == currentVoipMeeting) {
            return;
        }


        List<ShowListItem> contactList = SelectedContactList.getContactList();
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(this);
        progressDialogHelper.show();


        ZoomVoipManager.INSTANCE.inviteMeeting(this, currentVoipMeeting.mWorkplusVoipMeetingId, currentVoipMeeting.mMeetingInfo, currentVoipMeeting.mVoipType, ContactHelper.transferContactList(contactList), new VoipManager.OnInviteVoipMeetingListener() {
            @Override
            public void onSuccess(List<VoipMeetingMember> meetingMemberList) {
                progressDialogHelper.dismiss();

                AtworkToast.showToast("邀请成功");
                finish();
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                progressDialogHelper.dismiss();

                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }
        });
    }
}
