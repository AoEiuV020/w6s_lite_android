package com.foreveross.atwork.modules.workbench.fragment.admin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.foreverht.workplus.ui.component.popUpView.W6sPopUpView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.workbench.activity.admin.*
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminAddOrModifyActivity.Companion.MODE_MODIFY
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchAdminAdminCardAdapter
import com.foreveross.atwork.modules.workbench.component.WorkbenchCardItemDecoration
import com.foreveross.atwork.modules.workbench.manager.IWorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.manager.QueryWorkbenchAdminMultiResult
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchCardContentManager
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.foreveross.atwork.utils.startActivityByNoAnimation
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_workbench.rvCards
import kotlinx.android.synthetic.main.fragment_workbench.swipeLayout
import kotlinx.android.synthetic.main.fragment_workbench_admin_admin.*

class WorkbenchAdminAdminFragment : BackHandledFragment(), OnRefreshListener {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var ivRightest: ImageView

    private lateinit var cardViewsAdapter: WorkbenchAdminAdminCardAdapter

    private val displayCards = ArrayList<WorkbenchCard>()
    private var workbenchData: WorkbenchData? = null



    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST -> {
                    intent.getParcelableExtra<WorkbenchData>(IWorkbenchAdminManager.DATA_WORKBENCH_DATA)?.let {
                        workbenchData = it

                        displayCards.clear()
                        displayCards.addAll(WorkbenchAdminManager.produce(it).workbenchCards)

                    }

//                    swipeLayout.autoRefresh()
                    refreshUI()
                }

                IWorkbenchAdminManager.ACTION_REFRESH_UPDATE_CARD -> {
                    intent.getParcelableExtra<WorkbenchCardDetailData>(IWorkbenchAdminManager.DATA_CARD_DATA)?.let { cardDetailData->
                        val workbenchDataHandle = workbenchData?: return

                        val workbenchCardDetailDataList = workbenchDataHandle.workbenchCardDetailDataList.toMutableList()
                        workbenchCardDetailDataList.remove(cardDetailData)
                        workbenchCardDetailDataList.add(cardDetailData)
                        workbenchDataHandle.workbenchCardDetailDataList = workbenchCardDetailDataList

                        displayCards.clear()
                        displayCards.addAll(WorkbenchAdminManager.produce(workbenchDataHandle).workbenchCards)
                        refreshUI()


                    }


                }

                IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST_TOTALLY -> {
                    swipeLayout.autoRefresh()
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
        registerBdListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterBdListener()
    }

    private fun registerBdListener() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST)
        intentFilter.addAction(IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST_TOTALLY)
        intentFilter.addAction(IWorkbenchAdminManager.ACTION_REFRESH_UPDATE_CARD)

        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBdListener() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(broadcastReceiver)
    }


    private fun initViews() {
        tvTitle.setText(R.string.admin_workbench)
        cardViewsAdapter = WorkbenchAdminAdminCardAdapter(workbenchData, displayCards)
//        cardViewsAdapter.setHasStableIds(true)

        cardViewsAdapter.bindToRecyclerView(rvCards)

        rvCards.addItemDecoration(WorkbenchCardItemDecoration(displayCards))

        if (rvCards.itemAnimator is SimpleItemAnimator) {
            (rvCards.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }


        rvCards.minimumHeight = ScreenUtils.getScreenHeight(AtworkApplicationLike.baseContext)

        swipeLayout.setOnRefreshListener(this)

        tryLoadFromCache()

        swipeLayout.autoRefresh()

    }

    private fun tryLoadFromCache() {
        val workbenchData = WorkbenchAdminManager.cacheWorkbenchData ?: return

        if(workbenchData.id != WorkbenchAdminManager.getCurrentAdminWorkbench(AtworkApplicationLike.baseContext)) {
            return
        }


        this.workbenchData = workbenchData
        cardViewsAdapter.workbenchData = workbenchData
        displayCards.clear()
        displayCards.addAll(WorkbenchAdminManager.produce(workbenchData).workbenchCards)
        refreshUI()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerListener()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
        ivRightest = view.findViewById(R.id.title_bar_common_right_img)
    }

    private fun initData() {


    }

    private fun registerListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        tvAddCard.setOnClickListener {
            if(null == workbenchData) {
                return@setOnClickListener
            }

            context?.let { it.startActivityByNoAnimation(WorkbenchAdminAddCardActivity.getIntent(it, workbenchData!!)) }


        }

        tvSetScope.setOnClickListener {
            if(null == workbenchData) {
                return@setOnClickListener
            }

            context?.let { it.startActivityByNoAnimation(WorkbenchAdminSetScopeActivity.getIntent(it, workbenchData!!)) }
        }


        tvSort.setOnClickListener {
            if(null == workbenchData) {
                return@setOnClickListener
            }

            context?.let { it.startActivityByNoAnimation(WorkbenchAdminSortCardActivity.getIntent(it, workbenchData!!)) }
        }

        ivRightest.setOnClickListener {
            if(null == workbenchData) {
                return@setOnClickListener
            }

            context?.let { it.startActivityByNoAnimation(WorkbenchAdminAddOrModifyActivity.getIntent(
                    context = it,
                    mode = MODE_MODIFY,
                    currentAdminWorkbenchData = workbenchData!!
            )) }

        }

        cardViewsAdapter.onAdminActionListener = {position, view, event->

            val w6sPopUpView = W6sPopUpView(activity!!).apply {
                addPopItem(-1, R.string.edit, 0)
                addPopItem(-1, R.string.label_remove, 1)

                setPopItemOnClickListener(object : W6sPopUpView.PopItemOnClickListener {
                    override fun click(title: String, pos: Int) {
                        when(title) {
                            getStrings(R.string.edit)  -> actionCardEdit(position)
                            getStrings(R.string.label_remove)  -> actionCardRemoved(position)
                        }

                    }

                })



            }


            w6sPopUpView.popSpaceCompatible(view, event)





        }

    }

    private fun actionCardEdit(position: Int) {

        val workbenchData = workbenchData?: return
        val context = activity ?: return

        val originalWorkbenchCardDetailData = displayCards[position].originalWorkbenchCardDetailData?: return
        val intent = WorkbenchAdminModifyCardActivity.getIntent(context, workbenchData, originalWorkbenchCardDetailData)
        context.startActivityByNoAnimation(intent)
    }

    private fun actionCardRemoved(position: Int) {
        if (null == workbenchData) {
            return
        }

        val progressLoading = ProgressDialogHelper(activity)
        progressLoading.show()

        WorkbenchAdminManager.deleteCard(
                context = AtworkApplicationLike.baseContext,
                orgCode = workbenchData!!.orgCode,
                widgetId = displayCards[position].id.toString(),
                originalWorkbenchData = workbenchData!!,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressLoading.dismiss()
                        toastOver(R.string.remove_successfully)
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressLoading.dismiss()
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
        WorkbenchCardContentManager.clearRequestRecord()

        val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext)
        WorkbenchAdminManager.queryWorkbench(
                context = AtworkApplicationLike.baseContext,
                orgCode = currentOrgCode,
                widgetId = WorkbenchAdminManager.getCurrentAdminWorkbench(AtworkApplicationLike.baseContext).toString(),
                listener = object : BaseNetWorkListener<QueryWorkbenchAdminMultiResult> {
                    override fun onSuccess(multiResult: QueryWorkbenchAdminMultiResult) {
                        workbenchData = multiResult.originalWorkbenchData

                        cardViewsAdapter.workbenchData = workbenchData

                        displayCards.clear()
                        displayCards.addAll(multiResult.localResult.workbenchCards)
                        refreshUI()

                        swipeLayout.finishRefresh()


                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        swipeLayout.finishRefresh()

                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }

                }
        )
    }


    private fun refreshUI() {
        if(null == workbenchData) {
            tvTitle.setText(R.string.admin_workbench)
        } else {

            tvTitle.text = "${getStrings(R.string.admin_workbench)}-${workbenchData!!.getNameI18n(AtworkApplicationLike.baseContext)}"
            ivRightest.setImageResource(R.mipmap.icon_setting_dark)
            ivRightest.isVisible = true
        }

        cardViewsAdapter.notifyDataSetChanged()
    }
}