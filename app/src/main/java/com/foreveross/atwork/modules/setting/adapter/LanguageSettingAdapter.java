package com.foreveross.atwork.modules.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSetting;

import java.util.List;

/**
 * Created by dasunsy on 2017/4/20.
 */

public class LanguageSettingAdapter extends RecyclerView.Adapter {

    public Context mContext;

    private final LayoutInflater mInflater;
    private List<Integer> mLanguageList;
    private int mSelectLanguage;


    public LanguageSettingAdapter(Context context, List<Integer> voipMeetingGroupList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mLanguageList = voipMeetingGroupList;
        this.mSelectLanguage = CommonShareInfo.getLanguageSetting(context);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_language_setting, parent, false);
        LanguageSettingItemViewHolder itemViewHolder = new LanguageSettingItemViewHolder(rootView);

        rootView.setOnClickListener(v -> {
            int position = itemViewHolder.getAdapterPosition();
            mSelectLanguage = mLanguageList.get(position);
            notifyDataSetChanged();
        });

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LanguageSettingItemViewHolder languageSettingItemViewHolder = (LanguageSettingItemViewHolder) holder;
        int language = mLanguageList.get(position);
        String title = getLanguageTitle(language);


        languageSettingItemViewHolder.mTvTitle.setText(title);
        if(language == mSelectLanguage) {
            languageSettingItemViewHolder.mIvSelect.setVisibility(View.VISIBLE);

        } else {
            languageSettingItemViewHolder.mIvSelect.setVisibility(View.GONE);

        }

    }

    @NonNull
    private String getLanguageTitle(int language) {
        String title = StringUtils.EMPTY;
        if(LanguageSetting.AUTO == language) {
            title = mContext.getString(R.string.auto_system_language);

        } else if(LanguageSetting.SIMPLIFIED_CHINESE == language) {
            title = mContext.getString(R.string.simplified_chinese);

        } else if(LanguageSetting.TRADITIONAL_CHINESE == language) {
            title = mContext.getString(R.string.traditional_chinese);

        } else if(LanguageSetting.ENGLISH == language) {
            title = mContext.getString(R.string.english);
        }
        return title;
    }

    public int getSelectLanguage() {
        return mSelectLanguage;
    }


    @Override
    public int getItemCount() {
        return mLanguageList.size();
    }

    private static class LanguageSettingItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvTitle;
        public ImageView mIvSelect;

        public LanguageSettingItemViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mIvSelect = itemView.findViewById(R.id.arrow_right);


        }
    }

}
