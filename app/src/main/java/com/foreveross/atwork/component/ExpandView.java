package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.chat.component.MessageTagItem;
import com.w6s.module.MessageTags;

import java.util.ArrayList;
import java.util.List;

public class ExpandView extends FrameLayout {

    public static final String ALL_TAG_ID = "ALL_TAG_ID";

    private Animation mExpandAnimation;
    private Animation mCollapseAnimation;
    private boolean mIsExpand;

    private ListView mListView;

    private MessageTagAdapter mAdapter;

    private List<MessageTags> mMessageTagList = new ArrayList<>();

    private OnMessageTagSelectListener mListener;

    private MessageTags mFlagAllTag;

    private MessageTags mSelectedTag;

    public ExpandView(@NonNull Context context) {
        this(context, null);
    }

    public ExpandView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initExpandView(context);
    }

    public void setSelectedTag(MessageTags selectedTag) {
        mSelectedTag = selectedTag;
    }

    private void initExpandView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_tag_expand, this, true);
        mListView = view.findViewById(R.id.message_tag_list);
        mFlagAllTag = new MessageTags();
        mFlagAllTag.setTagId(ALL_TAG_ID);
        mSelectedTag = mFlagAllTag;
        mAdapter = new MessageTagAdapter();
        mListView.setAdapter(mAdapter);

        mExpandAnimation = AnimationUtils.loadAnimation(context, R.anim.expand_show);
        mExpandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mCollapseAnimation = AnimationUtils.loadAnimation(context, R.anim.expand_hide);
        mCollapseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (mListener == null) {
                return;
            }
            collapse();
            mSelectedTag = mMessageTagList.get(position);
            mListener.onTagSelected(mSelectedTag);
        });

    }

    public void collapse() {
        if (mIsExpand) {
            mIsExpand = false;
            clearAnimation();
            startAnimation(mCollapseAnimation);

        }
    }

    public void expand() {
        if (!mIsExpand) {
            mIsExpand = true;
            clearAnimation();
            startAnimation(mExpandAnimation);

        }
    }

    public void setOnTagSelectListener(OnMessageTagSelectListener listener) {
        mListener = listener;
    }

    public void setMessageTags(List<MessageTags> list) {
        mMessageTagList.clear();
        mMessageTagList.add(mFlagAllTag);
        mMessageTagList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    public boolean isExpand() {
        return mIsExpand;
    }

    private class MessageTagAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMessageTagList.size();
        }

        @Override
        public MessageTags getItem(int position) {
            return mMessageTagList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new MessageTagItem(getContext());
            }
            MessageTagItem item = (MessageTagItem)convertView;
            MessageTags messageTags = mMessageTagList.get(position);
            String title = "";
            if (ALL_TAG_ID == messageTags.getTagId()) {
                title = getContext().getString(R.string.all);
            } else {
                title = messageTags.getShowName(getContext());
            }
            item.setText(title);
            item.isSelected(messageTags.getTagId().equalsIgnoreCase(mSelectedTag.getTagId()) );
            return item;
        }
    }

    public interface OnMessageTagSelectListener {
        void onTagSelected(MessageTags messageTags);
    }

}
