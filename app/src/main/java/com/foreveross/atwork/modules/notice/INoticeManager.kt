package com.foreveross.atwork.modules.notice

import com.foreveross.atwork.api.sdk.app.model.LightNoticeData

interface INoticeManager {
    fun queryNoticeData(key: String, url: String, onGetResult: (LightNoticeData?)-> Unit)
}