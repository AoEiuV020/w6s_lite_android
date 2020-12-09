package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchGridType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchShortcut1Card
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardContent
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping
import com.foreveross.atwork.modules.main.data.TabNoticeManager
import com.foreveross.atwork.modules.main.helper.TabHelper
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchShortcut1Adapter
import com.foreveross.atwork.modules.workbench.component.skeleton.WorkbenchShortcutCardSkeletonView
import com.foreveross.atwork.modules.workbench.manager.WorkbenchCardContentManager
import kotlinx.android.synthetic.main.component_workbench_shortcut_card_1.view.*
import java.util.*
import kotlin.math.ceil

class WorkbenchShortcutCard1View : WorkbenchBasicCardView<WorkbenchShortcut1Card> {


    override lateinit var workbenchCard: WorkbenchShortcut1Card

    private lateinit var shortcut1Adapter: WorkbenchShortcut1Adapter

    private val shortcutCardGroupList: ArrayList<List<WorkbenchShortcutCardItem>> = arrayListOf()

    private var ivDotList = ArrayList<ImageView>()

    var skeletonView: WorkbenchShortcutCardSkeletonView? = null


    constructor(context: Context) : super(context) {
        initViews(context)
        registerListener()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initViews(context)
        registerListener()
    }


    fun initViews(context: Context) {
        shortcut1Adapter = WorkbenchShortcut1Adapter(context, shortcutCardGroupList)
        vpCardItems.adapter = shortcut1Adapter
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


    override fun refresh(workbenchCard: WorkbenchShortcut1Card) {
        this.workbenchCard = workbenchCard
        shortcut1Adapter.workbenchCard = workbenchCard

        //refresh local firstly
        refreshView(workbenchCard)

        refreshContentRemote()
    }


    override fun refreshContentRemote(immediate: Boolean) {


        val requested = WorkbenchCardContentManager.requestShortcutCardContent(workbenchCard) { cardIdRequest, cardContentResult ->


            if (cardIdRequest != workbenchCard.id) {
                return@requestShortcutCardContent
            }

            if (null == cardContentResult) {
                showFail()
                return@requestShortcutCardContent
            }


            val oldNoticeInfoMap = workbenchCard.getNoticeDataUrlInfos()
            workbenchCard.workbenchCardContent = cardContentResult

            val newNoticeInfoMap = workbenchCard.getNoticeDataUrlInfos()

            checkNoticeDataMapping(oldNoticeInfoMap, newNoticeInfoMap)

            refreshView(workbenchCard)

        }


        if (requested) {
            showLoading()
        }
    }

    private fun checkNoticeDataMapping(oldNoticeInfoMap: Map<String, String>?, newNoticeInfoMap: Map<String, String>?) {

        //notice url changed
        if (oldNoticeInfoMap != newNoticeInfoMap) {
            oldNoticeInfoMap?.forEach {
                TabNoticeManager.getInstance().unregisterLightNotice(TabHelper.getWorkbenchFragmentId(), it.key)
            }

            newNoticeInfoMap?.forEach {
                val noticeDataMapping = SimpleLightNoticeMapping.createInstance(it.value, TabHelper.getWorkbenchFragmentId(), it.key)
                TabNoticeManager.getInstance().registerLightNoticeMapping(noticeDataMapping)
            }

            TabNoticeManager.getInstance().update(TabHelper.getWorkbenchFragmentId())

        }
    }


    override fun refreshView(workbenchCard: WorkbenchShortcut1Card) {
        vWorkbenchCommonTitleView.refreshView(workbenchCard)

        if(workbenchCard.isContentDataEmpty()) {
            showEmpty()
            return
        }


        restoreHolderView()


        val shortcutItems = workbenchCard.getCardContent<WorkbenchShortcutCardContent>()?.itemList

        if (null == shortcutItems) {
            return
        }

        shortcutCardGroupList.clear()

        refreshShortCutGroupItems(workbenchCard, shortcutItems)

        shortcut1Adapter.notifyDataSetChanged()

        drawSliderPoint()
    }

    private fun refreshShortCutGroupItems(workbenchCard: WorkbenchShortcut1Card, shortcutItems: List<WorkbenchShortcutCardItem>) {
        val perPageItemCount = getPerPageItemCount(workbenchCard)

        val pageCount = ceil((shortcutItems.size / perPageItemCount.toDouble())).toInt()


        for (i in 0 until pageCount) {
            val startIndex = i * perPageItemCount
            var endIndex = startIndex + perPageItemCount
            if (endIndex > shortcutItems.size) {
                endIndex = shortcutItems.size
            }
            val perPageItems = shortcutItems.subList(startIndex, endIndex)
            shortcutCardGroupList.add(perPageItems)
        }
    }

    private fun getPerPageItemCount(workbenchCard: WorkbenchShortcut1Card): Int {
        val perPageItemCount = when (workbenchCard.shortCut1Type) {
            WorkbenchGridType.TYPE_2_4 -> 8
            WorkbenchGridType.TYPE_1_4 -> 4
            else -> 8
        }
        return perPageItemCount
    }

    private fun drawSliderPoint() {
        llGalleryPoint.removeAllViews()
        ivDotList.clear()
        for (i in 0 until shortcut1Adapter.count) {

            val params = LinearLayout.LayoutParams(
                    15, 15)

            val pointView = ImageView(context)
            // 获取
            val pixels = DensityUtil.dip2px(8f)
            params.leftMargin = pixels
            if (1 == shortcut1Adapter.count) {
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
        LayoutInflater.from(context).inflate(R.layout.component_workbench_shortcut_card_1, this)
    }

    override fun skeletonView(): View? {


        if (null == skeletonView) {
            skeletonView = WorkbenchShortcutCardSkeletonView(context).apply {
                refresh(WorkbenchCardType.SHORTCUT_1, getPerPageItemCount(workbenchCard))
            }
        }


        return skeletonView
    }

    override fun contentView(): View? = rlContent

    override fun getViewType(): Int = WorkbenchCardType.SHORTCUT_1.hashCode()

}