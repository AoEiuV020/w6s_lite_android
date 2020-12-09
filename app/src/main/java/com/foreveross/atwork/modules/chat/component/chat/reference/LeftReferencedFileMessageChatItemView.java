package com.foreveross.atwork.modules.chat.component.chat.reference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.modules.chat.component.chat.MessageSourceView;
import com.foreveross.atwork.modules.chat.component.chat.SomeStatusView;
import com.foreveross.atwork.modules.chat.component.chat.definition.IFileChatView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.presenter.FileChatViewRefreshUIPresenter;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class LeftReferencedFileMessageChatItemView extends LeftBasicReferenceUserChatItemView implements IFileChatView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private LinearLayout mLlContent;

    private ImageView mIvSelect;

    private ReferenceMessage mReferencedChatMessage;

    private TextView mTvAuthorName;

    private ImageView mIvCover;

    private TextView mTvFileName;

    private TextView mTvReply;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;
    private FrameLayout mFlReply;

    private LinearLayout mLlTags;


    public LeftReferencedFileMessageChatItemView(Context context) {
        super(context);
        findView();
        registerListener();

        mChatViewRefreshUIPresenter = new FileChatViewRefreshUIPresenter(this);

    }


    @Override
    protected View getMessageRootView() {
        return mVRoot;
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
        return mReferencedChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();


        mLlContent.setOnClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(false);
            if (mSelectMode) {
                mReferencedChatMessage.select = !mReferencedChatMessage.select;
                select(mReferencedChatMessage.select);

            } else {
                mChatItemClickListener.referenceClick(mReferencedChatMessage);
            }
        });

        mLlContent.setOnLongClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(true);
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.referenceLongClick(mReferencedChatMessage, anchorInfo);
                return true;
            }
            return false;
        });
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_referenced_file_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mTvName = view.findViewById(R.id.chat_left_text_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_text_sub_title);
        mIvAvatar = view.findViewById(R.id.chat_left_text_avatar);
        mLlContent = view.findViewById(R.id.ll_chat_left_content);
        mTvAuthorName = view.findViewById(R.id.tv_title);
        mIvCover = view.findViewById(R.id.iv_cover);
        mTvFileName = view.findViewById(R.id.tv_name);
        mTvReply = view.findViewById(R.id.tv_reply);
        mIvSelect = view.findViewById(R.id.left_text_select);

        mFlReply = view.findViewById(R.id.fl_reply);
        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);
        mLlTags = view.findViewById(R.id.ll_tags);
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvContentShow(mTvReply)
                .setIvStatus(mIvSomeStatus)
                .setTvTime(mTvTime)
                .setTvTimeTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
                .setMaxTvContentWidthBaseOn(mFlReply, mLlSomeStatusInfo)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mReferencedChatMessage = (ReferenceMessage) message;






    }


    @Override
    protected TextView getNameView() {
        return mTvName;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubTitle;
    }

    @androidx.annotation.Nullable
    @Override
    public View getTagLinerLayout() {
        return mLlTags;
    }

    @Override
    protected TextView getConfirmEmergencyView() {
        return null;
    }

    @Override
    protected void burnSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlContent);

    }

    @Override
    protected void themeSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlContent);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlContent;
    }


    @Override
    TextView getAuthorNameView() {
        return mTvAuthorName;
    }

    @Override
    TextView getReplyView() {
        return mTvReply;
    }

    @NotNull
    @Override
    public TextView titleView() {
        return mTvFileName;
    }

    @NotNull
    @Override
    public ImageView coverView() {
        return mIvCover;
    }

    @Nullable
    @Override
    public TextView sizeView() {
        return null;
    }
}
