package com.foreveross.atwork.modules.meeting.util;

import android.content.Context;
import android.net.Uri;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingJoinRequest;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.MeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.plugin.UCCalendarPlugin;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UCCalendarManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.meeting.service.UmeetingReflectService;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ErrorHandleUtil;

/**
 * Created by dasunsy on 2017/11/15.
 */

public class MeetingHelper {

    public static void handleSchemaUrl(Context context, String schemaUrl) {
        Uri uri = Uri.parse(schemaUrl.toLowerCase());
        String type = uri.getQueryParameter("type");
        String action = uri.getQueryParameter("action");

        if("umeeting".equalsIgnoreCase(type) && "join".equalsIgnoreCase(action)) {

            String meetingId = uri.getQueryParameter("id");
            String name = LoginUserInfo.getInstance().getLoginUserName(BaseApplicationLike.baseContext);

            UmeetingJoinRequest umeetingJoinRequest = UmeetingJoinRequest.newInstance()
                    .setContext(context)
                    .setDisplayName(name)
                    .setMeetingNo(meetingId);

            UmeetingReflectService.joinMeeting(umeetingJoinRequest);
        }
    }

    public static void handleClick(Context context, MeetingNoticeChatMessage meetingNoticeChatMessage) {
        if (MeetingNotifyMessage.Type.UMEETING == meetingNoticeChatMessage.mType) {

            handleZoomLogic(context, meetingNoticeChatMessage, true);

            return;
        }

        if (MeetingNotifyMessage.Type.ZOOM == meetingNoticeChatMessage.mType) {

            handleZoomLogic(context, meetingNoticeChatMessage, false);

            return;
        }


        if (MeetingNotifyMessage.Type.BIZCONF == meetingNoticeChatMessage.mType) {

            handleZoomLogic(context, meetingNoticeChatMessage, false);

            return;
        }


        if (MeetingNotifyMessage.Type.QSY == meetingNoticeChatMessage.mType) {
            handleUCCalendarLogic(context, meetingNoticeChatMessage);
            return;
        }

        AtworkToast.showResToast(R.string.error_happened);

    }

    private static void handleZoomLogic(Context context, MeetingNoticeChatMessage meetingNoticeChatMessage, boolean hideTitle) {
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(context);
        progressDialogHelper.show();

            ZoomVoipManager.INSTANCE.queryZoomTypeMeetingStatus(meetingNoticeChatMessage.mMeetingId, new BaseNetWorkListener<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    progressDialogHelper.dismiss();

                    if(result) {
                        routeZoomUrl(context, meetingNoticeChatMessage, hideTitle);

                    } else {
//                        routeZoomUrl(context, meetingNoticeChatMessage, hideTitle);

                        AtworkToast.showResToast(R.string.meeting_is_illegal);
                    }
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    progressDialogHelper.dismiss();
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }
            });

    }

    private static void routeZoomUrl(Context context, MeetingNoticeChatMessage meetingNoticeChatMessage, boolean hideTitle) {
        if(StringUtils.isEmpty(meetingNoticeChatMessage.mUrl)) {
            AtworkToast.showResToast(R.string.error_happened);
        } else {

            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(meetingNoticeChatMessage.mUrl)
                    .setNeedShare(false)
                    .setHideTitle(hideTitle);

            UrlRouteHelper.routeUrl(context, webViewControlAction);
//
//            Intent intent = WebViewActivity.getIntent(context, webViewControlAction);
//            context.startActivity(intent);
        }
    }

    private static void handleUCCalendarLogic(Context context, MeetingNoticeChatMessage message) {
        if (message.mOperation.equals(MeetingNotifyMessage.Operation.CANCEL)) {
            AtworkToast.showResToast(R.string.meeting_had_canceled);
            return;
        }
        ProgressDialogHelper dialog = new ProgressDialogHelper(context);
        dialog.show(false, 60000);
        String attendPassword = message.mAttendeePassword;
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        if (message.mOperatorId.equalsIgnoreCase(userId)) {
            attendPassword = message.mHostPassword;
        }
        try {
            int confId = Integer.valueOf(message.mMeetingId);
            User user = UserManager.getInstance().queryLocalUser(userId);
            UCCalendarManager.getInstance().onJoinConfImmediately(context, confId, user.getShowName(), attendPassword, new UCCalendarPlugin.OnUCCalendarJoinConfListener() {
                @Override
                public void onJoinConfSuccess() {

                    dialog.dismiss();
                }

                @Override
                public void onJoinConfFail() {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
