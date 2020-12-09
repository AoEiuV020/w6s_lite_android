package com.foreveross.atwork.modules.chat.component.chat;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.ShareMsgHelper;
import com.foreveross.atwork.utils.AvatarHelper;

public class LeftOrgInviteShareChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mSelectView;

    private ImageView mAvatarView;

    private TextView mNameView;

    private TextView mTvSubTitle;

    private ImageView mCoverView;

    private TextView mDigestView;

    private TextView mTitleView;

    private LinearLayout mContentView;

    private ShareChatMessage mShareChatMessage;

    private Context mContext;

    private MessageSourceV2View mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;

    public LeftOrgInviteShareChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
    }

    public LeftOrgInviteShareChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        View view = inflater.inflate(R.layout.chat_left_share_message_orginvite, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mSelectView = view.findViewById(R.id.left_share_select_org);
        mAvatarView = view.findViewById(R.id.chat_left_share_avatar_org);
        mContentView = view.findViewById(R.id.chat_left_share_content_org);
        mNameView = view.findViewById(R.id.chat_left_share_org_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_share_org_sub_title);
        mCoverView = view.findViewById(R.id.chat_left_share_cover_org);
        mDigestView = view.findViewById(R.id.chat_left_share_digest_org);
        mTitleView = view.findViewById(R.id.chat_left_share_title_org);
        mSelectView.setVisibility(GONE);
        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        mLlTags = view.findViewById(R.id.ll_tags);
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
            ShareMsgHelper.jumpOrgInvite(mContext, mShareChatMessage);
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
    protected View getMessageRootView() {
        return mVRoot;
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
            mTitleView.setText(mContext.getString(R.string.invite_you, mShareChatMessage.getContent().mOrgName));
        }

        EmployeeManager.getInstance().setEmployeeNameForShareMsg(mShareChatMessage, mDigestView);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mContentView;
    }


}
