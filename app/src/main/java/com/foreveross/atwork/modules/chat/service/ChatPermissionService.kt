package com.foreveross.atwork.modules.chat.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.foreverht.db.service.repository.SessionRepository
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.Employee.EmployeeSyncNetService
import com.foreveross.atwork.api.sdk.Employee.responseModel.QueryOrgAndEmpListResponse
import com.foreveross.atwork.api.sdk.users.UserSyncNetService
import com.foreveross.atwork.api.sdk.users.responseJson.CheckSpecialViewResponse
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.domain.ChatConnectionMode
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.manager.EmployeeManager
import com.foreveross.atwork.manager.UserManager
import com.foreveross.atwork.manager.model.CheckTalkAuthResult
import com.foreveross.atwork.utils.AtworkToast


object ChatPermissionService {


    @SuppressLint("StaticFieldLeak")
    fun canChat(empTargetList: List<Employee>, userId: String, domainId: String, action: (result: CheckTalkAuthResult) -> Unit) {

        object : AsyncTask<Void, Void, CheckTalkAuthResult>() {

            override fun doInBackground(vararg params: Void?): CheckTalkAuthResult {
                return canChatSync(empTargetList, userId, domainId)

            }


            override fun onPostExecute(result: CheckTalkAuthResult) {
                action.invoke(result)
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }


    @SuppressLint("StaticFieldLeak")
    fun canChatCommonAction(empTargetList: List<Employee>, userId: String, domainId: String, action: (result: CheckTalkAuthResult) -> Unit) {

        object : AsyncTask<Void, Void, CheckTalkAuthResult>() {

            override fun doInBackground(vararg params: Void?): CheckTalkAuthResult {
                return canChatSync(empTargetList, userId, domainId)


            }


            override fun onPostExecute(result: CheckTalkAuthResult) {
                when(result) {
                    CheckTalkAuthResult.CANNOT_TALK, CheckTalkAuthResult.MAY_NOT_TALK -> AtworkToast.showToast(DomainSettingsManager.getInstance().connectionNonsupportPrompt)
                    CheckTalkAuthResult.NETWORK_FAILED -> AtworkToast.showResToast(R.string.network_not_avaluable)
                }

                action.invoke(result)
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    private fun canChatSync(empTargetList: List<Employee>, userId: String, domainId: String): CheckTalkAuthResult {
        val context = BaseApplicationLike.baseContext
        if (UserManager.getInstance().isYourFriendSync(userId)) {
            return CheckTalkAuthResult.CAN_TALK
        }

        val exist = SessionRepository.getInstance().isSessionExist(userId)
        //本地存在聊天记录
        if (exist) {
            return CheckTalkAuthResult.CAN_TALK
        }

        //消息权限设置开关判定
        val canChatByMessagePermissionSettingsResult = canChatByMessagePermissionSettingsSync(empTargetList)

        if (canChatByMessagePermissionSettingsResult.isSureState) {
            return canChatByMessagePermissionSettingsResult
        }

        //服务器消息记录存在判断
        if (-1 != DomainSettingsManager.getInstance().connectionRetainDays) {
            val isSessionEmptyRemoteResult = isSessionEmptyRemoteSync(context, userId, domainId)

            if (isSessionEmptyRemoteResult.isSureState) {
                return isSessionEmptyRemoteResult
            }
        }

        //高管与聊天视图权限判断
        val canChatByRankAndEmpSeniorPermissionResult = canChatByRankAndEmpSeniorPermission(context, userId, empTargetList)

        if (canChatByRankAndEmpSeniorPermissionResult.isSureState) {
            return canChatByRankAndEmpSeniorPermissionResult
        }

        //特殊视图判断
        val doCanChatBySpecialViewResult = doCanChatBySpecialView(context, userId)

        return doCanChatBySpecialViewResult
    }

    private fun canChatByMessagePermissionSettingsSync(empTargetList: List<Employee>): CheckTalkAuthResult {

        if (ChatConnectionMode.FRIEND_ONLY == DomainSettingsManager.getInstance().chatCheckPermission) {
            return CheckTalkAuthResult.MAY_NOT_TALK
        }

        val canSeeOrgEmpResult = !ListUtil.isEmpty(empTargetList)



        if (ChatConnectionMode.UN_LIMIT == DomainSettingsManager.getInstance().chatCheckPermission) {
            if (!canSeeOrgEmpResult) {
                return CheckTalkAuthResult.CAN_TALK
            }
        }


        if (ChatConnectionMode.RELATION_EXISTENCE == DomainSettingsManager.getInstance().chatCheckPermission) {
            if (!canSeeOrgEmpResult) {
                return CheckTalkAuthResult.MAY_NOT_TALK
            }
        }

        return CheckTalkAuthResult.MAY_TALK
    }


    private fun canChatByRankAndEmpSeniorPermission(context: Context, userId: String, targetEmpList: List<Employee>): CheckTalkAuthResult {


        var hasMayTalkByEmpSeniorPermissionResult = false
        //判断高管是否符合
        targetEmpList.forEach {
            val canChatByEmpSeniorPermissionResult = EmployeeManager.getInstance().canChatByEmpSeniorPermissionSync(context, it, true)
            if (canChatByEmpSeniorPermissionResult.isSureState) {
                return canChatByEmpSeniorPermissionResult
            }


            if(CheckTalkAuthResult.MAY_TALK == canChatByEmpSeniorPermissionResult) {
                hasMayTalkByEmpSeniorPermissionResult = true
            }


        }

        //再判断职级聊天视图是否满足
        val canChatByRankRemoteResult = doCanChatByRankRemoteSync(userId)

        if (hasMayTalkByEmpSeniorPermissionResult && CheckTalkAuthResult.MAY_TALK == canChatByRankRemoteResult) {
            return CheckTalkAuthResult.CAN_TALK
        }


        return CheckTalkAuthResult.MAY_NOT_TALK

    }

    private fun doCanChatByRankRemoteSync(userId: String): CheckTalkAuthResult {
        val httpResult = EmployeeSyncNetService.getInstance().queryOrgAndEmpList(BaseApplicationLike.baseContext, userId, true, false)
        if (!httpResult.isRequestSuccess) {
            return CheckTalkAuthResult.NETWORK_FAILED
        }

        val response: QueryOrgAndEmpListResponse = httpResult.resultResponse as QueryOrgAndEmpListResponse
        if (ListUtil.isEmpty(response.mResultList)) {
            return CheckTalkAuthResult.MAY_NOT_TALK
        }

        return CheckTalkAuthResult.MAY_TALK
    }


    private fun canSeeOrgEmpByRemoteSync(userId: String): Boolean? {
        val httpResult = EmployeeSyncNetService.getInstance().queryOrgAndEmpList(BaseApplicationLike.baseContext, userId, true, true)
        if (!httpResult.isRequestSuccess) {
            return null
        }

        val response: QueryOrgAndEmpListResponse = httpResult.resultResponse as QueryOrgAndEmpListResponse
        if (ListUtil.isEmpty(response.mResultList)) {
            return false
        }

        return true

    }


    private fun isSessionEmptyRemoteSync(context: Context, userId: String, domainId: String): CheckTalkAuthResult {
        //服务器查询是否存在聊天记录
        val remoteSessionEmptyResult = ChatService.isSessionEmptyRemote(context, userId, domainId)
        return if (null != remoteSessionEmptyResult) {
            if (remoteSessionEmptyResult) {
                CheckTalkAuthResult.MAY_NOT_TALK

            } else {
                CheckTalkAuthResult.CAN_TALK

            }

        } else CheckTalkAuthResult.NETWORK_FAILED

    }


    private fun doCanChatBySpecialView(context: Context, userId: String): CheckTalkAuthResult {
        val httpResult = UserSyncNetService.getInstance().getSpecialViewCheck(context, userId)
        if (httpResult.isRequestSuccess) {
            val checkSpecialViewResponse = httpResult.resultResponse as CheckSpecialViewResponse
            return if (checkSpecialViewResponse.result) {
                CheckTalkAuthResult.CAN_TALK

            } else {
                CheckTalkAuthResult.MAY_NOT_TALK
            }
        }

        return CheckTalkAuthResult.NETWORK_FAILED
    }


}