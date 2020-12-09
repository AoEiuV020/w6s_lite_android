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
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper;



public class LeftMultipartChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mAvatarView;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private LinearLayout mLlMultipartContent;

    private TextView mTvTitle;

    private TextView mTvContent;

    private ImageView mSelectView;

    private MultipartChatMessage mMultipartChatMessage;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;

    public LeftMultipartChatItemView(Context context) {
        super(context);
        findView();
        registerListener();

    }

    @Override
    protected ImageView getAvatarView() {
        return mAvatarView;
    }

    @Override
    protected ImageView getSelectView() {
        return mSelectView;
    }

    @Override
    protected MessageSourceView getMessageSourceView() {
        return null;
    }

    @Override
    protected TextView getNameView() {
        return mTvName;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubTitle;
    }

    @Override
    public View getTagLinerLayout() {
        return mLlTags;
    }

    @Override
    protected TextView getConfirmEmergencyView() {
        return null;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mMultipartChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        mAvatarView.setOnClickListener(v -> {
            if (ParticipantType.App.equals(mMultipartChatMessage.mFromType)) {
                return;
            }
            if (!mSelectMode && mChatItemClickListener != null) {
                mChatItemClickListener.avatarClick(mMultipartChatMessage.from, mMultipartChatMessage.mFromDomain);
            }
        });

        mAvatarView.setOnLongClickListener(v -> {
            //只有群聊的时候才触发
            if (ParticipantType.Discussion.equals(mMultipartChatMessage.mToType)) {

                if (!mSelectMode && mChatItemClickListener != null) {
                    mChatItemClickListener.avatarLongClick(mMultipartChatMessage.from, mMultipartChatMessage.mFromDomain);
                }
            }
            return true;
        });

        setContentViewClick();

        setContentViewLongClick();

        setOnClickListener(v -> {

            mChatItemClickListener.hideAll();
            if (mSelectMode) {
                mMultipartChatMessage.select = !mMultipartChatMessage.select;
                select(mMultipartChatMessage.select);
            }
        });

    }

    private void setContentViewLongClick() {
        mLlMultipartContent.setOnLongClickListener(v -> {
            return handleLongClick();
        });


    }

    private void setContentViewClick() {
        mLlMultipartContent.setOnClickListener(v -> handleClick());
    }

    private void handleClick() {
        AutoLinkHelper.getInstance().setLongClick(false);
        if (mSelectMode) {
            mMultipartChatMessage.select = !mMultipartChatMessage.select;
            select(mMultipartChatMessage.select);

        } else {
            mChatItemClickListener.LeftMultipartClick(mMultipartChatMessage);
        }
    }

    private boolean handleLongClick() {
        AnchorInfo anchorInfo = getAnchorInfo();
        AutoLinkHelper.getInstance().setLongClick(true);
        if (!mSelectMode) {
            mChatItemLongClickListener.multipartLongClick(mMultipartChatMessage, anchorInfo);
            return true;
        }
        return false;
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_multipart_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mAvatarView = view.findViewById(R.id.chat_left_multipart_avatar);
        mTvName = view.findViewById(R.id.chat_left_multipart_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_multipart_sub_title);
        mLlMultipartContent = view.findViewById(R.id.ll_chat_left_content);
        mTvTitle = view.findViewById(R.id.tv_multipart_title);
        mTvContent = view.findViewById(R.id.tv_multipart_content);
        mSelectView = view.findViewById(R.id.left_multipart_select);
        mSelectView.setVisibility(GONE);


        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        mLlTags = view.findViewById(R.id.ll_tags);
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

        mMultipartChatMessage = (MultipartChatMessage) message;

        mTvTitle.setText(MultipartMsgHelper.getTitle(mMultipartChatMessage));
        mTvContent.setText(mMultipartChatMessage.mContent);

    }




    protected void themeSkin() {
//        mLlMultipartContent.setBackgroundResource(R.mipmap.bg_chat_left);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlMultipartContent;
    }


    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }
}

