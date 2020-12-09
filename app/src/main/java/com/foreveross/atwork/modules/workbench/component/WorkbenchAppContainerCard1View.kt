package com.foreveross.atwork.modules.workbench.component

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchAppContainer1Card
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.search.util.SearchHelper
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminModifyAppEntryActivity
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchAppContainer1Adapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.modules.workbench.model.WorkbenchAppContainerItemDataWrapper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.foreveross.atwork.utils.startActivityByNoAnimation
import kotlinx.android.synthetic.main.component_workbench_app_container_card_1.view.*

class WorkbenchAppContainerCard1View : WorkbenchBasicCardView<WorkbenchAppContainer1Card> {

    override lateinit var workbenchCard: WorkbenchAppContainer1Card

    private lateinit var adapter: WorkbenchAppContainer1Adapter
    private val itemList = arrayListOf<WorkbenchAppContainerItemDataWrapper>()


    constructor(context: Context) : super(context) {
        initViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
        registerListener()
    }

    private fun initViews() {
        adapter = WorkbenchAppContainer1Adapter(itemList)

        rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvList.adapter = adapter
    }


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_app_container_card_1, this)

    }

    private fun registerListener() {

        adapter.onRemoveItemAppBundleListener = { position ->
            handleItemRemovedAction(position)
        }

        adapter.setOnItemClickListener { adapter, view, position ->
            val appContainerItemDataWrapper = itemList[position]

            if (workbenchCard.adminDisplay) {
                val originalWorkbenchCardDetailData = workbenchCard.originalWorkbenchCardDetailData
                        ?: return@setOnItemClickListener
                val intent = WorkbenchAdminModifyAppEntryActivity.getIntent(context, originalWorkbenchCardDetailData)
                context.startActivityByNoAnimation(intent)

                return@setOnItemClickListener
            }

            SearchHelper.handleSearchResultCommonClick(context as Activity, StringUtils.EMPTY, appContainerItemDataWrapper.appBundle, null, null)
        }
    }

    private fun handleItemRemovedAction(position: Int) {
        val workbenchCardDetailDataRequest = workbenchCard.originalWorkbenchCardDetailData?.copy()
        workbenchCardDetailDataRequest?.let {
            val itemRemoved = itemList[position]

            val progressLoading = ProgressDialogHelper(context)
            progressLoading.show()


            val appContainer = workbenchCardDetailDataRequest.workbenchCardDisplayDefinitions?.appContainer?.toMutableList()
            appContainer?.removeAll { it.entryId == itemRemoved.appBundle?.mBundleId }

            workbenchCardDetailDataRequest.workbenchCardDisplayDefinitions?.appContainer = appContainer

            WorkbenchAdminManager.modifyCard(
                    context = AtworkApplicationLike.baseContext,
                    orgCode = workbenchCardDetailDataRequest.orgCode,
                    workbenchCardDetailData = workbenchCardDetailDataRequest,
                    listener = object : BaseCallBackNetWorkListener {
                        override fun onSuccess() {
                            progressLoading.dismiss()

                            AtworkToast.showResToast(R.string.delete_success)

                        }

                        override fun networkFail(errorCode: Int, errorMsg: String?) {
                            progressLoading.dismiss()
                            ErrorHandleUtil.handleError(errorCode, errorMsg)
                        }

                    }
            )
        }
    }

    override fun refresh(workbenchCard: WorkbenchAppContainer1Card) {
        this.workbenchCard = workbenchCard

        refreshView(workbenchCard)
    }

    override fun refreshView(workbenchCard: WorkbenchAppContainer1Card) {
        vWorkbenchCommonTitleView.refreshView(workbenchCard)

        if (workbenchCard.adminDisplay) {
            refreshViewAdminMode(workbenchCard)

        } else {
            refreshViewUserMode(workbenchCard)

        }


    }

    private fun refreshViewUserMode(workbenchCard: WorkbenchAppContainer1Card) {
        val appBundlesShouldDisplay = getAppBundlesShouldDisplay()

//        val appBundlesShouldDisplay = AppManager.getInstance().appBundleList.filter { appBundleData ->
//            appBundlesInAppContainer.any { it.id == appBundleData.id }
//        }

        val appContainerItemDataWrapperList = appBundlesShouldDisplay.map { WorkbenchAppContainerItemDataWrapper(appBundle = it) }.toMutableList()

        var cutEndIndex = workbenchCard.listCount
        if (cutEndIndex > appContainerItemDataWrapperList.size) {
            cutEndIndex = appContainerItemDataWrapperList.size
        }

        itemList.clear()
        itemList.addAll(appContainerItemDataWrapperList.subList(0, cutEndIndex))
        refreshUI()
    }

    private fun getAppBundlesShouldDisplay(): ArrayList<AppBundles> {
       return WorkbenchManager.getAppBundlesShouldDisplay(this.workbenchCard.appContainer)
    }

    private fun refreshViewAdminMode(workbenchCard: WorkbenchAppContainer1Card) {
        val appBundles = this.workbenchCard.appContainer
        val appContainerItemDataWrapperList = appBundles.map { WorkbenchAppContainerItemDataWrapper(appBundle = it) }.toMutableList()

        if (appContainerItemDataWrapperList.size >= this.workbenchCard.listCount) {
            var cutEndIndex = workbenchCard.listCount
            if (cutEndIndex > appContainerItemDataWrapperList.size) {
                cutEndIndex = appContainerItemDataWrapperList.size
            }

            itemList.clear()
            itemList.addAll(appContainerItemDataWrapperList.subList(0, cutEndIndex))

        } else {
            val leftEntryCount = this.workbenchCard.listCount - appContainerItemDataWrapperList.size
            itemList.clear()
            itemList.addAll(appContainerItemDataWrapperList)
            for (i in 0 until leftEntryCount) {
                itemList.add(WorkbenchAppContainerItemDataWrapper())
            }
        }

        refreshUI()
    }

    private fun refreshUI() {
        if (ListUtil.isEmpty(itemList)) {
            hide()
        } else {
            show()
        }

        adapter.workbenchCard = workbenchCard
        adapter.notifyDataSetChanged()
    }

    fun show() {
        llRoot.visibility = View.VISIBLE
    }

    fun hide() {
        llRoot.visibility = View.GONE
    }

    override fun getViewType(): Int = WorkbenchCardType.APP_CONTAINER_1.hashCode()
}