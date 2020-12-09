package com.foreveross.atwork.modules.workbench.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.ui.component.recyclerview.RecyclerViewDragDropManager
import com.foreverht.workplus.ui.component.recyclerview.utils.AbstractDraggableItemViewHolder
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchAdminSortCardAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.modules.workbench.model.WorkbenchSortCardItem
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.ErrorHandleUtil

import com.foreveross.atwork.utils.finishByPullAnimation
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_sort.*
import kotlinx.android.synthetic.main.fragment_workbench_admin_set_scope.rlRoot
import kotlinx.android.synthetic.main.fragment_workbench_admin_set_scope.tvCancel

class WorkbenchAdminSortCardFragment: BackHandledPushOrPullFragment() {

    private lateinit var customSortWrappedAdapter: RecyclerView.Adapter<AbstractDraggableItemViewHolder>
    private lateinit var adapter: WorkbenchAdminSortCardAdapter
    private lateinit var recyclerViewDragDropManager: RecyclerViewDragDropManager

    private var dataList: MutableList<WorkbenchSortCardItem> = ArrayList()
    private lateinit var workbenchData: WorkbenchData



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_card_sort, container, false)
    }

    override fun getAnimationRoot(): View = llContentRoot

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
        registerListener()
    }

    override fun findViews(view: View) {

    }

    private fun initViews() {
        adapter = WorkbenchAdminSortCardAdapter(dataList)

        recyclerViewDragDropManager = RecyclerViewDragDropManager()

        customSortWrappedAdapter = recyclerViewDragDropManager.createWrappedAdapter(adapter) as RecyclerView.Adapter<AbstractDraggableItemViewHolder>
        rvCards.adapter = customSortWrappedAdapter
        recyclerViewDragDropManager.attachRecyclerView(rvCards)

        rvCards.setMaxListViewHeight(ScreenUtils.getScreenHeight(AtworkApplicationLike.baseContext) * 3 / 4)
    }

    private fun initData() {
        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let { workbenchData->
            this@WorkbenchAdminSortCardFragment.workbenchData = workbenchData

            val workbenchCards = WorkbenchAdminManager.produce(workbenchData).workbenchCards
            workbenchCards.sort()

            dataList.addAll(workbenchCards.map { WorkbenchSortCardItem(card = it, cardDisplay = it.displayable) })
        }

    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        tvSure.setOnClickListener { sureSortCards() }

        adapter.onMovedItemChangedListener = object : WorkbenchAdminSortCardAdapter.OnCardsChangedListener {

            override fun onMoveItem(fromPosition: Int, toPosition: Int) {
                val itemFrom = dataList[fromPosition]
                val itemTo = dataList[toPosition]

                val toRealPosition = dataList.indexOf(itemTo)
                dataList.remove(itemFrom)

                if(-1 != toRealPosition) {
                    dataList.add(toRealPosition, itemFrom)
                }

                adapter.notifyDataSetChanged()

            }

            override fun onAddOrRemove(position: Int) {

            }

        }
    }

    private fun sureSortCards() {
        val progressLoadingHelper = ProgressDialogHelper(activity)
        progressLoadingHelper.show()

        WorkbenchAdminManager.sortCards(
                context = AtworkApplicationLike.baseContext,
                orgCode = workbenchData.orgCode,
                cardListSorted = dataList.mapNotNull { it.card },
                originalWorkbenchData = workbenchData,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressLoadingHelper.dismiss()
                        toastOver(R.string.setting_success)

                        onBackPressed()
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressLoadingHelper.dismiss()
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }
                }
        )
    }

}