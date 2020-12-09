package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchNews3Card
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchContentEventType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListContent
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchNews3Adapter
import com.foreveross.atwork.modules.workbench.component.skeleton.WorkbenchNews3SliderCardSkeletonView
import com.foreveross.atwork.modules.workbench.manager.WorkbenchCardContentManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import kotlinx.android.synthetic.main.component_workbench_news_card_3.view.*

class WorkbenchNews3CardView : WorkbenchBasicCardView<WorkbenchNews3Card> {

    private val itemList = arrayListOf<WorkbenchListItem>()
    private lateinit var adapter: WorkbenchNews3Adapter

    override lateinit var workbenchCard: WorkbenchNews3Card

    var skeletonView: WorkbenchNews3SliderCardSkeletonView? = null

    constructor(context: Context) : super(context) {
        initViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
        registerListener()
    }

    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_news_card_3, this)
    }


    private fun registerListener() {
        adapter.setOnItemClickListener { adapter, view, position ->

            val workbenchListItem = itemList[position]
            val eventType = WorkbenchContentEventType.parse(workbenchListItem.eventType)

            workbenchListItem.eventValue?.let {
                WorkbenchManager.route(context, this.workbenchCard, eventType, eventValue = it)

            }
        }
    }



    private fun initViews() {


        adapter = WorkbenchNews3Adapter(itemList)
        rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvList.adapter = adapter

        rvList.addItemDecoration(WorkbenchNews3ItemDecoration())
    }


    override fun refresh(workbenchCard: WorkbenchNews3Card) {
        this.workbenchCard = workbenchCard

        //refresh local firstly
        refreshView(workbenchCard)


        refreshContentRemote()
    }


    override fun refreshContentRemote(immediate: Boolean) {

        val requested = WorkbenchCardContentManager.requestListContent(workbenchCard) { cardIdRequest, cardContentResult ->


            if (cardIdRequest != workbenchCard.id) {
                return@requestListContent
            }

            if(null == cardContentResult) {
                showFail()
                return@requestListContent
            }


            workbenchCard.workbenchCardContent = cardContentResult

            refreshView(workbenchCard)
        }

        if(requested) {
            showLoading()
        }
    }


    override fun refreshView(workbenchCard: WorkbenchNews3Card) {
        vWorkbenchCommonTitleView.refreshView(workbenchCard)

        if(workbenchCard.isContentDataEmpty()) {
            showEmpty()
            return
        }

        restoreHolderView()

        val sliderListItems = workbenchCard.getCardContent<WorkbenchListContent>()?.itemList

        if (null == sliderListItems) {
            return
        }



        var cutEndIndex = workbenchCard.listCount
        if (cutEndIndex > sliderListItems.size) {
            cutEndIndex = sliderListItems.size
        }

        itemList.clear()
        itemList.addAll(sliderListItems.subList(0, cutEndIndex))


        adapter.notifyDataSetChanged()
    }

    override fun skeletonView(): View? {
        if(null == skeletonView) {
            skeletonView= WorkbenchNews3SliderCardSkeletonView(context)
        }

        return skeletonView
    }

    override fun contentView(): View? = rvList

    override fun getViewType(): Int = WorkbenchCardType.NEWS_3.hashCode()


}