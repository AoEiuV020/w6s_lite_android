package com.foreveross.atwork.modules.main.helper.extension

import android.app.Activity
import android.content.Context
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService.OnQueryUserListener
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.foreveross.atwork.utils.ErrorHandleUtil
import java.util.*

fun handleNewDiscussionChat(context: Activity) {

    handleNewDiscussionChatCommon(context)


}

private fun handleNewDiscussionChatCommon(context: Activity) {
    SelectedContactList.clear()

    AtworkApplicationLike.getLoginUser(object : OnQueryUserListener {
        override fun onSuccess(loginUser: User) {
            val notAllowContactList: MutableList<ShowListItem> = ArrayList()
            notAllowContactList.add(loginUser)
            val userSelectControlAction = UserSelectControlAction()
            userSelectControlAction.selectMode = UserSelectActivity.SelectMode.SELECT
            userSelectControlAction.selectAction = UserSelectActivity.SelectAction.DISCUSSION
            userSelectControlAction.setSelectedContacts(notAllowContactList)
            userSelectControlAction.fromTag = MainActivity.TAG
            val intent = UserSelectActivity.getIntent(context, userSelectControlAction)
            context.startActivityForResult(intent, MainActivity.CREATE_DICUSSION_CHAT)
            //界面切换效果
            context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
        }

        override fun networkFail(errorCode: Int, errorMsg: String) {
            ErrorHandleUtil.handleBaseError(errorCode, errorMsg)
        }
    })
}