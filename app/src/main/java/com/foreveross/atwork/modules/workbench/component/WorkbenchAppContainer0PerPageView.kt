package com.foreveross.atwork.modules.workbench.component

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.search.util.SearchHelper
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminModifyAppEntryActivity
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchAppContainer0PerPageAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.model.WorkbenchAppContainerItemDataWrapper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.foreveross.atwork.utils.startActivityByNoAnimation
import kotlinx.android.synthetic.main.component_workbench_app_container_card_0_per_page.view.*


class WorkbenchAppContainer0PerPageView : FrameLayout {

    val appContainerItemDataWrapperList = arrayListOf<WorkbenchAppContainerItemDataWrapper>()
    private lateinit var adapter: WorkbenchAppContainer0PerPageAdapter
    var workbenchCard: WorkbenchCard? = null


    constructor(context: Context) : super(context) {
        findViews()
        initViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
        initViews()
        registerListener()
    }


    private fun initViews() {
        adapter = WorkbenchAppContainer0PerPageAdapter(appContainerItemDataWrapperList)
        adapter.workbenchCard = workbenchCard

        rvEntries.layoutManager = GridLayoutManager(context, 4)
        rvEntries.adapter = adapter

        rvEntries.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = DensityUtil.dip2px(16f)
            }
        })

    }


    private fun registerListener() {
        adapter.setOnItemClickListener { adapter, view, position ->
            val appContainerItemDataWrapper = appContainerItemDataWrapperList[position]

            if(true == workbenchCard?.adminDisplay) {
                val originalWorkbenchCardDetailData = workbenchCard?.originalWorkbenchCardDetailData?: return@setOnItemClickListener
                val intent = WorkbenchAdminModifyAppEntryActivity.getIntent(context, originalWorkbenchCardDetailData)
                context.startActivityByNoAnimation(intent)

                return@setOnItemClickListener
            }


            SearchHelper.handleSearchResultCommonClick(context as Activity, StringUtils.EMPTY, appContainerItemDataWrapper.appBundle, null, null)
        }


        adapter.onRemoveItemAppBundleListener = { position->
            val workbenchCardDetailDataRequest = workbenchCard?.originalWorkbenchCardDetailData?.copy()
            workbenchCardDetailDataRequest?.let {
                val itemRemoved = appContainerItemDataWrapperList[position]

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
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_app_container_card_0_per_page, this)

    }

    fun refreshViews(appContainerItemDataWrapperList: List<WorkbenchAppContainerItemDataWrapper>, workbenchCard: WorkbenchCard?) {
        this.appContainerItemDataWrapperList.clear()
        this.appContainerItemDataWrapperList.addAll(appContainerItemDataWrapperList)

        this.workbenchCard = workbenchCard
        adapter.workbenchCard = workbenchCard
        adapter.notifyDataSetChanged()
    }
}