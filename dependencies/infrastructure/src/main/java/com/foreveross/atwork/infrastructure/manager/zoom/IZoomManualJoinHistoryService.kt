package com.foreveross.atwork.infrastructure.manager.zoom

import android.content.Context
import com.foreveross.atwork.infrastructure.model.zoom.ZoomJoinMeetingHistoryDataItem

interface IZoomManualJoinHistoryService {

    fun updateJoinData(context: Context, dataItem: ZoomJoinMeetingHistoryDataItem)

    fun getLastJoinData(context: Context): ZoomJoinMeetingHistoryDataItem?

    fun getJoinDataList(context: Context): List<ZoomJoinMeetingHistoryDataItem>?
}