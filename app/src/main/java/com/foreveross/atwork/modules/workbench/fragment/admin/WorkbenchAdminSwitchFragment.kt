package com.foreveross.atwork.modules.workbench.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchAdminSwitchListAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_workbench_admin_switch.*

class WorkbenchAdminSwitchFragment: BackHandledPushOrPullFragment() {

    lateinit var adapter: WorkbenchAdminSwitchListAdapter
    private val dataList = ArrayList<WorkbenchData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_switch, container, false)
    }

    override fun getAnimationRoot(): View = llContentRoot

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvWorkbenchList.setMaxListViewHeight(ScreenUtils.getScreenHeight(AtworkApplicationLike.baseContext) * 3 / 4)

        initData()
        registerListener()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        WorkbenchAdminManager.queryWorkbenchList(
                context = AtworkApplicationLike.baseContext,
                orgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext),
                listener = object : BaseNetWorkListener<List<WorkbenchData>> {

                    override fun onSuccess(result: List<WorkbenchData>) {
                        pbLoading.isVisible = false

                        dataList.clear()
                        dataList.addAll(result)
                        adapter.notifyDataSetChanged()

                        rvWorkbenchList.isVisible = true
                    }


                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }

                })

    }

    override fun findViews(view: View) {

    }

    private fun initData() {
        adapter = WorkbenchAdminSwitchListAdapter(dataList)
        rvWorkbenchList.adapter = adapter

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvWorkbenchList.layoutManager = layoutManager
    }

    private fun registerListener() {

        rlRoot.setOnClickListener { onBackPressed() }

        tvCancel.setOnClickListener { onBackPressed() }

        llContentRoot.setOnClickListener {  }

        adapter.setOnItemClickListener { adapter, view, position ->
            this@WorkbenchAdminSwitchFragment.adapter.apply {
                selectIdShouldSubmit = dataList[position].id
                notifyDataSetChanged()
            }
        }


        tvSure.setOnClickListener {
            WorkbenchAdminManager.setCurrentAdminWorkbench(AtworkApplicationLike.baseContext, adapter.getSelectWorkbenchId())
            WorkbenchManager.notifySwitchAdminPreviewWorkbench()
            onBackPressed()
        }
    }

}