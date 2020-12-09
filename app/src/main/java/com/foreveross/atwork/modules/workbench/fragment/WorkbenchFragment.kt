package com.foreveross.atwork.modules.workbench.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.*
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.LongUtil
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.app.util.AppRefreshHelper
import com.foreveross.atwork.modules.common.fragment.ImageGuidePageDialogFragment
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.foreveross.atwork.modules.main.data.TabNoticeManager
import com.foreveross.atwork.modules.notice.NoticeManager
import com.foreveross.atwork.modules.workbench.activity.WorkbenchCustomSortCardActivity
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchCardAdapter
import com.foreveross.atwork.modules.workbench.component.WorkbenchCardItemDecoration
import com.foreveross.atwork.modules.workbench.component.WorkbenchMoreSolutionsView
import com.foreveross.atwork.modules.workbench.manager.*
import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_workbench.*

class WorkbenchFragment : NoticeTabAndBackHandledFragment(), OnRefreshListener {



    private lateinit var cardViewsAdapter: WorkbenchCardAdapter

    private var workbench: Workbench? = null

    private val currentDisplayCards = ArrayList<WorkbenchCard>()
    private val currentNoticeDataMappings = HashMap<String, LightNoticeMapping>()
    private var mode = MODE_USER

    private lateinit var footerCustomSortView: View
    private lateinit var footerWorkbenchMoreSolutionsView: WorkbenchMoreSolutionsView
    private lateinit var emptyCardsView: View
    private lateinit var vWorkbenchMoreSolutionsInEmptyCardsView: WorkbenchMoreSolutionsView
    private lateinit var tvCustomSortInEmptyCardsView: TextView
    private lateinit var tvCustomSort: TextView

    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var  progressHelperDialog: ProgressDialogHelper

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (isAdded) {
                val action = intent.action


                when (action) {
                    IWorkbenchCardContentManager.ACTION_REFRESH_WORKBENCH_BANNER_ADVERTISEMENTS -> {
                        //refresh advertisements
                        val advertisements = intent.getParcelableArrayListExtra<AdvertisementConfig>(IWorkbenchCardContentManager.DATA_ADVERTISEMENTS)

                        refreshAdvertisements(advertisements)
                    }


                    IWorkbenchManager.ACTION_REFRESH_WORKBENCH -> {
                        refreshWorkbenchDataInUserMode()
                    }

                    IWorkbenchManager.ACTION_REFRESH_ADMIN_PREVIEW_WORKBENCH -> {
                        progressHelperDialog.show()

                        onRefreshInAdminPreviewMode()

                    }

                    IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST -> {
                        val workbenchData = intent.getParcelableExtra<WorkbenchData>(IWorkbenchAdminManager.DATA_WORKBENCH_DATA)
                        refreshWorkbenchDataInAdminPreviewMode(workbenchData)
                    }


                    IWorkbenchAdminManager.ACTION_REFRESH_UPDATE_CARD -> {
                        intent.getParcelableExtra<WorkbenchCardDetailData>(IWorkbenchAdminManager.DATA_CARD_DATA)?.let { cardDetailData->
                            val workbenchDataHandle = WorkbenchAdminManager.cacheWorkbenchData?: return

                            val workbenchCardDetailDataList = workbenchDataHandle.workbenchCardDetailDataList.toMutableList()
                            workbenchCardDetailDataList.remove(cardDetailData)
                            workbenchCardDetailDataList.add(cardDetailData)
                            workbenchDataHandle.workbenchCardDetailDataList = workbenchCardDetailDataList

                            refreshWorkbenchDataInAdminPreviewMode(workbenchDataHandle)


                        }


                    }

                    IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST_TOTALLY -> {
                        if(MODE_ADMIN_PREVIEW == mode) {
                            swipeLayout.autoRefresh()
                        }
                    }


                    AppRefreshHelper.ACTION_REFRESH_APP, AppRefreshHelper.ACTION_REFRESH_APP_LIGHTLY -> {
                        refreshCommonUsedApp()
                    }
                }

            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        registerListener()

        refreshWorkbenchDataInUserMode()


        registerBroadcast()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()

        refreshNoticeData()
        refreshCardViewsLightly()


        handleWorkbenchFirstGuidePageShown()
    }


    override fun onDestroyView() {
        super.onDestroyView()

        unregisterBroadcast()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            refreshNoticeData()
            refreshCardViewsLightly()

            handleWorkbenchFirstGuidePageShown()

        } else {
            ivWorkbenchGuide?.visibility = View.GONE

            //不可见时, 切换回用户模式
            switchModeUser()
        }
    }

    private fun switchModeUser() {
        refreshWorkbenchDataInUserMode()
    }


    override fun findViews(view: View) {
        footerCustomSortView = LayoutInflater.from(activity).inflate(R.layout.component_workbench_card_custom_sort_btn, null)
        footerWorkbenchMoreSolutionsView = WorkbenchMoreSolutionsView(view.context)
        emptyCardsView = LayoutInflater.from(activity).inflate(R.layout.component_workbench_empty_cards, null)

        tvCustomSort = footerCustomSortView.findViewById(R.id.tvCustomSort)
        tvCustomSortInEmptyCardsView = emptyCardsView.findViewById(R.id.tvCustomSort)
        vWorkbenchMoreSolutionsInEmptyCardsView = emptyCardsView.findViewById(R.id.vWorkbenchCardMoreSolutions)

        progressHelperDialog = ProgressDialogHelper(activity)

    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun getFakeStatusBar(): View? {
        return vFakeStatusbar
    }


    private fun initViews() {
        footerWorkbenchMoreSolutionsView.isVisible = false

        cardViewsAdapter = WorkbenchCardAdapter(currentDisplayCards)
        cardViewsAdapter.bindToRecyclerView(rvCards)

        cardViewsAdapter.emptyView = emptyCardsView
        cardViewsAdapter.addFooterView(footerWorkbenchMoreSolutionsView)
        cardViewsAdapter.addFooterView(footerCustomSortView)


        rvCards.addItemDecoration(WorkbenchCardItemDecoration(currentDisplayCards))




        if (rvCards.itemAnimator is SimpleItemAnimator) {
            (rvCards.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        rvCards.minimumHeight = ScreenUtils.getScreenHeight(AtworkApplicationLike.baseContext)


        layoutManager = rvCards.layoutManager as LinearLayoutManager

//        swipeLayout.setColorSchemeResources(R.color.common_blue_bg)
//        swipeLayout.isEnabled = true
//        swipeLayout.setOnRefreshListener(this)

        swipeLayout.setOnRefreshListener(this)

        tvCustomSort.setBackgroundResource(R.drawable.shape_blue_workbench_custom_sort_btn_light_bg)

        tvCustomSort.isVisible = AtworkConfig.WORKBENCH_CONFIG.isNeedCustomBtn
        tvCustomSortInEmptyCardsView.isVisible = AtworkConfig.WORKBENCH_CONFIG.isNeedCustomBtn



    }

    private fun handleWorkbenchFirstGuidePageShown() {
        if(!isFragmentVisibleAndHintVisible) {
            return
        }


        if(!AtworkConfig.WORKBENCH_CONFIG.needGuideByImage) {
            return
        }


        if(null == ivWorkbenchGuide) {
            return
        }

        if(!isAdded) {
            return
        }


        var shownCount = PersonalShareInfo.getInstance().getWorkbenchFirstGuidePageShownCount(BaseApplicationLike.baseContext)
        if (shownCount >= PersonalShareInfo.DEFAULT_SHOULD_MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT) {
            return
        }

        val dialogFragment = ImageGuidePageDialogFragment()
        dialogFragment.setGuideImage(R.mipmap.workbench_guide)
        dialogFragment.show(fragmentManager!!, "MeTabGuidePage")

        PersonalShareInfo.getInstance().putWorkbenchFirstGuidePageShownCount(BaseApplicationLike.baseContext, ++shownCount)
    }


    private fun refreshWorkbenchDataInUserMode() {
        var changed = false
        if(MODE_USER != mode) {
            mode = MODE_USER
            changed = true
        }

        WorkbenchManager.getCurrentOrgWorkbench { workbench ->

            if(MODE_USER != mode) {
                return@getCurrentOrgWorkbench
            }

            handleWorkbenchRefresh(workbench)

            if(changed) {
                swipeLayout.autoRefresh()
            }
        }
    }


    private fun refreshWorkbenchDataInAdminPreviewMode(originalWorkbenchData: WorkbenchData? = null) {

        mode = MODE_ADMIN_PREVIEW

        originalWorkbenchData?.let { WorkbenchAdminManager.cacheWorkbenchData = it }


        WorkbenchManager.getAdminPreviewModeWorkbench { workbench ->
            if(MODE_ADMIN_PREVIEW != mode) {
                return@getAdminPreviewModeWorkbench
            }

            handleWorkbenchRefresh(workbench)
        }


    }

    private fun handleWorkbenchRefresh(workbench: Workbench?) {
        this.workbench = workbench

        workbench?.let { WorkbenchAdminManager.setCurrentAdminWorkbench(AtworkApplicationLike.baseContext, it.id) }


        currentDisplayCards.clear()


        workbench?.let {
            currentDisplayCards.addAll(WorkbenchManager.filterDisplayAndSored(it).cardsDisplay)
        }

        refreshNoticeData()

        refreshFakeStatusBar()

        refreshCardViewsLightly()

        //do updating
        updateItemTab()
    }


    private fun updateItemTab() {
        val mainActivity = activity
        val workbench = workbench ?: return

        if(mainActivity is MainActivity) {

            mainActivity.getItemHomeTab(TAB_ID)?.let {
                it.setTitle(workbench.getNameI18n(AtworkApplicationLike.baseContext))
            }
        }
    }

    private fun refreshNoticeData() {


        currentNoticeDataMappings.clear()


        currentDisplayCards
                .filter { it.supportNotice() && null != it.getNoticeDataUrlInfos() }
                .flatMap {
                    it.getNoticeDataUrlInfos().orEmpty().toList()
                }.forEach {
                    val noticeDataId = it.first
                    val noticeDataUrl = it.second
                    val noticeDataMapping = SimpleLightNoticeMapping.createInstance(noticeDataUrl, mId, noticeDataId)
                    currentNoticeDataMappings[noticeDataId] = noticeDataMapping

                }



        TabNoticeManager.getInstance().unregisterTab(mId)
        currentNoticeDataMappings.forEach {
            TabNoticeManager.getInstance().registerLightNoticeMapping(it.value)
        }


        loadCardNoticeData()

    }


    private fun loadCardNoticeData() {

        TabNoticeManager.getInstance().update(mId)

        currentNoticeDataMappings.forEach { entry ->

            val noticeMapping = entry.value

            NoticeManager.queryNoticeData(entry.key, noticeMapping.noticeUrl) { noticeData ->

                if (null != noticeData) {
                    if (TabNoticeManager.getInstance().checkLightNoticeUpdate(noticeMapping.appId, noticeData)) {

                        TabNoticeManager.getInstance().update(noticeMapping, noticeData)
                        refreshCardViewsLightly()
                    }
                }
            }
        }
    }

    private fun refreshAdvertisements(advertisements: java.util.ArrayList<AdvertisementConfig>) {
        advertisements.forEach {
            val advertisementBannerId = LongUtil.parseLong(it.mSerialNo)
            currentDisplayCards
                    .indexOfFirst { it.id == advertisementBannerId }
                    .takeIf { -1 < it }
                    ?.let { cardViewsAdapter.notifyItemChanged(it) }


        }
    }

    private fun refreshCommonUsedApp() {
        currentDisplayCards
                .indexOfFirst { it is WorkbenchCommonAppCard }
                .takeIf { -1 < it }
                ?.let { cardViewsAdapter.notifyItemChanged(it) }
    }


    private fun refreshFakeStatusBar() {
        fakeStatusBar?.let {

            if (isBannerTop()) {

                if(0 != (swipeLayout.layoutParams as RelativeLayout.LayoutParams).rules[RelativeLayout.BELOW]) {
                    swipeLayout.updateLayoutParams<RelativeLayout.LayoutParams> {
                        this.removeRule(RelativeLayout.BELOW)
                    }

                }


                if(0 == layoutManager.findFirstVisibleItemPosition()) {
                    it.visibility = View.GONE
                } else {
                    it.visibility = View.VISIBLE

                }


            } else {


                if(0 == (swipeLayout.layoutParams as RelativeLayout.LayoutParams).rules[RelativeLayout.BELOW]) {
                    swipeLayout.updateLayoutParams<RelativeLayout.LayoutParams> {
                        this.addRule(RelativeLayout.BELOW, R.id.vFakeStatusbar)
                    }

                }

                if(!it.isVisible) {
                    handleFakeStatusBar()
                }
            }
        }


    }

    private fun isBannerTop() =
            0 < currentDisplayCards.size
                    && currentDisplayCards[0] is WorkbenchBannerCard
                    && (currentDisplayCards[0] as WorkbenchBannerCard).shouldVisible()

    private fun haveAdminCard() = workbench?.workbenchCards?.any { it is WorkbenchAdminCard }?: false


    private fun refreshCardViewsLightly() {
        if (::cardViewsAdapter.isInitialized) {
            cardViewsAdapter.notifyDataSetChanged()

            if(haveAdminCard()) {
                footerWorkbenchMoreSolutionsView.isVisible = true
                vWorkbenchMoreSolutionsInEmptyCardsView.isVisible = true
            }
        }
    }


    fun clear() {
        workbench = null
        currentDisplayCards.clear()
    }


    private fun registerListener() {


        ivWorkbenchGuide.setOnClickListener {
            ivWorkbenchGuide.visibility = View.GONE
        }

        tvCustomSort.setOnClickListener {
            activity?.let {
                val intent = WorkbenchCustomSortCardActivity.getIntent(it, mode)
                startActivity(intent)
            }
        }


        tvCustomSortInEmptyCardsView.setOnClickListener {
            activity?.let {
                val intent = WorkbenchCustomSortCardActivity.getIntent(it, mode)
                startActivity(intent)
            }
        }



        rvCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                refreshFakeStatusBar()

                if(isBannerTop()) {
                    LogUtil.e("onScrolled  hide fake status bar")

                } else {
                    LogUtil.e("onScrolled  show fake status bar")
                }
            }
        })

        vWorkbenchMoreSolutionsInEmptyCardsView.setOnClickListener {
            routeMoreSolutions()
        }

        footerWorkbenchMoreSolutionsView.setOnClickListener {
            routeMoreSolutions()
        }
    }


    private fun routeMoreSolutions() {
        val url = String.format(UrlConstantManager.getInstance().appStoreUrl, PersonalShareInfo.getInstance().getCurrentOrg(mActivity), LoginUserInfo.getInstance().getLoginUserId(mActivity), true)

        val webViewControlAction = WebViewControlAction.newAction().setUrl(url)
        val intent = WebViewActivity.getIntent(mActivity, webViewControlAction)
        startActivity(intent)
    }

    private fun registerBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(IWorkbenchCardContentManager.ACTION_REFRESH_WORKBENCH_BANNER_ADVERTISEMENTS)
        intentFilter.addAction(IWorkbenchManager.ACTION_REFRESH_WORKBENCH)
        intentFilter.addAction(IWorkbenchManager.ACTION_REFRESH_ADMIN_PREVIEW_WORKBENCH)
        intentFilter.addAction(AppRefreshHelper.ACTION_REFRESH_APP)
        intentFilter.addAction(AppRefreshHelper.ACTION_REFRESH_APP_LIGHTLY)
        intentFilter.addAction(IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST)
        intentFilter.addAction(IWorkbenchAdminManager.ACTION_REFRESH_UPDATE_CARD)
        intentFilter.addAction(IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST_TOTALLY)

        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).registerReceiver(receiver, intentFilter)
    }

    private fun unregisterBroadcast() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(receiver)

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        LogUtil.e("onRefresh(refreshLayout: RefreshLayout) ")

        if (MODE_ADMIN_PREVIEW == mode) {
            onRefreshInAdminPreviewMode()
            return
        }


        onRefreshInUserMode()


    }

    private fun onRefreshInAdminPreviewMode() {
        WorkbenchCardContentManager.clearRequestRecord()

        val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext)
        WorkbenchAdminManager.queryWorkbench(
                context = AtworkApplicationLike.baseContext,
                orgCode = currentOrgCode,
                widgetId = WorkbenchAdminManager.getCurrentAdminWorkbench(AtworkApplicationLike.baseContext).toString(),
                listener = object : BaseNetWorkListener<QueryWorkbenchAdminMultiResult> {
                    override fun onSuccess(multiResult: QueryWorkbenchAdminMultiResult) {

                        progressHelperDialog.dismiss()
//                        workbenchData = multiResult.originalWorkbenchData
//
//                        cardViewsAdapter.workbenchData = workbenchData
//
//                        displayCards.clear()
//                        displayCards.addAll(multiResult.localResult.workbenchCards)
//
//                        refreshCardViewsLightly()


                        refreshWorkbenchDataInAdminPreviewMode(multiResult.originalWorkbenchData)
                        swipeLayout?.finishRefresh()


                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressHelperDialog.dismiss()

                        swipeLayout.finishRefresh()


                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }

                }
        )

    }

    private fun onRefreshInUserMode() {
        WorkbenchCardContentManager.clearRequestRecord()

        WorkbenchManager.checkWorkbenchRemote(requestImmediately = true) {

            swipeLayout?.finishRefresh()

            //            swipeLayout?.let {
            //                //                it.isRefreshing = false
            //                it.finishRefresh()
            //            }

            refreshCardViewsLightly()
        }
    }


    companion object {
        const val TAB_ID = "workbench"

        const val MODE_USER = 0
        const val MODE_ADMIN_PREVIEW = 1
    }

}