package com.foreveross.atwork.modules.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.chat.component.ChatMoreView;
import com.foreveross.atwork.modules.chat.model.ChatMoreItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/11/13.
 */

public class ChatMorePagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<List<ChatMoreItem>> mChatMoreItemList;

    private ChatMoreView.ChatMoreViewListener mChatMoreViewListener;

    private List<ChatMorePagerItemGroupAdapter> mGroupAdapters = new ArrayList<>();

    private boolean mBurnMode;
    private int mSlideLayoutHeight;


    public ChatMorePagerAdapter(Context context, List<List<ChatMoreItem>> chatMoreItemList) {
        this.mContext = context;
        this.mChatMoreItemList = chatMoreItemList;
    }

    @Override
    public int getCount() {
        return mChatMoreItemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_pager_chat_more, null);
        if (null == view.getParent()) {
            container.addView(view);
        }

        RecyclerView rvPager = view.findViewById(R.id.rv_item_pager_chat_more);

        rvPager.getRecycledViewPool().setMaxRecycledViews(0, 8);
        GridLayoutManager gridLayoutMgr = new GridLayoutManager(mContext, 4);
        rvPager.setLayoutManager(gridLayoutMgr);

        ChatMorePagerItemGroupAdapter chatMorePagerItemGroupAdapter = new ChatMorePagerItemGroupAdapter(mContext, mChatMoreItemList.get(position));
        chatMorePagerItemGroupAdapter.setChatMoreViewListener(mChatMoreViewListener);
        chatMorePagerItemGroupAdapter.setBurnMode(mBurnMode);
        chatMorePagerItemGroupAdapter.setSlideLayoutHeight(mSlideLayoutHeight);
        rvPager.setAdapter(chatMorePagerItemGroupAdapter);

        mGroupAdapters.add(chatMorePagerItemGroupAdapter);

        return view;
    }

    public void setChatMoreViewListener(ChatMoreView.ChatMoreViewListener chatMoreViewListener) {
        this.mChatMoreViewListener = chatMoreViewListener;
    }

    public void setBurnMode(boolean burnMode) {
        this.mBurnMode = burnMode;

        for(ChatMorePagerItemGroupAdapter adapter : mGroupAdapters) {
            adapter.setBurnMode(mBurnMode);
            adapter.notifyDataSetChanged();
        }
    }

    public void setSlideLayoutHeight(int slideLayoutHeight) {
        mSlideLayoutHeight = slideLayoutHeight;
    }
}
