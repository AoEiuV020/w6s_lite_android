package com.foreveross.atwork.modules.discussion.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.modules.discussion.component.DiscussionMemberContactItemView
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberContactItemInfo
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberItemInfoType
import kotlinx.android.synthetic.main.item_view_discussion_member.view.*

class DiscussionMembersInCreatingAdapter(contactList: MutableList<DiscussionMemberContactItemInfo>): BaseQuickAdapter<DiscussionMemberContactItemInfo, DiscussionMembersInCreatingAdapter.ChatInfoContactItemViewHolder>(-1, contactList) {


    private val VIEW_NORMAL = 0
    private val VIEW_ADD = 1
    private val VIEW_REMOVE = 2

    var removeMode = false

    var onItemRemoveBtnClick: ((pos: Int, contact: ShowListItem) -> Unit)? = null

    override fun getDefItemViewType(position: Int) = when (getItem(position).type) {
        DiscussionMemberItemInfoType.ADD -> VIEW_ADD
        DiscussionMemberItemInfoType.REMOVE -> VIEW_REMOVE
        else -> VIEW_NORMAL
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): ChatInfoContactItemViewHolder {

        val itemView = DiscussionMemberContactItemView(context)

        val chatInfoContactItemViewHolder = ChatInfoContactItemViewHolder(itemView)

        when(viewType) {
            VIEW_ADD -> itemView.refreshLocal(R.mipmap.icon_add_discussion_member)
            VIEW_REMOVE -> itemView.refreshLocal(R.mipmap.icon_remove_discussion_member)
            else -> {
                itemView.ivRemoveMember.setOnClickListener {
                    getItem(chatInfoContactItemViewHolder.adapterPosition)
                            .let { it.contact }
                            ?.let {
                                onItemRemoveBtnClick?.invoke(chatInfoContactItemViewHolder.adapterPosition, it)
                    }
                }
            }
        }


        return chatInfoContactItemViewHolder
    }


    override fun convert(holder: ChatInfoContactItemViewHolder, item: DiscussionMemberContactItemInfo) {
        item.contact?.let { holder.templateView.refreshView(it, removeMode) }
    }


    class ChatInfoContactItemViewHolder (itemView: View) : BaseViewHolder(itemView) {
        var templateView: DiscussionMemberContactItemView = itemView as DiscussionMemberContactItemView
    }

}



