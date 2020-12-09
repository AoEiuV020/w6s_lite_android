package com.foreveross.atwork.modules.app.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementKind
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementOpsType
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.advertisement.manager.AdvertisementManager
import com.foreveross.atwork.modules.app.adapter.AppTopAdvertsAdapter

open class AdvertisementBannerCardView : FrameLayout {


    protected lateinit var vpAutoScroll: AutoScrollViewPager
    private lateinit var llGalleryPoint: LinearLayout
    private lateinit var tvAdminSetBanner: TextView
    private lateinit var vTitleBar: RelativeLayout
    private var ivDotList = ArrayList<ImageView>()

    protected lateinit var appToAdapter: AppTopAdvertsAdapter
    private var advertisementConfigList = ArrayList<AdvertisementConfig>()

    var mode = Mode.APP

    var isPlaying = false
        private set

    constructor(context: Context) : super(context) {
        findViews(context)
        initViews()
        registerListener()

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
        initViews()
        registerListener()

    }

    private fun findViews(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.component_app_top_adverts, this)
        vpAutoScroll = view.findViewById(R.id.vp_auto_scroll)
        llGalleryPoint = view.findViewById(R.id.ll_gallery_point)
        vTitleBar = view.findViewById(R.id.app_title_bar)
        tvAdminSetBanner = view.findViewById(R.id.tv_set_banner)


    }

    fun settingBannerHeight(height: Int){
        val params = vpAutoScroll.layoutParams as RelativeLayout.LayoutParams
        params.height = DensityUtil.dip2px(height.toFloat())
        vpAutoScroll.layoutParams = params
        appToAdapter.changeBannerHeight(height)
    }

    fun  refreshAdvertisementData(advertisementConfigList: List<AdvertisementConfig>, interval: Long?) {
        if (this.advertisementConfigList == advertisementConfigList
                && (null != interval && vpAutoScroll.interval == interval)) {

            return
        }


        stopAutoScroll()

//        vpAutoScroll.adapter
        this.advertisementConfigList.clear()
        this.advertisementConfigList.addAll(advertisementConfigList)

        drawSliderPoint()

        appToAdapter.notifyDataSetChanged()

        if (1 >= appToAdapter.getRealCount()) {
            stopAutoScroll()

        } else {
            startAutoScroll(interval)

        }

        if (!advertisementConfigList.isEmpty()) {
            goToCenter(0)
        }
    }


    open protected fun registerListener() {

        vpAutoScroll.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            var lastSetCurrentTime = -1L
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                LogUtil.e("onPageScrolled  position: $position positionOffset: $positionOffset  positionOffsetPixels: $positionOffsetPixels   ")

                val currentTime = System.currentTimeMillis()
                if (3000 > currentTime - lastSetCurrentTime) {
                    return
                }


                if (0 == position && 0f == positionOffset) {
                    lastSetCurrentTime = currentTime

                    goToCenter(position)


                }

                if (appToAdapter.count - 1 == position && 0f == positionOffset) {
                    lastSetCurrentTime = currentTime

                    goToCenter(position)


                }
            }

            override fun onPageSelected(position: Int) {
                val realPosition = appToAdapter.getRealPosition(position)
                setSelectedDot(realPosition)


                val advertisementConfig = advertisementConfigList[realPosition]
                if (!StringUtils.isEmpty(advertisementConfig.mMediaId)) {
                    val advertisementEvent = advertisementConfig.toAdvertisementEvent(AdvertisementOpsType.Display)
                    AdvertisementManager.updateAppTopBannerStatistics(advertisementConfig.mMediaId, advertisementEvent)
                }
            }

        })
    }

    private fun goToCenter(position: Int) {
        vpAutoScroll.setCurrentItem(appToAdapter.count / 2 - appToAdapter.count / 2 % appToAdapter.getRealCount() + appToAdapter.getRealPosition(position), false)
    }

    private fun initViews() {

        appToAdapter = AppTopAdvertsAdapter(context, advertisementConfigList)
        vpAutoScroll.adapter = appToAdapter

    }

    fun startAutoScrollAppBanner() {
        val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext)
        startAutoScroll(OrganizationSettingsManager.getInstance().getAppTopBannerIntervalSeconds(currentOrgCode) * 1000)
    }

    open fun startAutoScroll(interval: Long?) {

        if(null == interval) {
            return
        }

        val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext)
        if (!OrganizationSettingsManager.getInstance().isAppTopBannerNeedAutoScroll(currentOrgCode)) {
            return
        }

        if (1 >= appToAdapter.getRealCount()) {
            return
        }

        vpAutoScroll.interval = interval

        vpAutoScroll.startAutoScroll()
    }

    fun stopAutoScroll() {
        vpAutoScroll.stopAutoScroll()
    }

    private fun drawSliderPoint() {
        llGalleryPoint.removeAllViews()
        ivDotList.clear()
        for (i in 0 until appToAdapter.getRealCount()) {

            val params = LinearLayout.LayoutParams(
                    15, 15)

            val pointView = ImageView(context)
            // 获取
            val pixels = DensityUtil.dip2px(8f)
            params.leftMargin = pixels
            if (1 == appToAdapter.getRealCount()) {
                pointView.visibility = View.INVISIBLE
            }

            ivDotList.add(pointView)
            val realCurrentItemPosition = appToAdapter.getRealPosition(vpAutoScroll.currentItem)
            if (i == realCurrentItemPosition) {
                pointView.setImageResource(R.drawable.shape_app_top_advert_blue)
            } else {
                pointView.setImageResource(R.drawable.shape_app_top_advert_gray)
            }

            llGalleryPoint.addView(pointView, params)
        }
    }

    private fun setSelectedDot(position: Int) {
        for (i in ivDotList.indices) {
            val dot = ivDotList[i]
            if (i == position) {
                ivDotList[position].setImageResource(R.drawable.shape_app_top_advert_blue)
            } else {
                dot.setImageResource(R.drawable.shape_app_top_advert_gray)
            }
        }
    }

    fun getTvAdminSetBanner(): TextView = tvAdminSetBanner

    fun showAdminSetBannerView() {
        tvAdminSetBanner.isVisible = true
    }

    fun hideAdminSetBannerView() {
        tvAdminSetBanner.isVisible = false
    }

    fun show() {
        vpAutoScroll.visibility = View.VISIBLE
        llGalleryPoint.visibility = View.VISIBLE

        if (Mode.APP == mode) {
            vTitleBar.visibility = View.VISIBLE
        }

        isPlaying = true
    }

    fun hide() {
        vpAutoScroll.visibility = View.GONE
        llGalleryPoint.visibility = View.GONE
        vTitleBar.visibility = View.GONE

        isPlaying = false
    }


    fun refreshAppBanner () {
        val advertisementKind = AdvertisementKind.APP_BANNER
        AdvertisementManager.getCurrentOrgAdvertisementsLegal(advertisementKind).let {
            if (it.isEmpty()) {
                hide()

            } else {
                val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext)
                refreshAdvertisementData(it, OrganizationSettingsManager.getInstance().getAppTopBannerIntervalSeconds(currentOrgCode) * 1000)
                show()
            }
        }

    }



    enum class Mode {

        APP,

        WORKBENCH
    }


}