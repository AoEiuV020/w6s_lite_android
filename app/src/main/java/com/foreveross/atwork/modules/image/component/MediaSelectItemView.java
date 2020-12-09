package com.foreveross.atwork.modules.image.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.file.ImageItem;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.utils.MicroVideoHelper;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageChatHelper;
import com.foreveross.atwork.utils.TimeViewUtil;


public class MediaSelectItemView extends FrameLayout {

    private static final String TAG = MediaSelectItemView.class.getSimpleName();

    private ImageView mImageView;
    private CheckBox mSelectBtn;
    private RelativeLayout mRlTag;
    private ImageView mIvVideoTag;
    private TextView mTvVideoDuration;
    private TextView mTvGifTag;

    private MediaItem imageItem;

    public MediaSelectItemView(Context context) {
        super(context);
        initView(context);
    }

    public MediaSelectItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MediaSelectItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_media_select, this);
        mImageView = view.findViewById(R.id.media_item);
        mSelectBtn = view.findViewById(R.id.media_select);
        mRlTag = view.findViewById(R.id.rl_media_tag);
        mRlTag = view.findViewById(R.id.rl_media_tag);
        mIvVideoTag = view.findViewById(R.id.iv_video_tag);
        mTvVideoDuration = view.findViewById(R.id.tv_video_duration);
        mTvGifTag = view.findViewById(R.id.tv_gif_tag);


        int size = ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext) / 4;
        ViewUtil.setSize(mImageView, size, size);
    }

    public void setData(final MediaItem mediaItem) {
        if (mediaItem == null) {
            return;
        }

        mSelectBtn.setChecked(mediaItem.isSelected);
        handleTagView(mediaItem);

        this.imageItem = mediaItem;

        if (!TextUtils.isEmpty(mediaItem.filePath)) {
            ImageCacheHelper.displayImage(mediaItem.filePath, mImageView,ImageCacheHelper.getPinteresteImageitemOptions());
            return;
        }
        if (this.imageItem.message != null) {
            if (this.imageItem.message instanceof MicroVideoChatMessage) {
                MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage)this.imageItem.message;
                Bitmap bitmap = MicroVideoHelper.getVideoBitmap(getContext(), microVideoChatMessage);
                if (bitmap == null) {
                    if (!TextUtils.isEmpty(microVideoChatMessage.thumbnailMediaId)) {
                        ImageCacheHelper.displayImageByMediaId(microVideoChatMessage.thumbnailMediaId, mImageView, ImageCacheHelper.getImageOptions());
                    } else {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.loading_gray_holding);
                    }

                }
//                ImageChatHelper.scaleImageView(bitmap, mImageView);
                mImageView.setImageBitmap(bitmap);
                return;
            }
            if (this.imageItem.message instanceof ImageChatMessage) {
                ImageChatMessage imageChatMessage = (ImageChatMessage)this.imageItem.message;
                ImageChatHelper.initImageContent(imageChatMessage, mImageView, R.mipmap.loading_gray_holding, false);
            }
        }

    }


    private void handleTagView(MediaItem mediaItem) {
        if(mediaItem instanceof VideoItem) {
            VideoItem videoItem = (VideoItem) mediaItem;
            mRlTag.setVisibility(VISIBLE);
            mTvGifTag.setVisibility(GONE);
            mIvVideoTag.setVisibility(VISIBLE);
            mTvVideoDuration.setVisibility(VISIBLE);


            mTvVideoDuration.setText(TimeViewUtil.getShowDuration(videoItem.getDuration(), true));
            return;
        }

        if(mediaItem instanceof ImageItem) {
            ImageItem imageItem = (ImageItem) mediaItem;
            if(imageItem.isGif()) {
                mRlTag.setVisibility(VISIBLE);
                mTvGifTag.setVisibility(VISIBLE);
                mIvVideoTag.setVisibility(GONE);
                mTvVideoDuration.setVisibility(GONE);
                return;

            }
        }

        mRlTag.setVisibility(GONE);

    }

    public void setChecked(boolean checked) {
        mSelectBtn.setChecked(checked);
    }

    public void setCheckBoxUnShow(){
        mSelectBtn.setVisibility(View.GONE);
    }
}
