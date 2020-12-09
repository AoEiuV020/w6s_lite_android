package com.foreveross.atwork.infrastructure.shared.dev

import android.content.Context
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import java.text.NumberFormat

object LogCommonShareInfo: CommonShareInfo() {

    private const val KEY_SEND_MSG_LOG = "KEY_SEND_MSG_LOG"

    private fun getSentMsgLogKey(socketUrl: String) = KEY_SEND_MSG_LOG + "_" + socketUrl

    fun getSentMsgLog(context: Context, socketUrl: String): SendMsgLogData {
        val sendMsgLogStr = PreferencesUtils.getString(context, getSpName(), getSentMsgLogKey(socketUrl), StringUtils.EMPTY)

        return getSentMsgLog(sendMsgLogStr, socketUrl)
    }

    private fun getSentMsgLog(sendMsgLogStr: String?, socketUrl: String): SendMsgLogData {
        if (StringUtils.isEmpty(sendMsgLogStr)) {
            return SendMsgLogData(socketUrl = socketUrl, logs = HashSet())
        }

        return JsonUtil.fromJson(sendMsgLogStr, SendMsgLogData::class.java)
                ?: return SendMsgLogData(socketUrl = socketUrl, logs = HashSet())
    }

    fun updateSentMsgLog(context: Context, sendMsgLog: SendMsgLogData) {
        PreferencesUtils.putString(context, getSpName(), getSentMsgLogKey(sendMsgLog.socketUrl), JsonUtil.toJson(sendMsgLog))
    }

    fun getAllSentMsgLogs(context: Context): List<SendMsgLogDisplay> {
        val allLogsMap = PreferencesUtils.getAll(context, getSpName())
        val sendMsgLogList = ArrayList<SendMsgLogDisplay>()

        for(logStr in allLogsMap.values) {

            if(logStr is String) {
                val sendMsgLog = JsonUtil.fromJson(logStr, SendMsgLogData::class.java)

                sendMsgLog?.let {
                    sendMsgLogList.add(SendMsgLogDisplay.transfer(it))

                }
            }

        }

        return sendMsgLogList
    }

    private fun getSpName(): String = SP_COMMON + "_" + "log"
}

data class SendMsgLogDisplay (
        @SerializedName("socket_url")
        var socketUrl: String,

        @SerializedName("logs")
        var logs: List<SendMsgItemLogDisplay>?
) {


    fun exportPercents() {

        val totalCount = logs?.sumByDouble { it.sentMsgCount.toDouble() } ?: 0.0
        logs?.forEach {
            //保留 2 位
            val nf = NumberFormat.getNumberInstance()
            nf.maximumFractionDigits = 2

            it.percent = "${nf.format((it.sentMsgCount / totalCount) * 100) }%"
        }

    }

    companion object {
        fun transfer(sendMsgLogData: SendMsgLogData): SendMsgLogDisplay {
            val logsDisplay: ArrayList<SendMsgItemLogDisplay> = ArrayList()

            val logList = sendMsgLogData.logs?.let {
                ArrayList(it)
            }

            logList?.sortBy { it.speed.sortValue() }

            logList?.forEach {
                logsDisplay.add(SendMsgItemLogDisplay.transfer(it))
            }

            val sendMsgLogDisplay = SendMsgLogDisplay(sendMsgLogData.socketUrl, logsDisplay)
            sendMsgLogDisplay.exportPercents()

            return sendMsgLogDisplay
        }
    }

}

data class SendMsgItemLogDisplay(

        @SerializedName("speed_label")
        var speedLabel: String,

        @SerializedName("sent_msg_count")
        var sentMsgCount: Long,

        @SerializedName("average_duration")
        var averageDuration: String




) {

    @SerializedName("percent")
    var percent: String = "0%"


    companion object {

        fun transfer(sendMsgItemLogData: SendMsgItemLogData): SendMsgItemLogDisplay {
            val nf = NumberFormat.getNumberInstance()
            nf.maximumFractionDigits = 2

            return SendMsgItemLogDisplay(speedLabel = sendMsgItemLogData.speed.label(), sentMsgCount =  sendMsgItemLogData.sentMsgCount, averageDuration = "${nf.format(sendMsgItemLogData.averageDuration)}ms")
        }
    }
}


data class SendMsgLogData (

    @SerializedName("socket_url")
    var socketUrl: String,

    @SerializedName("logs")
    var logs: HashSet<SendMsgItemLogData>?



) {

    override fun toString(): String {
        return "SendMsgLog(socketUrl='$socketUrl', logs=$logs)"
    }

}





data class SendMsgItemLogData(

        @SerializedName("speed")
        var speed: SendMsgSpeed,

        @SerializedName("sent_msg_count")
        var sentMsgCount: Long,

        @SerializedName("average_duration")
        var averageDuration: Double


) {



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SendMsgItemLogData

        if (speed != other.speed) return false

        return true
    }

    override fun hashCode(): Int {
        return speed.hashCode()
    }

}

enum class SendMsgSpeed {

    /**
     * 0~50ms
     * */
    LEVEL0 {
        override fun sortValue(): Int  = 0

        override fun label(): String = "0~50ms"
    },


    /**
     * 50~100ms
     * */
    LEVEL1 {

        override fun sortValue(): Int  = 1


        override fun label(): String = "50~100ms"

    },


    /**
     * 100~200ms
     * */
    LEVEL2{

        override fun sortValue(): Int  = 2


        override fun label(): String = "100~200ms"
    },


    /**
     * 200~300ms
     * */
    LEVEL3{

        override fun sortValue(): Int  = 3


        override fun label(): String = "200~300ms"
    },

    /**
     * 300~500ms
     * */
    LEVEL4{

        override fun sortValue(): Int  = 4


        override fun label(): String = "300~500ms"
    },

    /**
     * 500~1000ms
     * */
    LEVEL5 {

        override fun sortValue(): Int  = 5


        override fun label(): String = "500~1000ms"
    },


    /**
     * 1000~3000ms
     * */
    LEVEL6{

        override fun sortValue(): Int  = 6


        override fun label(): String = "1000~3000ms"
    },


    /**
     * 3000~5000ms
     * */
    LEVEL7{

        override fun sortValue(): Int  = 7


        override fun label(): String = "3000~5000ms"
    },

    /**
     * 5000ms+
     * */
    LEVEL8 {

        override fun sortValue(): Int  = 8


        override fun label(): String = "5000ms+"

    };

    abstract fun label(): String

    abstract fun sortValue(): Int

    companion object {

        @JvmStatic
        fun matchSpeed(speedVal: Long): SendMsgSpeed =
                when (speedVal) {
                    in 0 until 50 -> LEVEL0
                    in 50 until 100 -> LEVEL1
                    in 100 until 200 -> LEVEL2
                    in 200 until 300 -> LEVEL3
                    in 300 until 500 -> LEVEL4
                    in 500 until 1000 -> LEVEL5
                    in 1000 until 3000 -> LEVEL6
                    in 3000 until 5000 -> LEVEL7
                    else -> LEVEL8
                }

    }



}