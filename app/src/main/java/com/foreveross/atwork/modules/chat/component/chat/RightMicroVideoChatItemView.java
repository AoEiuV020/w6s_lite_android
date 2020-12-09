package com.foreveross.atwork.modules.chat.component.chat;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
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
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.ImageChatHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;

import java.io.File;

/**
 * Created by reyzhang22 on 15/12/24.
 */
public class RightMicroVideoChatItemView extends RightBasicUserChatItemView {

    private Context mContext;

    private View mVRoot;

    private FrameLayout mFlChatRightView;

    private ImageView mAvatarView;

    private ImageView mIvVideoThumbnail;

    private ChatSendStatusView mChatSendStatusView;

    private MicroVideoChatMessage mMicroVideoChatMessage;

    private TextView mProgressView;

    private ImageView mSelectView;

    private Bitmap mBitmap;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;


    public RightMicroVideoChatItemView(Context context) {
        super(context);
        mContext = context;
        initViews();

//        scaleVideoLayout(context);

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
        return mSourceView;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mMicroVideoChatMessage;
    }


    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mIvVideoThumbnail.setTag(message.deliveryId);
        setMicroVideoChatMessage((MicroVideoChatMessage) message);

        if (needCurrentRefresh(message)) {
            initImageContent();
            refreshProgress((MicroVideoChatMessage) message);

        }

    }


    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_micro_video_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mFlChatRightView = view.findViewById(R.id.chat_right_layout);
        mAvatarView = view.findViewById(R.id.chat_right_avatar);
        mSelectView = view.findViewById(R.id.right_select);
        mIvVideoThumbnail = view.findViewById(R.id.chat_right_thumbnail);
        mProgressView = view.findViewById(R.id.chat_right_upload_progress);
        mChatSendStatusView = view.findViewById(R.id.chat_right_status);
        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        registerListener();

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
    protected void registerListener() {
        super.registerListener();


        mIvVideoThumbnail.setOnClickListener(v -> {
            if (mSelectMode) {
                mMicroVideoChatMessage.select = !mMicroVideoChatMessage.select;
                select(mMicroVideoChatMessage.select);
            } else {
                if (mChatItemClickListener != null) {
                    mChatItemClickListener.microVideoClick(mMicroVideoChatMessage);
                }
            }
        });

        mIvVideoThumbnail.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.microVideoLongClick(mMicroVideoChatMessage, anchorInfo);
                return true;
            }
            return false;
        });

    }

    private void initImageContent() {
        //TODO.....
        mBitmap = getBitmap();
        if (mBitmap == null) {
            handleBitmap();
        }

        if (needCurrentRefresh(mMicroVideoChatMessage) && null != mBitmap) {
            ImageChatHelper.scaleImageView(mBitmap, mIvVideoThumbnail);
            mIvVideoThumbnail.setImageBitmap(mBitmap);

        }
    }

    private void handleBitmap() {

        //todo 线程
        File video = FileHelper.getMicroExistVideoFile(mContext, mMicroVideoChatMessage);
        if (video.exists()) {
            String originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(video.getAbsolutePath(), false);
            mBitmap = ThumbnailUtils.createVideoThumbnail(originalPath, MediaStore.Images.Thumbnails.MINI_KIND);
            return;
        }

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.loading_gray_holding);
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = null;
        if (mMicroVideoChatMessage.thumbnails != null) {
            bitmap = BitmapFactory.decodeByteArray(mMicroVideoChatMessage.thumbnails, 0, mMicroVideoChatMessage.thumbnails.length);
            if (bitmap != null) {
                return bitmap;
            }
        }
        byte[] b = ImageShowHelper.getThumbnailImage(mContext, mMicroVideoChatMessage.deliveryId);
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }


    /**
     * 当前视图是否合适刷新
     *
     * @param message
     * @return boolean
     * */
    private boolean needCurrentRefresh(ChatPostMessage message) {
        return (null != mIvVideoThumbnail.getTag() && mIvVideoThumbnail.getTag().equals(message.deliveryId));

    }


    private void refreshProgress(MicroVideoChatMessage message) {
        if (needCurrentRefresh(message)) {
            if (ChatStatus.Sending.equals(message.chatStatus) && (FileStatus.DOWNLOADING.equals(message.fileStatus) || FileStatus.SENDING.equals(message.fileStatus))) {
                progress(mMicroVideoChatMessage.progress);
            } else {
//                mFlChatRightView.getBackground().setAlpha(255);
                mIvVideoThumbnail.setAlpha(255);
                mProgressView.setVisibility(GONE);
            }
        }
    }

    /**
     * 更新图片上传进度
     *
     * @param progress
     */
    private void progress(int progress) {
        int alpha = (progress * 2) + 50;
//        mFlChatRightView.getBackground().setAlpha(alpha);
        mIvVideoThumbnail.setAlpha(alpha);
        if (100 == progress || 0 == progress) {
            mProgressView.setVisibility(GONE);
        } else {
            mProgressView.setVisibility(VISIBLE);
            mProgressView.setText(String.valueOf(progress));
        }
    }


    public void setMicroVideoChatMessage(MicroVideoChatMessage message) {
        mMicroVideoChatMessage = message;

    }

    public void scaleVideoLayout(Context context) {
        int width = ScreenUtils.getScreenWidth(context);
        ViewUtil.setWidth(mIvVideoThumbnail, width / 5 * 2);
        ViewUtil.setHeight(mIvVideoThumbnail, width / 5 * 2);
    }



    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mFlChatRightView);
    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mFlChatRightView);
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mFlChatRightView;
    }
}
