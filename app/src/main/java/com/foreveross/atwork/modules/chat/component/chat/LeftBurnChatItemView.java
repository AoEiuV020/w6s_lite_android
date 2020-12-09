package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;


public class LeftBurnChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private LinearLayout mLlChatLeftBurnContent;

    private TextView mTvContent;

    private ImageView mIvSelect;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private ChatPostMessage mChatMessage;

    public LeftBurnChatItemView(Context context) {
        super(context);
        findView();
        registerListener();
    }

    @Override
    protected ImageView getAvatarView() {
        return mIvAvatar;
    }

    @Override
    protected ImageView getSelectView() {
        return mIvSelect;
    }

    @Override
    protected MessageSourceView getMessageSourceView() {
        return null;
    }

    @Override
    protected TextView getNameView() {
        return null;
    }

    @Override
    protected TextView getSubTitleView() {
        return null;
    }

    @Nullable
    @Override
    public View getTagLinerLayout() {
        return null;
    }

    @Override
    protected TextView getConfirmEmergencyView() {
        return null;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        mTvContent.setOnClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(false);
            if (mSelectMode) {
                mChatMessage.select = !mChatMessage.select;
                select(mChatMessage.select);
            } else {
                mChatItemClickListener.burnClick(mChatMessage);
            }
        });

        mTvContent.setOnLongClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(true);

            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.burnLongClick(mChatMessage, anchorInfo);
                return true;
            }
            return false;
        });
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_burn_message, this);
        mVRoot = view.findViewById(R.id.rl_root);

        mIvAvatar = view.findViewById(R.id.iv_chat_left_burn_avatar);
        mLlChatLeftBurnContent = view.findViewById(R.id.ll_chat_left_burn_content);
        mTvContent = view.findViewById(R.id.tv_chat_left_burn_content);
        mIvSelect = view.findViewById(R.id.iv_left_burn_select);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setIvStatus(mIvSomeStatus)
                .setTvTime(mTvTime)
                .setTvTimeTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_999))
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }


    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mChatMessage = message;
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return  mLlChatLeftBurnContent;
    }


    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }
}
