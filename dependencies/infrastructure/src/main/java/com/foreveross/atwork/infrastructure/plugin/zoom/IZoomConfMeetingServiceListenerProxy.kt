package com.foreveross.atwork.infrastructure.plugin.zoom

import com.foreveross.atwork.infrastructure.plugin.zoom.ZoomMeetingStatusProxy

interface IZoomConfMeetingServiceListenerProxy {

    fun onMeetingStatusChanged(zoomMeetingStatus: ZoomMeetingStatusProxy, errorCode: Int, internalErrorCode: Int)
}