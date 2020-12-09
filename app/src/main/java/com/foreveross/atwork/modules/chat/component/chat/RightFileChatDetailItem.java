package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.utils.ThemeResourceHelper;


public class RightFileChatDetailItem extends RightBasicUserChatItemView {

    private View mVRoot;

    private FrameLayout mFlChatRightTextSendStatus;

    private FileTransferChatMessage mFileTransferChatMessage;

    private ChatFileItemView mChatFileItemView;

    private ImageView mSelectView;


    private ImageView mAvatarView;

    private ChatSendStatusView mChatSendStatusView;


    private MessageSourceView mSourceView;

    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return mChatSendStatusView;
    }

    public RightFileChatDetailItem(Context context) {
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
        return mFileTransferChatMessage;
    }


    @Override
    protected void registerListener() {
        super.registerListener();

        mChatFileItemView.setOnClickListener(v -> {
            if (mSelectMode) {
                mFileTransferChatMessage.select = !mFileTransferChatMessage.select;
                select(mFileTransferChatMessage.select);
            } else {
                mChatItemClickListener.fileClick(mFileTransferChatMessage);
            }

        });

        mChatFileItemView.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.fileLongClick(mFileTransferChatMessage, anchorInfo);
                return true;
            }
            return false;
        });

    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_file_transfer_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mFlChatRightTextSendStatus = view.findViewById(R.id.fl_chat_right_text_send_status);
        mChatFileItemView = view.findViewById(R.id.chat_right_file_line);
        mSelectView = view.findViewById(R.id.right_file_select);
        mAvatarView = view.findViewById(R.id.chat_right_file_icon);
        mChatSendStatusView = view.findViewById(R.id.chat_right_file_status);

        mSourceView = view.findViewById(R.id.message_srouce_from);

    }

    private void initFileTransferChatMessage(FileTransferChatMessage message) {
        this.mFileTransferChatMessage = message;
    }


    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mChatFileItemView.getLlSomeStatusInfoWrapperParent())
                .setIvStatus(mChatFileItemView.getIvSomeStatus())
                .setTvTime(mChatFileItemView.getTvTime())
                .setLlSomeStatusInfo(mChatFileItemView.getLlSomeStatusInfo());

    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        initFileTransferChatMessage((FileTransferChatMessage) message);

        mChatFileItemView.refreshFileItem(mFileTransferChatMessage);

//        if(shouldHoldAvatarView(message)) {
//            ViewUtil.setLeftMarginDp(mChatFileItemView, 64);
//            ViewUtil.setLeftMarginDp(mFlChatRightTextSendStatus, -59);
//
//        } else {
//            ViewUtil.setLeftMarginDp(mChatFileItemView,104);
//            ViewUtil.setLeftMarginDp(mFlChatRightTextSendStatus, -99);
//
//        }


    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mChatFileItemView);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mChatFileItemView.rlContentRoot;
    }

    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mChatFileItemView);

    }
}
