package com.foreveross.atwork.modules.chat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.modules.chat.component.ChatMoreView;
import com.foreveross.atwork.modules.chat.model.ChatMoreItem;
import com.foreveross.atwork.modules.chat.util.KeyboardHeightHelper;

import java.util.List;

/**
 * Created by dasunsy on 2017/11/13.
 */

public class ChatMorePagerItemGroupAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ChatMoreItem> mChatMoreItemList;

    private ChatMoreView.ChatMoreViewListener mChatMoreViewListener;

    private boolean mBurnMode;

    private int mSlideLayoutHeight = 0;

    public ChatMorePagerItemGroupAdapter(Context context , List<ChatMoreItem> chatMoreItemList) {
        this.mContext = context;
        this.mChatMoreItemList = chatMoreItemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_more, parent, false);

        ChatMoreItemViewHolder chatMoreItemViewHolder = new ChatMoreItemViewHolder(view);
        chatMoreItemViewHolder.mLlRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                chatMoreItemViewHolder.mLlRoot.getViewTreeObserver().removeOnPreDrawListener(this);

                int top = (getKeyBoardHeight() - chatMoreItemViewHolder.mLlRoot.getHeight() * 2 - mSlideLayoutHeight) / 3;
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) chatMoreItemViewHolder.mLlRoot.getLayoutParams();
                layoutParams.topMargin = top;

                chatMoreItemViewHolder.mLlRoot.setLayoutParams(layoutParams);

                return false;
            }
        });

        view.setOnClickListener(v -> {
            if(null == mChatMoreViewListener) {
                return;
            }

            int position = chatMoreItemViewHolder.getAdapterPosition();
            ChatMoreItem chatMoreItem = mChatMoreItemList.get(position);

            if(ChatMoreItem.ChatMoreAction.PHOTO == chatMoreItem.mChatMoreAction) {
                mChatMoreViewListener.photoClick();
                return;
            }

            if(ChatMoreItem.ChatMoreAction.CAMERA == chatMoreItem.mChatMoreAction) {
                mChatMoreViewListener.cameraClick();
                return;
            }

            if (ChatMoreItem.ChatMoreAction.LOCATION.equals(chatMoreItem.mChatMoreAction)) {
                mChatMoreViewListener.locationClick();
                return;
            }

            if(ChatMoreItem.ChatMoreAction.FILE == chatMoreItem.mChatMoreAction) {
                mChatMoreViewListener.fileClick();
                return;
            }

            if(ChatMoreItem.ChatMoreAction.MICRO_VIDEO == chatMoreItem.mChatMoreAction) {
                if(CommonUtil.isFastClick(3000)) {
                    return;
                }


                mChatMoreViewListener.microVideoClick();
                return;
            }

            if(ChatMoreItem.ChatMoreAction.CARD == chatMoreItem.mChatMoreAction) {
                mChatMoreViewListener.cardClick();
                return;
            }

            if(ChatMoreItem.ChatMoreAction.VOIP == chatMoreItem.mChatMoreAction) {
                mChatMoreViewListener.voipConfClick();
                return;
            }


            if(ChatMoreItem.ChatMoreAction.Meeting == chatMoreItem.mChatMoreAction) {
                mChatMoreViewListener.meetingClick();
                return;
            }

        });


        return chatMoreItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMoreItemViewHolder chatMoreItemViewHolder = (ChatMoreItemViewHolder) holder;
        ChatMoreItem chatMoreItem = mChatMoreItemList.get(position);

        chatMoreItemViewHolder.mIvCover.setImageResource(chatMoreItem.mResId);
        chatMoreItemViewHolder.mTvLabel.setText(chatMoreItem.mText);

        if(mBurnMode) {
            switch (chatMoreItem.mChatMoreAction) {
                case PHOTO:
                case CAMERA:
                    chatMoreItemViewHolder.mLlRoot.setAlpha(1f);
                    break;
                default:
                    chatMoreItemViewHolder.mLlRoot.setAlpha(0.3f);
            }

            chatMoreItemViewHolder.mTvLabel.setTextColor(Color.WHITE);


        } else {
            chatMoreItemViewHolder.mLlRoot.setAlpha(1f);
            chatMoreItemViewHolder.mTvLabel.setTextColor(Color.parseColor("#46484a"));

        }
    }

    @Override
    public int getItemCount() {
        return mChatMoreItemList.size();
    }

    private static class ChatMoreItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mLlRoot;
        public ImageView mIvCover;
        public TextView mTvLabel;


        public ChatMoreItemViewHolder(View itemView) {
            super(itemView);
            mLlRoot = itemView.findViewById(R.id.ll_item_root);
            mIvCover = itemView.findViewById(R.id.iv_cover);
            mTvLabel = itemView.findViewById(R.id.tv_discussion_label_in_basic_info_area);


        }
    }

    public void setChatMoreViewListener(ChatMoreView.ChatMoreViewListener chatMoreViewListener) {
        this.mChatMoreViewListener = chatMoreViewListener;
    }

    public void setBurnMode(boolean burnMode) {
        this.mBurnMode = burnMode;
    }

    public void setSlideLayoutHeight(int slideLayoutHeight) {
        mSlideLayoutHeight = slideLayoutHeight;
    }

    private int getKeyBoardHeight() {
        return KeyboardHeightHelper.getFunctionAreaShowHeight(CommonShareInfo.getKeyBoardHeight(mContext));
    }

}
