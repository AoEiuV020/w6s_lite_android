package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchShortcut0Card
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping
import com.foreveross.atwork.modules.main.data.TabNoticeManager
import com.foreveross.atwork.modules.main.helper.TabHelper
import com.foreveross.atwork.modules.workbench.component.skeleton.WorkbenchShortcutCardSkeletonView
import com.foreveross.atwork.modules.workbench.manager.WorkbenchCardContentManager
import kotlinx.android.synthetic.main.component_workbench_shortcut_card_0.view.*

class WorkbenchShortcutCard0View : WorkbenchBasicCardView<WorkbenchShortcut0Card> {


    override lateinit var workbenchCard: WorkbenchShortcut0Card

    private val cardItemViews: MutableList<WorkbenchShortcutCardItemView> = ArrayList()

    var skeletonView: WorkbenchShortcutCardSkeletonView? = null

    constructor(context: Context) : super(context) {

        initCardItemCards()

        registerListener()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        registerListener()
        initCardItemCards()

    }


    private fun initCardItemCards() {
        cardItemViews.add(vWorkbenchShortcutCardItemView0)
        cardItemViews.add(vWorkbenchShortcutCardItemView1)
        cardItemViews.add(vWorkbenchShortcutCardItemView2)
        cardItemViews.add(vWorkbenchShortcutCardItemView3)
        cardItemViews.add(vWorkbenchShortcutCardItemViewMore)
    }


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_shortcut_card_0, this)

    }

    override fun contentView(): View? = llContent

    override fun skeletonView(): View? {

        if (null == skeletonView) {

            skeletonView = WorkbenchShortcutCardSkeletonView(context).apply {
                refresh(WorkbenchCardType.SHORTCUT_0, workbenchCard.entryCount)
            }
        }


        return skeletonView
    }


    private fun registerListener() {

    }


    override fun refresh(workbenchCard: WorkbenchShortcut0Card) {

        this.workbenchCard = workbenchCard

        //refresh local firstly
        refreshView(workbenchCard)

        refreshContentRemote()
    }

    override fun refreshContentRemote(immediate: Boolean) {


        val requested = WorkbenchCardContentManager.requestShortcutCardContent(workbenchCard, immediate) { cardIdRequest, cardContentResult ->

            LogUtil.e("restoreHolderView~~~~~~~~~~ requestShortcutCardContent back")


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

    override fun refreshView(workbenchCard: WorkbenchShortcut0Card) {
        if(workbenchCard.isContentDataEmpty()) {
            showEmpty()
            return
        }


        restoreHolderView()


        handleCardViewsVisibility(workbenchCard)

        val itemList: List<WorkbenchShortcutCardItem>? = workbenchCard.getShowItemList()

        //refresh the "more" item
        val lastItem = itemList?.lastOrNull()
        if (null == lastItem) {
            rlWorkbenchShortcutCardItemViewMore.visibility = View.INVISIBLE

        } else {
            rlWorkbenchShortcutCardItemViewMore.visibility = View.VISIBLE
            vWorkbenchShortcutCardItemViewMore.refresh(lastItem)
            vWorkbenchShortcutCardItemViewMore.setOnClickListener { vWorkbenchShortcutCardItemViewMore.clickAction(this.workbenchCard, lastItem) }

        }


        var itemListNoLast: ArrayList<WorkbenchShortcutCardItem>? = null

        if (!ListUtil.isEmpty(itemList)) {
            itemListNoLast = ArrayList(itemList!!)
            itemListNoLast.remove(itemListNoLast.last())
        }


        cardItemViews
                .filter { View.VISIBLE == it.visibility && it !== vWorkbenchShortcutCardItemViewMore }
                .forEachIndexed { index, workbenchShortcutCardItemView ->
                    val workbenchShortcutCardItem = itemListNoLast?.getOrNull(index)
                    workbenchShortcutCardItemView.refresh(workbenchShortcutCardItem)

                    workbenchShortcutCardItem?.let {shortcutCardItem ->
                        workbenchShortcutCardItemView.setOnClickListener { vWorkbenchShortcutCardItemViewMore.clickAction(this.workbenchCard, shortcutCardItem) }
                    }


                }


    }


    private fun handleCardViewsVisibility(workbenchCard: WorkbenchShortcut0Card) {
        val goneRangeMax = cardItemViews.size - workbenchCard.entryCount - 1
        val goneRange = 0..goneRangeMax


        for (i in cardItemViews.indices) {

            if (i in goneRange) {
                cardItemViews[i].visibility = View.GONE
            } else {

                cardItemViews[i].visibility = View.VISIBLE

            }

        }
    }


    override fun getViewType(): Int = WorkbenchCardType.SHORTCUT_0.hashCode()

}