package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageChatHelper;

public class LeftImageChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private FrameLayout mFlContentRoot;

    private ImageView mIvContent;

    private ChatPostMessage mImageChatMessage;

    private ImageView mIvSelect;

    private Context mContext;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;


    public LeftImageChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
//        targetImgWidth = (int) (ScreenUtils.getScreenWidth(context) / 3.5);
//        targetImgHeight = ScreenUtils.getScreenHeight(context) / 5;
//
//        minImgWidth = targetImgWidth / 2;
//        minImgHeight = DensityUtil.dip2px(context, 60);
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
        return mSourceView;
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
        return mImageChatMessage;
    }


    @Override
    protected void registerListener() {
        super.registerListener();

        /**
         * 点击图片事件
         */
        mIvContent.setOnClickListener(v -> {
            if (mSelectMode) {
                mImageChatMessage.select = !mImageChatMessage.select;
                select(mImageChatMessage.select);
            } else {
                if (mChatItemClickListener != null) {
                    mChatItemClickListener.imageClick(mImageChatMessage);
                }
            }
        });

        mIvContent.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.imageLongClick(mImageChatMessage, anchorInfo);
                return true;
            }
            return false;
        });


    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_image_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_left_image_avatar);
        mTvName = view.findViewById(R.id.chat_left_image_name);
        mTvSubTitle = view.findViewById(R.id.chat_left_image_sub_title);
        mFlContentRoot = view.findViewById(R.id.fl_content);
        mIvContent = view.findViewById(R.id.chat_left_image_content);
        mIvSelect = view.findViewById(R.id.left_image_select);
        mSourceView = view.findViewById(R.id.message_srouce_from);

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
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);
        if (message instanceof ImageChatMessage) {
            mImageChatMessage =  message;
            ImageChatHelper.initImageContent((ImageChatMessage)message, mIvContent, R.mipmap.loading_gray_holding, true);

            return;
        }

        if (message instanceof StickerChatMessage) {
            StickerChatMessage stickerChatMessage = (StickerChatMessage)message;
            String stickerLocalPath = stickerChatMessage.getChatStickerUrl(mContext, UrlConstantManager.getInstance().getStickerImageUrl());
            ImageCacheHelper.displayImageSimply(stickerLocalPath, mIvContent);
        }

    }


    protected void themeSkin() {
//        mIvContent.setBackgroundResource(R.mipmap.bg_chat_left);
        super.themeSkin();
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mFlContentRoot;
    }
}
