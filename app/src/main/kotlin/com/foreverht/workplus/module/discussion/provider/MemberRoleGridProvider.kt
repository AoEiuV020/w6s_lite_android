package com.foreverht.workplus.module.discussion.provider

import com.foreverht.workplus.module.discussion.model.DiscussionMemberDataShower
import java.util.*

class MemberRoleGridProvider(list: LinkedList<MemberGridConcreteData>) : GridProvider() {

    private val data: LinkedList<MemberGridConcreteData> = list

    private var lastRemovedDataMemberGrid: MemberGridConcreteData? = null
    private var lastRemovedPosition = -1

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): Data {
        if (index < 0 || index >= getCount()) {
            throw IndexOutOfBoundsException("index = $index")
        }
        return data[index]
    }

    override fun removeItem(position: Int) {
        val removedItem: MemberGridConcreteData = data.removeAt(position)

        lastRemovedDataMemberGrid = removedItem
        lastRemovedPosition = position
    }

    override fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }

        val item: MemberGridConcreteData = data.removeAt(fromPosition)

        data.add(toPosition, item)
        lastRemovedPosition = -1
    }

    override fun swapItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }

        Collections.swap(data, toPosition, fromPosition)
        lastRemovedPosition = -1
    }


    class MemberGridConcreteData internal constructor(override val id: Long, override val dataSet: DiscussionMemberDataShower?) : Data() {
        override var isPinned = false

    }
}