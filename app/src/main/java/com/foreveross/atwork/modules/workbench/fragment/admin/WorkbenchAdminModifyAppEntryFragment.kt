package com.foreveross.atwork.modules.workbench.fragment.admin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.app.requestJson.QueryAppListByAdminRequest
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppListByAdminResponseResult
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.app.admin.QueryAppItemResultEntry
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminModifyAppEntryActivity
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchAdminAppEntriesAdapter
import com.foreveross.atwork.modules.workbench.adapter.admin.provider.AppEntryChildNode
import com.foreveross.atwork.modules.workbench.adapter.admin.provider.AppEntryParentNode
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_workbench_admin_app_container_modify_entry.*

class WorkbenchAdminModifyAppEntryFragment: BackHandledPushOrPullFragment() {

    private lateinit var adapter: WorkbenchAdminAppEntriesAdapter
    private val dataList: MutableList<AppEntryParentNode> = ArrayList()

    private lateinit var workbenchCardDetailData: WorkbenchCardDetailData

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                WorkbenchAdminModifyAppEntryActivity.ACTION_REFRESH_TOTALLY -> {
                    initLoadAppEntriesData()
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_app_container_modify_entry, container, false)
    }

    override fun getAnimationRoot(): View = llContentRoot

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
        registerListener()
        registerBdListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Handler().postDelayed(delayInMillis = 100) { initLoadAppEntriesData() }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        unregisterBdListener()
    }

    private fun initData() {
        arguments?.getParcelable<WorkbenchCardDetailData>(WorkbenchCardDetailData::class.java.toString())?.let {
            workbenchCardDetailData = it
        }
    }

    private fun registerBdListener() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WorkbenchAdminModifyAppEntryActivity.ACTION_REFRESH_TOTALLY)

        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBdListener() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(broadcastReceiver)
    }


    private fun initLoadAppEntriesData() {
        pbLoading.isVisible = true
        rvEntries.isVisible = false
        vMoreSolutions.isVisible = false


        val queryAppListByAdminRequest = QueryAppListByAdminRequest(
                orgCode = workbenchCardDetailData.orgCode,
                skip = 0,
                limit = 200
        )

        AppManager.getInstance().queryAppsByAdmin(AtworkApplicationLike.baseContext, queryAppListByAdminRequest, object : BaseNetWorkListener<QueryAppListByAdminResponseResult> {
            override fun onSuccess(result: QueryAppListByAdminResponseResult) {
                pbLoading?.isVisible = false

                val nodeLost = buildNodeList(result)

                checkNodeListSelectStatus(nodeLost)

                dataList.clear()
                dataList.addAll(nodeLost)

                adapter.setList(dataList)

                rvEntries?.isVisible = true
                vMoreSolutions?.isVisible = true
            }


            override fun networkFail(errorCode: Int, errorMsg: String?) {
                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })

    }

    private fun checkNodeListSelectStatus(nodeLost: ArrayList<AppEntryParentNode>) {
        nodeLost.forEach { parentNode ->
            parentNode.select = shouldParentSelect(parentNode)


            parentNode.appEntriesChildren.forEach { childNode ->
                if (childNode is AppEntryChildNode) {
                    childNode.select = shouldChildSelect(childNode)
                }


            }

        }
    }

    private fun buildNodeList(result: QueryAppListByAdminResponseResult): ArrayList<AppEntryParentNode> {
        val nodeLost = ArrayList<AppEntryParentNode>()
        result.appList?.forEach { app ->

            val queryAppItemResult = result.appResults?.find { appResult -> appResult.appKey.appId == app.id }

            val childrenNodes = app.mBundles.filter { !it.isMainBundle }.map { AppEntryChildNode(it, queryAppItemResult = queryAppItemResult, maxSelected = getMaxSelectedCount()) }
            app.mBundles
                    .find { it.isMainBundle }
                    ?.let {
                        AppEntryParentNode(appEntry = it, queryAppItemResult = queryAppItemResult, appEntriesChildren = childrenNodes.toMutableList(), maxSelected = getMaxSelectedCount())
                    }

                    ?.let { nodeLost.add(it) }
        }
        return nodeLost
    }

    private fun getMaxSelectedCount(): Int  {
        val listCount = workbenchCardDetailData.workbenchCardDisplayDefinitions?.listCount ?: return -1

        if (0 < listCount) {
            return listCount
        }

        return -1
    }


    private fun shouldChildSelect(childNode: AppEntryChildNode) =
            workbenchCardDetailData.workbenchCardDisplayDefinitions?.appContainer?.any { it.entryId == childNode.appEntry.mBundleId }
                    ?: false

    private fun shouldParentSelect(parentNode: AppEntryParentNode) =
            workbenchCardDetailData.workbenchCardDisplayDefinitions?.appContainer?.any { it.entryId == parentNode.appEntry.mBundleId }
                    ?: false

    private fun initViews() {
        adapter = WorkbenchAdminAppEntriesAdapter()
        adapter.setList(dataList)
        rvEntries.adapter = adapter
        rvEntries.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val rvEntriesBottomMargin = vMoreSolutions.getInfo()[1] + DensityUtil.dip2px(20f)
        LogUtil.e("rvEntriesBottomMargin : $rvEntriesBottomMargin")
        ViewUtil.setBottomMargin(rvEntries, rvEntriesBottomMargin)
        rvEntries.setMaxListViewHeight(ScreenUtils.getScreenHeight(AtworkApplicationLike.baseContext) * 3 / 4 - rvEntriesBottomMargin)

    }

    override fun findViews(view: View) {

    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        tvSure.setOnClickListener { handleSureAction() }

        vMoreSolutions.setOnClickListener { routeMoreSolutions() }
    }

    private fun routeMoreSolutions() {
        val url = String.format(UrlConstantManager.getInstance().appStoreUrl, PersonalShareInfo.getInstance().getCurrentOrg(mActivity), LoginUserInfo.getInstance().getLoginUserId(mActivity), true)

        val webViewControlAction = WebViewControlAction.newAction().setUrl(url)
        val intent = WebViewActivity.getIntent(mActivity, webViewControlAction)
        startActivity(intent)
    }

    private fun handleSureAction() {
        val progressLoading = ProgressDialogHelper(activity)
        progressLoading.show()

        val workbenchCardDetailDataRequest = workbenchCardDetailData.copy()
        val appContainer = ArrayList<QueryAppItemResultEntry>()

        addSelectAppBundles(appContainer)

        workbenchCardDetailDataRequest.workbenchCardDisplayDefinitions?.appContainer = appContainer

        WorkbenchAdminManager.modifyCard(
                context = AtworkApplicationLike.baseContext,
                orgCode = workbenchCardDetailDataRequest.orgCode,
                workbenchCardDetailData = workbenchCardDetailDataRequest,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressLoading.dismiss()
                        workbenchCardDetailData = workbenchCardDetailDataRequest

                        toastOver(R.string.add_successfully)
                        onBackPressed()
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressLoading.dismiss()
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }

                }
        )
    }

    private fun addSelectAppBundles(appContainer: MutableList<QueryAppItemResultEntry>) {
        dataList.forEach { parentNode ->
            if (parentNode.select) {
                parentNode.getQueryAppItemResultEntryData()?.let { appContainer.add(it) }

            }


            parentNode.appEntriesChildren.forEach { childNode ->
                if (childNode is AppEntryChildNode && childNode.select) {
                    childNode.getQueryAppItemResultEntryData()?.let { appContainer.add(it) }
                }
            }

        }
    }




}