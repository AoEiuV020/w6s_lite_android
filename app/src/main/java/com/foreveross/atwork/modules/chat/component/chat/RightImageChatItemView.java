package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.Bitmap;
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


public class RightImageChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;
    private FrameLayout mFlContentRoot;
    private ImageView mIvContent;
    private ChatSendStatusView mChatSendStatusView;
    private ImageChatMessage mImageChatMessage;
    private TextView mProgressView;

    private ImageView mIvSelect;
    private Context mContext;

    private int targetImgWidth;

    private int targetImgHeight;

    private int minImgHeight;

    private int minImgWidth;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    public RightImageChatItemView(Context context) {
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
        return mImageChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

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
        View view = inflater.inflate(R.layout.chat_right_image_mesasge, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_right_image_avatar);
        mFlContentRoot = view.findViewById(R.id.chat_right_image_layout);
        mIvContent = view.findViewById(R.id.chat_right_image_content);
        mChatSendStatusView = view.findViewById(R.id.chat_right_image_status);
        mProgressView = view.findViewById(R.id.chat_right_image_upload_progress);
        mProgressView.setVisibility(GONE);
        mIvSelect = view.findViewById(R.id.right_image_select);

        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);





    }

    public void setImageChatMessage(ImageChatMessage message) {
        this.mImageChatMessage = message;
    }


    /**
     * 根据屏幕调整聊天图片的大小
     */
    private void scaleImageView(Bitmap bitmap) {
        float scaleSize = 1;
        if (bitmap.getWidth() < bitmap.getHeight()) {
            scaleSize = ((float) targetImgHeight) / bitmap.getHeight();

        } else {
            scaleSize = ((float) targetImgWidth) / bitmap.getWidth();

        }

        int scaledWidth = (int) (bitmap.getWidth() * scaleSize);
        int scaledHeight = (int) (bitmap.getHeight() * scaleSize);
        scaledWidth = scaledWidth < minImgWidth ? minImgWidth : scaledWidth;
        scaledHeight = scaledHeight < minImgHeight ? minImgHeight : scaledHeight;

        mIvContent.getLayoutParams().width = scaledWidth;
        mIvContent.getLayoutParams().height = scaledHeight;
    }

    private void refreshProgress(ImageChatMessage message) {
        if (needCurrentRefresh(message)) {
            if (ChatStatus.Sending.equals(message.chatStatus) && (FileStatus.DOWNLOADING.equals(message.fileStatus) || FileStatus.SENDING.equals(message.fileStatus))) {
                progress(message.progress);
            } else {
//                mIvContent.getBackground().setAlpha(255);
                mIvContent.setAlpha(255);
                mProgressView.setVisibility(GONE);
            }
        }
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
            setImageChatMessage((ImageChatMessage) message);
            ImageChatHelper.initImageContent(mImageChatMessage, mIvContent, R.mipmap.loading_gray_holding, true);
            refreshProgress((ImageChatMessage) message);


            return;
        }

    }


    /**
     * 更新图片上传进度
     *
     * @param progress
     */
    public void progress(int progress) {
        int alpha = (progress * 2) + 50;
//        mIvContent.getBackground().setAlpha(alpha);
        mIvContent.setAlpha(alpha);
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
        return (null != mIvContent.getTag() && mIvContent.getTag().equals(message.deliveryId));
    }


    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mIvContent);

    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mIvContent);
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mFlContentRoot;
    }
}
