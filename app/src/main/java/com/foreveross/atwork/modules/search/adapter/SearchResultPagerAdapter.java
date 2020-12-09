package com.foreveross.atwork.modules.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.group.module.SelectToHandleAction;
import com.foreveross.atwork.modules.search.fragment.NewSearchFragment;
import com.foreveross.atwork.modules.search.listener.OnScrollListViewListener;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.search.model.SearchMoreItem;
import com.foreveross.atwork.modules.search.model.SearchShowItemList;
import com.foreveross.atwork.modules.search.model.SearchTextTitleItem;
import com.foreveross.atwork.modules.search.util.SearchHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by dasunsy on 2017/12/13.
 */

public class SearchResultPagerAdapter extends PagerAdapter {

    private Context mContext;
    protected LinkedHashMap<SearchContent, SearchShowItemList<ShowListItem>> mSearchResultMap = new LinkedHashMap<>();
    protected List<SearchContent> mSearchContentList = new ArrayList<>();

    private String mKey;

    private SearchAction mSearchAction = SearchAction.DEFAULT;
    private SelectToHandleAction mSelectToHandleAction;
    private OnScrollListViewListener mOnScrollListViewListener;
    private OnMoreItemClickListener mOnMoreItemClickListener;

    public SearchResultPagerAdapter(Context context) {
        this.mContext = context;
    }

    public void initResultData(SearchContent[] searchContents) {
        if (1 != searchContents.length) {
            mSearchContentList.add(SearchContent.SEARCH_ALL);
            mSearchResultMap.put(SearchContent.SEARCH_ALL, new SearchShowItemList<>());
        }

        for(SearchContent searchContent : searchContents) {
            mSearchContentList.add(searchContent);
            mSearchResultMap.put(searchContent, new SearchShowItemList<>());
        }
    }

    public void clearTargetData(SearchContent searchContent) {
        mSearchResultMap.get(searchContent).clear();
        notifyDataSetChanged();
    }

    public void clearData() {
        for(List<ShowListItem> searchContents : mSearchResultMap.values()) {
            searchContents.clear();
        }
        notifyDataSetChanged();
    }

    public void setMoreItemClickListener(OnMoreItemClickListener listener) {
        this.mOnMoreItemClickListener = listener;
    }

    public void refreshResultData(SearchContent searchContent, List<? extends ShowListItem> resultList) {
        List<ShowListItem> savedData = mSearchResultMap.get(searchContent);
        savedData.clear();
        savedData.addAll(resultList);

//        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_pager_search_result, null);
        if (null == view.getParent()) {
            container.addView(view);
        }

        RecyclerView resultListView = view.findViewById(R.id.rv_item_pager_search_result);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        SearchResultListAdapter adapter = new SearchResultListAdapter(mContext, getSearchResultList(position),false);
        adapter.setKey(mKey);
        adapter.setSearchAction(mSearchAction);
        adapter.setOnMainHandleClickListener((positionClick, result) -> {
            if (result instanceof SearchMoreItem) {
                SearchMoreItem item = (SearchMoreItem)result;
                int i = 0;
                for (SearchContent searchContent: mSearchContentList) {
                    if (searchContent.equals(item.searchContent)) {
                        mOnMoreItemClickListener.moreItemClickContent(i);
                    }
                    i++;
                }
                return;
            }
            if (mContext instanceof Activity) {
                SearchHelper.handleSearchResultCommonClick((Activity) mContext, mKey, result, mSearchAction, mSelectToHandleAction);
            }
        });


        resultListView.setLayoutManager(linearLayoutManager);
        resultListView.setAdapter(adapter);

        resultListView.setOnTouchListener((v, event) -> {
            if(null != mOnScrollListViewListener) {
                mOnScrollListViewListener.onScroll();
            }
            return false;
        });

        return view;
    }



    @Override
    public int getCount() {
        return mSearchResultMap.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return SearchHelper.getSearchContentTip(mSearchContentList.get(position));
    }

    public List<ShowListItem> getSearchResultList(int position) {
        SearchContent searchContent = mSearchContentList.get(position);
        if(SearchContent.SEARCH_ALL == searchContent) {
            return getAllSearchResultList();
        }

        return mSearchResultMap.get(searchContent);
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public View getTabView(Context context, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tab = layoutInflater.inflate(R.layout.custom_tab, null);
        TextView tv = tab.findViewById(R.id.tv_custom);
        tv.setText(getPageTitle(position));
        tv.setTextColor(ContextCompat.getColor(context, R.color.common_text_color_999));
        return tab;
    }


    public List<ShowListItem> getAllSearchResultList() {
        List<ShowListItem> allResultList = new ArrayList<>();

        SearchShowItemList<ShowListItem> userResultList = mSearchResultMap.get(SearchContent.SEARCH_USER);
        if(!ListUtil.isEmpty(userResultList)) {
            allResultList.add(getUserItem());
            allResultList.addAll(userResultList.getThresholdList());

            if (userResultList.size() > NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
                allResultList.add(getMoreTipItem(mContext.getResources().getString(R.string.more_contacts), SearchContent.SEARCH_USER));
            }
        }



        SearchShowItemList<ShowListItem> deviceResultList = mSearchResultMap.get(SearchContent.SEARCH_DEVICE);
        if(!ListUtil.isEmpty(deviceResultList)) {
            allResultList.add(getDeviceItem());
            allResultList.addAll(deviceResultList.getThresholdList());
            if (deviceResultList.size() > NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
                allResultList.add(getMoreTipItem(mContext.getResources().getString(R.string.more_devices), SearchContent.SEARCH_DEVICE));
            }
        }


        SearchShowItemList<ShowListItem> discussionResultList = mSearchResultMap.get(SearchContent.SEARCH_DISCUSSION);
        if(!ListUtil.isEmpty(discussionResultList)) {
            allResultList.add(getDiscussionItem());
            allResultList.addAll(discussionResultList.getThresholdList());
            if (discussionResultList.size() > NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
                allResultList.add(getMoreTipItem(mContext.getResources().getString(R.string.more_discussion), SearchContent.SEARCH_DISCUSSION));
            }
        }


        SearchShowItemList<ShowListItem> appResultList = mSearchResultMap.get(SearchContent.SEARCH_APP);
        if(!ListUtil.isEmpty(appResultList)) {
            allResultList.add(getAppItem());
            allResultList.addAll(appResultList.getThresholdList());
            if (appResultList.size() > NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
                allResultList.add(getMoreTipItem(mContext.getResources().getString(R.string.more_app), SearchContent.SEARCH_APP));
            }
        }

        SearchShowItemList<ShowListItem> chatResultList = mSearchResultMap.get(SearchContent.SEARCH_MESSAGES);
        if(!ListUtil.isEmpty(chatResultList)) {
            allResultList.add(getChatItem());
            allResultList.addAll(chatResultList.getThresholdList());
            if (chatResultList.size() > NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
                allResultList.add(getMoreTipItem(mContext.getResources().getString(R.string.more_chat), SearchContent.SEARCH_MESSAGES));
            }
        }

        SearchShowItemList<ShowListItem> departResult = mSearchResultMap.get(SearchContent.SEARCH_DEPARTMENT);
        if (!ListUtil.isEmpty(departResult)) {
            allResultList.add(getDepartItem());
            allResultList.addAll(departResult.getThresholdList());
            if (departResult.size() > NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
                allResultList.add(getMoreTipItem(mContext.getResources().getString(R.string.more_department), SearchContent.SEARCH_DEPARTMENT));
            }
        }

        return allResultList;

    }

    public void setOnScrollListViewListener(OnScrollListViewListener onScrollListViewListener) {
        mOnScrollListViewListener = onScrollListViewListener;
    }

    public void setSearchAction(SearchAction searchAction) {
        mSearchAction = searchAction;
    }

    public void setSelectToHandleAction(SelectToHandleAction selectToHandleAction) {
        mSelectToHandleAction = selectToHandleAction;
    }

    public List<ShowListItem> getResultMap(SearchContent searchContent) {
        return mSearchResultMap.get(searchContent);
    }

    public SearchMoreItem getMoreTipItem(String moreTipStr, SearchContent searchContent) {
        SearchMoreItem moreTip = new SearchMoreItem(moreTipStr, searchContent);
        return moreTip;
    }

    public SearchTextTitleItem getUserItem() {
        SearchTextTitleItem userItem = new SearchTextTitleItem(mContext.getResources().getString(R.string.search_title_user));
        return userItem;
    }

    public SearchTextTitleItem getDeviceItem() {
        SearchTextTitleItem searchItem = new SearchTextTitleItem(mContext.getResources().getString(R.string.device));
        return searchItem;
    }

    public SearchTextTitleItem getDiscussionItem() {
        SearchTextTitleItem groupItem = new SearchTextTitleItem(mContext.getResources().getString(R.string.search_title_group));
        return groupItem;
    }

    public SearchTextTitleItem getAppItem() {
        SearchTextTitleItem appItem = new SearchTextTitleItem(mContext.getResources().getString(R.string.search_title_app));
        return appItem;
    }

    public SearchTextTitleItem getChatItem() {
        SearchTextTitleItem chatItem = new SearchTextTitleItem(mContext.getResources().getString(R.string.search_title_chat));
        return chatItem;
    }

    public SearchTextTitleItem getBingItem() {
        SearchTextTitleItem bingItem = new SearchTextTitleItem(mContext.getResources().getString(R.string.search_title_bing));
        return bingItem;
    }

    public SearchTextTitleItem getDepartItem() {
        SearchTextTitleItem departItem = new SearchTextTitleItem(mContext.getResources().getString(R.string.organization2));
        return departItem;
    }


    public interface OnMoreItemClickListener {
        void moreItemClickContent(int pos);
    }
}

