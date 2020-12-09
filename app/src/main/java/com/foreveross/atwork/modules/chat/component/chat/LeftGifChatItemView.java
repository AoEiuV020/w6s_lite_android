package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.ChatItemClickListener;
import com.foreveross.atwork.utils.ImageChatHelper;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class LeftGifChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private FrameLayout mFlContentViewRoot;

    private GifImageView mGivContent;

    private ImageView mIvTagGif;

    private ImageView mSelectView;

    private ImageChatMessage mImageChatMessage;

    private Context mContext;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;


    public LeftGifChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
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

    public void setChatItemClickListener(ChatItemClickListener chatItemClickListener) {
        this.mChatItemClickListener = chatItemClickListener;
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mFlContentViewRoot;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        /**
         * 点击图片事件
         */
        mGivContent.setOnClickListener(v -> {
            if (mSelectMode) {
                mImageChatMessage.select = !mImageChatMessage.select;
                select(mImageChatMessage.select);
            } else {
                if (mChatItemClickListener != null) {
                    mChatItemClickListener.imageClick(mImageChatMessage);
                }
            }
        });

        mGivContent.setOnLongClickListener(v -> {
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
        View view = inflater.inflate(R.layout.chat_left_gif_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_left_image_avatar);
        mTvName = view.findViewById(R.id.chat_left_image_name);
        mTvSubTitle = view.findViewById(R.id.chat_left_image_sub_title);
        mFlContentViewRoot = view.findViewById(R.id.left_gif_content);
        mGivContent = view.findViewById(R.id.chat_left_image_content);
        mIvTagGif = view.findViewById(R.id.iv_tag_gif);
        mSelectView = view.findViewById(R.id.left_image_select);
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

        mImageChatMessage = (ImageChatMessage) message;

        refreshGif(message);
    }



    private void refreshGif(ChatPostMessage message) {
        if(message.deliveryId.equals(mGivContent.getTag())) {

            Drawable drawable = mGivContent.getDrawable();

            if (null != drawable && drawable instanceof GifDrawable) {
                GifDrawable gifDrawable = (GifDrawable) drawable;
                if (!gifDrawable.isPlaying()) {
                    mGivContent.setImageDrawable(gifDrawable);
                }

                return;
            }
        }

        mGivContent.setTag(message.deliveryId);
        ImageChatHelper.showGif(getContext(), mGivContent, mIvTagGif, (ImageChatMessage) message);


    }


    @Override
    protected void themeSkin() {
//        mGivContent.setBackgroundResource(R.mipmap.bg_chat_left);
        super.themeSkin();

    }


}
