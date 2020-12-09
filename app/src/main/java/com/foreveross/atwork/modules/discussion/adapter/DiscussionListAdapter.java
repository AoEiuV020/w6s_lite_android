package com.foreveross.atwork.modules.discussion.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.component.DiscussionListItemView;


public class DiscussionListAdapter extends ArrayAdapter<Discussion> {

    private Context context;


    private UserSelectActivity.SelectMode mSelectMode;
    private boolean mSingleSelect;


    public DiscussionListAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Discussion discussion = getItem(position);
        if (convertView == null) {
            convertView = new DiscussionListItemView(context);
        }
        DiscussionListItemView discussionListItemView = (DiscussionListItemView) convertView;
        discussionListItemView.setSelectMode(mSelectMode);
        discussionListItemView.setSingleSelect(mSingleSelect);
        discussionListItemView.refreshView(discussion);
        return convertView;
    }


    public void setSelectMode(UserSelectActivity.SelectMode selectMode) {
        mSelectMode = selectMode;
    }

    public void setSingleSelect(boolean singleSelect) {
        mSingleSelect = singleSelect;
    }
}
