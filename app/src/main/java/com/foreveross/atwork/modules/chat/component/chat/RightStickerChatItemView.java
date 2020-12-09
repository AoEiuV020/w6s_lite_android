package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;


public class RightStickerChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;
    private ImageView mIvAvatar;
    private FrameLayout mFlContent;
    private ImageView mIvContent;
    private ChatSendStatusView mChatSendStatusView;
    private StickerChatMessage mStickerChatMessage;
    private TextView mProgressView;

    private ImageView mIvSelect;
    private Context mContext;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;


    private MessageSourceView mSourceView;

    public RightStickerChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();

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
    protected ChatPostMessage getMessage() {
        return mStickerChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

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
        View view = inflater.inflate(R.layout.chat_right_sticker_mesasge, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_right_image_avatar);
        mFlContent = view.findViewById(R.id.fl_content);
        mIvContent = view.findViewById(R.id.chat_right_sticker_content);
        mChatSendStatusView = view.findViewById(R.id.chat_right_image_status);
        mIvSelect = view.findViewById(R.id.right_sticker_select);
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
                .setTvTimeTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white))
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setLlSomeStatusInfo(mLlSomeStatusInfo)
                .setSomeStatusInfoAreaGrayBg(AtworkApplicationLike.baseContext);

    }



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
        Log.e("loadurl " , "load url = " + loadUrl);
        Glide.with(getContext()).load(loadUrl).apply(options).into(mIvContent);
    }


    @Override
    protected void burnSkin() {
        super.burnSkin();
//        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mIvContent);

    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
//        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mIvContent);
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
