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

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.utils.ImageChatHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class RightGifChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;
    private ImageView mAvatarView;
    private FrameLayout mFlContentRoot;
    private GifImageView mGivContent;
    private ImageView mIvTagGif;
    private TextView mProgressView;
    private ImageView mSelectView;

    private ChatSendStatusView mChatSendStatusView;
    private ImageChatMessage mImageChatMessage;

    private Context mContext;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;


    public RightGifChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
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
        return mImageChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();


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
        View view = inflater.inflate(R.layout.chat_right_gif_mesasge, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mAvatarView = view.findViewById(R.id.chat_right_image_avatar);
        mFlContentRoot = view.findViewById(R.id.chat_right_image_layout);
        mGivContent = view.findViewById(R.id.chat_right_image_content);
        mIvTagGif = view.findViewById(R.id.iv_tag_gif);
        mChatSendStatusView = view.findViewById(R.id.chat_right_image_status);
        mProgressView = view.findViewById(R.id.chat_right_image_upload_progress);
        mProgressView.setVisibility(GONE);
        mSelectView = view.findViewById(R.id.right_image_select);
        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

    }

    public void setImageChatMessage(ImageChatMessage message) {
        this.mImageChatMessage = message;

    }


    private void refreshProgress(ImageChatMessage message) {
        if (needCurrentRefresh(message)) {
            if (ChatStatus.Sending.equals(message.chatStatus) && (FileStatus.DOWNLOADING.equals(message.fileStatus) || FileStatus.SENDING.equals(message.fileStatus))) {
                progress(mImageChatMessage.progress);
            } else {
//                mGivContent.getBackground().setAlpha(255);
                mGivContent.setAlpha(255);
                mProgressView.setVisibility(GONE);
            }
        }
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

        setImageChatMessage((ImageChatMessage) message);
        refreshProgress((ImageChatMessage) message);

        refreshGif(message);

    }

    /**
     * 更新图片上传进度
     *
     * @param progress
     */
    public void progress(int progress) {
        int alpha = (progress * 2) + 50;
//        mGivContent.getBackground().setAlpha(alpha);
        mGivContent.setAlpha(alpha);
        if (100 == progress || 0 == progress) {
            mProgressView.setVisibility(GONE);
        } else {
            mProgressView.setVisibility(VISIBLE);
            mProgressView.setText(String.valueOf(progress));
        }
    }


    /**
     * 当前视图是否合适刷新
     *
     * @param message
     * @return boolean
     */
    private boolean needCurrentRefresh(ChatPostMessage message) {
        return (null != mGivContent.getTag() && mGivContent.getTag().equals(message.deliveryId));

    }


    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mGivContent);

    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mGivContent);
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mFlContentRoot;
    }
}
