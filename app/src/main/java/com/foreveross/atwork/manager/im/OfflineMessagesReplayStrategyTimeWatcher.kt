package com.foreveross.atwork.manager.im

import com.foreverht.db.service.repository.MessageRecordRepository
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.message.MessageSyncNetService
import com.foreveross.atwork.api.sdk.message.model.QueryChecksumResponse
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage
import com.foreveross.atwork.infrastructure.newmessage.record.MessageRecord
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.Logger
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils
import com.foreveross.atwork.services.ImSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object OfflineMessagesReplayStrategyTimeWatcher {

    val TAG = "OfflineMessagesReplayStrategyTimeWatcher"

    private var messageRecords: MutableList<MessageRecord>? = null

    private const val LIMIT_ONE_BATCH = 100


    fun updateMessageRecordSync(message: PostTypeMessage): Boolean {
        if (!MessageRecord.isLegal(message)) {
            return false
        }
        val messageRecord = MessageRecord.transfer(message)

        if (null == messageRecords) {
            messageRecords = MessageRecordRepository.queryMessageRecords()
        }

        messageRecords?.add(messageRecord)

        return MessageRecordRepository.updateMessageRecord(messageRecord)
    }

    fun clear() {
        messageRecords?.clear()
        messageRecords = null
    }

    fun setLatestMessageTime(message: PostTypeMessage) {
        val isOfflinePulling = LoginUserInfo.getInstance().isOfflinePulling
        val isOfflinePullingError = LoginUserInfo.getInstance().isOfflinePullingError

        if(isOfflinePulling || isOfflinePullingError) {
            return
        }


        if (LIMIT_ONE_BATCH <= messageRecords?.size ?: 0) {
            messageRecords?.clear()

            GlobalScope.launch(Dispatchers.IO) {
                synchronized(OfflineMessagesReplayStrategyTimeWatcher::class.java) {
                    val brakeTime = MessageRecordRepository.queryLatestMessageRecord()?.msgTime?: return@launch
                    trySetLatestMessageTimeSync(brakeTime)

                }

            }
        }
    }

    private fun trySetLatestMessageTimeSync(brakeTime: Long) {
        val messageRecordsInDb = MessageRecordRepository.queryMessageRecords(LIMIT_ONE_BATCH)

        if(messageRecordsInDb.size  <=  2) {
            return
        }

        Logger.e(TAG, "messageRecordsInDb -> ${messageRecordsInDb}")

        val messageRecordMax = messageRecordsInDb.maxBy { it.msgTime } ?: return

        val httpResult = MessageSyncNetService.queryChecksum(
                AtworkApplicationLike.baseContext,
                PersonalShareInfo.getInstance().getLatestMessageTime(AtworkApplicationLike.baseContext),
                messageRecordMax.msgTime, LIMIT_ONE_BATCH)

        messageRecords?.clear()

        if (httpResult.isNetSuccess) {
            val queryChecksumResponse = httpResult.resultResponse as QueryChecksumResponse

            val localChecksumSync = getLocalChecksumSync(messageRecordsInDb)

            Logger.e(TAG, "localChecksumSync : ${localChecksumSync}    queryChecksumResponse.result.checksum : ${queryChecksumResponse.result.checksum} ")

            if (localChecksumSync == queryChecksumResponse.result.checksum) {
                Logger.e(TAG, "checksum 对比成功")


                //对比成功
                PersonalShareInfo.getInstance().setLatestMessageTime(AtworkApplicationLike.baseContext, messageRecordMax.msgTime, messageRecordMax.msgId)
                MessageRecordRepository.removeMessageRecords(messageRecordMax.msgTime)

                //消息记录数据库还有数据, 继续取出来, 再次checksum对比
                if(messageRecordMax.msgTime < brakeTime) {
                    trySetLatestMessageTimeSync(brakeTime)
                }

            } else {
                Logger.e(TAG, "checksum 对比失败, 断开IM 重连")

                //对比失败, 也即丢消息了, 此时断开IM, 重拉消息
                ImSocketService.closeConnection()

            }

        }


    }



    fun removeMessageRecordsAndSaveLast(lastMessage: PostTypeMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            MessageRecordRepository.removeMessageRecords(lastMessage.deliveryTime)
            updateMessageRecordSync(lastMessage)
        }
    }

    fun removeMessageRecordsAll(onFinish: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            MessageRecordRepository.removeMessageRecordsAll()
            clear()

            withContext(Dispatchers.Main) {
                onFinish()
            }
        }
    }

    private fun getLocalChecksumSync(messageRecords: List<MessageRecord>): String {

        return messageRecords
                .map { it.msgId }
                .sorted()
                .reduce { acc, str -> acc + str }
                .let { MD5Utils.md5ToBase64(it) }
    }


}