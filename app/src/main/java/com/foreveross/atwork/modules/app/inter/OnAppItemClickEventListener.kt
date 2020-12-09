package com.foreveross.atwork.modules.app.inter

import com.foreveross.atwork.infrastructure.model.app.AppBundles

interface OnAppItemClickEventListener {
    fun onCustomModeClick(appBundle: AppBundles)
}