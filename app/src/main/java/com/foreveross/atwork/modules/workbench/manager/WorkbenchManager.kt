package com.foreveross.atwork.modules.workbench.manager

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreverht.db.service.repository.WorkbenchRepository
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.api.sdk.workbench.WorkbenchSyncNetService
import com.foreveross.atwork.api.sdk.workbench.model.response.WorkbenchQueryResponse
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.model.workbench.Workbench
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchAdminCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchContentEventType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchContentSystemEvent
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.model.workbench.factory.WorkbenchFactory
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.manager.OrganizationManager
import com.foreveross.atwork.modules.app.route.routeUrl
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity
import com.foreveross.atwork.modules.voip.activity.VoipHistoryActivity
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.modules.workbench.model.WorkbenchFilterCardsResult
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.atwork.utils.ErrorHandleUtil

object WorkbenchManager : IWorkbenchManager {


    private var lastRequestSuccessfullyTime = -1L

    private var cacheWorkbench: Workbench? = null

    override fun clear() {
        cacheWorkbench = null
        clearRequestRecord()
        WorkbenchCardContentManager.clearRequestRecord()
    }

    override fun clearRequestRecord() {
        lastRequestSuccessfullyTime = -1L
    }

    override fun filterDisplayAndSored(workbench: Workbench): WorkbenchFilterCardsResult {
        val context = AtworkApplicationLike.baseContext

        val cardListFiltered = arrayListOf<WorkbenchCard>()
        val cardIdListCustomSorted: List<String>? = OrgPersonalShareInfo.getInstance().getCustomSortedCardIdList(context, workbench.orgCode, workbench.id)

        val workbenchCards = ArrayList<WorkbenchCard>(workbench.workbenchCards)

//        workbenchCards.sortBy { it.sortOrder }
        workbenchCards.sort()

        cardIdListCustomSorted?.forEach { customSortId ->

            workbenchCards
                    .find { it.id.toString() == customSortId }
                    ?.let { cardListFiltered.add(it) }

        }


        //得到剩余没手动操作过的卡片
        workbenchCards.removeAll(cardListFiltered)

        //把没手动操作过的卡片依次添加进来
        workbenchCards.forEach {



        }

        //把没手动操作过的卡片依次添加进来
        workbenchCards.forEachIndexed { index, workbenchCard ->
            var addIndex = index
            if (addIndex > cardListFiltered.size) {
                addIndex = cardListFiltered.size
            }

            cardListFiltered.add(addIndex, workbenchCard)
        }

        val result = WorkbenchFilterCardsResult()

        result.workbench = workbench
        result.cardsSort = cardListFiltered
        result.classify()


        return result

    }

    override fun handleClickAction(card: WorkbenchCard?, clickAction: () -> Unit) {
        if(null == card) {
            return
        }

        if(!card.adminDisplay) {
            clickAction()
        }
    }

    override fun getAppBundlesShouldDisplay(appContainer: List<AppBundles>): ArrayList<AppBundles> {
        val appBundlesInAppContainer = appContainer

        val appBundlesShouldDisplay = ArrayList<AppBundles>()
        appBundlesInAppContainer.forEach { appBundleInAppContainer ->
            AppManager.getInstance().appBundleList
                    .find { it.id == appBundleInAppContainer.id }
                    ?.let {
                        appBundlesShouldDisplay.add(it)
                    }
        }
        return appBundlesShouldDisplay
    }


    override fun route(context: Context, card: WorkbenchCard?, eventType: WorkbenchContentEventType, eventValue: String) {
        handleClickAction(card) {
            doRoute(context, card, eventType, eventValue)

        }
    }

    private fun doRoute(context: Context, card: WorkbenchCard?, eventType: WorkbenchContentEventType, eventValue: String) {
        when (eventType) {
            WorkbenchContentEventType.URL -> {

                routeUrlEvent(context, eventValue)

                mayTriggerRefresh(eventValue, card)

            }

            WorkbenchContentEventType.SYSTEM -> routeSystemEvent(context, eventValue)
        }
    }

    private fun mayTriggerRefresh(eventValue: String, card: WorkbenchCard?) {
        when {
            eventValue.contains("w6s_workbench_refresh=1") -> {
                WorkbenchCardContentManager.clearRequestRecord()

            }
            eventValue.contains("w6s_workbench_refresh=2") -> {
                card?.let { WorkbenchCardContentManager.removeRequestRecord(card = it) }
            }
        }
    }


    private fun routeUrlEvent(context: Context, eventValue: String) {
        val webViewControlAction = WebViewControlAction.newAction()
                .setUrl(eventValue)

        routeUrl(context, webViewControlAction,true)
    }

    private fun routeSystemEvent(context: Context, eventValue: String) {
        val systemEvent = WorkbenchContentSystemEvent.parse(eventValue)
        when (systemEvent) {
            WorkbenchContentSystemEvent.CREATE_GROUP -> createDiscussion(context)

            WorkbenchContentSystemEvent.QRCODE_SCAN -> qrcodeScan(context)

            WorkbenchContentSystemEvent.VOICE_MEETING -> voipHistory(context)
        }
    }

    private fun voipHistory(context: Context) {
        val intent = VoipHistoryActivity.getIntent(context)
        context.startActivity(intent)
    }

    private fun createDiscussion(context: Context) {
        if(context !is Activity) {
            return
        }

        AtworkApplicationLike.getLoginUser(object : UserAsyncNetService.OnQueryUserListener {
            override fun onSuccess(loginUser: User) {
                val notAllowContactList = java.util.ArrayList<ShowListItem>()
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

    private fun qrcodeScan(context: Context) {
        if(context !is Activity) {
            return
        }



        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
            return
        }


        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(context, arrayOf(Manifest.permission.CAMERA), object : PermissionsResultAction() {
            override fun onGranted() {
                val intent = QrcodeScanActivity.getIntent(context)
                context.startActivity(intent)
            }

            override fun onDenied(permission: String) {
                AtworkUtil.popAuthSettingAlert(context, permission)
            }
        })
    }


    override fun notifyRefresh() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(Intent(IWorkbenchManager.ACTION_REFRESH_WORKBENCH))
    }

    override fun notifySwitchAdminPreviewWorkbench() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(Intent(IWorkbenchManager.ACTION_REFRESH_ADMIN_PREVIEW_WORKBENCH))

    }

    override fun initCurrentOrgWorkbench() {


        if (!LoginUserInfo.getInstance().isLogin(AtworkApplicationLike.baseContext)) {
            return
        }

        HighPriorityCachedTreadPoolExecutor.getInstance().execute {
            val beginTime = System.currentTimeMillis()

            getCurrentOrgWorkbenchSync()

            LogUtil.e("初始化工作台耗时 :  ${System.currentTimeMillis() - beginTime}")
        }


    }

    override fun getCurrentOrgWorkbenchWithoutContent(): Workbench? {
        val context = AtworkApplicationLike.baseContext
        val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)
        val workbenchData: WorkbenchData? = OrgPersonalShareInfo.getInstance().getWorkbenchData(context, currentOrgCode)
        return workbenchData?.let { WorkbenchFactory.produce(it) }
    }

    override fun isCurrentOrgWorkbenchLegal(): Boolean {

        getCurrentOrgWorkbenchWithoutContent()?.let {
            return it.isLegal()
        }

        return false
    }


    @SuppressLint("StaticFieldLeak")
    override fun checkWorkbenchRemote(requestImmediately: Boolean, requestFinish: (() -> Unit)?) {


        if (!checkRequestLegal(requestImmediately)) {
            return
        }

        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val context = AtworkApplicationLike.baseContext
                val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)
                val preWorkbenchData = OrgPersonalShareInfo.getInstance().getWorkbenchDataStr(context, currentOrgCode)

                val httpResult = WorkbenchSyncNetService.queryWorkbench(context, currentOrgCode)

                if (httpResult.isRequestSuccess) {
                    val workbenchQueryResponse = httpResult.resultResponse as WorkbenchQueryResponse
                    if (workbenchQueryResponse.isLegal()) {
                        OrgPersonalShareInfo.getInstance().setWorkbenchData(context, currentOrgCode, workbenchQueryResponse.workbenchQueryResult)


                    }

                    lastRequestSuccessfullyTime = System.currentTimeMillis()

                } else {
                    httpResult.resultResponse?.let {
                        if (ErrorHandleUtil.handleTokenError(it.status, it.message)) {
                            return httpResult

                        }

                        //工作台不存在
                        if (208102 == it.status) {
                            OrgPersonalShareInfo.getInstance().clearWorkBenchData(context, currentOrgCode)

                        }

                        //工作台的子卡片为空
                        if (208104 == it.status) {
                            OrgPersonalShareInfo.getInstance().clearWorkBenchData(context, currentOrgCode)
                        }

                    }

                }

                val newWorkbenchData = OrgPersonalShareInfo.getInstance().getWorkbenchDataStr(context, currentOrgCode)

                if (newWorkbenchData != preWorkbenchData) {
                    cacheWorkbench = null
                }

                notifyRefresh()


                return httpResult

            }

            override fun onPostExecute(httpResult: HttpResult) {
                requestFinish?.invoke()

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

    private fun checkRequestLegal(requestImmediately: Boolean): Boolean {

        if (!isCurrentOrgWorkbenchLegal()) {
            return true
        }

        if (requestImmediately) {
            return true
        }


        if (REQUEST_INTERVAL <= System.currentTimeMillis() - lastRequestSuccessfullyTime) {
            return true
        }

        return false
    }


    @SuppressLint("StaticFieldLeak")
    override fun getCurrentOrgWorkbench(onGetWorkbench: ((Workbench?) -> Unit)) {

        cacheWorkbench?.let {
            val context = AtworkApplicationLike.baseContext
            val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)

            if (currentOrgCode == it.orgCode) {
                onGetWorkbench.invoke(it)

                return
            }
        }



        object : AsyncTask<Void, Void, Workbench?>() {

            override fun doInBackground(vararg params: Void?): Workbench? {

                return getCurrentOrgWorkbenchSync()
            }

            override fun onPostExecute(result: Workbench?) {
                onGetWorkbench.invoke(result)
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }


    @SuppressLint("StaticFieldLeak")
    fun getAdminPreviewModeWorkbench(onGetWorkbench: ((Workbench?) -> Unit)) {
        val cacheWorkbenchData = WorkbenchAdminManager.cacheWorkbenchData?: return
        getOrgWorkbench(cacheWorkbenchData, onGetWorkbench)

    }

    @SuppressLint("StaticFieldLeak")
    override fun getOrgWorkbench(workbenchData: WorkbenchData, onGetWorkbench: ((Workbench?) -> Unit)) {
        object : AsyncTask<Void, Void, Workbench?>() {

            override fun doInBackground(vararg params: Void?): Workbench? {
                return cookSync(WorkbenchFactory.produce(workbenchData), needCache = false)
            }

            override fun onPostExecute(result: Workbench?) {
                onGetWorkbench.invoke(result)
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

    private fun getCurrentOrgWorkbenchSync(): Workbench? {
        val workbench = getCurrentOrgWorkbenchWithoutContent()

        if (null == workbench) {
            return null
        }


//        workbench.workbenchCards.forEach {
//            if(WorkbenchAdminCard.DEFAULT_SORT_ORDER <= it.sortOrder) {
//                it.sortOrder++
//            }
//        }

        return cookSync(workbench, needCache = true)
    }



    private fun cookSync(workbench: Workbench, needCache: Boolean = true): Workbench? {
        //加入管理卡片
        val currentOrgYouAdminSync = OrganizationManager.getInstance().isCurrentOrgYouAdminSync(AtworkApplicationLike.baseContext)
        LogUtil.e("currentOrgYouAdminSync   ->    $currentOrgYouAdminSync")

        if (currentOrgYouAdminSync) {


            val workbenchAdminCard = WorkbenchAdminCard()
            workbenchAdminCard.name = AtworkApplicationLike.getResourceString(R.string.admin_control) + AtworkApplicationLike.getResourceString(R.string.admin_control_tip)
            if (-1 == workbenchAdminCard.sortOrder) {
                workbenchAdminCard.sortOrder = workbench.workbenchCards.size + 1
            }

            workbench.workbenchCards.forEach {
                if (workbenchAdminCard.sortOrder <= it.sortOrder) {
                    it.sortOrder++
                }
            }

            workbench.workbenchCards.add(workbenchAdminCard)

        }

        val contentList = WorkbenchRepository.queryWorkbenchCardContents(workbench)
        contentList.forEach { content ->

            workbench.findWorkbenchCard(content.widgetsId).forEach {
                it.workbenchCardContent = content
            }


        }

        if (needCache) {
            cacheWorkbench = workbench
        }


        return workbench
    }


}