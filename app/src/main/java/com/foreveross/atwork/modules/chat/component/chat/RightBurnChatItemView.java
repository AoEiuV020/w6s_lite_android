package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;


public class RightBurnChatItemView extends RightBasicUserChatItemView{

    private View mVRoot;

    private ImageView mIvAvatar;

    private LinearLayout mLlContent;

    private TextView mTvContent;

    private ImageView mIvSelect;

    private ChatSendStatusView mChatSendStatusView;

    private ChatPostMessage mChatMessage;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;


    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return mChatSendStatusView;
    }

    public RightBurnChatItemView(Context context) {
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
        View view = inflater.inflate(R.layout.chat_right_burn_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.iv_chat_right_burn_avatar);
        mLlContent = view.findViewById(R.id.ll_tv_content);
        mTvContent = view.findViewById(R.id.tv_chat_right_burn_content);
        mIvSelect = view.findViewById(R.id.iv_right_burn_select);
        mChatSendStatusView = view.findViewById(R.id.chat_right_image_status);

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
                .setTvTimeTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white))
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
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

    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlContent);
    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlContent);
//        mTvContent.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color));

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlContent;
    }


    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }
}
