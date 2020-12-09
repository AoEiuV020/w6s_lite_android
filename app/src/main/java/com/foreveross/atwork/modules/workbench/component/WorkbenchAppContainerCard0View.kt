package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchAppContainer0Card
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchGridType
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchAppContainer0Adapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.modules.workbench.model.WorkbenchAppContainerItemDataWrapper
import kotlinx.android.synthetic.main.component_workbench_app_container_card_0.view.*
import java.util.*
import kotlin.math.ceil

class WorkbenchAppContainerCard0View: WorkbenchBasicCardView<WorkbenchAppContainer0Card> {

    override lateinit var workbenchCard: WorkbenchAppContainer0Card

    private lateinit var adapter: WorkbenchAppContainer0Adapter

    private val appContainer0CardWrapperGroupList: ArrayList<List<WorkbenchAppContainerItemDataWrapper>> = arrayListOf()

    private var ivDotList = ArrayList<ImageView>()

    constructor(context: Context) : super(context) {
        initViews()
        registerListener()
        vpCardItems.adapter = adapter

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
        registerListener()
        vpCardItems.adapter = adapter

    }

    private fun initViews() {
        adapter = WorkbenchAppContainer0Adapter(context, appContainer0CardWrapperGroupList)
    }

    private fun registerListener() {


        vpCardItems.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                setSelectedDot(position)
            }

        })

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


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_app_container_card_0, this)
    }

    override fun refresh(workbenchCard: WorkbenchAppContainer0Card) {
        this.workbenchCard = workbenchCard

        refreshView(workbenchCard)
    }

    override fun refreshView(workbenchCard: WorkbenchAppContainer0Card) {
        vWorkbenchCommonTitleView.refreshView(workbenchCard)

        if (workbenchCard.adminDisplay) {
            refreshViewAdminMode(workbenchCard)
        } else {
            refreshViewUserMode(workbenchCard)

        }
    }

    private fun refreshViewUserMode(workbenchCard: WorkbenchAppContainer0Card) {
        val appBundlesShouldDisplay = getAppBundlesShouldDisplay()

//        val appBundlesShouldDisplay = AppManager.getInstance().appBundleList.filter { appBundleData ->
//            appBundlesInAppContainer.any { it.id == appBundleData.id }
//        }

        val appContainerItemDataWrapperList = appBundlesShouldDisplay.map { WorkbenchAppContainerItemDataWrapper(appBundle = it) }.toMutableList()
        refreshGroupItems(workbenchCard, appContainerItemDataWrapperList)
        refreshUI()

        drawSliderPoint()
    }

    private fun getAppBundlesShouldDisplay(): ArrayList<AppBundles> {
        return WorkbenchManager.getAppBundlesShouldDisplay(this.workbenchCard.appContainer)
    }

    private fun refreshViewAdminMode(workbenchCard: WorkbenchAppContainer0Card) {
        val appBundles = this.workbenchCard.appContainer
        val appContainerItemDataWrapperList = appBundles.map { WorkbenchAppContainerItemDataWrapper(appBundle = it) }.toMutableList()

        val countStandNum = when(workbenchCard.gridType) {
            WorkbenchGridType.TYPE_2_4 -> 8
            WorkbenchGridType.TYPE_1_4 -> 4
            else -> -1
        }

        //需要额外追加用来"占位", 显示"+"的数量(引导用户添加快捷卡片)
        val additionCount = if (-1 != countStandNum) {
            (appContainerItemDataWrapperList.size / countStandNum + 1) * countStandNum - appContainerItemDataWrapperList.size
        } else {
            1
        }

        for (i in 0 until additionCount) {
            appContainerItemDataWrapperList.add(WorkbenchAppContainerItemDataWrapper())
        }


        refreshGroupItems(workbenchCard, appContainerItemDataWrapperList)

        refreshUI()

        drawSliderPoint()
    }

    private fun refreshUI() {
        if(ListUtil.isEmpty(appContainer0CardWrapperGroupList)) {
            hide()
        } else {
            show()
        }

        adapter.workbenchCard = workbenchCard
        adapter.notifyDataSetChanged()
    }

    override fun getViewType(): Int  = WorkbenchCardType.APP_CONTAINER_0.hashCode()

    private fun refreshGroupItems(workbenchCard: WorkbenchAppContainer0Card, wrapperItems: List<WorkbenchAppContainerItemDataWrapper>) {
        appContainer0CardWrapperGroupList.clear()

        val perPageItemCount = getPerPageItemCount(workbenchCard)


        if (0 < perPageItemCount) {
            val morePageCount = ceil(((wrapperItems.size / perPageItemCount.toDouble()))).toInt()

            for (i in 0 until  morePageCount) {
                val startIndex = i * perPageItemCount
                var endIndex = startIndex + perPageItemCount
                if (endIndex > wrapperItems.size) {
                    endIndex = wrapperItems.size
                }
                val perPageItems = wrapperItems.subList(startIndex, endIndex)
                appContainer0CardWrapperGroupList.add(perPageItems)
            }
        } else {

            if (!ListUtil.isEmpty(wrapperItems)) {
                appContainer0CardWrapperGroupList.addAll(listOf(wrapperItems))
            }
        }
    }


    private fun getPerPageItemCount(workbenchCard: WorkbenchAppContainer0Card): Int {
        val perPageItemCount = when (workbenchCard.gridType) {
            WorkbenchGridType.TYPE_2_4 -> 8
            WorkbenchGridType.TYPE_1_4 -> 4
            else -> -1
        }
        return perPageItemCount
    }


    private fun drawSliderPoint() {
        llGalleryPoint.removeAllViews()
        ivDotList.clear()
        for (i in 0 until adapter.count) {

            val params = LinearLayout.LayoutParams(
                    15, 15)

            val pointView = ImageView(context)
            // 获取
            val pixels = DensityUtil.dip2px(8f)
            params.leftMargin = pixels
            if (1 == adapter.count) {
                pointView.visibility = View.INVISIBLE
            }

            ivDotList.add(pointView)
            if (i == vpCardItems.currentItem) {
                pointView.setImageResource(R.drawable.shape_app_top_advert_blue)
            } else {
                pointView.setImageResource(R.drawable.shape_app_top_advert_gray)
            }

            llGalleryPoint.addView(pointView, params)
        }


        if (1 >= ivDotList.size) {
            llGalleryPoint.visibility = View.GONE
        } else {
            llGalleryPoint.visibility = View.VISIBLE
        }
    }

    fun show() {
        rlRoot.visibility = View.VISIBLE
    }

    fun hide() {
        rlRoot.visibility = View.GONE
    }

}