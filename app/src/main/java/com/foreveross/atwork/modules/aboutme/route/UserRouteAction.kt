package com.foreveross.atwork.modules.aboutme.route

import android.content.Context
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.modules.aboutme.activity.PersonalQrcodeActivity
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams
import com.foreveross.atwork.utils.ErrorHandleUtil


class UserRouteAction(routeParams: RouteParams? = null): RouteAction(routeParams) {

    override fun action(context: Context) {

        val path = routeParams?.getUri()?.path

        if("/businessCard/qrcode".equals(path, ignoreCase = true) ) {
            AtworkApplicationLike.getLoginUser(object: UserAsyncNetService.OnQueryUserListener {
                override fun onSuccess(user: User) {
                    val intent = PersonalQrcodeActivity.getIntent(context, user)
                    context.startActivity(intent)
                }

                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

            })

        }

    }
}