package com.foreveross.atwork.modules.meeting.route.manager

import com.foreveross.atwork.modules.meeting.route.JoinMeetingRouteAction
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.manager.IRouteActionProducer
import com.foreveross.atwork.modules.route.model.RouteParams

class MeetingRouteActionProducer: IRouteActionProducer {

    override fun produce(routeParams: RouteParams): RouteAction? {
        val uri = routeParams.getUri()
        val type = uri?.getQueryParameter("type")
        val action = uri?.getQueryParameter("action")

        when(type) {
            "umeeting" -> {

                when(action) {
                    "join" -> {
                        return JoinMeetingRouteAction(routeParams)
                    }
                }
            }

            "bizconf", "zoom" -> {

                when(action) {
                    "join" -> {
                        return JoinMeetingRouteAction(routeParams)
                    }
                }
            }
        }

        return null
    }



}