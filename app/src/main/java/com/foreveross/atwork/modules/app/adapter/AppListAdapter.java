package com.foreveross.atwork.modules.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.List;

/**
 * Created by dasunsy on 2017/7/6.
 */

public class AppListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<App> mAppList;
    private final LayoutInflater mInflater;

    public AppListAdapter(Context context, List<App> appList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mAppList = appList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_list_app, parent, false);
        AppItemViewHolder holder = new AppItemViewHolder(rootView);
        setItemClickListener(rootView, holder);
        return holder;
    }

    private void setItemClickListener(View rootView, AppItemViewHolder holder) {
        rootView.setOnClickListener(v -> {
            App serviceApp = mAppList.get(holder.getAdapterPosition());

            EntrySessionRequest entrySessionRequest = EntrySessionRequest
                    .newRequest(SessionType.Service, serviceApp)
                    .setOrgId(serviceApp.mOrgId);

            ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);
            Intent intent = ChatDetailActivity.getIntent(mContext, ((ServiceApp) serviceApp).mAppId);
            intent.putExtra(ChatDetailFragment.RETURN_BACK, true);
            mContext.startActivity(intent);
            if (mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        App app = mAppList.get(position);
        AppItemViewHolder itemViewHolder = (AppItemViewHolder) holder;

        AvatarHelper.setAppAvatarById(itemViewHolder.mIvAvatar, app.mAppId, app.mOrgId, true, true);
        itemViewHolder.mTvTitle.setText(app.getTitleI18n(BaseApplicationLike.baseContext));

    }

    @Override
    public int getItemCount() {
        return mAppList.size();
    }

    public void refreshAppList(List<App> appList) {
        mAppList = appList;
        notifyDataSetChanged();
    }

    private static class AppItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvAvatar;
        public TextView mTvTitle;

        public AppItemViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.iv_header_avatar);
            mTvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
