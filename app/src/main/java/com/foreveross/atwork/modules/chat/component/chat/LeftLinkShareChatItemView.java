package com.foreveross.atwork.modules.chat.component.chat;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.modules.chat.component.chat.definition.IShareLinkChatView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.presenter.ShareLinkChatViewRefreshUIPresenter;
import com.foreveross.atwork.modules.chat.util.ShareMsgHelper;

import org.jetbrains.annotations.NotNull;

public class LeftLinkShareChatItemView extends LeftBasicUserChatItemView implements IShareLinkChatView {

    private View mVRoot;

    private ImageView mSelectView;

    private ImageView mAvatarView;

    private TextView mNameView;

    private TextView mTvSubTitle;

    private ImageView mCoverView;

    private TextView mTitleView;

    private LinearLayout mContentView;

    private ShareChatMessage mShareChatMessage;

    private Context mContext;

    private MessageSourceView mSourceView;
    private TextView mSummaryView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;


    public LeftLinkShareChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();

        mChatViewRefreshUIPresenter = new ShareLinkChatViewRefreshUIPresenter(this);
    }

    public LeftLinkShareChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
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
    protected TextView getNameView() {
        return mNameView;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubTitle;
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
        return mShareChatMessage;
    }



    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_share_message_link_new, this);

        mVRoot = view.findViewById(R.id.rl_root);
        mSelectView = view.findViewById(R.id.left_share_select);
        mAvatarView = view.findViewById(R.id.chat_left_share_avatar);
        mContentView = view.findViewById(R.id.chat_left_share_content);
        mNameView = view.findViewById(R.id.chat_left_share_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_share_sub_title);
        mCoverView = view.findViewById(R.id.chat_left_share_cover);
        mTitleView = view.findViewById(R.id.chat_left_share_title);
        mSummaryView = view.findViewById(R.id.link_summary);
        mSelectView.setVisibility(GONE);
        mSourceView = view.findViewById(R.id.message_source_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        mLlTags = view.findViewById(R.id.ll_tags);

        mTvTime.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_999));

    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setIvStatus(mIvSomeStatus)
                .setTvTime(mTvTime)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    protected void registerListener() {
        super.registerListener();

        mContentView.setOnClickListener(v -> {
            if (mSelectMode) {
                mShareChatMessage.select = !mShareChatMessage.select;
                select(mShareChatMessage.select);
                return;
            }
            ShareMsgHelper.jumpLinkShare(mContext, mShareChatMessage);
        });

        mContentView.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.shareLongClick(mShareChatMessage, anchorInfo);
                return true;
            }
            return false;
        });

    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mShareChatMessage = (ShareChatMessage) message;


    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mContentView;
    }


    @NotNull
    @Override
    public TextView titleView() {
        return mTitleView;
    }

    @NotNull
    @Override
    public TextView summaryView() {
        return mSummaryView;
    }

    @NotNull
    @Override
    public ImageView coverView() {
        return mCoverView;
    }
}

