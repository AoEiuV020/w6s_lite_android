package com.foreverht.workplus.ui.component.listview.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import com.foreverht.workplus.ui.component.listview.DockingExpandableListView
import com.foreverht.workplus.ui.component.listview.DockingExpandableListView.IDockingController.Companion.DOCKING_HEADER_HIDDEN

/**
 *  create by reyzhang22 at 2020/3/9
 *  使用DockingExpandableListview， 其adapter继承此adapter即可
 */
open class DockingExpandableListViewAdapter() : BaseExpandableListAdapter(), DockingExpandableListView.IDockingController {

    private lateinit var context: Context

    private lateinit var listView: ExpandableListView

    private lateinit var dataSource: IDockingAdapterDataSource

    constructor(context: Context, listview: ExpandableListView, data: IDockingAdapterDataSource) : this() {
        this.context = context
        this.listView = listview
        this.dataSource = data
    }

    override fun getGroup(groupPosition: Int): Any {
        return dataSource.getGroup(groupPosition)
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        return dataSource.getGroupView(groupPosition, isExpanded, convertView, parent)
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return dataSource.getChildCount(groupPosition)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return dataSource.getChild(groupPosition, childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        return dataSource.getChildView(groupPosition, childPosition, isLastChild, convertView, parent)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return dataSource.getGroupCount()
    }

    override fun getDockingState(firstVisibleGroup: Int, firstVisibleChild: Int): Int {
        if (firstVisibleChild == -1 && !listView.isGroupExpanded(firstVisibleGroup)) {
            return DOCKING_HEADER_HIDDEN;
        }

        if (firstVisibleChild == getChildrenCount(firstVisibleGroup) - 1) {
            return DockingExpandableListView.IDockingController.DOCKING_HEADER_DOCKING;
        }

        return DockingExpandableListView.IDockingController.DOCKING_HEADER_DOCKED;
    }

}