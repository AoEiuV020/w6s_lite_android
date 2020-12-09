package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class RightBusinessCardShareChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;

    private FrameLayout mFlChatRightShareSendStatusCard;

    private ImageView mSelectView;

    private ImageView mAvatarView;

    private ImageView mCoverView;

    private TextView mTitleView;

    private ImageView mIvGender;

    private TextView mTvJobTitle;

    private TextView mTvSignature;

    private FrameLayout mFlContentView;

    private ChatSendStatusView chatSendStatusView;

    private ShareChatMessage mShareChatMessage;

    private Context mContext;

    private MessageSourceV2View mSourceView;

    private String mCoverAvatar;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return chatSendStatusView;
    }

    public RightBusinessCardShareChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
    }

    public RightBusinessCardShareChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setIvStatus(mIvSomeStatus)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setTvTime(mTvTime)
                .setLlSomeStatusInfo(mLlSomeStatusInfo)
                .setSomeStatusInfoAreaGrayBg(getContext());

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
        return null;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mShareChatMessage;
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_share_message_businesscard_new, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mFlChatRightShareSendStatusCard = view.findViewById(R.id.fl_chat_right_share_send_status_card);
        mSelectView = view.findViewById(R.id.right_share_select_card);
        mAvatarView = view.findViewById(R.id.chat_right_share_avatar_card);
        mFlContentView = view.findViewById(R.id.chat_right_share_content_card);
        mCoverView = view.findViewById(R.id.chat_right_share_cover_card);
        mTitleView = view.findViewById(R.id.chat_right_share_name_card);
        mIvGender = view.findViewById(R.id.iv_gender);
        mTvJobTitle = view.findViewById(R.id.tv_job_title);
        mTvSignature = view.findViewById(R.id.tv_signature);
        chatSendStatusView = view.findViewById(R.id.chat_right_share_send_status_card);
        mSelectView.setVisibility(GONE);
        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

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
            User user = UserManager.getInstance().queryLocalUser(mShareChatMessage.getContent().mShareUserId);
            if (user == null) {
                user = new User();
                user.mUserId = mShareChatMessage.getContent().mShareUserId;
                user.mAvatar = mShareChatMessage.getContent().mShareUserAvatar;
                user.mName = mShareChatMessage.getContent().mShareUserName;
                user.mDomainId = mShareChatMessage.getContent().mShareDomainId;
            }
            mContext.startActivity(PersonalInfoActivity.getIntent(mContext, user));

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

        setCardAvatarByAvaId(mCoverView, mShareChatMessage.getContent().mShareUserAvatar);

        if (!StringUtils.isEmpty(mShareChatMessage.getContent().mShareUserName)) {
            mTitleView.setText(mShareChatMessage.getContent().mShareUserName);
        }

        if (!StringUtils.isEmpty(mShareChatMessage.getContent().mShareUserGender)) {

            boolean setGender = false;
            if("male".equalsIgnoreCase(mShareChatMessage.getContent().mShareUserGender)) {
                mIvGender.setImageResource(R.mipmap.icon_gender_male);
                setGender = true;
            }

            if("female".equalsIgnoreCase(mShareChatMessage.getContent().mShareUserGender)) {
                mIvGender.setImageResource(R.mipmap.icon_gender_female);

                setGender = true;

            }

            if(!setGender) {
                mIvGender.setImageResource(0);

            }


        }

        if (!StringUtils.isEmpty(mShareChatMessage.getContent().mShareUserJobTitle)) {
            mTvJobTitle.setText(mShareChatMessage.getContent().mShareUserJobTitle);
            mTvJobTitle.setVisibility(View.VISIBLE);
        } else {
            mTvJobTitle.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(mShareChatMessage.getContent().mShareUserSignature)) {
            mTvSignature.setText(mShareChatMessage.getContent().mShareUserSignature);
            mTvSignature.setVisibility(View.VISIBLE);
        } else {
            mTvSignature.setVisibility(View.GONE);

        }

        mSourceView.refreshSourceFlag(R.string.personal_card, R.mipmap.icon_business_card_gray);

//        if(shouldHoldAvatarView(message)) {
//            ViewUtil.setLeftMarginDp(mLlContentView, 70);
//            ViewUtil.setLeftMarginDp(mFlChatRightShareSendStatusCard, -55);
//
//        } else {
//            ViewUtil.setLeftMarginDp(mLlContentView,110);
//            ViewUtil.setLeftMarginDp(mFlChatRightShareSendStatusCard, -95);
//
//        }


    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mFlContentView);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mFlContentView;
    }

    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewWhiteBgDrawable(mFlContentView);

    }


    private void setCardAvatarByAvaId(ImageView avatarView, String avatar) {
        boolean isViewNeedReset = isViewNeedReset(avatar);

        mCoverAvatar = avatar;

        if(StringUtils.isEmpty(avatar)) {
            ImageCacheHelper.setImageResource(avatarView, R.mipmap.icon_default_user_square);
            return;
        }


        int loadingId = -1;
        if (isViewNeedReset) {
            loadingId = R.mipmap.icon_default_user_square;
        }

        DisplayImageOptions options = getRectOptions(R.mipmap.icon_default_user_square, loadingId);
        ImageCacheHelper.displayImageByMediaId(avatar, avatarView, options);

    }

    private boolean isViewNeedReset(String avatar) {
        boolean viewNeedReset = true;
        if(null != this.mCoverAvatar && this.mCoverAvatar.equals(avatar)) {
            viewNeedReset = false;
        }
        return viewNeedReset;
    }

    private DisplayImageOptions getRectOptions(int resId, int loadingId) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        if (-1 == loadingId) {
            builder.showImageOnLoading(null);
        } else {
            builder.showImageOnLoading(loadingId);
        }

        if (-1 != resId) {
            builder.showImageForEmptyUri(resId);
            builder.showImageOnFail(resId);
        }

        return builder.build();
    }

}
