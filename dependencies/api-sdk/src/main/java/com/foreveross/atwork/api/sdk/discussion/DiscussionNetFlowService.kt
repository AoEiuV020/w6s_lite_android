@file:JvmName("DiscussionNetFlowService")

package com.foreveross.atwork.api.sdk.discussion

import android.content.Context
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryReadOrUnreadResponse
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import kotlinx.coroutines.flow.flow


fun queryReadUnread(ctx: Context, messageId: String, readOrUnread: String) = flow {
    val httpResult = DiscussionSyncNetService.getInstance().queryReadUnread(ctx, messageId, readOrUnread)
    if(httpResult.isRequestSuccess) {
        val response = httpResult.resultResponse as QueryReadOrUnreadResponse
        emit(response.resultList)
        return@flow
    }

    throw NetworkHttpResultErrorHandler.toException(httpResult)
}