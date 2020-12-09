package com.foreveross.atwork.infrastructure.plugin.zoom

interface IZoomMeetingFinishedListenerProxy {
    fun onMeetingFinished()

    fun inMeetingStatus()
}