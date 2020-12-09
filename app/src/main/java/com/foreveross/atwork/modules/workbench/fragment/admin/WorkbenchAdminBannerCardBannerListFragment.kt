package com.foreveross.atwork.modules.workbench.fragment.admin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.ui.component.popUpView.W6sPopUpView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminBannerCardBannerAddActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminBannerCardBannerDetailActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminBannerCardBannerListActivity.Companion.ACTION_DELETE_BANNER_ITEM
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminBannerCardBannerListActivity.Companion.ACTION_REFRESH_BANNER_LIST
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchAdminBannerCardBannerListAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.model.WorkbenchAdminBannerCardBannerItemDataWrapper
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.foreveross.atwork.utils.startActivityByNoAnimation
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_workbench_admin_banner_card_banner_list.*

class WorkbenchAdminBannerCardBannerListFragment : BackHandledFragment(), OnRefreshListener {

    private val MAX_BANNER_COUNT = 5

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvHeaderTitle: TextView

    private lateinit var adapter: WorkbenchAdminBannerCardBannerListAdapter
    private lateinit var workbenchCardDetailData: WorkbenchCardDetailData
    private lateinit var workbenchData: WorkbenchData

    private val adminAdvertisementConfigList = ArrayList<AdminAdvertisementConfig>()
    private val dataList = ArrayList<WorkbenchAdminBannerCardBannerItemDataWrapper>()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_REFRESH_BANNER_LIST -> {
                    val refreshData = intent.getParcelableExtra<AdminAdvertisementConfig>(AdminAdvertisementConfig::class.java.toString())
                    if (null == refreshData) {
                        swipeLayout.autoRefresh()
                    } else {
                        adminAdvertisementConfigList.removeAll { it.id == refreshData.id }
                        adminAdvertisementConfigList.add(refreshData)
                        refreshData(adminAdvertisementConfigList)

                    }
                }

                ACTION_DELETE_BANNER_ITEM -> {
                    val deleteData = intent.getParcelableExtra<AdminAdvertisementConfig>(AdminAdvertisementConfig::class.java.toString())
                    adminAdvertisementConfigList.removeAll { it.id == deleteData.id }
                    refreshData(adminAdvertisementConfigList)

                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_banner_card_banner_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initData()
        registerListener()

        registerBdListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        unregisterBdListener()
    }

    private fun registerBdListener() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_REFRESH_BANNER_LIST)
        intentFilter.addAction(ACTION_DELETE_BANNER_ITEM)

        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBdListener() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(broadcastReceiver)
    }

    private fun initViews() {
        tvTitle.setText(R.string.admin_add_card_name_banner)
        tvHeaderTitle.text = getStrings(R.string.admin_add_banner_count_max, MAX_BANNER_COUNT.toString())

        adapter = WorkbenchAdminBannerCardBannerListAdapter(dataList)

        tvHeaderTitle.isVisible = false
        adapter.addHeaderView(tvHeaderTitle)

        rvBanners.adapter = adapter
        rvBanners.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val divider = DensityUtil.dip2px(10f)

                outRect.bottom = divider

            }
        })

        swipeLayout.setOnRefreshListener(this)
        swipeLayout.autoRefresh()
    }

    private fun initData() {
        arguments?.getParcelable<WorkbenchCardDetailData>(WorkbenchCardDetailData::class.java.toString())?.let {
            workbenchCardDetailData = it
        }

        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let {
            workbenchData = it
        }

//        refreshAdapter()
    }

    private fun refreshAdapter() {
        if (MAX_BANNER_COUNT > dataList.count { it.isItemDisplay() }) {
            dataList.add(WorkbenchAdminBannerCardBannerItemDataWrapper())
        }

        adapter.notifyDataSetChanged()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)

        tvHeaderTitle = LayoutInflater.from(activity).inflate(R.layout.component_workbench_banner_card_banner_header_title, null) as TextView
    }

    private fun registerListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        adapter.setOnItemClickListener { adapter, view, position ->
            val workbenchAdminBannerCardBannerItemDataWrapper = dataList[position]
            if (workbenchAdminBannerCardBannerItemDataWrapper.isItemDisplay()) {
                val context = activity ?: return@setOnItemClickListener
                val intent = WorkbenchAdminBannerCardBannerDetailActivity.getIntent(context, workbenchData, workbenchCardDetailData, workbenchAdminBannerCardBannerItemDataWrapper.advertisementConfig!!)
                context.startActivityByNoAnimation(intent)


            } else {
                val context = activity ?: return@setOnItemClickListener
                val intent = WorkbenchAdminBannerCardBannerAddActivity.getIntent(context, workbenchData, workbenchCardDetailData)
                context.startActivityByNoAnimation(intent)
            }
        }

        adapter.onItemInfoClickListener = { position, infoView, event ->
            val w6sPopUpView = W6sPopUpView(activity!!).apply {
                addPopItem(-1, R.string.common_view, 0)
                addPopItem(-1, R.string.delete_action, 1)

                setPopItemOnClickListener(object : W6sPopUpView.PopItemOnClickListener {
                    override fun click(title: String, pos: Int) {
                        when (title) {
                            getStrings(R.string.common_view) -> actionCardView(position)
                            getStrings(R.string.delete_action) -> actionCardDelete(position)
                        }

                    }

                })


            }

            w6sPopUpView.popSpaceCompatible(infoView, event)

        }

        adapter.onItemSwPutawayClickListener = { position, swView ->
            //position 考虑加了头部后的偏移
            val adminAdvertisementConfig = dataList[position - 1].advertisementConfig!!
            val checked = swView.isChecked
            WorkbenchAdminManager.adminPutawayBannerItem(
                    context = AtworkApplicationLike.baseContext,
                    workbenchData = workbenchData,
                    widgetId = workbenchCardDetailData.id.toString(),
                    adminAdvertisementConfig = adminAdvertisementConfig,
                    putaway = !checked,
                    listener = object : BaseCallBackNetWorkListener {
                        override fun onSuccess() {
                            swView.isChecked = !checked

                        }

                        override fun networkFail(errorCode: Int, errorMsg: String?) {
                            ErrorHandleUtil.handleError(errorCode, errorMsg)
                        }

                    }
            )

        }


    }

    private fun actionCardView(position: Int) {
        val context = activity ?: return

        //position 考虑加了头部后的偏移
        val adminAdvertisementConfig = dataList[position - 1].advertisementConfig!!
        val intent = WorkbenchAdminBannerCardBannerDetailActivity.getIntent(context, workbenchData, workbenchCardDetailData, adminAdvertisementConfig)
        context.startActivityByNoAnimation(intent)
    }

    private fun actionCardDelete(position: Int) {
        //position 考虑加了头部后的偏移
        val adminAdvertisementConfig = dataList[position - 1].advertisementConfig!!

        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()

        WorkbenchAdminManager.adminDeleteBannerItem(
                context = AtworkApplicationLike.baseContext,
                workbenchData = workbenchData,
                widgetId = workbenchCardDetailData.id.toString(),
                adminAdvertisementConfig = adminAdvertisementConfig,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressDialogHelper.dismiss()
                        toastOver(R.string.delete_success)
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressDialogHelper.dismiss()
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }

                }
        )
    }


    override fun onBackPressed(): Boolean {
        finish()
        return false
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {

        WorkbenchAdminManager.adminQueryBannerList(AtworkApplicationLike.baseContext, workbenchData.orgCode, workbenchCardDetailData.id.toString(), object : BaseNetWorkListener<List<AdminAdvertisementConfig>> {
            override fun onSuccess(advertisementConfigs: List<AdminAdvertisementConfig>) {
                swipeLayout.finishRefresh()

                adminAdvertisementConfigList.clear()
                adminAdvertisementConfigList.addAll(advertisementConfigs)
                refreshData(advertisementConfigs)

            }


            override fun networkFail(errorCode: Int, errorMsg: String?) {
                swipeLayout.finishRefresh()

                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })
    }

    private fun refreshData(advertisementConfigs: List<AdminAdvertisementConfig>) {

        advertisementConfigs
                .sortedBy { it.sort }
                .map { WorkbenchAdminBannerCardBannerItemDataWrapper(advertisementConfig = it) }.let {
                    dataList.clear()
                    dataList.addAll(it)
                }

        tvHeaderTitle.isVisible = true
        refreshAdapter()
    }

}