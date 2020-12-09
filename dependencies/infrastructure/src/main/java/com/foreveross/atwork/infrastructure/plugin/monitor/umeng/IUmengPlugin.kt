package com.foreveross.atwork.infrastructure.plugin.monitor.umeng

import android.content.Context
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin
import java.util.HashMap

interface IUmengPlugin: WorkplusPlugin {

    fun init(context: Context, appKey: String, channelId: String)

    fun openActivityDurationTrack(isOpen: Boolean)

    fun onResume(context: Context)

    fun onPause(context: Context)

    fun onPageStart(tag: String)

    fun onPageEnd(tag: String)


    fun onKillProcess(context: Context)

    fun onEvent(context: Context, eventId: String)

    fun onEvent(context: Context, eventId: String, map: HashMap<String, String>)

    fun onEventValue(context: Context, eventId: String, map: HashMap<String, String>, du: Int)



}