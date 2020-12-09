package com.foreveross.atwork.modules.workbench.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.modules.app.adapter.AppTopAdvertsAdapter.Companion.displayBanner
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_workbench_admin_banner_card_banner_detail.*

class WorkbenchAdminBannerCardBannerDetailFragment : BackHandledPushOrPullFragment() {

    private lateinit var workbenchData: WorkbenchData
    private lateinit var workbenchCardDetailData: WorkbenchCardDetailData
    private lateinit var adminAdvertisementConfig: AdminAdvertisementConfig


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_banner_card_banner_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        registerListener()

        initViews()
    }

    override fun findViews(view: View) {

    }

    override fun getAnimationRoot(): View = swMainAll


    private fun initData() {
        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let {
            workbenchData = it
        }

        arguments?.getParcelable<WorkbenchCardDetailData>(WorkbenchCardDetailData::class.java.toString())?.let {
            workbenchCardDetailData = it
        }

        arguments?.getParcelable<AdminAdvertisementConfig>(AdminAdvertisementConfig::class.java.toString())?.let {
            adminAdvertisementConfig = it
        }
    }

    private fun initViews() {
        tvBannerName.text = adminAdvertisementConfig.name
        displayBanner(ivBannerImg, adminAdvertisementConfig.parse())
        tvBannerSort.text = adminAdvertisementConfig.sort.toString()
        tvBannerLink.text = adminAdvertisementConfig.linkUrl
        if (StringUtils.isEmpty(tvBannerLink.text.toString())) {
            tvBannerLink.text = getStrings(R.string.common_nothing)
        }

        val beginTimeDateStr = TimeUtil.getStringForMillis(adminAdvertisementConfig.beginTime, TimeUtil.getTimeFormat1(AtworkApplicationLike.baseContext))
        val endTimeDateStr = TimeUtil.getStringForMillis(adminAdvertisementConfig.endTime, TimeUtil.getTimeFormat1(AtworkApplicationLike.baseContext))
        tvBannerValidDuration.text = "$beginTimeDateStr ~$endTimeDateStr"
        swPutaway.isChecked = !adminAdvertisementConfig.disabled


    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        swPutaway.setOnClickNotPerformToggle {

            val checked = swPutaway.isChecked
            WorkbenchAdminManager.adminPutawayBannerItem(
                    context = AtworkApplicationLike.baseContext,
                    workbenchData = workbenchData,
                    widgetId = workbenchCardDetailData.id.toString(),
                    adminAdvertisementConfig = adminAdvertisementConfig,
                    putaway = !checked,
                    listener = object : BaseCallBackNetWorkListener {
                        override fun onSuccess() {
                            swPutaway.isChecked = !checked

                        }

                        override fun networkFail(errorCode: Int, errorMsg: String?) {
                            ErrorHandleUtil.handleError(errorCode, errorMsg)
                        }

                    }
            )

        }
    }


}