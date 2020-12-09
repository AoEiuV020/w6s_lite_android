package com.foreveross.atwork.modules.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.chat.model.TranslateLanguageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzejie on 19/12/6.
 * Description:智能翻译
 */
public class TranslateLanguageRecyclerAdapter  extends  RecyclerView.Adapter<TranslateLanguageRecyclerAdapter.ViewHolder>  {

    private List<TranslateLanguageType> mLanguageList = new ArrayList<>();
    private Context mContext;
    private OnClickEventListener mOnClickEventListener;

    public TranslateLanguageRecyclerAdapter(Context context, List<TranslateLanguageType> languageList){
        mLanguageList = languageList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_language, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        registerListener(holder ,i);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TranslateLanguageType translateLanguageType = mLanguageList.get(i);
        viewHolder.tvTranslateLanguageChName.setText(translateLanguageType.getName());
        viewHolder.tvTranslateLanguageName.setText(translateLanguageType.getChName());
        if(translateLanguageType.getIsSelected()){
            viewHolder.ivIntelligentTranslation.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivIntelligentTranslation.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mLanguageList.size();
    }

    private void registerListener(ViewHolder holder, int i){
        holder.RlTranslateLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickEventListener.onClick(mLanguageList.get(holder.getPosition()).getShortName());

            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTranslateLanguageChName;
        TextView tvTranslateLanguageName;
        ImageView ivIntelligentTranslation;
        RelativeLayout RlTranslateLanguage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTranslateLanguageChName = (TextView) itemView.findViewById(R.id.tv_translate_language_ch_name);
            tvTranslateLanguageName = (TextView) itemView.findViewById(R.id.tv_translate_language_name);
            ivIntelligentTranslation = (ImageView) itemView.findViewById(R.id.iv_intelligent_translation);
            RlTranslateLanguage = (RelativeLayout) itemView.findViewById(R.id.rl_translate_language);

        }
    }

    public void setOnClickEventListener(OnClickEventListener onClickEventListener) {
        this.mOnClickEventListener = onClickEventListener;
    }

   public interface OnClickEventListener {
        void onClick(String languageSelected);
    }
}
