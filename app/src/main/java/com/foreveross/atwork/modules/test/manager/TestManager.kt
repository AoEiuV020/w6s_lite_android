package com.foreveross.atwork.modules.test.manager

import com.foreverht.db.service.repository.MessageRepository
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.utils.CloneUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import java.util.*

object TestManager {

    fun cloneMessageBatch(messageList: List<ChatPostMessage>, finishDb: () -> Unit) {
        DbThreadPoolExecutor.getInstance().execute {
            val fakeMsgList = ArrayList<ChatPostMessage>()


            val beginTime = System.currentTimeMillis()
            var loopTimes = 1
            while (10000 > fakeMsgList.size) {


                for (message in messageList) {
                    val fakeMessage = CloneUtil.cloneTo(message)
                    fakeMessage.deliveryId = UUID.randomUUID().toString()
                    fakeMessage.deliveryTime -= loopTimes

                    fakeMsgList.add(fakeMessage)
                }

                loopTimes++

            }

            LogUtil.e("cloneMessageBatch clone time ->>>>> ${System.currentTimeMillis() - beginTime}" )

            MessageRepository.getInstance().batchInsertMessages(fakeMsgList)
            AtworkApplicationLike.runOnMainThread {
                finishDb()
            }

        }

    }


    fun queryCount(chatId: String, getCount: (Int) -> Unit) {
        DbThreadPoolExecutor.getInstance().execute {
            val count = MessageRepository.getInstance().queryMessageCount(chatId)

            AtworkApplicationLike.runOnMainThread {
                getCount(count)
            }
        }
    }
}