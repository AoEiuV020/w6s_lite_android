package com.foreverht.workplus.module.discussion.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.ui.component.recyclerview.DraggableItemState
import com.foreverht.workplus.ui.component.recyclerview.DraggableItemViewHolder
import com.foreverht.workplus.ui.component.recyclerview.annotation.DraggableItemStateFlags
import com.foreverht.workplus.ui.component.recyclerview.expandable.ExpandableItemState
import com.foreverht.workplus.ui.component.recyclerview.expandable.ExpandableItemViewHolder

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), DraggableItemViewHolder, ExpandableItemViewHolder {

    private val expandState = ExpandableItemState()
    private val dragState = DraggableItemState()

    override fun setDragStateFlags(@DraggableItemStateFlags flags: Int) {
        dragState.flags = flags
    }

    /**
     * {@inheritDoc}
     */
    @DraggableItemStateFlags
    override fun getDragStateFlags(): Int {
        return dragState.flags
    }

    /**
     * {@inheritDoc}
     */
    override fun getDragState(): DraggableItemState {
        return dragState
    }

    override fun getExpandState(): ExpandableItemState {
        return expandState
    }

    override fun getExpandStateFlags(): Int {
        return expandState.flags
    }

    override fun setExpandStateFlags(flags: Int) {
        expandState.flags = flags
    }
}