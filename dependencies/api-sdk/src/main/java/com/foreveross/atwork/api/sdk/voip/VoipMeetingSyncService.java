package com.foreveross.atwork.api.sdk.voip;

import android.content.Context;
import androidx.annotation.Nullable;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.voip.requestJson.HandleMeetingRequestJson;
import com.foreveross.atwork.api.sdk.voip.requestJson.ZoomHandleInfo;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.api.sdk.voip.responseJson.InviteMembersResponseJson;
import com.foreveross.atwork.api.sdk.voip.responseJson.JoinConfResponseJson;
import com.foreveross.atwork.api.sdk.voip.responseJson.LeaveConfResponseJson;
import com.foreveross.atwork.api.sdk.voip.responseJson.QueryZoomTypeMeetingStatusResponse;
import com.foreveross.atwork.api.sdk.voip.responseJson.RejectConfResponseJson;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberSettingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.model.zoom.ZoomSdk;
import com.foreveross.atwork.infrastructure.newmessage.post.voip.GateWay;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dasunsy on 16/7/14.
 */
public class VoipMeetingSyncService {

    /**
     * 发起 voip 会议
     * */
    public static HttpResult createMeeting(Context context, MeetingInfo meetingInfo, UserHandleInfo creator, VoipType voipType, List<UserHandleInfo> memberList, @Nullable ZoomHandleInfo zoomInfo) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_startMeeting(), accessToken);

        HandleMeetingRequestJson requestJson = new HandleMeetingRequestJson();
        if (null != zoomInfo) {
            assembleGateWay(zoomInfo, requestJson);

            requestJson.mBizconfHoldDuration = zoomInfo.getBizconfHoldDuration();

        } else {
            assembleSdkBasedVoipRequestJsonGateWay(requestJson, true);
        }

        requestJson.mMemberList = memberList;
        requestJson.mMeetingInfo = meetingInfo;
        requestJson.mVoipType = voipType;
        requestJson.mOperator = creator;

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(requestJson), 30 * 1000);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CreateOrQueryMeetingResponseJson.class));
        }

        return httpResult;
    }

    private static void assembleGateWay(@NotNull ZoomHandleInfo zoomInfo, HandleMeetingRequestJson requestJson) {
        if (ZoomSdk.BIZCONF == zoomInfo.getZoomSdk()) {
            requestJson.mGateway = GateWay.GATE_WAY_BIZCONF;
        } else if(ZoomSdk.ZOOM == zoomInfo.getZoomSdk()) {
            requestJson.mGateway = GateWay.GATE_WAY_ZOOM;
        }
    }

    public static void assembleSdkBasedVoipRequestJsonGateWay(HandleMeetingRequestJson requestJson, boolean isOwner) {

        if (VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
            requestJson.mGateway = GateWay.GATE_WAY_QUANSHIYUN;

            wrapQsyRequestJson(requestJson, isOwner);


        } else {
            requestJson.mGateway = GateWay.GATE_WAY_AGORA;

        }
    }

    /**
     * 查询会议当前状态
     * */
    public static HttpResult queryMeeting(Context context, String meetingId) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_queryMeeting(), meetingId, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CreateOrQueryMeetingResponseJson.class));
        }

        return httpResult;
    }

    /**
     * 邀请会议
     * */
    public static HttpResult inviteMeeting(Context context, String meetingId, MeetingInfo meetingInfo, VoipType voipType, List<UserHandleInfo> memberList, @Nullable ZoomHandleInfo zoomInfo) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_handleMeeting(), meetingId, accessToken);

        HandleMeetingRequestJson confRequestJson = new HandleMeetingRequestJson();

        if (null != zoomInfo) {
            assembleGateWay(zoomInfo, confRequestJson);

        } else {
            assembleSdkBasedVoipRequestJsonGateWay(confRequestJson, true);

        }

        confRequestJson.mMemberList = memberList;
        confRequestJson.mMeetingInfo = meetingInfo;
        confRequestJson.mVoipType = voipType;
        confRequestJson.mOps = HandleMeetingRequestJson.ConfOps.INVITE.toString();


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(confRequestJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, InviteMembersResponseJson.class));
        }

        return httpResult;
    }

    /**
     * 加入 voip 会议
     * */
    public static HttpResult joinMeeting(Context context, String meetingId, MeetingInfo meetingInfo, VoipType voipType, @Nullable ZoomHandleInfo zoomInfo) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_handleMeeting(), meetingId, accessToken);

        HandleMeetingRequestJson confRequestJson = new HandleMeetingRequestJson();

        if(null != zoomInfo) {
            assembleGateWay(zoomInfo, confRequestJson);

        } else {
            assembleSdkBasedVoipRequestJsonGateWay(confRequestJson, false);

        }

        confRequestJson.mMeetingInfo = meetingInfo;
        confRequestJson.mOps = HandleMeetingRequestJson.ConfOps.JOIN.toString();
        confRequestJson.mVoipType = voipType;



        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(confRequestJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, JoinConfResponseJson.class));
        }

        return httpResult;
    }

    /**
     * 挂电话
     * */
    public static HttpResult rejectConf(Context context, String meetingId, MeetingInfo meetingInfo, UserHandleInfo operator, @Nullable ZoomHandleInfo zoomInfo) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_handleMeeting(), meetingId, accessToken);

        HandleMeetingRequestJson confRequestJson = new HandleMeetingRequestJson();
        if(null != zoomInfo) {
            assembleGateWay(zoomInfo, confRequestJson);

        } else {
            assembleSdkBasedVoipRequestJsonGateWay(confRequestJson, false);

        }

        confRequestJson.mOperator = operator;
        confRequestJson.mMeetingInfo = meetingInfo;
        confRequestJson.mOps = HandleMeetingRequestJson.ConfOps.REJECT.toString();

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(confRequestJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, RejectConfResponseJson.class));
        }

        return httpResult;
    }


    /**
     * 离开会议
     * */
    public static HttpResult leaveMeeting(Context context, String meetingId, MeetingInfo meetingInfo, UserHandleInfo operator, @Nullable ZoomHandleInfo zoomInfo) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_handleMeeting(), meetingId, accessToken);

        HandleMeetingRequestJson confRequestJson = new HandleMeetingRequestJson();

        if(null != zoomInfo) {
            assembleGateWay(zoomInfo, confRequestJson);

        } else {
            assembleSdkBasedVoipRequestJsonGateWay(confRequestJson, false);
        }

        confRequestJson.mOperator = operator;
        confRequestJson.mMeetingInfo = meetingInfo;
        confRequestJson.mOps = HandleMeetingRequestJson.ConfOps.LEAVE.toString();

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(confRequestJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LeaveConfResponseJson.class));
        }

        return httpResult;
    }

    /**
     * busy 中, 不处理电话
     * */
    public static HttpResult busyMeeting(Context context, String meetingId, MeetingInfo meetingInfo) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_handleMeeting(), meetingId, accessToken);

        HandleMeetingRequestJson confRequestJson = new HandleMeetingRequestJson();
        assembleSdkBasedVoipRequestJsonGateWay(confRequestJson, false);

        confRequestJson.mMeetingInfo = meetingInfo;
        confRequestJson.mOps = HandleMeetingRequestJson.ConfOps.BUSY.toString();

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(confRequestJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LeaveConfResponseJson.class));
        }

        return httpResult;
    }

    /**
     * 定期请求, 通知后台服务当前成员仍然在会议中, 后台也用此接口来调整语音会议时长, 避免因意外断开会议导致无法统计的情况
     * */
    public static HttpResult refreshMeeting(Context context, String meetingId, VoipMeetingMemberSettingInfo meetingMemberSettingInfo) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_handleMeeting(), meetingId, accessToken);

        LogUtil.e("request ->  refresh " + url);

        HandleMeetingRequestJson confRequestJson = new HandleMeetingRequestJson();
        confRequestJson.mOps = HandleMeetingRequestJson.ConfOps.REFRESH.toString();
        confRequestJson.mVoipMeetingMemberSettingInfo = meetingMemberSettingInfo;

        String postParams = JsonUtil.toJson(confRequestJson);

        LogUtil.e("request ->  refresh body " + postParams);


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, postParams);
        LogUtil.e("request ->  refresh result " + httpResult.result);

        if(httpResult.isNetSuccess()) {

            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CreateOrQueryMeetingResponseJson.class));
        }

        return httpResult;
    }



    /**
     * 查询zoom 产商的会议状态
     * @param meetingId
     * */
    public static HttpResult queryZoomTypeMeetingStatus(String meetingId) {
        String url = String.format(UrlConstantManager.getInstance().getQueryZoomTypeMeetingStatusUrl(), AtworkConfig.DOMAIN_ID, meetingId, AtworkConfig.ZOOM_CONFIG.getSdk().toString().toLowerCase());
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {

            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryZoomTypeMeetingStatusResponse.class));
        }

        return httpResult;
    }



    private static void wrapQsyRequestJson(HandleMeetingRequestJson confRequestJson, boolean isOwner) {
        confRequestJson.mClientType = "6";
        confRequestJson.mIsOwner = isOwner;
        confRequestJson.mIpAddr = "0.0.0.0";
        confRequestJson.mRoleMap = new HashMap<>();
        confRequestJson.mRoleMap.put("4", new String[0]);
    }

}
