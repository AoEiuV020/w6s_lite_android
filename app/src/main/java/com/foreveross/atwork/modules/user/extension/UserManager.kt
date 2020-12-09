package com.foreveross.atwork.modules.user.extension

import android.content.Context
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.net.HttpResultException
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.manager.UserManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun UserManager.getLoginUser() = callbackFlow<User> {

    AtworkApplicationLike.getLoginUser(object : UserAsyncNetService.OnQueryUserListener {
        override fun onSuccess(user: User) {
            offer(user)
            close()

        }

        override fun networkFail(errorCode: Int, errorMsg: String?) {
            close(HttpResultException(errorCode, errorMsg))
        }

    });

    awaitClose()
}


fun UserManager.queryUserByUserId(context: Context, userId: String, domainId: String) = callbackFlow<User> {

    UserManager.getInstance().queryUserByUserId(context, userId, domainId, object : UserAsyncNetService.OnQueryUserListener {
        override fun onSuccess(user: User) {
            offer(user)
            close()

        }

        override fun networkFail(errorCode: Int, errorMsg: String?) {
            close(HttpResultException(errorCode, errorMsg))
        }

    })

    awaitClose()

}