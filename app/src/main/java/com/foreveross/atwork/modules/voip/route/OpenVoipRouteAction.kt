package com.foreveross.atwork.modules.voip.route

import android.app.Activity
import android.content.Context
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams
import com.foreveross.atwork.modules.voip.activity.VoipHistoryActivity
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.atwork.utils.ErrorHandleUtil
import java.util.*

class OpenVoipRouteAction (routeParams: RouteParams? = null): RouteAction(routeParams) {

    override fun action(context: Context) {

        context.asType<Activity>()
                ?.let { handleMorePopClickStartVoip(it) }
    }



    private fun handleMorePopClickStartVoip(activity: Activity) {
        //TODO:保留逻辑。。。原本想先处理一段sql语句处理
        val hasVoipHistory = true

        if (hasVoipHistory) {
            val intent = VoipHistoryActivity.getIntent(activity)
            activity.startActivity(intent)
            return
        }

        if (AtworkUtil.isSystemCalling()) {
            AtworkToast.showResToast(R.string.alert_is_handling_system_call)
            return
        }

        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
            return
        }

        val notAllowContactList = ArrayList<ShowListItem>()
        AtworkApplicationLike.getLoginUser(object : UserAsyncNetService.OnQueryUserListener {
            override fun onSuccess(user: User) {
                notAllowContactList.add(user)

                val userSelectControlAction = UserSelectControlAction()
                userSelectControlAction.selectMode = UserSelectActivity.SelectMode.SELECT
                userSelectControlAction.selectAction = UserSelectActivity.SelectAction.VOIP
                userSelectControlAction.setSelectedContacts(notAllowContactList)
                userSelectControlAction.fromTag = MainActivity.TAG

                val intent = UserSelectActivity.getIntent(activity, userSelectControlAction)
                activity.startActivityForResult(intent, MainActivity.CREATE_VOIP_MEETING)
            }

            override fun networkFail(errorCode: Int, errorMsg: String) {
                ErrorHandleUtil.handleBaseError(errorCode, errorMsg)
            }
        })

    }
}