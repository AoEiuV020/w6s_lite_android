package com.foreveross.atwork.modules.route.manager

import android.net.Uri
import androidx.core.net.toUri
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util
import com.foreveross.atwork.modules.aboutme.route.UserRouteAction
import com.foreveross.atwork.modules.app.route.OpenAppRouteAction
import com.foreveross.atwork.modules.email.route.SystemEmailRouteAction
import com.foreveross.atwork.modules.meeting.route.manager.MeetingRouteActionProducer
import com.foreveross.atwork.modules.qrcode.service.QrcodeManager
import com.foreveross.atwork.modules.voip.route.OpenVoipRouteAction
import com.foreveross.atwork.modules.route.action.*
import com.foreveross.atwork.modules.route.model.RouteParams
import com.foreveross.atwork.modules.route.pay.WxPayRouteActionProducer
import com.foreveross.atwork.modules.search.route.OpenSearchRouteAction
import com.foreveross.atwork.modules.web.route.MainViewRouteAction
import com.foreveross.atwork.modules.web.route.OpenLinkRouteAction
import com.foreveross.atwork.modules.web.route.TargetUrlRouteAction

object RouteActionFactory: IRouteActionProducer {


    override fun produce(routeParams: RouteParams): RouteAction? {
        val uri = routeParams.getUri()
        when (uri?.scheme) {
            "mailto" -> return SystemEmailRouteAction(routeParams)
            "weixin" -> return WxPayRouteActionProducer().produce(routeParams)
            "http", "https" -> return compatibleWorkplusProduce(routeParams)
        }

        return when (routeParams.getEntryType()) {
            Session.EntryType.To_APP -> ApplyRouteAction(routeParams)
            Session.EntryType.To_Chat_Detail -> ChatDetailRouteAction(routeParams)
            Session.EntryType.To_ORG_APPLYING -> ApplyRouteAction(routeParams)
            Session.EntryType.To_URL -> TargetUrlRouteAction(routeParams)
            else -> {
                workplusProduce(routeParams)
            }
        }

    }

    override fun canRoute(routeParams: RouteParams): Boolean  = null != produce(routeParams)


    private fun compatibleWorkplusProduce(routeParams: RouteParams): RouteAction? {
        val uri = routeParams.getUri()
        val url = uri?.toString() ?: return null

        if(url.contains(QrcodeManager.ACTION_MEETING_JUMP)) {
            val startIndex = url.indexOf(QrcodeManager.ACTION_MEETING_JUMP) + QrcodeManager.ACTION_MEETING_JUMP.length
            val routeInfo = url.substring(startIndex)

            val routeUrl = "workplus://meeting$routeInfo"
            return workplusProduce(RouteParams.newRouteParams().uri(routeUrl.toUri()).build())

        }


        if (url.contains("w6sCompatibleRoute=")) {

            val w6sRouteUrl = Base64Util.decode2Str(url.substringAfterLast("w6sCompatibleRoute=", StringUtils.EMPTY))

            val routeAction = workplusProduce(RouteParams.newRouteParams().uri(Uri.parse(w6sRouteUrl)).build())
            if(null != routeAction) {
                return routeAction
            }

        }


        if (url.contains("w6sRoute=")) {

            val w6sRouteUrl = Base64Util.decode2Str(url.substringAfterLast("w6sRoute=", StringUtils.EMPTY))

            val routeAction = workplusProduce(RouteParams.newRouteParams().uri(Uri.parse(w6sRouteUrl)).build())
            if(null != routeAction) {
                return routeAction
            }

        }




        return null

    }


    private fun workplusProduce(routeParams: RouteParams): RouteAction? {
        val uri = routeParams.getUri()

        if(!isLegal(uri?.scheme)) {
            return null
        }

        return when (uri?.host) {
            "openlink" -> OpenLinkRouteAction(routeParams)
            "meeting" -> MeetingRouteActionProducer().produce(routeParams)
            "openapp", "openApp" -> OpenAppRouteAction(routeParams)
            "search" -> OpenSearchRouteAction(routeParams)
            "user" -> UserRouteAction(routeParams)
            "main" -> MainViewRouteAction(routeParams)
            "voip" -> OpenVoipRouteAction(routeParams)
            else -> null
        }
    }


    private fun isLegal(scheme: String?): Boolean {
        if(null == scheme) {
            return false
        }


        if(BeeWorks.getInstance().config.scheme.equals(scheme, ignoreCase = true) ) {
            return true
        }

        val schema = scheme.toLowerCase()

        if ("workplus-inner" == schema) {
            return true
        }

        if ("workplus" == schema) {
            return true
        }

        if ("rfchina" == schema) {
            return true
        }

        if ("sykj" == schema) {
            return true
        }

        if ("zjhes" == schema) {
            return true
        }

        if ("cimc" == schema) {
            return true
        }

        if ("zoom" == schema) {
            return true
        }


        return false

    }



}