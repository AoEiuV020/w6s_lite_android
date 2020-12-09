package com.foreveross.atwork.modules.discussion.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionMemberTag
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.modules.discussion.component.DiscussionMemberContactItemView
import com.foreveross.atwork.modules.discussion.component.DiscussionMemberTagItemTitleView
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberContactItemInfo
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberItemInfoType
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberTagItemInfo
import com.foreveross.atwork.modules.discussion.model.IDiscussionMemberItemDisplay
import kotlinx.android.synthetic.main.item_view_discussion_member.view.*


class DiscussionMemberTagsInManagerAdapter(data: MutableList<IDiscussionMemberItemDisplay>) : BaseQuickAdapter<IDiscussionMemberItemDisplay, BaseViewHolder>(-1, data), DraggableModule {


    var tagInRemoveMode: DiscussionMemberTag? = null

    companion object {

        const val VIEW_NORMAL = 0
        const val VIEW_ADD = 1
        const val VIEW_REMOVE = 2
        const val VIEW_TITLE = 3
    }


    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
         when(viewType) {
            VIEW_TITLE -> {
                return DiscussionMemberTagItemTitleViewHolder(DiscussionMemberTagItemTitleView(context))
            }


            else  -> {
                val itemView = DiscussionMemberContactItemView(context)

                val discussionMemberInfoViewHolder = DiscussionMemberInfoViewHolder(itemView)

                when(viewType) {
                    VIEW_ADD -> itemView.refreshLocal(R.mipmap.icon_add_discussion_member)
                    VIEW_REMOVE -> itemView.refreshLocal(R.mipmap.icon_remove_discussion_member)
                    else -> {
                        itemView.ivRemoveMember.setOnClickListener {

                        }
                    }
                }


                return discussionMemberInfoViewHolder
            }
        }
    }


    override fun convert(holder: BaseViewHolder, item: IDiscussionMemberItemDisplay) {
        when(item) {
            is DiscussionMemberTagItemInfo -> {
                holder.asType<DiscussionMemberTagItemTitleViewHolder>()?.let { viewHolder->
                    viewHolder.titleItemView.refreshView(item)
                }
            }

            is DiscussionMemberContactItemInfo -> {

                holder.asType<DiscussionMemberInfoViewHolder>()?.let { viewHolder->
                    item.contact?.let { viewHolder.memberInfoItemView.refreshView(it, tagInRemoveMode == item.tag) }

                }
            }
        }
    }

    override fun getDefItemViewType(position: Int): Int {
        return when(val item = getItem(position)) {
            is DiscussionMemberTagItemInfo -> VIEW_TITLE
            is DiscussionMemberContactItemInfo -> {
                when(item.type) {
                    DiscussionMemberItemInfoType.ADD -> VIEW_ADD
                    DiscussionMemberItemInfoType.REMOVE -> VIEW_REMOVE
                    else -> VIEW_NORMAL
                }

            }
            else -> VIEW_NORMAL
        }
    }


    class DiscussionMemberInfoViewHolder (itemView: View) : BaseViewHolder(itemView) {
        var memberInfoItemView: DiscussionMemberContactItemView = itemView as DiscussionMemberContactItemView
    }



    class DiscussionMemberTagItemTitleViewHolder (itemView: View) : BaseViewHolder(itemView) {
        var titleItemView: DiscussionMemberTagItemTitleView = itemView as DiscussionMemberTagItemTitleView
    }

}

