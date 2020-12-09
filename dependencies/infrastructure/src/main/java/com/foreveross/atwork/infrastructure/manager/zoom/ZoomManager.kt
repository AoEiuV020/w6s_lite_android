package com.foreveross.atwork.infrastructure.manager.zoom

import android.content.Context
import android.net.Uri
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo
import com.foreveross.atwork.infrastructure.model.zoom.ZoomJoinMeetingHistoryDataItem
import com.foreveross.atwork.infrastructure.model.zoom.ZoomSdk
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore
import com.foreveross.atwork.infrastructure.plugin.zoom.IZoomConfMeetingServiceListenerProxy
import com.foreveross.atwork.infrastructure.plugin.zoom.IZoomMeetingFinishedListenerProxy
import com.foreveross.atwork.infrastructure.plugin.zoom.IZoomPlugin
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException

object ZoomManager: IZoomPlugin, IZoomManualJoinHistoryService {


    private var plugin: IZoomPlugin? = null

    var isMeetingCalling = false

    var currentConfId: String? = null


    private fun checkPlugin() {
        if (null == plugin) {
            try {
                WorkplusPluginCore.registerPresenterPlugin("com.foreverht.workplus.zoom.ZoomPresenter")
                plugin = WorkplusPluginCore.getPlugin(IZoomPlugin::class.java)
            } catch (e: ReflectException) {
                e.printStackTrace()
            }

        }
    }


    override fun init(context: Context, channelId: String, autoKey: String, domain: String) {
        checkPlugin()
        plugin?.init(context, channelId, autoKey, domain)


        addMeetingFinishedListener(context, object : IZoomMeetingFinishedListenerProxy {
            override fun onMeetingFinished() {
                LogUtil.e("bizconf", "meeting onMeetingFinished")

                isMeetingCalling = false
            }

            override fun inMeetingStatus() {
                LogUtil.e("bizconf", "meeting inMeetingStatus")

                isMeetingCalling = true

            }

        })

    }

    override fun startMeeting(context: Context, handleMeetingInfo: HandleMeetingInfo) {

        if(!StringUtils.isEmpty(handleMeetingInfo.meetingUrl)) {
            currentConfId = Uri.parse(handleMeetingInfo.meetingUrl).getQueryParameter("confId")

            LogUtil.e("bizconf", "startMeeting  meetingUrl : ${handleMeetingInfo.meetingUrl}")

        }

        checkPlugin()
        plugin?.startMeeting(context, handleMeetingInfo)
    }

    override fun joinMeeting(context: Context, handleMeetingInfo: HandleMeetingInfo) {

        if(!StringUtils.isEmpty(handleMeetingInfo.meetingUrl)) {
            currentConfId = Uri.parse(handleMeetingInfo.meetingUrl).getQueryParameter("confId")

            LogUtil.e("bizconf", "joinMeeting  meetingUrl : ${handleMeetingInfo.meetingUrl}")

        }

        checkPlugin()
        plugin?.joinMeeting(context, handleMeetingInfo)
    }

    override fun isInitialized(context: Context): Boolean {
        checkPlugin()

        if(null == plugin) {
            return false
        }

        return plugin!!.isInitialized(context)
    }

    override fun addMeetingFinishedListener(context: Context, listenerProxy: IZoomMeetingFinishedListenerProxy) {
        checkPlugin()
        plugin?.addMeetingFinishedListener(context, listenerProxy)
    }

    override fun addZoomConfMeetingServiceListener(context: Context, listenerProxy: IZoomConfMeetingServiceListenerProxy) {
        checkPlugin()
        plugin?.addZoomConfMeetingServiceListener(context, listenerProxy)
    }

    override fun setMuteMyMicrophoneWhenJoinMeeting(value: Boolean) {
        checkPlugin()
        plugin?.setMuteMyMicrophoneWhenJoinMeeting(value)
    }

    override fun setTurnOffMyVideoWhenJoinMeeting(value: Boolean) {
        checkPlugin()
        plugin?.setTurnOffMyVideoWhenJoinMeeting(value)
    }

    override fun updateJoinData(context: Context, dataItem: ZoomJoinMeetingHistoryDataItem) {
        CommonShareInfo.updateZoomJoinMeetingHistoryData(context, dataItem)
    }

    override fun getLastJoinData(context: Context): ZoomJoinMeetingHistoryDataItem? {
        return getJoinDataList(context)?.getOrNull(0)
    }


    override fun getJoinDataList(context: Context): List<ZoomJoinMeetingHistoryDataItem>? {
        val dataList = CommonShareInfo.getZoomJoinMeetingHistoryData(context)?.dataList
        dataList?.sortByDescending { it.joinTime }
        return dataList
    }
}