package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchListTypeCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchContentEventType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListContent
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchList1Adapter
import com.foreveross.atwork.modules.workbench.component.skeleton.WorkbenchList1CardSkeletonView
import com.foreveross.atwork.modules.workbench.manager.WorkbenchCardContentManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import kotlinx.android.synthetic.main.component_workbench_list_card_0.view.*

class WorkbenchListCard1View : WorkbenchBasicCardView<WorkbenchListTypeCard> {


    private lateinit var adapter: WorkbenchList1Adapter
    private val itemList = arrayListOf<WorkbenchListItem>()

    var skeletonView: WorkbenchList1CardSkeletonView? = null


    constructor(context: Context) : super(context) {
        initViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
        registerListener()

    }

    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_list_card_1, this)

    }

    private fun initViews() {
        adapter = WorkbenchList1Adapter(itemList)
        rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvList.adapter = adapter
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

    override lateinit var workbenchCard: WorkbenchListTypeCard

    override fun refresh(workbenchCard: WorkbenchListTypeCard) {
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


    override fun refreshView(workbenchCard: WorkbenchListTypeCard) {
        vWorkbenchCommonTitleView.refreshView(workbenchCard)

        if(workbenchCard.isContentDataEmpty()) {
            showEmpty()
            return
        }

        restoreHolderView()


        val listItems = workbenchCard.getCardContent<WorkbenchListContent>()?.itemList

        if (null == listItems) {
            return
        }


        var cutEndIndex = workbenchCard.listCount
        if (cutEndIndex > listItems.size) {
            cutEndIndex = listItems.size
        }

        itemList.clear()
        itemList.addAll(listItems.subList(0, cutEndIndex))
        adapter.notifyDataSetChanged()

    }

    override fun skeletonView(): View? {

        if(null == skeletonView) {
            skeletonView = WorkbenchList1CardSkeletonView(context)
        }
        return skeletonView
    }

    override fun contentView(): View? = rvList

    override fun getViewType(): Int = WorkbenchCardType.LIST_1.hashCode()


}