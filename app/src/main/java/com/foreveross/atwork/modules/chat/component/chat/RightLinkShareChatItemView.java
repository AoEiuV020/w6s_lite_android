package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.component.chat.definition.IShareLinkChatView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.presenter.ShareLinkChatViewRefreshUIPresenter;
import com.foreveross.atwork.modules.chat.util.ShareMsgHelper;

import org.jetbrains.annotations.NotNull;

public class RightLinkShareChatItemView extends RightBasicUserChatItemView implements IShareLinkChatView {

    private View mVRoot;

    private ImageView mSelectView;

    private ImageView mAvatarView;

    private ImageView mCoverView;

    private TextView mTitleView;

    private TextView mSummaryView;

    private LinearLayout mLlContentView;

    private ChatSendStatusView mChatSendStatusView;

    private ShareChatMessage mShareChatMessage;


    private Context mContext;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;


    public RightLinkShareChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();

        mChatViewRefreshUIPresenter = new ShareLinkChatViewRefreshUIPresenter(this);

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
        return mSourceView;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mShareChatMessage;
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_share_message_link_new, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mSelectView = view.findViewById(R.id.right_share_select);
        mAvatarView = view.findViewById(R.id.chat_right_share_avatar);
        mLlContentView = view.findViewById(R.id.chat_right_share_content);
        mCoverView = view.findViewById(R.id.chat_right_share_cover);
        mTitleView = view.findViewById(R.id.chat_right_share_title);
        mSummaryView = view.findViewById(R.id.link_summary);
        mChatSendStatusView = view.findViewById(R.id.chat_right_share_send_status);
        mSelectView.setVisibility(GONE);

        mSourceView = view.findViewById(R.id.message_source_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

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
    protected View getMessageRootView() {
        return mVRoot;
    }


    @Override
    protected void registerListener() {
        super.registerListener();


        mLlContentView.setOnClickListener(v -> {
            if (mSelectMode) {
                mShareChatMessage.select = !mShareChatMessage.select;
                select(mShareChatMessage.select);
                return;
            }

            ShareMsgHelper.jumpLinkShare(mContext, mShareChatMessage);
        });

        mLlContentView.setOnLongClickListener(v -> {
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




    @Override
    protected void burnSkin() {
        super.burnSkin();
        //ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mLlContentView);
    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        //ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mLlContentView);
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlContentView;
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
