package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;

public class LeftFileChatDetailView extends LeftBasicUserChatItemView {

    private View mVRoot;

    //头像
    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubName;

    private ChatFileItemView mChatFileItemView;

    private FileTransferChatMessage mFileTransferChatMessage;

    private ImageView mSelectView;

    private MessageSourceView mSourceView;

    private LinearLayout mLlTags;



    public LeftFileChatDetailView(Context context) {
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
        return mSelectView;
    }

    @Override
    protected MessageSourceView getMessageSourceView() {
        return mSourceView;
    }

    @Override
    protected TextView getNameView() {
        return mTvName;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubName;
    }

    @Nullable
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
        View view = inflater.inflate(R.layout.chat_left_file_transfer_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_left_file_avatar);
        mChatFileItemView = view.findViewById(R.id.chat_left_file_line);
        mSelectView = view.findViewById(R.id.left_file_select);
        mTvName = view.findViewById(R.id.chat_left_file_username);
        mTvSubName = view.findViewById(R.id.chat_left_file_sub_title);
        mSourceView = view.findViewById(R.id.message_srouce_from);
        mLlTags = view.findViewById(R.id.ll_tags);

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

        this.mFileTransferChatMessage = (FileTransferChatMessage) message;


        if (mFileTransferChatMessage.fileStatus == null) {
            mFileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
        }

        mChatFileItemView.refreshFileItem(mFileTransferChatMessage);


//        if(shouldHoldAvatarView(message)) {
//            ViewUtil.setRightMarginDp(mChatFileItemView, 64);
//
//        } else {
//            ViewUtil.setRightMarginDp(mChatFileItemView,104);
//
//        }

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mChatFileItemView.rlContentRoot;
    }


}
