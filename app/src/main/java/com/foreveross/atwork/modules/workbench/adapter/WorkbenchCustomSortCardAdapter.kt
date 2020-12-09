package com.foreveross.atwork.modules.workbench.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.ui.component.recyclerview.DraggableItemAdapter
import com.foreverht.workplus.ui.component.recyclerview.ItemDraggableRange
import com.foreverht.workplus.ui.component.recyclerview.utils.AbstractDraggableItemViewHolder
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.workbench.model.WorkbenchSortCardItem


class WorkbenchCustomSortCardAdapter(
        var dataList: MutableList<WorkbenchSortCardItem>

) : RecyclerView.Adapter<AbstractDraggableItemViewHolder>(), DraggableItemAdapter<AbstractDraggableItemViewHolder> {

    val ITEM_TYPE_TITLE = 0

    val ITEM_TYPE_CARD_ITEM = 1

    lateinit var onMovedItemChangedListener: OnCardsChangedListener


    init {
        setHasStableIds(true)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractDraggableItemViewHolder {
        return when (viewType) {
            ITEM_TYPE_TITLE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.component_workbench_custom_sort_cards_item_title, parent, false)
                CustomSortCardTitleViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.component_workbench_custom_sort_cards_item, parent, false)
                val customSortCardItemViewHolder = CustomSortCardItemViewHolder(view)

                customSortCardItemViewHolder.ivHandleIcon.setOnClickListener {
                    val position = customSortCardItemViewHolder.adapterPosition

                    onMovedItemChangedListener.onAddOrRemove(position)

                }

                customSortCardItemViewHolder
            }

        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        if (dataList[position].isTitle()) {
            return ITEM_TYPE_TITLE
        }

        return ITEM_TYPE_CARD_ITEM
    }

    override fun getItemId(position: Int): Long {
        val id = dataList[position].id

        LogUtil.e("WorkbenchCustomSortCardAdapter longId -> $id")


        return id
    }


    override fun onBindViewHolder(holder: AbstractDraggableItemViewHolder, position: Int) {
        val customSortCardItem = dataList[position]
        when (holder) {
            is CustomSortCardTitleViewHolder -> {
                holder.tvTitle.text = customSortCardItem.titleContent
            }

            is CustomSortCardItemViewHolder -> {

                customSortCardItem.card?.let {
                    holder.tvCardName.text = it.getNameI18n(AtworkApplicationLike.baseContext)
                }


                holder.ivSort.isVisible = showSortBtn(customSortCardItem)

                if (customSortCardItem.cardDisplay) {

                    if (customSortCardItem.card!!.deletable) {
                        holder.ivHandleIcon.setImageResource(R.mipmap.icon_workbench_delete)

                    } else {
                        holder.ivHandleIcon.setImageResource(R.mipmap.icon_workbench_cannot_delete)

                    }

                } else {
                    holder.ivHandleIcon.setImageResource(R.mipmap.icon_workbench_add)
                }

            }
        }
    }

    private fun showSortBtn(sortCardItem: WorkbenchSortCardItem): Boolean {
        if (!sortCardItem.cardDisplay) {
            return false
        }


//        if(null != customSortCardItem.card && customSortCardItem.card is WorkbenchSearchCard) {
//            return false
//        }

        return true
    }


    override fun onGetItemDraggableRange(holder: AbstractDraggableItemViewHolder, position: Int): ItemDraggableRange? {
        var lastPosition = dataList.indexOfLast { it.isTitle() }

        if (0 >= lastPosition) {
            lastPosition = dataList.size
        }

        lastPosition -= 1

        val startPosition = 1

        return ItemDraggableRange(startPosition, lastPosition)

    }

    override fun onCheckCanStartDrag(holder: AbstractDraggableItemViewHolder, position: Int, x: Int, y: Int): Boolean {

        // x, y --- relative from the itemView's top-left

        // return false if the item is a section header
        if (holder is CustomSortCardItemViewHolder) {
            val dragHandleView = holder.ivSort

            return hitTest(dragHandleView, x, y)
        }



        return false


    }

    override fun onItemDragStarted(position: Int) {
        notifyDataSetChanged()
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {

        if (fromPosition == toPosition) {
            return
        }


        onMovedItemChangedListener.onMoveItem(fromPosition, toPosition)
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean = true

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }


    private fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (v.translationX + 0.5f).toInt()
        val ty = (v.translationY + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return x >= left && x <= right && y >= top && y <= bottom
    }


    interface OnCardsChangedListener {

        fun onMoveItem(fromPosition: Int, toPosition: Int)

        fun onAddOrRemove(position: Int)
    }

    class CustomSortCardTitleViewHolder(itemView: View) : AbstractDraggableItemViewHolder(itemView) {

        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)

    }


    class CustomSortCardItemViewHolder(itemView: View) : AbstractDraggableItemViewHolder(itemView) {

        var rlRoot: RelativeLayout = itemView.findViewById(R.id.rl_root)
        var ivHandleIcon: ImageView = itemView.findViewById(R.id.iv_handle_icon)
        var tvCardName: TextView = itemView.findViewById(R.id.tv_card_name)
        var ivSort: ImageView = itemView.findViewById(R.id.iv_sort)

    }
}