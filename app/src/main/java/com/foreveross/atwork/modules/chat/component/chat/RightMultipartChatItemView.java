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
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;


public class RightMultipartChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;

    private ImageView mAvatarView;

    private LinearLayout mLlMultipartContent;

    private TextView mTvTitle;

    private TextView mTvContent;

    private ChatSendStatusView mChatSendStatusView;

    private ImageView mSelectView;

    private MultipartChatMessage mMultipartMessage;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;



    public RightMultipartChatItemView(Context context) {
        super(context);
        findView();
        registerListener();

    }

    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return mChatSendStatusView;
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
    protected ChatPostMessage getMessage() {
        return mMultipartMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        setContentViewClick();

        setContentViewLongClick();
    }

    private void setContentViewLongClick() {
        mLlMultipartContent.setOnLongClickListener(v -> {
            return handleLongClick();
        });


    }

    private void setContentViewClick() {
        mLlMultipartContent.setOnClickListener(v -> handleClick());
    }

    private boolean handleLongClick() {
        AutoLinkHelper.getInstance().setLongClick(true);
        AnchorInfo anchorInfo = getAnchorInfo();
        if (!mSelectMode) {
            mChatItemLongClickListener.multipartLongClick(mMultipartMessage, anchorInfo);
            return true;
        }
        return false;
    }

    private void handleClick() {
        AutoLinkHelper.getInstance().setLongClick(false);
        if (mSelectMode) {
            mMultipartMessage.select = !mMultipartMessage.select;
            select(mMultipartMessage.select);
        } else {

            mChatItemClickListener.RightMultipartClick(mMultipartMessage);
        }
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_multipart_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mAvatarView = view.findViewById(R.id.chat_right_multipart_avatar);
        mTvTitle = view.findViewById(R.id.tv_multipart_title);
        mTvContent = view.findViewById(R.id.tv_multipart_content);
        mLlMultipartContent = view.findViewById(R.id.ll_chat_right_content);
        mChatSendStatusView = view.findViewById(R.id.chat_right_multipart_send_status);
        mSelectView = view.findViewById(R.id.right_multipart_select);

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

        mMultipartMessage = (MultipartChatMessage) message;

        mTvTitle.setText(MultipartMsgHelper.getTitle(mMultipartMessage));
        mTvContent.setText(mMultipartMessage.mContent);

    }



    @Override
    protected void burnSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlMultipartContent);

    }

    @Override
    protected void themeSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlMultipartContent);

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
