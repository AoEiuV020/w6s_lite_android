package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.modules.chat.component.chat.LeftBasicUserChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.MessageSourceView;
import com.foreveross.atwork.modules.chat.component.chat.SomeStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;


public class LeftStickerChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private FrameLayout mFlContent;

    private ImageView mIvContent;

    private StickerChatMessage mStickerChatMessage;

    private ImageView mIvSelect;

    private Context mContext;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;

    public LeftStickerChatItemView(Context context) {
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
        return mStickerChatMessage;
    }


    @Override
    protected void registerListener() {
        super.registerListener();

        /**
         * 点击图片事件
         */
        mIvContent.setOnClickListener(v -> {
            if (mSelectMode) {
                mStickerChatMessage.select = !mStickerChatMessage.select;
                select(mStickerChatMessage.select);
            } else {
                if (mChatItemClickListener != null) {
                    mChatItemClickListener.stickerClick(mStickerChatMessage);
                }
            }
        });

        mIvContent.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.stickerLongClick(mStickerChatMessage, anchorInfo);
                return true;
            }
            return false;
        });

    }


    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_sticker_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_left_image_avatar);
        mTvName = view.findViewById(R.id.chat_left_image_name);
        mTvSubTitle = view.findViewById(R.id.chat_left_image_sub_title);
        mFlContent = view.findViewById(R.id.fl_content);
        mIvContent = view.findViewById(R.id.chat_left_sticker_content);
        mIvSelect = view.findViewById(R.id.left_image_select);
        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        mLlTags = view.findViewById(R.id.ll_tags);

    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setIvStatus(mIvSomeStatus)
                .setTvTime(mTvTime)
                .setTvTimeTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white))
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setLlSomeStatusInfo(mLlSomeStatusInfo)
                .setSomeStatusInfoAreaGrayBg(AtworkApplicationLike.baseContext);

    }


    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }


    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);
        mStickerChatMessage = (StickerChatMessage) message;
        refreshGif();
    }

    private void refreshGif() {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fallback(R.mipmap.loading_gray_holding)
                .error(R.mipmap.loading_gray_holding);
        String loadUrl = "";
        if (BodyType.Sticker.equals(mStickerChatMessage.mBodyType)) {
            String name = mStickerChatMessage.getStickerName();
            if (name.contains(".")) {
                name = name.split("\\.")[0];
            }
            loadUrl = AtWorkDirUtils.getInstance().getAssetStickerUri(mStickerChatMessage.getThemeName(), name);

        } else {
            loadUrl = mStickerChatMessage.getChatStickerUrl(mContext, UrlConstantManager.getInstance().getStickerImageUrl());
        }
        Log.e("loadurl " , "left load url = " + loadUrl);
        Glide.with(getContext()).load(loadUrl).apply(options).into(mIvContent);
    }

    protected void themeSkin() {
//        mIvContent.setBackgroundResource(R.mipmap.bg_chat_left);
//        super.themeSkin();
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mIvContent;
    }

    @Override
    protected void handleMaskForeground() {
        //do nothing
    }

    @NonNull
    @Override
    protected View getContentBlinkView() {
        return mFlContent;
    }
}
