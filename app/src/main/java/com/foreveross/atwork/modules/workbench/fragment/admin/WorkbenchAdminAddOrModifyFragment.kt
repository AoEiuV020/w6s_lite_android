package com.foreveross.atwork.modules.workbench.fragment.admin

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchHandleRequest
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.component.recyclerview.layoutManager.FlowLayoutManager
import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.group.module.UserSelectControlAction
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminAddOrModifyActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminAddOrModifyActivity.Companion.MODE_ADD
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminAddOrModifyActivity.Companion.MODE_MODIFY
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchScopeSelectedListAdapter
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchScopeSelectedSpaceItemDecoration
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.EditTextUtil
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_workbench_admin_add_or_modify.*


class WorkbenchAdminAddOrModifyFragment: BackHandledPushOrPullFragment() {

    private val DATA_REQUEST_SELECT_CONTACTS = 1

    private lateinit var scopeListAdapter: WorkbenchScopeSelectedListAdapter
    private val scopeDataList = ArrayList<Scope>()

    private var mode = MODE_ADD
    private var workbenchData: WorkbenchData? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_add_or_modify, container, false)
    }

    override fun getAnimationRoot(): View = rlContentRoot

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()

        registerListener()

    }

    private fun initViews() {
        rvRangeSelect.background.asType<GradientDrawable>()?.run {
            mutate()
            setStroke(DensityUtil.dip2px(1F), ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_bg_gray))
        }


        if(MODE_ADD == mode) {
            tvTitle.setText(R.string.admin_add_workbench)
        } else if(MODE_MODIFY == mode) {
            tvTitle.setText(R.string.admin_modify_workbench)
            workbenchData?.let {
                initInfoViews(it)
            }
        }

        EditTextUtil.setEditTextMaxStringLengthInput(etWorkbenchName.etInput, 6, true)
        EditTextUtil.setEditTextMaxStringLengthInput(etWorkbenchTwName.etInput, 6, true)
        EditTextUtil.setEditTextMaxStringLengthInput(etWorkbenchEnName.etInput, 6, true)

        refreshSureBtnStatus()
    }

    private fun initInfoViews(workbench: WorkbenchData) {
        etWorkbenchName.etInput.setText(workbench.name)
        etWorkbenchEnName.etInput.setText(workbench.enName)
        etWorkbenchTwName.etInput.setText(workbench.twName)
        etWorkbenchRemark.etInput.setText(workbench.remarks)
        swPutaway.isChecked = workbench.disabled?.not() ?: false


        scopeDataList.clear()
        scopeDataList.addAll(workbench.getScopeList(AtworkApplicationLike.baseContext))
        notifyScopeAdapterChanged()
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




    }

    override fun findViews(view: View) {
    }

    private fun initData() {

        arguments?.getInt(WorkbenchAdminAddOrModifyActivity.DATA_MODE)?.let {
            mode = it
        }

        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let {
            workbenchData = it
        }

        scopeListAdapter = WorkbenchScopeSelectedListAdapter(scopeDataList)

        val flowLayoutManager = FlowLayoutManager()
        flowLayoutManager.isAutoMeasureEnabled = true

        rvRangeSelect.layoutManager = flowLayoutManager
        rvRangeSelect.addItemDecoration(WorkbenchScopeSelectedSpaceItemDecoration())
        rvRangeSelect.adapter = scopeListAdapter
    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        swPutaway.setOnClickNotPerformToggle {
            swPutaway.toggle()
        }


        ivRangeAdd.setOnClickListener { routeScopeHandle() }

        tvSure.setOnClickListener { handleSureAction() }

        etWorkbenchName.setInputTextWatcher(object : TextWatcherAdapter() {
            override fun afterTextChanged(text: Editable?) {
                refreshSureBtnStatus()
            }
        })

        scopeListAdapter.setOnItemClickListener { adapter, view, position ->
            scopeDataList.removeAt(position)
            notifyScopeAdapterChanged()
        }

    }

    private fun routeScopeHandle() {
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

    private fun handleSureAction() {

        val workbenchAddRequest = WorkbenchHandleRequest(
                name = etWorkbenchName.text,
                enName = etWorkbenchEnName.text,
                twName = etWorkbenchTwName.text,
                remarks = etWorkbenchRemark.text,
                scopes = scopeDataList.map { it.path }.toTypedArray(),
                scopesData = scopeDataList.toArray(Array(scopeDataList.size){ index -> scopeDataList[index] }),
                disable = !swPutaway.isChecked

        )

        if(MODE_ADD == mode) {
            handleAddAction(workbenchAddRequest)
        } else if(MODE_MODIFY == mode){
            handleModifyAction(workbenchAddRequest)

        }


    }

    private fun handleModifyAction(workbenchAddRequest: WorkbenchHandleRequest) {
        val workbenchData = workbenchData ?: return

        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()

        WorkbenchAdminManager.modifyWorkbench(
                context = AtworkApplicationLike.baseContext,
                orgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext),
                widgetId = workbenchData.id.toString(),
                workbenchHandleRequest = workbenchAddRequest,
                originalWorkbenchData = workbenchData,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressDialogHelper.dismiss()
                        toastOver(R.string.setting_success)

                        onBackPressed()
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }
                })
    }

    private fun handleAddAction(workbenchAddRequest: WorkbenchHandleRequest) {
        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()

        WorkbenchAdminManager.addWorkbench(
                context = AtworkApplicationLike.baseContext,
                orgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext),
                workbenchHandleRequest = workbenchAddRequest,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressDialogHelper.dismiss()
                        toastOver(R.string.add_successfully)

                        onBackPressed()
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }
                })
    }

    private fun inputInfoLegal(): Boolean {
        if(StringUtils.isEmpty(etWorkbenchName.text)) {
            return false
        }

        if(ListUtil.isEmpty(scopeDataList)) {
            return false
        }

        return true
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

    private fun notifyScopeAdapterChanged() {
        scopeListAdapter.notifyDataSetChanged()
        refreshSureBtnStatus()
    }


}