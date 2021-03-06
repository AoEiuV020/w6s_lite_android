package com.foreveross.atwork.manager.im

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.foreverht.cache.MessageCache
import com.foreverht.db.service.repository.SessionFaultageRecordRepository
import com.foreverht.threadGear.OfflineMessagesSessionStrategyThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService
import com.foreveross.atwork.api.sdk.message.MessageSyncNetService
import com.foreveross.atwork.api.sdk.message.model.MessagesResult
import com.foreveross.atwork.api.sdk.message.model.QueryMessagesOnSessionRequest
import com.foreveross.atwork.api.sdk.message.model.QuerySessionListRequest
import com.foreveross.atwork.api.sdk.message.model.QuerySessionListResponse
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionFaultage
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.ReceivingTitleQueueManager
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.gather.manager.GaherMessageManager
import com.foreveross.atwork.modules.voip.service.VoipEventService
import com.foreveross.atwork.services.receivers.AtworkReceiveListener
import com.foreveross.atwork.utils.ChatMessageHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object OfflineMessageSessionStrategyManager: OfflineMessageManager() {

    const val TAG = "OFFLINE_SESSION_STRATEGY"

    val taskQueue = HashMap<String, ArrayList<AsyncTask<out Any, out Any, out Any>>>()

    var currentTaskKey: String = StringUtils.EMPTY

    fun cancelAll() {
        taskQueue.flatMap { it.value }.forEach { it.cancel(true) }

    }

    /**
     * ????????????????????????
     */
    @SuppressLint("StaticFieldLeak")
    fun querySessionList(atworkReceiveListener: AtworkReceiveListener) {

        LogUtil.e(TAG, "???????????????????????????")

        cancelAll()

        val taskKey = UUID.randomUUID().toString()
        currentTaskKey = taskKey

        val context = AtworkApplicationLike.baseContext
        ReceivingTitleQueueManager.getInstance().push(context, ReceivingTitleQueueManager.TAG_GET_OFFLINE)


        val task = object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg voids: Void): HttpResult {

                val flowBeginTime = System.currentTimeMillis()


                val timestamp = PersonalShareInfo.getInstance().getLatestMessageTime(AtworkApplicationLike.baseContext)
//                val timestamp = -1L
                val httpResult = MessageSyncNetService.querySessionList(QuerySessionListRequest(timestamp, 100))
                if(httpResult.isRequestSuccess) {
                    val response = httpResult.resultResponse as QuerySessionListResponse

                    //?????????????????????
                    val sessionFaultages = ArrayList<SessionFaultage>()

                    ChatSessionDataWrap.getInstance().checkSessionListUpdate(false)
                    //????????? session ??????
                    val lastSessionList = ArrayList<Session>(ChatSessionDataWrap.getInstance().sessions)
                    val lastSessionInfoMap = HashMap<String, Long>()
                    lastSessionList.forEach {
                        lastSessionInfoMap[it.identifier] = it.lastTimestamp
                    }

                    val sessionLastMessagesResult = MessagesResult()
                    sessionLastMessagesResult.mSuccess = true
                    sessionLastMessagesResult.mPostTypeMessages = response.result?.map { it.lastMessage }

                    OfflineMessageReplayStrategyManager.getInstance().replayOfflineMessage(context, sessionLastMessagesResult, atworkReceiveListener)
                    val newSessionList = ArrayList<Session>(ChatSessionDataWrap.getInstance().sessions)
//
                    response.result?.forEach { querySessionItemData ->

                        val queryItemDataSessionId = ChatMessageHelper.getChatUserSessionId(querySessionItemData.lastMessage)
                        val newLastMessageSession = newSessionList.find { it.identifier == queryItemDataSessionId }


                        //??????????????????????????????session, ????????????????????????, ??????, ????????????????????????????????????"??????"
                        if(null != newLastMessageSession && lastSessionInfoMap.containsKey(queryItemDataSessionId)) {
                            val sessionFaultage = SessionFaultage(sessionId = queryItemDataSessionId, sessionLastMessageTime = lastSessionInfoMap[queryItemDataSessionId]!!, beginTimeInSyncMessages = querySessionItemData.lastMessage!!.deliveryTime)
                            sessionFaultages.add(sessionFaultage)
                        }

                    }

                    SessionFaultageRecordRepository.batchInsertNewFaultages(sessionFaultages)

                    val pullMessagesOnSessionResultMap = HashMap<String, Boolean>()

                    //pull messages on per session

                    val pullMessagesOnSessionApiCount = response.result?.count()

                    if(0 == pullMessagesOnSessionApiCount) {
                        LoginUserInfo.getInstance().isOfflinePullingError = false
                        LoginUserInfo.getInstance().setOfflineIsPulling(context, false)

                        finishPullMessagesFlow(taskKey, flowBeginTime)

                        return httpResult
                    }

                    response.result?.forEach { querySessionItemData ->

                        val querySessionItemDataSessionId = ChatMessageHelper.getChatUserSessionId(querySessionItemData.lastMessage)

                        queryMessagesOnSession(context, taskKey, sessionFaultages.find { it.sessionId ==  querySessionItemDataSessionId} , QueryMessagesOnSessionRequest(begin = timestamp, end = querySessionItemData.lastDeliveryTime, limit = 100, remoteConversionId = querySessionItemData.conversationId, sessionId = querySessionItemDataSessionId), atworkReceiveListener, MessageAsyncNetService.GetMessagePerPageListener { messagesResult, begin ->

                            pullMessagesOnSessionResultMap[querySessionItemData.conversationId] = messagesResult.mSuccess


                            val pullMessagesOnSessionFinishedCount = pullMessagesOnSessionResultMap.count { it.value }
                            val pullMessagesOnSessionFailedCount = pullMessagesOnSessionResultMap.count { !it.value }

                            LogUtil.e(TAG, "????????????????????? ??????????????????: $pullMessagesOnSessionFinishedCount  ??????????????????: $pullMessagesOnSessionFailedCount  ??????????????????: $pullMessagesOnSessionApiCount")


                            //??????????????????????????????????????????, ?????????????????????????????????
                            if(pullMessagesOnSessionResultMap.size == pullMessagesOnSessionApiCount) {



                                //??????????????????????????????????????????, ???????????????????????????????????????
                                if(pullMessagesOnSessionFinishedCount == pullMessagesOnSessionApiCount) {


                                    LoginUserInfo.getInstance().isOfflinePullingError = false
                                    LoginUserInfo.getInstance().setOfflineIsPulling(context, false)

                                } else {
                                    LoginUserInfo.getInstance().isOfflinePullingError = true
                                }

                                //?????? voip ??????
                                VoipEventService.getInstance().offlineEventController.replayOfflineEvent(context)

                                //?????? gather ??????
                                GaherMessageManager.replayOfflinePullingData()

                                //????????????????????????????????????
                                response.result
                                        ?.maxBy { it.lastDeliveryTime }
                                        ?.let { PersonalShareInfo.getInstance().setLatestMessageTime(context, it.lastDeliveryTime, null)

                                }

                                finishPullMessagesFlow(taskKey, flowBeginTime)

                            }


                        })
                    }


                    return httpResult

                }


                //????????????????????????
                LoginUserInfo.getInstance().isOfflinePullingError = true
                finishPullMessagesFlow(taskKey, flowBeginTime)

                return httpResult
            }


        }.executeOnExecutor(OfflineMessagesSessionStrategyThreadPool.getInstance())

        addTask(taskKey, task)
    }

    private fun addTask(taskKey: String, task: AsyncTask<Void, Void, out Any>) {
        var taskList = taskQueue[taskKey]
        if (null == taskList) {
            taskList = ArrayList()
        }
        taskList.add(task)
    }

    private fun finishPullMessagesFlow(taskKey: String, flowBeginTime: Long) {
        notifyRefreshSessionInOffline(AtworkApplicationLike.baseContext)

        val flowEndTime = System.currentTimeMillis()

        LogUtil.e(TAG, "???????????????????????????: ${flowEndTime - flowBeginTime}")

        taskQueue[taskKey]?.clear()
    }

    /**
     * ???????????????????????????
     */
    @SuppressLint("StaticFieldLeak")
    fun queryMessagesOnSession(context: Context, taskKey: String, preSessionFaultage: SessionFaultage?, request: QueryMessagesOnSessionRequest, atworkReceiveListener: AtworkReceiveListener, getOfflineMessageListener: MessageAsyncNetService.GetMessagePerPageListener) {

        val asyncTask = object : AsyncTask<Void, Void, MessagesResult>() {

            override fun doInBackground(vararg voids: Void): MessagesResult {
                val httpResult = MessageSyncNetService.queryMessagesOnSession(request)
                val messagesResult = MessageAsyncNetService.produceMessagesResult(context, httpResult)

                if (messagesResult.mSuccess) {//????????????
                    val sessionId = request.sessionId

                    messagesResult.mSessionIdPullingMessages = sessionId
                    OfflineMessageReplayStrategyManager.getInstance().replayOfflineMessage(context, messagesResult, atworkReceiveListener)

                    //????????????????????????,???????????????????????????
                    if (null != preSessionFaultage) {

                        if (request.limit > messagesResult.mRealOfflineMsgSize) {
                            SessionFaultageRecordRepository.deleteFaultage(sessionId)
                        } else {
                            val result = SessionFaultageRecordRepository.deleteFaultage(sessionId, messagesResult.firstMessage?.deliveryTime ?: -1, messagesResult.lastMessage?.deliveryTime ?: -1)

                            //???????????????, ???????????????????????????
                            if (!result) {
                                val newSessionFaultage = SessionFaultage(sessionId = sessionId, sessionLastMessageTime = preSessionFaultage.sessionLastMessageTime, beginTimeInSyncMessages = messagesResult.firstMessage?.deliveryTime ?: -1)
                                SessionFaultageRecordRepository.batchInsertNewFaultages(ListUtil.makeSingleList(newSessionFaultage))
                            }
                        }

                    }
                }

                return messagesResult

            }

            override fun onPostExecute(messagesResult: MessagesResult) {
                getOfflineMessageListener.getMessagePerPage(messagesResult, request.begin)
            }
        }.executeOnExecutor(OfflineMessagesSessionStrategyThreadPool.getInstance())

        addTask(taskKey, asyncTask)

    }


    fun buildSession(chatPostMessage: PostTypeMessage?) : Session? {
        val chatPostMessage = (chatPostMessage?:return null) as? ChatPostMessage ?: return null

        if(!ChatSessionDataWrap.getInstance().isLegalMessageBuildSession(chatPostMessage)) {
            return null
        }

        val chatUser = ChatMessageHelper.getChatUser(chatPostMessage)

        //?????????????????????
        if (MessageCache.getInstance().isMessageShouldNotReceive(chatUser.mUserId, chatPostMessage)) {
            return null
        }

        ChatSessionDataWrap.getInstance().checkSessionListUpdate(false)
        //????????????
        if(Session.ASSET_NOTIFY_SYSTEM == chatPostMessage.from) {

            return ChatSessionDataWrap.getInstance().buildWorkplusAssetNotifySession(chatPostMessage)
        }

        //???????????????
        if (ParticipantType.App == chatPostMessage.mFromType
                || ParticipantType.App == chatPostMessage.mToType) {

            return ChatSessionDataWrap.getInstance().buildAppSession(chatPostMessage)
        }


        //??????
        if (ParticipantType.User == chatPostMessage.mToType && ParticipantType.System != chatPostMessage.mFromType) {
            return ChatSessionDataWrap.getInstance().buildP2pSession(chatPostMessage, false)
        }
        //??????
        if (ParticipantType.Discussion == chatPostMessage.mToType) {
            return ChatSessionDataWrap.getInstance().buildDiscussionSession(chatPostMessage, false)
        }

        //??????
        if (ParticipantType.System == chatPostMessage.mFromType && BodyType.System == chatPostMessage.mBodyType) {
            return ChatSessionDataWrap.getInstance().buildNotifySystemSession(chatPostMessage)

        }

        //????????????????????????(umeeting)
        if (ParticipantType.Meeting == chatPostMessage.mToType && BodyType.MeetingNotice == chatPostMessage.mBodyType) {
            return ChatSessionDataWrap.getInstance().buildMeetingNotifySession(chatPostMessage)
        }


        return null
    }


}