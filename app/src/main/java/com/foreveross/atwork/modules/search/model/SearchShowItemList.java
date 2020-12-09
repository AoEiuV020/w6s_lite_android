package com.foreveross.atwork.modules.search.model;

import com.foreveross.atwork.modules.search.fragment.NewSearchFragment;

import java.util.ArrayList;

public class SearchShowItemList<ShowListItem> extends ArrayList<ShowListItem> {

    public SearchShowItemList<ShowListItem> getThresholdList() {
        if (this.isEmpty() || this.size() <= NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD) {
            return this;
        }
        SearchShowItemList<ShowListItem> returnList = new SearchShowItemList<>();
        for(int i = 0; i < NewSearchFragment.SEARCH_NEED_MORE_EXACTLY_THRESHOLD; i++) {
            returnList.add(this.get(i));
        }
        return returnList;
    }
}
