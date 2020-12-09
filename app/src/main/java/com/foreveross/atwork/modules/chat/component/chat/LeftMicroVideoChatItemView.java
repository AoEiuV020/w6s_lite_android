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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.utils.MicroVideoHelper;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageChatHelper;

/**
 * Created by reyzhang22 on 15/12/24.
 */
public class LeftMicroVideoChatItemView extends LeftBasicUserChatItemView {

    private Context mContext;

    private View mVRoot;

    private View mVContentRoot;

    private ImageView mSelectView;

    private ImageView mAvatarView;

    private ImageView mIvVideoThumbnail;

    private TextView mNameView;

    private TextView mTvSubTitle;

    private MicroVideoChatMessage mMicroVideoChatMessage;

    private Bitmap mBitmap;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;


    public LeftMicroVideoChatItemView(Context context) {
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
    protected TextView getNameView() {
        return mNameView;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubTitle;
    }

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
        return mMicroVideoChatMessage;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mMicroVideoChatMessage = (MicroVideoChatMessage) message;

        initThumbnail();

    }



    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_micro_video_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mVContentRoot = view.findViewById(R.id.micro_video_content_left);
        mSelectView = view.findViewById(R.id.left_select);
        mAvatarView = view.findViewById(R.id.chat_left_avatar);
        mNameView = view.findViewById(R.id.chat_left_image_name);
        mTvSubTitle = view.findViewById(R.id.chat_left_image_sub_title);
        mIvVideoThumbnail = view.findViewById(R.id.chat_left_thumbnail);
        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        mLlTags = view.findViewById(R.id.ll_tags);

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
        mIvVideoThumbnail.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.microVideoLongClick(mMicroVideoChatMessage, anchorInfo);
                return true;
            }
            return false;
        });

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

    }


    private void initThumbnail() {
        mIvVideoThumbnail.setTag(mMicroVideoChatMessage.deliveryId);
        //TODO: 做一个获取视频文件的缩略图获取方法。。。。。

        mBitmap = MicroVideoHelper.getVideoBitmap(mContext, mMicroVideoChatMessage);
        if (mBitmap == null) {
            if (!TextUtils.isEmpty(mMicroVideoChatMessage.thumbnailMediaId)) {
                ImageCacheHelper.displayImageByMediaId(mMicroVideoChatMessage.thumbnailMediaId, mIvVideoThumbnail, ImageCacheHelper.getImageOptions());

            } else {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.loading_gray_holding);
            }

        }
        if (null != mIvVideoThumbnail.getTag() && mIvVideoThumbnail.getTag().equals(mMicroVideoChatMessage.deliveryId)
                && null != mBitmap) {
            ImageChatHelper.scaleImageView(mBitmap, mIvVideoThumbnail);
            mIvVideoThumbnail.setImageBitmap(mBitmap);

        }

    }

    public void scaleVideoLayout(Context context) {
        int width = ScreenUtils.getScreenWidth(context);
        ViewUtil.setWidth(mIvVideoThumbnail, width / 5 * 2);
        ViewUtil.setHeight(mIvVideoThumbnail, width / 5 * 2);
    }


    protected void themeSkin() {
//        mVContentRoot.setBackgroundResource(R.mipmap.bg_chat_left);
        super.themeSkin();
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mVContentRoot;
    }
}
