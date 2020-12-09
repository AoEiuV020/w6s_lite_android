package com.foreveross.atwork.modules.discussion.manager.extension

import android.content.Context
import com.foreverht.db.service.repository.DiscussionRepository
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService
import com.foreveross.atwork.api.sdk.discussion.DiscussionSyncNetService
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionSettingsResponse
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryDiscussionResponseJson
import com.foreveross.atwork.api.sdk.net.HttpResultException
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.discussion.Discussion
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.extension.coroutineScope
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

fun DiscussionManager.queryDiscussion(context: Context, discussionId: String, takeMemberDetailInfo: Boolean = false, forcedNeedEmpInfo: Boolean = false) = callbackFlow<Discussion> {
    this@queryDiscussion.queryDiscussion(context, discussionId, takeMemberDetailInfo, forcedNeedEmpInfo, object : DiscussionAsyncNetService.OnQueryDiscussionListener {
        override fun onSuccess(discussion: Discussion) {
            offer(discussion)
            close()
        }

        override fun networkFail(errorCode: Int, errorMsg: String?) {
            close(HttpResultException(errorCode, errorMsg))
        }

    })

    awaitClose()
}


fun DiscussionManager.createDiscussion(context: Context, contactList: List<ShowListItem>, name: String?, avatar: String?, orgCode: String? = null, templateId: String?, needEvent: Boolean= true) = callbackFlow<Discussion> {
    this@createDiscussion.createDiscussion(context, contactList, name, avatar, orgCode, templateId, needEvent, object : DiscussionAsyncNetService.OnCreateDiscussionListener {

        override fun onDiscussionSuccess(discussion: Discussion) {
            offer(discussion)
            close()

        }

        override fun networkFail(errorCode: Int, errorMsg: String?) {
            close(HttpResultException(errorCode, errorMsg))
        }


    })
    awaitClose()

}


fun DiscussionManager.queryDiscussionBasicInfoRemote(context: Context, discussionId: String, onGetResult: (result: Any) -> Unit) {

    queryDiscussionBasicInfoRemote(context, discussionId)
            .flowOn(Dispatchers.IO)
            .onEach {
                onGetResult(it)
            }
            .catch { ErrorHandleUtil.handleTokenError(it) }
            .launchIn(context.coroutineScope)
}

fun DiscussionManager.queryDiscussionBasicInfoRemote(context: Context, discussionId: String): Flow<Any> = flow {
    val httpResult = DiscussionSyncNetService.getInstance().queryDiscussionBasicInfo(context, discussionId)
    if(httpResult.isRequestSuccess) {
        val discussionResult = httpResult.resultResponse as QueryDiscussionResponseJson
        //just discussion info, no discussion member list
        discussionResult.discussion.mMemberList?.clear()
        val discussion = discussionResult.discussion

        DiscussionRepository.getInstance().insertDiscussionBasicInfo(discussion)
        DiscussionManager.getInstance().addDiscussion(discussion)

        emit(discussion)

        val resultText = NetWorkHttpResultHelper.getResultText(httpResult.result)
        val settingsInfo = JsonUtil.fromJson(resultText, DiscussionSettingsResponse::class.java)
        emit(settingsInfo)

        return@flow
    }

    throw NetworkHttpResultErrorHandler.toException(httpResult)
}




