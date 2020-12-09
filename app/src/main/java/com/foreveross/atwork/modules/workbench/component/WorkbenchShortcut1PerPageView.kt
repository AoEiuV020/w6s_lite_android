package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchShortCut1PerPageAdapter
import kotlinx.android.synthetic.main.component_workbench_shortcut_card_1_per_page.view.*


class WorkbenchShortcut1PerPageView : FrameLayout {

    val shortcutCardItems = arrayListOf<WorkbenchShortcutCardItem>()
    private lateinit var adapter: WorkbenchShortCut1PerPageAdapter
    var workbenchCard: WorkbenchCard? = null

    constructor(context: Context) : super(context) {
        findViews()
        initViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
        initViews()
        registerListener()
    }


    private fun initViews() {
        adapter = WorkbenchShortCut1PerPageAdapter(shortcutCardItems)
        rvShortcut.layoutManager = GridLayoutManager(context, 4)
        rvShortcut.adapter = adapter

    }

    private fun registerListener() {
        adapter.setOnItemClickListener { adapter, view, position ->

            val workbenchListItem = shortcutCardItems[position]

            if(view is WorkbenchShortcutCardItemView) {
                view.clickAction(this.workbenchCard, workbenchListItem)
            }

        }
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_shortcut_card_1_per_page, this)

    }

    fun refreshViews(shortcutCardItems: List<WorkbenchShortcutCardItem>) {
        this.shortcutCardItems.clear()
        this.shortcutCardItems.addAll(shortcutCardItems)
        adapter.notifyDataSetChanged()
    }
}