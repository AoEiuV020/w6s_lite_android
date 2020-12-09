package com.foreveross.atwork.modules.meeting.route

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingJoinRequest
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.app.route.routeUrl
import com.foreveross.atwork.modules.meeting.activity.ZoomJoinVoipMeetingActivity
import com.foreveross.atwork.modules.meeting.service.UmeetingReflectService
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams

class JoinMeetingRouteAction(routeParams: RouteParams) : RouteAction(routeParams) {


    override fun action(context: Context) {
        joinMeeting(context)


    }


    fun joinMeeting(context: Context) {

        Handler(Looper.getMainLooper()).post {

            routeParams?.getUri()?.apply {
                val type = getQueryParameter("type")
                val action = getQueryParameter("action")

                if ("umeeting".equals(type, ignoreCase = true)
                        && "join".equals(action, ignoreCase = true)) {

                    joinUmeeting(this, context)

                    return@apply
                }

                if ("bizconf".equals(type, ignoreCase = true)
                        || "zoom".equals(type, ignoreCase = true)) {

                    if ("join".equals(action, ignoreCase = true)) {
                        joinZoomMeeting(this, context)
                    }
                }


            }

        }
    }

    private fun joinZoomMeeting(uri: Uri, context: Context) {
        if (!ZoomManager.isInitialized(context)) {
            return
        }
        val viewAction = uri.getQueryParameter("view")?.toInt()
        if(1 == viewAction) {
            context.startActivity(ZoomJoinVoipMeetingActivity.getIntent(context))
            return
        }


        val meetingId = uri.getQueryParameter("id")
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        val userName = LoginUserInfo.getInstance().getLoginUserUserName(context)
        val url = routeParams?.getUri().toString()
        var meetingUrl = ""

        if (!StringUtils.isEmpty(meetingId)) {
            val webViewControlAction = WebViewControlAction.newAction()
                    .setUrl("${AtworkConfig.ZOOM_CONFIG.detailUrl}?confId=$meetingId")
                    .setNeedShare(false)
                    .setHideTitle(false)

            routeUrl(context, webViewControlAction)

            return
        }


        if (url.contains("url=")) {
            val key = "url="
            meetingUrl = url.substring(url.lastIndexOf(key) + key.length)
        }
        val handleMeetingInfo = HandleMeetingInfo(userId, userName, null, meetingId, null, null, if (meetingUrl.isEmpty()) null else meetingUrl)
        ZoomManager.joinMeeting(context, handleMeetingInfo)
    }

    private fun joinUmeeting(uri: Uri, context: Context) {
        if (!UmeetingReflectService.isInitialized()) {
            return
        }

        val name = LoginUserInfo.getInstance().getLoginUserName(context)

        val meetingId = uri.getQueryParameter("id")
        val umeetingJoinRequest = UmeetingJoinRequest.newInstance()
                .setContext(context)
                .setMeetingNo(meetingId)
                .setDisplayName(name)

        UmeetingReflectService.joinMeeting(umeetingJoinRequest)
    }

}