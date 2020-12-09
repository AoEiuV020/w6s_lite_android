package com.foreverht.workplus.ui.component.listview.adapter

import android.view.View
import android.view.ViewGroup

/**
 *  create by reyzhang22 at 2020/3/9
 */
interface IDockingAdapterDataSource {

    fun getGroupCount(): Int

    fun getChildCount(groupPos: Int): Int

    fun getGroup(groupPos: Int): Any

    fun getChild(groupPos: Int, childPos: Int): Any

    fun getGroupView(groupPos: Int, expanded: Boolean, convertView: View?, parent: ViewGroup?): View

    fun getChildView(groupPos: Int, childPos: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View
}