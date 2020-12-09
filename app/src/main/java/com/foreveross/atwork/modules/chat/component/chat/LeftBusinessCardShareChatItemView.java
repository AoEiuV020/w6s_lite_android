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
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class LeftBusinessCardShareChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mSelectView;

    private ImageView mIvAvatar;

    private ImageView mIvGender;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private ImageView mCoverView;

    private TextView mTitleView;

    private TextView mTvJobTitle;

    private TextView mTvSignature;

    private FrameLayout mContentView;

    private ShareChatMessage mShareChatMessage;

    private Context mContext;

    private MessageSourceV2View mSourceView;

    private String mCoverAvatar;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;


    public LeftBusinessCardShareChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
    }

    public LeftBusinessCardShareChatItemView(Context context, AttributeSet attrs) {
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
    protected ImageView getAvatarView() {
        return mIvAvatar;
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
        return mTvName;
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
        View view = inflater.inflate(R.layout.chat_left_share_message_businesscard_new, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mSelectView = view.findViewById(R.id.left_share_select_card);
        mIvAvatar = view.findViewById(R.id.chat_left_share_avatar_card);
        mContentView = view.findViewById(R.id.chat_left_share_content_card);
        mTvName = view.findViewById(R.id.chat_left_share_username_card);
        mTvSubTitle = view.findViewById(R.id.chat_left_share_sub_title);
        mIvGender = view.findViewById(R.id.iv_gender);
        mCoverView = view.findViewById(R.id.chat_left_share_cover_card);
        mTitleView = view.findViewById(R.id.chat_left_share_name_card);
        mTvJobTitle = view.findViewById(R.id.tv_job_title);
        mTvSignature = view.findViewById(R.id.tv_signature);
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

            if (StringUtils.isEmpty(mShareChatMessage.getContent().mShareUserId)){
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
//            ViewUtil.setRightMarginDp(mContentView, 70);
//
//        } else {
//            ViewUtil.setRightMarginDp(mContentView,110);
//
//        }


    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mContentView;
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
