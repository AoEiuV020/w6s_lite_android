package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchBannerCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchBannerCardContent
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.app.component.AdvertisementBannerCardView
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminBannerCardBannerListActivity
import com.foreveross.atwork.modules.workbench.manager.WorkbenchCardContentManager

class WorkbenchBannerCardView: AdvertisementBannerCardView, IWorkbenchCardRefreshView<WorkbenchBannerCard> {

    override lateinit var workbenchCard: WorkbenchBannerCard

    var workbenchData: WorkbenchData? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun registerListener() {
        super.registerListener()

        getTvAdminSetBanner().setOnClickListener {
            val workbenchData = workbenchData?:return@setOnClickListener
            val workbenchCardDetailData = workbenchCard.originalWorkbenchCardDetailData?: return@setOnClickListener

            val intent = WorkbenchAdminBannerCardBannerListActivity.getIntent(context, workbenchData, workbenchCardDetailData)
            context.startActivity(intent)
        }
    }

    override fun startAutoScroll(interval: Long?) {

        if(null == interval) {
            return
        }


        if (1 >= appToAdapter.getRealCount()) {
            return
        }

        vpAutoScroll.interval = interval
        vpAutoScroll.startAutoScroll()


    }

    override fun refresh(workbenchCard: WorkbenchBannerCard) {

        this.workbenchCard = workbenchCard

        refreshView(workbenchCard)


    }

    override fun refreshView(workbenchCard: WorkbenchBannerCard) {

        if(workbenchCard.adminDisplay) {
            refreshAdminTypeView(workbenchCard)
        } else {
            refreshUserTypeView(workbenchCard)

            val bannerRecord = this.workbenchCard.getCardContent<WorkbenchBannerCardContent>()?.records

            bannerRecord?.let {
                WorkbenchCardContentManager.checkBannerMediaDownloadSilently(it)

            }

        }
    }

    override fun refreshWorkbenchData(workbenchData: WorkbenchData) {
        this.workbenchData = workbenchData
    }

    private fun refreshAdminTypeView(workbenchCard: WorkbenchBannerCard) {
        handleBannerHeight(workbenchCard)

        val bannerRecord = workbenchCard.getCardContent<WorkbenchBannerCardContent>()?.records
        val bannerRecordLegal = bannerRecord
                ?.filter { it.isValidDuration }
                ?.toMutableList()
                ?: ArrayList()

        if(ListUtil.isEmpty(bannerRecordLegal)) {
            val element = AdvertisementConfig().apply {
                mId = workbenchCard.id.toString()
                mDefaultHolderImg = R.mipmap.workbench_banner_card_mock_preview
            }


            bannerRecordLegal.add(element)
        }

        refreshAdvertisementData(bannerRecordLegal, workbenchCard.intervalSeconds?.times(1000))

        showAdminSetBannerView()
        show()

    }

    private fun refreshUserTypeView(workbenchCard: WorkbenchBannerCard) {
        val bannerRecordLegal = this.workbenchCard.getBannerRecordsLegal()

        if (ListUtil.isEmpty(bannerRecordLegal)) {
            hide()
            return
        }

        handleBannerHeight(workbenchCard)

        refreshAdvertisementData(bannerRecordLegal!!, workbenchCard.intervalSeconds?.times(1000))
        show()
    }

    private fun handleBannerHeight(workbenchCard: WorkbenchBannerCard) {
        if (!TextUtils.isEmpty(workbenchCard.bannerHeights) && Integer.parseInt(workbenchCard.bannerHeights!!) > 0) {
            settingBannerHeight(Integer.parseInt(workbenchCard.bannerHeights!!))
        } else {
            settingBannerHeight(210)
        }
    }


    override fun getViewType(): Int = WorkbenchCardType.BANNER.hashCode()
}