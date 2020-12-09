package com.foreveross.atwork.modules.chat.component.chat.anno;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage;
import com.foreveross.atwork.modules.chat.component.chat.ChatFileItemView;
import com.foreveross.atwork.modules.chat.component.chat.LeftBasicUserChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.MessageSourceView;
import com.foreveross.atwork.modules.chat.component.chat.SomeStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;

public class LeftAnnoFileChatDetailView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private LinearLayout mlLAnnoFileRoot;

    //头像
    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubName;

    private TextView mTvComment;

    private ChatFileItemView mChatFileItemView;

    private AnnoFileTransferChatMessage mAnnoFileTransferChatMessage;

    private ImageView mSelectView;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;
    private FrameLayout mFlReply;

    private LinearLayout mLlTags;



    public LeftAnnoFileChatDetailView(Context context) {
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
        View view = inflater.inflate(R.layout.chat_left_file_transfer_message_anno_with_comment, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mlLAnnoFileRoot = view.findViewById(R.id.ll_anno_file_root);
        mIvAvatar = view.findViewById(R.id.chat_left_file_avatar);
        mChatFileItemView = view.findViewById(R.id.chat_left_file_line);
        mTvComment = view.findViewById(R.id.tv_comment);
        mSelectView = view.findViewById(R.id.left_file_select);
        mTvName = view.findViewById(R.id.chat_left_file_username);
        mTvSubName = view.findViewById(R.id.chat_left_file_sub_title);
        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent_in_anno_file_view);
        mFlReply = mLlSomeStatusInfoWrapperParent.findViewById(R.id.fl_reply);
        mLlSomeStatusInfo = mLlSomeStatusInfoWrapperParent.findViewById(R.id.ll_some_status_info);
        mTvTime = mLlSomeStatusInfoWrapperParent.findViewById(R.id.tv_time);
        mIvSomeStatus = mLlSomeStatusInfoWrapperParent.findViewById(R.id.iv_some_status);

        mLlTags = view.findViewById(R.id.ll_tags);
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvContentShow(mTvComment)
                .setIvStatus(mIvSomeStatus)
                .setTvTime(mTvTime)
                .setTvTimeTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
                .setMaxTvContentWidthBaseOn(mFlReply, mLlSomeStatusInfo)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }


    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }


    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        this.mAnnoFileTransferChatMessage = (AnnoFileTransferChatMessage) message;


        if (mAnnoFileTransferChatMessage.fileStatus == null) {
            mAnnoFileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
        }

        mChatFileItemView.refreshFileItem(mAnnoFileTransferChatMessage);
        mChatFileItemView.getLlSomeStatusInfo().setVisibility(GONE);

        mTvComment.setText(mAnnoFileTransferChatMessage.comment);



    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mlLAnnoFileRoot;
    }


}
