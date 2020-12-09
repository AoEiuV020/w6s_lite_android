package com.foreveross.atwork.modules.main.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.modules.main.model.MainFabBottomPopItem;

import java.util.List;

/**
 * Created by dasunsy on 2017/12/10.
 */

public class MainFabBottomPopAdapter extends RecyclerView.Adapter {

    public Activity mContext;

    private final LayoutInflater mInflater;
    private List<MainFabBottomPopItem> mMainFabBottomPopItemList;

    private OnClickItemListener mOnClickItemListener;

    public MainFabBottomPopAdapter(Activity context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_fab_main_bottom_item, parent, false);
        if (3 < PersonalShareInfo.getInstance().getTextSizeLevel(mContext)) {
            int paddingLength = DensityUtil.dip2px(10);
            rootView.setPadding(paddingLength, 0, paddingLength, 0);

        } else {
            rootView.getLayoutParams().width = ScreenUtils.getScreenWidth(mContext) / 4;
        }

        MainFabBottomItemViewHolder mainFabBottomItemViewHolder = new MainFabBottomItemViewHolder(rootView);

        rootView.setOnClickListener(v -> {
            MainFabBottomPopItem mainFabBottomPopItem = mMainFabBottomPopItemList.get(mainFabBottomItemViewHolder.getAdapterPosition());
            if (null != mOnClickItemListener) {
                mOnClickItemListener.onClick(mainFabBottomPopItem.mMainFabBottomAction);
            }

        });

        return mainFabBottomItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainFabBottomItemViewHolder mainFabBottomItemViewHolder = (MainFabBottomItemViewHolder) holder;
        MainFabBottomPopItem mainFabBottomPopItem = mMainFabBottomPopItemList.get(position);

        mainFabBottomItemViewHolder.mTvTitleItem.setText(mainFabBottomPopItem.mTitle);
        mainFabBottomItemViewHolder.mIvIconItem.setImageResource(mainFabBottomPopItem.mResId);

    }

    @Override
    public int getItemCount() {
        if(null == mMainFabBottomPopItemList) {
            return 0;
        }

        return mMainFabBottomPopItemList.size();
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.mOnClickItemListener = onClickItemListener;
    }


    public void refreshData(List<MainFabBottomPopItem> mainFabBottomPopItemList) {
        this.mMainFabBottomPopItemList = mainFabBottomPopItemList;
        notifyDataSetChanged();
    }

    private static class MainFabBottomItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvTitleItem;
        public ImageView mIvIconItem;

        public MainFabBottomItemViewHolder(View itemView) {
            super(itemView);

            mIvIconItem = itemView.findViewById(R.id.iv_fab_item);
            mTvTitleItem = itemView.findViewById(R.id.tv_fab_item);
        }


    }

    public interface OnClickItemListener {
        void onClick(MainFabBottomPopItem.MainFabBottomAction action);
    }
}
