package com.foreverht.workplus.module.discussion.provider

abstract class ExpandProvider {

    abstract class DiscussionBaseData {
        abstract var isPinned: Boolean
    }

    abstract class GroupData : DiscussionBaseData() {
        abstract val groupId: Long
        abstract val header: String
    }

    abstract class ChildData : DiscussionBaseData() {
        abstract val childId: Long
        abstract val childProvider: GridProvider
    }

    abstract fun getGroupCount(): Int
    abstract fun getChildCount(groupPosition: Int): Int

    abstract fun getGroupItem(groupPosition: Int): GroupData
    abstract fun getChildItem(groupPosition: Int, childPosition: Int): ChildData

    abstract fun moveGroupItem(fromGroupPosition: Int, toGroupPosition: Int)
    abstract fun moveChildItem(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int)

    abstract fun removeGroupItem(groupPosition: Int)
    abstract fun removeChildItem(groupPosition: Int, childPosition: Int)

}