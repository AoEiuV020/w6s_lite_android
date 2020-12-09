package com.foreveross.atwork.modules.discussion.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.component.DiscussionListItemView;
import com.foreveross.atwork.modules.discussion.fragment.DiscussionSearchListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class DiscussionSearchListAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private List<Discussion> mDiscussionList = new ArrayList<>();

    private DiscussionSearchListFragment.OnHandleOnItemCLickListener mOnHandleOnItemCLickListener;

    public DiscussionSearchListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DiscussionListItemView discussionListItemView = new DiscussionListItemView(mContext);
        DiscussionItemViewHolder itemViewHolder = new DiscussionItemViewHolder(discussionListItemView);

        discussionListItemView.setOnClickListener(v -> {
            if(null != mOnHandleOnItemCLickListener) {

                mOnHandleOnItemCLickListener.onItemClick(mDiscussionList.get(itemViewHolder.getAdapterPosition()));
            }
        });


        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiscussionItemViewHolder itemViewHolder = (DiscussionItemViewHolder) holder;
        Discussion discussion = mDiscussionList.get(position);

        itemViewHolder.mDiscussionListItemView.setSelectMode(UserSelectActivity.SelectMode.SELECT);
        itemViewHolder.mDiscussionListItemView.refreshView(discussion);

    }

    @Override
    public int getItemCount() {
        return mDiscussionList.size();
    }

    public void clearData() {
        mDiscussionList.clear();
        notifyDataSetChanged();
    }

    public void setDiscussionList(List<Discussion> discussionList) {
        mDiscussionList = discussionList;
        notifyDataSetChanged();

    }



    public void setOnHandleOnItemCLickListener(DiscussionSearchListFragment.OnHandleOnItemCLickListener onHandleOnItemCLickListener) {
        mOnHandleOnItemCLickListener = onHandleOnItemCLickListener;
    }

    public static class DiscussionItemViewHolder extends RecyclerView.ViewHolder {

        public DiscussionListItemView mDiscussionListItemView;

        public DiscussionItemViewHolder(View itemView) {
            super(itemView);
            mDiscussionListItemView = (DiscussionListItemView) itemView;
        }
    }
}
