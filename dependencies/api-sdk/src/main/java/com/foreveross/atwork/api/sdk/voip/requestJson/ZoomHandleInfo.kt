package com.foreveross.atwork.api.sdk.voip.requestJson

import com.foreveross.atwork.infrastructure.model.zoom.ZoomSdk
import com.foreveross.atwork.infrastructure.support.AtworkConfig

class ZoomHandleInfo (

        val bizconfHoldDuration: Int? = null,

        val zoomSdk: ZoomSdk? = AtworkConfig.ZOOM_CONFIG.sdk
)