package com.foreveross.atwork.infrastructure.shared

import android.content.Context
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils
import com.google.gson.annotations.SerializedName

object ReadAckPersonShareInfo: PersonalShareInfo() {

    private const val READ_ACK_SESSION_KEY = "READ_ACK_SESSION_KEY_"

    fun getReadAckSessionKey(sessionId: String) = READ_ACK_SESSION_KEY + sessionId

    fun getReadAckInfo(context: Context, sessionId: String): ReadAckInfo? {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        val sp = getPersonalSpName(username)
        val key = getReadAckSessionKey(sessionId)

        return PreferencesUtils.getObject(context, sp, key, ReadAckInfo::class.java)
    }

    fun updateSelfReadTime(context: Context, sessionId: String, selfReadTime: Long) {
        var readAckInfo = getReadAckInfo(context, sessionId)
        if(null == readAckInfo) {
            readAckInfo = ReadAckInfo()
        }

        if (selfReadTime > readAckInfo.selfReadTime) {
            readAckInfo.selfReadTime = selfReadTime
        }
        updateReadAckInfo(context, sessionId, readAckInfo)
    }

    fun updateTargetReadTime(context: Context, sessionId: String, targetReadTime: Long) {
        var readAckInfo = getReadAckInfo(context, sessionId)
        if(null == readAckInfo) {
            readAckInfo = ReadAckInfo()
        }

        if (targetReadTime > readAckInfo.targetReadTime) {
            readAckInfo.targetReadTime = targetReadTime
        }
        updateReadAckInfo(context, sessionId, readAckInfo)
    }

    fun updateReadAckInfo(context: Context, sessionId: String, readAckInfo: ReadAckInfo) {
        val username = LoginUserInfo.getInstance().getLoginUserUserName(context)
        val sp = getPersonalSpName(username)
        val key = getReadAckSessionKey(sessionId)

        PreferencesUtils.updateObject(context, sp, key, readAckInfo)

        LogUtil.e("updateReadAckInfo -> sessionId : ${sessionId}   readAckInfo: ${readAckInfo}")
    }

    class ReadAckInfo (
        @SerializedName("target_read_time")
        var targetReadTime: Long = -1L,

        @SerializedName("target_read_time_old_data_line")
        var targetReadTimeOldDataLine: Long = -1L,


        @SerializedName("self_read_time")
        var selfReadTime: Long = -1L


    ) {
        override fun toString(): String {
            return "ReadAckInfo(targetReadTime=$targetReadTime, selfReadTime=$selfReadTime)"
        }
    }

}