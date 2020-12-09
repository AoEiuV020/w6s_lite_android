package com.foreveross.atwork.modules.voip.support.zoom.interfaces

interface OnZoomVoipHandleListener {
    fun goMeeting(tokenKey: String)

    fun onInitVoipMeeting()
}