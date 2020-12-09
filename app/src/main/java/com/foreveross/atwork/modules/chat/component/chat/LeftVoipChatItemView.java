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

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;


public class LeftVoipChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private LinearLayout mLlChatLeftVoipContent;

    private TextView mTvContent;

    private ImageView mIvSelect;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private VoipChatMessage mVoipChatMessage;

    public LeftVoipChatItemView(Context context) {
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
            AnchorInfo anchorInfo = getAnchorInfo();
            AutoLinkHelper.getInstance().setLongClick(true);
            if (!mSelectMode) {
                mChatItemLongClickListener.voipLongClick(mVoipChatMessage, anchorInfo);
                return true;
            }
            return false;
        });
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_voip_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.iv_chat_left_voip_avatar);
        mLlChatLeftVoipContent = view.findViewById(R.id.ll_chat_left_voip_content);
        mTvContent = view.findViewById(R.id.tv_chat_left_voip_content);
        mIvSelect = view.findViewById(R.id.iv_left_voip_select);
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
                .setTvTimeTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
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
            mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_bizconf_blue), null, null, null);
            return;
        }

        if (MeetingStatus.SUCCESS.equals(mVoipChatMessage.mStatus)) {
            if (VoipType.VIDEO.equals(mVoipChatMessage.mVoipType)) {

                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_video_blue), null, null, null);
            } else {
                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_audio_blue), null, null, null);

            }

        } else {
            if (VoipType.VIDEO.equals(mVoipChatMessage.mVoipType)) {

                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_video_blue), null, null, null);
            } else {
                mTvContent.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.mipmap.icon_voip_audio_blue), null, null, null);

            }
        }
    }


    protected void burnSkin() {
//        mTvContent.setBackgroundResource(R.mipmap.bg_chat_left_burn);

    }

    protected void themeSkin() {
//        mTvContent.setBackgroundResource(R.mipmap.bg_chat_left);
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlChatLeftVoipContent;
    }


    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }
}
