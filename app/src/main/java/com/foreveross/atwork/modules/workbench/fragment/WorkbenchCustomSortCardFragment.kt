package com.foreveross.atwork.modules.workbench.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.ui.component.recyclerview.RecyclerViewDragDropManager
import com.foreverht.workplus.ui.component.recyclerview.utils.AbstractDraggableItemViewHolder
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.*
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo
import com.foreveross.atwork.modules.workbench.activity.WorkbenchCustomSortCardActivity
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchCustomSortCardAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.modules.workbench.model.WorkbenchFilterCardsResult
import com.foreveross.atwork.modules.workbench.model.WorkbenchSortCardItem
import com.foreveross.atwork.support.BackHandledFragment

class WorkbenchCustomSortCardFragment : BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var rvCards: RecyclerView

    private lateinit var customSortWrappedAdapter: RecyclerView.Adapter<AbstractDraggableItemViewHolder>
    private lateinit var adapter: WorkbenchCustomSortCardAdapter

    private lateinit var cardAddedTitleItem: WorkbenchSortCardItem
    private lateinit var cardNotAddTitleItem: WorkbenchSortCardItem

    private lateinit var recyclerViewDragDropManager: RecyclerViewDragDropManager
    private var dataList: MutableList<WorkbenchSortCardItem> = ArrayList()

    private lateinit var cardsOriginalFilterResult: WorkbenchFilterCardsResult

    private lateinit var workbench: Workbench

    private var hasChanged = false

    private var mode = WorkbenchFragment.MODE_USER


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_custom_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
        registerListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadCards()
    }

    private fun initData() {
        arguments?.getInt(WorkbenchCustomSortCardActivity.DATA_MODE)?.let {
            mode = it
        }
    }

    private fun loadCards() {
        if (WorkbenchFragment.MODE_ADMIN_PREVIEW == mode) {
            loadCardInModeAdminPreview()
            return
        }

        loadCardInModeUser()

    }

    private fun loadCardInModeAdminPreview() {
        WorkbenchManager.getAdminPreviewModeWorkbench { workbench ->
            handleWorkbenchLoad(workbench)

        }
    }

    private fun loadCardInModeUser() {
        WorkbenchManager.getCurrentOrgWorkbench { workbench ->

            handleWorkbenchLoad(workbench)

        }
    }

    private fun handleWorkbenchLoad(workbench: Workbench?) {
        if (null == workbench) {
            return
        }

        this.workbench = workbench

        val filterResult = WorkbenchManager.filterDisplayAndSored(workbench)

        cardsOriginalFilterResult = filterResult


        shuffleData()
    }

    private fun shuffleData() {
        dataList.clear()

        cardsOriginalFilterResult.cardsDisplay
                .filter { !isInVisible(it) }
                .forEach { itemCard ->
                    dataList.add(WorkbenchSortCardItem(card = itemCard, cardDisplay = true))
                }


        cardsOriginalFilterResult.cardsNotDisPlay
                .filter { !isInVisible(it) }
                .forEach { itemCard ->
                    dataList.add(WorkbenchSortCardItem(card = itemCard, cardDisplay = false))
                }


        assembleDataList()

        adapter.notifyDataSetChanged()
    }

    private fun isInVisible(card: WorkbenchCard): Boolean {
        when (card) {
            is WorkbenchBannerCard -> {
                return !card.shouldVisible()
            }

            is WorkbenchAppContainer0Card -> {
                return WorkbenchManager.getAppBundlesShouldDisplay(card.appContainer).isEmpty()
            }

            is WorkbenchAppContainer1Card -> {
                return WorkbenchManager.getAppBundlesShouldDisplay(card.appContainer).isEmpty()

            }
        }

        return false
    }


    private fun assembleDataList() {


        if (hasDisplayCardInData()) {
            if (!dataList.contains(cardAddedTitleItem)) {
                dataList.add(0, cardAddedTitleItem)
            }

        } else {
            dataList.remove(cardAddedTitleItem)
        }


        val cardNotDisplayFirstIndex = dataList.indexOfFirst { !it.cardDisplay }


        if (-1 != cardNotDisplayFirstIndex) {

            if (!dataList.contains(cardNotAddTitleItem)) {
                dataList.add(cardNotDisplayFirstIndex, cardNotAddTitleItem)

            }
        } else {
            dataList.remove(cardNotAddTitleItem)
        }


    }


    private fun hasDisplayCardInData() = null != dataList.find { it.cardDisplay }


    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
        rvCards = view.findViewById(R.id.rv_cards)
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

    override fun finish() {
        super.finish()

        if (WorkbenchFragment.MODE_ADMIN_PREVIEW == mode) {
            WorkbenchAdminManager.notifyRefresh()
            return
        }
        WorkbenchManager.notifyRefresh()
    }


    private fun initViews() {
        tvTitle.setText(R.string.custom_defined)


        cardAddedTitleItem = WorkbenchSortCardItem(titleContent = getStrings(R.string.cards_added))
        cardNotAddTitleItem = WorkbenchSortCardItem(titleContent = getStrings(R.string.cards_not_added))

        adapter = WorkbenchCustomSortCardAdapter(dataList)

        recyclerViewDragDropManager = RecyclerViewDragDropManager()


        customSortWrappedAdapter = recyclerViewDragDropManager.createWrappedAdapter(adapter) as RecyclerView.Adapter<AbstractDraggableItemViewHolder>
        rvCards.adapter = customSortWrappedAdapter
        recyclerViewDragDropManager.attachRecyclerView(rvCards)


    }


    private fun registerListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        adapter.onMovedItemChangedListener = object : WorkbenchCustomSortCardAdapter.OnCardsChangedListener {
            override fun onAddOrRemove(position: Int) {
                val customSortCardItem = dataList.getOrNull(position)

                if (null == customSortCardItem) {
                    return
                }

                if (!customSortCardItem.cardDisplay) {
                    customSortCardItem.cardDisplay = true

                    //重新添加时, 直接放到最后
                    val removedResult = cardsOriginalFilterResult.cardsSort.remove(customSortCardItem.card)
                    if (removedResult) {
                        cardsOriginalFilterResult.cardsSort.add(customSortCardItem.card!!)
                    }


                    saveAndShuffleData()
                } else {

                    customSortCardItem.card?.let {
                        if (it.deletable) {
                            customSortCardItem.cardDisplay = false
                            saveAndShuffleData()
                        }
                    }

                }
            }

            override fun onMoveItem(fromPosition: Int, toPosition: Int) {
                val itemFrom = dataList[fromPosition]
                val itemTo = dataList[toPosition]

                val toRealPosition = cardsOriginalFilterResult.cardsSort.indexOf(itemTo.card)
                cardsOriginalFilterResult.cardsSort.remove(itemFrom.card)

                if (-1 != toRealPosition) {
                    cardsOriginalFilterResult.cardsSort.add(toRealPosition, itemFrom.card!!)
                }



                saveAndShuffleData()


            }

        }
    }

    private fun saveAndShuffleData() {
        hasChanged = true

        val context = AtworkApplicationLike.baseContext



        cardsOriginalFilterResult.cardsSort
                .map { it.id.toString() }
                .let {
                    OrgPersonalShareInfo.getInstance().setCustomSortedCardIdList(context, workbench.orgCode, workbench.id, it)

                }

        dataList
                .filter { !it.isTitle() && it.cardDisplay }
                .map { it.id.toString() }
                .let {
                    OrgPersonalShareInfo.getInstance().setCustomDisplayCardIdList(context, workbench.orgCode, workbench.id, it)

                }

        cardsOriginalFilterResult.classify()

        shuffleData()
    }

}