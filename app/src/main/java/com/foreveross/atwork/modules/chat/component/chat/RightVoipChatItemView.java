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
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;


public class RightVoipChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private LinearLayout mLlChatRightVoipContent;
    private TextView mTvContent;

    private ImageView mIvSelect;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private VoipChatMessage mVoipChatMessage;


    public RightVoipChatItemView(Context context) {
        super(context);
        findView();
        registerListener();
    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return null;
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
        return mVoipChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();


        mTvContent.setOnClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(false);
            if (mSelectMode) {
                mVoipChatMessage.select = !mVoipChatMessage.select;
                select(mVoipChatMessage.select);

            } else {
                mChatItemClickListener.voipClick(mVoipChatMessage);
            }
        });

        mTvContent.setOnLongClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(true);
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.voipLongClick(mVoipChatMessage, anchorInfo);
                return true;
            }
            return false;
        });
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_voip_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.iv_chat_right_voip_avatar);
        mLlChatRightVoipContent = view.findViewById(R.id.ll_chat_right_voip_content);
        mTvContent = view.findViewById(R.id.tv_chat_right_voip_content);
        mIvSelect = view.findViewById(R.id.iv_right_voip_select);
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
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mVoipChatMessage = (VoipChatMessage) message;

        mTvContent.setText(mVoipChatMessage.mContent);


        refreshIconView();


    }

    private void refreshIconView() {
        if(mVoipChatMessage.isZoomProduct()) {
            mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_bizconf_white), null, null, null);
            return;
        }


        if (MeetingStatus.SUCCESS.equals(mVoipChatMessage.mStatus)) {
            if (VoipType.VIDEO.equals(mVoipChatMessage.mVoipType)) {

                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_video_white), null, null, null);
            } else {
                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_audio_white), null, null, null);

            }

        } else {
            if (VoipType.VIDEO.equals(mVoipChatMessage.mVoipType)) {

                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_video_white), null, null, null);
            } else {
                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_audio_white), null, null, null);

            }
        }
    }


    @Override
    protected void burnSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mTvContent);

    }

    @Override
    protected void themeSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mTvContent);
        mTvContent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlChatRightVoipContent;
    }

    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }
}
