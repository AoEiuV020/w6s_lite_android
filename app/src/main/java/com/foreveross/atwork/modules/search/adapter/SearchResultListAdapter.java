package com.foreveross.atwork.modules.search.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.component.SearchMoreView;
import com.foreveross.atwork.component.TitleItemView;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.modules.search.component.SearchListItemView;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchMoreItem;
import com.foreveross.atwork.modules.search.model.SearchTextTitleItem;
import com.foreveross.atwork.modules.search.model.SearchViewType;

import java.util.List;

/**
 * Created by dasunsy on 2017/12/13.
 */

public class SearchResultListAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private List<ShowListItem> mSearchItemList;

    private String mKey;

    private SearchAction mSearchAction = SearchAction.DEFAULT;

    private OnMainHandleClickListener mOnMainHandleClickListener;

    private boolean robotShow;

    public SearchResultListAdapter(Context context, List<ShowListItem> searchItemList,boolean robotShow) {
        this.mContext = context;
        this.mSearchItemList = searchItemList;
        this.robotShow = robotShow;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (SearchViewType.TITLE == viewType) {
            TitleItemView titleItemView = new TitleItemView(mContext);
            TextTitleViewHolder textTitleViewHolder = new TextTitleViewHolder(titleItemView);

            return textTitleViewHolder;
        }

        if (SearchViewType.MORE == viewType) {
            SearchMoreView moreView = new SearchMoreView(mContext);
            MoreViewHolder viewHolder = new MoreViewHolder(moreView);
            moreView.setOnClickListener(view ->{
                if(null != mOnMainHandleClickListener) {
                    int position = viewHolder.getAdapterPosition();
                    mOnMainHandleClickListener.onItemClick(position, mSearchItemList.get(position));
                }
            });

            return viewHolder;
        }


        SearchListItemView searchListItemView = new SearchListItemView(mContext, mSearchAction,robotShow);
        CommonItemViewHolder commonItemViewHolder = new CommonItemViewHolder(searchListItemView);

        searchListItemView.setOnClickListener(v -> {
            if(null != mOnMainHandleClickListener) {
                int position = commonItemViewHolder.getAdapterPosition();

                mOnMainHandleClickListener.onItemClick(position, mSearchItemList.get(position));
            }
        });
        return commonItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        ShowListItem searchItem = mSearchItemList.get(position);

        if (SearchViewType.TITLE == type) {
            SearchTextTitleItem textTitleItem = (SearchTextTitleItem) searchItem;

            TextTitleViewHolder textTitleViewHolder = (TextTitleViewHolder) holder;
            textTitleViewHolder.mTitleItemView.setTitle(searchItem.getTitle());

            if (textTitleItem.center) {
                textTitleViewHolder.mTitleItemView.center();
                textTitleViewHolder.mTitleItemView.white();
            } else {
                textTitleViewHolder.mTitleItemView.left();
                textTitleViewHolder.mTitleItemView.gray();
            }


            return;
        }

        if (SearchViewType.MORE == type) {
            SearchMoreItem moreItem = (SearchMoreItem)searchItem;
            MoreViewHolder moreViewHolder = (MoreViewHolder)holder;
            moreViewHolder.moreView.setTitle(moreItem.getTitle());
            return;
        }


        CommonItemViewHolder commonItemViewHolder = (CommonItemViewHolder) holder;
        if (position + 1 < getItemCount()) {
            ShowListItem nextItem = mSearchItemList.get(position+1);
            if (nextItem instanceof SearchMoreItem || nextItem instanceof  SearchTextTitleItem) {
                commonItemViewHolder.mSearchListItemView.hideLine(true);
            }
        } else {
            commonItemViewHolder.mSearchListItemView.hideLine(false);
        }
        commonItemViewHolder.mSearchListItemView.refreshView(searchItem, mKey);
    }

    @Override
    public int getItemCount() {
        return mSearchItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ShowListItem searchItem = mSearchItemList.get(position);
        if (searchItem instanceof SearchTextTitleItem) {
            return SearchViewType.TITLE;
        }

        if (searchItem instanceof SearchMoreItem) {
            return SearchViewType.MORE;
        }

        return SearchViewType.COMMON;
    }


    public void refreshData(List<ShowListItem> searchItemList) {
        mSearchItemList = searchItemList;
        notifyDataSetChanged();
    }

    public void setOnMainHandleClickListener(OnMainHandleClickListener onMainHandleClickListener) {
        mOnMainHandleClickListener = onMainHandleClickListener;
    }


    public void setSearchAction(SearchAction searchAction) {
        mSearchAction = searchAction;
    }

    public void setKey(String key) {
        this.mKey = key;
    }


    public static class TextTitleViewHolder extends RecyclerView.ViewHolder {

        public TitleItemView mTitleItemView;

        public TextTitleViewHolder(View itemView) {
            super(itemView);
            mTitleItemView = (TitleItemView) itemView;
        }
    }


    public static class CommonItemViewHolder extends RecyclerView.ViewHolder {

        public SearchListItemView mSearchListItemView;

        public CommonItemViewHolder(View itemView) {
            super(itemView);
            mSearchListItemView = (SearchListItemView) itemView;
        }
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder {
        public SearchMoreView moreView;

        public MoreViewHolder(@NonNull View itemView) {
            super(itemView);
            moreView = (SearchMoreView)itemView;
        }
    }


    public interface OnMainHandleClickListener {
        void onItemClick(int position, ShowListItem result);
    }


}
