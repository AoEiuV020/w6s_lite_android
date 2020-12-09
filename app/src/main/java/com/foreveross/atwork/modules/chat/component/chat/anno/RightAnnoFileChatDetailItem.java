package com.foreveross.atwork.modules.chat.component.chat.anno;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.component.chat.ChatFileItemView;
import com.foreveross.atwork.modules.chat.component.chat.MessageSourceView;
import com.foreveross.atwork.modules.chat.component.chat.RightBasicUserChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.SomeStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.utils.ThemeResourceHelper;


public class RightAnnoFileChatDetailItem extends RightBasicUserChatItemView {

    private View mVRoot;

    private LinearLayout mlLAnnoFileRoot;

    private FrameLayout mFlChatRightTextSendStatus;

    private AnnoFileTransferChatMessage mAnnoFileTransferChatMessage;

    private ChatFileItemView mChatFileItemView;

    private TextView mTvComment;

    private ImageView mSelectView;


    private ImageView mAvatarView;

    private ChatSendStatusView mChatSendStatusView;


    private MessageSourceView mSourceView;


    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;
    private FrameLayout mFlReply;

    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return mChatSendStatusView;
    }

    public RightAnnoFileChatDetailItem(Context context) {
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
        return mSourceView;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mAnnoFileTransferChatMessage;
    }


    @Override
    protected void registerListener() {
        super.registerListener();

        mChatFileItemView.setOnClickListener(v -> {
            if (mSelectMode) {
                mAnnoFileTransferChatMessage.select = !mAnnoFileTransferChatMessage.select;
                select(mAnnoFileTransferChatMessage.select);
            } else {
                mChatItemClickListener.fileClick(mAnnoFileTransferChatMessage);
            }

        });

        mChatFileItemView.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.fileLongClick(mAnnoFileTransferChatMessage, anchorInfo);
                return true;
            }
            return false;
        });

    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_file_transfer_message_anno_with_comment, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mlLAnnoFileRoot = view.findViewById(R.id.ll_anno_file_root);
        mFlChatRightTextSendStatus = view.findViewById(R.id.fl_chat_right_text_send_status);
        mChatFileItemView = view.findViewById(R.id.chat_right_file_line);
        mTvComment = view.findViewById(R.id.tv_comment);
        mSelectView = view.findViewById(R.id.right_file_select);
        mAvatarView = view.findViewById(R.id.chat_right_file_icon);
        mChatSendStatusView = view.findViewById(R.id.chat_right_file_status);

        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent_in_anno_file_view);
        mFlReply = mLlSomeStatusInfoWrapperParent.findViewById(R.id.fl_reply);
        mLlSomeStatusInfo = mLlSomeStatusInfoWrapperParent.findViewById(R.id.ll_some_status_info);
        mTvTime = mLlSomeStatusInfoWrapperParent.findViewById(R.id.tv_time);
        mIvSomeStatus = mLlSomeStatusInfoWrapperParent.findViewById(R.id.iv_some_status);

    }

    private void initFileTransferChatMessage(AnnoFileTransferChatMessage message) {
        this.mAnnoFileTransferChatMessage = message;
    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        initFileTransferChatMessage((AnnoFileTransferChatMessage) message);

        mChatFileItemView.setNormaTvColor("#ffffff");
        mChatFileItemView.setIllegalTvColor("#80ffffff");
        mChatFileItemView.allTvTextWhite();
        mChatFileItemView.refreshFileItem(mAnnoFileTransferChatMessage);
        mChatFileItemView.getLlSomeStatusInfo().setVisibility(GONE);

        mTvComment.setText(mAnnoFileTransferChatMessage.comment);

    }


    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvContentShow(mTvComment)
                .setIvStatus(mIvSomeStatus)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setTvTime(mTvTime)
                .setMaxTvContentWidthBaseOn(mFlReply, mLlSomeStatusInfo)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mChatFileItemView);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mlLAnnoFileRoot;
    }

    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mChatFileItemView);

    }
}
