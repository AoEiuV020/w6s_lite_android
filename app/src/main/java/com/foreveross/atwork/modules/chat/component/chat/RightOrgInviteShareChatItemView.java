package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
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
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.ShareMsgHelper;
import com.foreveross.atwork.utils.AvatarHelper;

public class RightOrgInviteShareChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;

    private ImageView mSelectView;

    private ImageView mAvatarView;

    private ImageView mCoverView;

    private TextView mDigestView;

    private TextView mTitleView;

    private FrameLayout mFlContentView;

    private ChatSendStatusView mChatSendStatusView;

    private ShareChatMessage mShareChatMessage;

    private Context mContext;

    private MessageSourceV2View mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;



    public RightOrgInviteShareChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
    }

    public RightOrgInviteShareChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
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
        return mShareChatMessage;
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_share_message_orginvite, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mSelectView = view.findViewById(R.id.right_share_select_org);
        mAvatarView = view.findViewById(R.id.chat_right_share_avatar_org);
        mFlContentView = view.findViewById(R.id.chat_right_share_content_org);
        mCoverView = view.findViewById(R.id.chat_right_share_cover_org);
        mDigestView = view.findViewById(R.id.chat_right_share_digest_org);
        mTitleView = view.findViewById(R.id.chat_right_share_title_org);
        mChatSendStatusView = view.findViewById(R.id.chat_right_share_send_status_org);
        mSelectView.setVisibility(GONE);

        mSourceView = view.findViewById(R.id.message_srouce_from);

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
    protected void registerListener() {
        super.registerListener();



        mFlContentView.setOnClickListener(v -> {
            if (mSelectMode) {
                mShareChatMessage.select = !mShareChatMessage.select;
                select(mShareChatMessage.select);
                return;
            }

            ShareMsgHelper.jumpOrgInvite(mContext, mShareChatMessage);

        });

        mFlContentView.setOnLongClickListener(v -> {
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

        AvatarHelper.setOrgIconrByOrgId(mShareChatMessage.getContent().mOrgAvatar, mCoverView, false, false);

        if (!TextUtils.isEmpty(mShareChatMessage.getContent().mOrgName)) {
            mDigestView.setText(mShareChatMessage.getContent().mOrgName);
        }

        if (!TextUtils.isEmpty(mShareChatMessage.getContent().mOrgName)) {

            mTitleView.setText(mShareChatMessage.getContent().mOrgName);
        }

        EmployeeManager.getInstance().setEmployeeNameForShareMsg(mShareChatMessage, mDigestView);


    }

    @Override
    protected void burnSkin() {
        //ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mLlContentView);

    }

    @Override
    protected void themeSkin() {
       // ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mLlContentView);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mFlContentView;
    }
}
