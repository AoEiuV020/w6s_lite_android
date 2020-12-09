package com.foreveross.atwork.modules.workbench.fragment.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchHandleRequest
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.component.recyclerview.layoutManager.FlowLayoutManager
import com.foreveross.atwork.infrastructure.model.orgization.Organization
import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchScopeSelectedListAdapter
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchScopeSelectedSpaceItemDecoration
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_workbench_admin_set_scope.*


class WorkbenchAdminSetScopeFragment: BackHandledPushOrPullFragment() {

    private val DATA_REQUEST_SELECT_CONTACTS = 1

    private lateinit var scopeListAdapter: WorkbenchScopeSelectedListAdapter

    private var selectScopeAll = true
    private lateinit var workbenchData: WorkbenchData

    private val scopeDataList = ArrayList<Scope>()
    private var rootOrg: Organization? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_set_scope, container, false)
    }

    override fun getAnimationRoot(): View = llContentRoot

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()

        refreshUI()
        registerListener()
    }

    override fun findViews(view: View) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(Activity.RESULT_OK == resultCode) {
            when(requestCode) {
                DATA_REQUEST_SELECT_CONTACTS -> {
                    scopeDataList.clear()
                    scopeDataList.addAll(SelectedContactList.getScopeList())


                    LogUtil.e("scopeDataList $scopeDataList")

                    notifyScopeAdapterChanged()
                }
            }
        }

    }


    private fun initData() {
        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let {
            workbenchData = it
        }

        handleInitScopeData()
        refreshSelectStatus()

        scopeListAdapter = WorkbenchScopeSelectedListAdapter(scopeDataList)

        val flowLayoutManager = FlowLayoutManager()
        flowLayoutManager.isAutoMeasureEnabled = true

        rvRangeSelect.layoutManager = flowLayoutManager
        rvRangeSelect.addItemDecoration(WorkbenchScopeSelectedSpaceItemDecoration())
        rvRangeSelect.adapter = scopeListAdapter
    }

    private fun handleInitScopeData() {
        if (::workbenchData.isInitialized) {
            workbenchData.scopeRecord?.let { scopeRecord ->
                rootOrg = scopeRecord.rootOrg

            }

            scopeDataList.addAll(workbenchData.getScopeList(AtworkApplicationLike.baseContext))

        }
    }


    private fun inputInfoLegal(): Boolean {

        if(selectScopeAll) {
            return true
        }

        if(ListUtil.isEmpty(scopeDataList)) {
            return false
        }

        return true
    }

    private fun refreshSureBtnStatus() {
        if (inputInfoLegal()) {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
            tvSure.isEnabled = true
        } else {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
            tvSure.isEnabled = false
        }
    }


    private fun refreshSelectStatus() {
        selectScopeAll = scopeDataList.any { it.isOrg() && it.path == rootOrg?.mPath }
    }

    private fun refreshUI() {
        if(selectScopeAll) {
            handleSelect(tvScopeAll)
            handleUnSelect(tvScopePart)

            llSetScopePartGuide.isInvisible = true
            rvRangeSelect.isVisible = false

        } else {
            handleSelect(tvScopePart)
            handleUnSelect(tvScopeAll)

            llSetScopePartGuide.isVisible = true
            rvRangeSelect.isVisible = true
        }

        refreshSureBtnStatus()
    }

    private fun handleUnSelect(tvView: TextView) {
        tvView.background
                .asType<GradientDrawable>()
                ?.run {
                    mutate()
                    setColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white))
                    setStroke(DensityUtil.dip2px(1F), Color.parseColor("#D4D4D4"))
                }

        tvView.setTextColor(Color.parseColor("#D4D4D4"))
    }

    private fun handleSelect(tvView: TextView) {
        tvView.background
                .asType<GradientDrawable>()
                ?.run {
                    mutate()
                    setColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
                    setStroke(DensityUtil.dip2px( 1F), ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
                }

        tvView.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white))
    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        tvScopeAll.setOnClickListener {
            selectScopeAll = true
            refreshUI()
        }

        tvScopePart.setOnClickListener {
            selectScopeAll = false
            refreshUI()
        }

        llSetScopePartGuide.setOnClickListener {
            SelectedContactList.clear()

            val userSelectControlAction = UserSelectControlAction().apply {
                selectMode = UserSelectActivity.SelectMode.SELECT
                selectAction = UserSelectActivity.SelectAction.SCOPE
                selectScopeSet = scopeDataList.toSet()
                isSelectCanNoOne = true
                isSuggestiveHideMe = false
                isNeedSetNotAllowList = false
                directOrgShow = true

            }


            val intent = UserSelectActivity.getIntent(activity, userSelectControlAction)

            startActivityForResult(intent, DATA_REQUEST_SELECT_CONTACTS)
        }

        tvSure.setOnClickListener {
            sureModifyWorkbench()
        }

        scopeListAdapter.setOnItemClickListener { adapter, view, position ->
            scopeDataList.removeAt(position)
            notifyScopeAdapterChanged()
        }
    }

    private fun sureModifyWorkbench() {
        val progressLoadHelper = ProgressDialogHelper(activity)
        progressLoadHelper.show()

        val scopes: Array<String>? = buildRequestPath()

        if(null == (scopes)) {
            toastOver(R.string.error_happened)
            return
        }

        WorkbenchAdminManager.modifyWorkbench(
                context = AtworkApplicationLike.baseContext,
                orgCode = workbenchData.orgCode,
                widgetId = workbenchData.id.toString(),
                workbenchHandleRequest = buildWorkbenchHandleRequest(scopes),
                originalWorkbenchData = workbenchData,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressLoadHelper.dismiss()

                        onBackPressed()
                        toastOver(R.string.setting_success)
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressLoadHelper.dismiss()
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }
                }
        )
    }

    private fun buildWorkbenchHandleRequest(scopes: Array<String>): WorkbenchHandleRequest {
        return WorkbenchHandleRequest(
                name = workbenchData.name ?: StringUtils.EMPTY,
                enName = workbenchData.enName,
                twName = workbenchData.twName,
                remarks = workbenchData.remarks,
                platforms = workbenchData.platforms.toTypedArray(),
                scopes = scopes,
                scopesData = scopeDataList.toTypedArray(),
                disable = workbenchData.disabled ?: false


        )
    }

    private fun buildRequestPath(): Array<String>? {
        val rootOrg = rootOrg?: return null

        val scopes: Array<String>? = if (selectScopeAll) {
            var rootScopePath: String? = rootOrg.transfer(AtworkApplicationLike.baseContext)?.path
            if (StringUtils.isEmpty(rootScopePath)) {
                rootScopePath = "/" + rootOrg.id + "/"
            }

            arrayOf(rootScopePath!!)

        } else {
            scopeDataList.map { it.path }.toTypedArray()
        }
        return scopes
    }

    private fun notifyScopeAdapterChanged() {
        scopeListAdapter.notifyDataSetChanged()
        refreshSureBtnStatus()
    }

}