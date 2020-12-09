package com.foreveross.atwork.modules.search.adapter;

import android.content.Context;

import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.search.model.SearchShowItemList;

/**
 * Created by dasunsy on 2017/12/13.
 */

public class MinjieSearchResultPagerAdapter extends SearchResultPagerAdapter {

    public MinjieSearchResultPagerAdapter(Context context) {
        super(context);
    }

    @Override
    public void initResultData(SearchContent[] searchContents) {
        for(SearchContent searchContent : searchContents) {
            mSearchContentList.add(searchContent);
            mSearchResultMap.put(searchContent, new SearchShowItemList<>());
        }

    }


}
