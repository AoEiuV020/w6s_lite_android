package com.foreveross.atwork.modules.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.search.util.SearchHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dasunsy on 2017/12/13.
 */

public class SearchWelcomeListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final LayoutInflater mInflater;

    private SearchContent[] mSearchContents;

    public SearchWelcomeListAdapter(Context context, SearchContent[] searchContents) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        initSearchContent(searchContents);
    }

    private void initSearchContent(SearchContent[] searchContents) {
        List<SearchContent> searchContentList =  new ArrayList<>(Arrays.asList(searchContents));
        searchContentList.remove(SearchContent.SEARCH_ALL);
        this.mSearchContents = searchContentList.toArray(new SearchContent[0]);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_search_welcome_entry, parent, false);
        SearchWelcomeItemHolder searchWelcomeItemHolder = new SearchWelcomeItemHolder(rootView);

        searchWelcomeItemHolder.mVDivider.getLayoutParams().height = ViewUtil.getTextHeight(searchWelcomeItemHolder.mTvEntry);

        return searchWelcomeItemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchWelcomeItemHolder searchWelcomeItemHolder = (SearchWelcomeItemHolder) holder;
        SearchContent searchContent = mSearchContents[position];

        searchWelcomeItemHolder.mTvEntry.setText(SearchHelper.getSearchContentTip(searchContent));

        if(0 == (position + 1) % mSearchContents.length ) {
            searchWelcomeItemHolder.mVDivider.setVisibility(View.GONE);

        } else {
            searchWelcomeItemHolder.mVDivider.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return mSearchContents.length;
    }


    public static class SearchWelcomeItemHolder extends RecyclerView.ViewHolder {

        public TextView mTvEntry;
        public View mVDivider;

        public SearchWelcomeItemHolder(View itemView) {
            super(itemView);
            mTvEntry = itemView.findViewById(R.id.tv_welcome_entry);
            mVDivider = itemView.findViewById(R.id.v_divider);
        }

    }

}
