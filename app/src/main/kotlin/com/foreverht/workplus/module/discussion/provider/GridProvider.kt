package com.foreverht.workplus.module.discussion.provider

import com.foreverht.workplus.module.discussion.model.DiscussionMemberDataShower

abstract class GridProvider {
    abstract class Data {
        abstract val id: Long
        abstract val dataSet: DiscussionMemberDataShower?
        abstract var isPinned: Boolean
    }

    abstract fun getCount(): Int

    abstract fun getItem(index: Int): Data

    abstract fun removeItem(position: Int)

    abstract fun moveItem(fromPosition: Int, toPosition: Int)

    abstract fun swapItem(fromPosition: Int, toPosition: Int)

}