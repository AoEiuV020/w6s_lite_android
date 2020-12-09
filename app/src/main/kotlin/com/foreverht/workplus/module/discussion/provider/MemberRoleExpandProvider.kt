package com.foreverht.workplus.module.discussion.provider

import androidx.core.util.Pair
import java.util.*

class MemberRoleExpandProvider(datas: LinkedList<Pair<GroupData, ChildData>>) : ExpandProvider() {

    private val datas: LinkedList<Pair<GroupData, ChildData>> = datas

    // for undo group item
    private var mLastRemovedGroup: Pair<GroupData, ChildData>? = null
    private var mLastRemovedGroupPosition = -1

    // for undo child item
    private var mLastRemovedChild: ChildData? = null
    private var mLastRemovedChildParentGroupId: Long? = -1
    private var mLastRemovedChildPosition = -1

    override fun getGroupCount(): Int {
        return datas.size
    }

    override fun getChildCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroupItem(groupPosition: Int): GroupData {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw IndexOutOfBoundsException("groupPosition = $groupPosition")
        }
        return datas[groupPosition].first!!
    }

    override fun getChildItem(groupPosition: Int, childPosition: Int): ChildData {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw java.lang.IndexOutOfBoundsException("groupPosition = $groupPosition")
        }

        return datas[groupPosition].second!!
    }

    override fun moveGroupItem(fromGroupPosition: Int, toGroupPosition: Int) {
        if (fromGroupPosition == toGroupPosition) {
            return
        }

        val item: Pair<GroupData, ChildData> = datas.removeAt(fromGroupPosition)
        datas.add(toGroupPosition, item)
    }

    override fun moveChildItem(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int) {
        if (fromGroupPosition == toGroupPosition && fromChildPosition == toChildPosition) {
            return
        }

        val fromGroup: Pair<GroupData, ChildData> = datas[fromGroupPosition]
        val toGroup: Pair<GroupData, ChildData> = datas[toGroupPosition]

        val item: ConcreteChildData = fromGroup.second as ConcreteChildData

        if (toGroupPosition != fromGroupPosition) {
            // assign a new ID
            val newId: Long = (toGroup.first as ConcreteGroupData).generateNewChildId()
            item.childId = newId
        }

        toGroup.second
    }

    override fun removeGroupItem(groupPosition: Int) {
        mLastRemovedGroup = datas.removeAt(groupPosition)
        mLastRemovedGroupPosition = groupPosition

        mLastRemovedChild = null
        mLastRemovedChildParentGroupId = -1
        mLastRemovedChildPosition = -1
    }

    override fun removeChildItem(groupPosition: Int, childPosition: Int) {
        mLastRemovedChild = datas[groupPosition].second
        mLastRemovedChildParentGroupId = datas[groupPosition].first?.groupId
        mLastRemovedChildPosition = childPosition

        mLastRemovedGroup = null
        mLastRemovedGroupPosition = -1
    }

    class ConcreteGroupData(override val groupId: Long, override val header: String) : GroupData() {
        override var isPinned = false
        private var mNextChildId: Long = 0

        fun generateNewChildId(): Long {
            val id = mNextChildId
            mNextChildId += 1
            return id
        }

    }

    class ConcreteChildData internal constructor(override var childId: Long, override val childProvider: GridProvider) : ChildData() {
        override var isPinned = false

    }
}