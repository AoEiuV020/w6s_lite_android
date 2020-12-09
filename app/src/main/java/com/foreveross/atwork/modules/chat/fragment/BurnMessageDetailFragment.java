package com.foreveross.atwork.modules.chat.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.foreverht.cache.BitmapCache;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WaveView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceMedia;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ArrayUtil;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.GifShowHelper;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ViewCompat;
import com.foreveross.atwork.infrastructure.utils.explosion.ExplosionField;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.modules.bing.listener.VoicePlayListener;
import com.foreveross.atwork.modules.chat.activity.BurnMessageDetailActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.util.AudioRecord;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.image.component.ItemEnlargeImageView;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageChatHelper;
import com.foreveross.watermark.core.WaterMarkUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by dasunsy on 2017/4/21.
 */

public class BurnMessageDetailFragment extends OnSensorChangedFragment {

    private View mVMaskLayer;
    private RelativeLayout mRlRoot;
    private ImageView mIvBack;
    private TextView mTvBurnText;
    private View mRlVoice;
    private View mLlAudioPlay;
    private TextView mTvPlayTime;
    private WaveView mWaveView;
    private ItemEnlargeImageView mIvImgShow;
    private ItemEnlargeImageView mStickerView;
    private TextView mTvTimeClock;
    private TextView mTvAudioPlayTip;
    private View mVWaterCover;

    private ChatPostMessage mBurnChatPostMessage;
    private Session mSession;

    private boolean mIsFinish = false;
    private boolean mIsPlayingAudio = false;

    private boolean mIsReceiver = true;

    private TimeClockHandler mTimeClockHandler;
    private AckPostMessage mAckPostMsgSaved;

    private ProgressDialogHelper mProgressDialogHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcast();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_burn_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();

        initData();
        refreshUI();

        if (mIsReceiver) {
            saveAckNeedCheck();
            ChatDetailExposeBroadcastSender.burnMessage(mBurnChatPostMessage);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();

        AudioRecord.stopPlaying();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private void saveAckNeedCheck() {

        mAckPostMsgSaved = ChatService.createAckNeedCheck(getActivity(), mSession, ListUtil.makeSingleList(mBurnChatPostMessage.deliveryId), 1);
        PersonalShareInfo.getInstance().setAckNeedCheck(getActivity(), mAckPostMsgSaved);
    }

    @Override
    protected void findViews(View view) {
        mRlRoot = view.findViewById(R.id.rl_root);
        mIvBack = view.findViewById(R.id.iv_back);
        mTvBurnText = view.findViewById(R.id.tv_burn_text);
        mRlVoice = view.findViewById(R.id.rl_voice);
        mLlAudioPlay = view.findViewById(R.id.ll_audio_play);
        mTvPlayTime = view.findViewById(R.id.tv_audio_time);
        mTvAudioPlayTip = view.findViewById(R.id.tv_audio_play_tip);
        mWaveView = view.findViewById(R.id.waveview);
        mIvImgShow = view.findViewById(R.id.iv_img_show);
        mStickerView = view.findViewById(R.id.iv_sticker_view);
        mTvTimeClock = view.findViewById(R.id.tv_clock);
        mVWaterCover = view.findViewById(R.id.v_watermark_bg);
        mVMaskLayer = getView().findViewById(R.id.v_mask_layer);

        mIvImgShow.setScaleGesture();
        mStickerView.setScaleGesture();

        mProgressDialogHelper = new ProgressDialogHelper(getActivity());

    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> {
            doFinish();

        });

        mLlAudioPlay.setOnClickListener(v -> {
            if (!mIsPlayingAudio) {
                mIsPlayingAudio = true;
                VoiceChatMessage voiceChatMessage = (VoiceChatMessage) mBurnChatPostMessage;
                playAudio(voiceChatMessage);
            }
        });
    }

    private void playAudio(VoiceChatMessage voiceChatMessage) {
        AudioRecord.playAudio(getActivity(), voiceChatMessage, new VoicePlayListener() {
            @Override
            public void start() {
                if (isAdded()) {
                    getActivity().runOnUiThread(() -> startPlayAudioAnimation());
                }
            }

            @Override
            public void stop(VoiceMedia voiceMedia) {
                if (isAdded()) {

                    getActivity().runOnUiThread(() -> {
                        playAudioEnd(getActivity());

                        if (mIsReceiver) {
                            stopPlayAudioAnimation();

                        } else {
                            resetVoicePlayView();
                            mIsPlayingAudio = false;

                        }

                    });
                }
            }
        });
    }

    private void doFinish() {
//        mIsReceiver = true;
        if (mIsReceiver) {
            doReceiverFinish();
        } else {
            doSenderFinish();
        }
    }

    private void doSenderFinish() {
        finish(true);
    }

    private void doReceiverFinish() {
        if (!mIsFinish) {
            mIsFinish = true;

            mTimeClockHandler.stopTimeClock();
            mTvTimeClock.setVisibility(View.GONE);

            ExplosionField explosionField = ExplosionField.attach2Window(getActivity());

            if (mBurnChatPostMessage instanceof TextChatMessage) {
                explosionField.explode(mTvBurnText);

            } else if (mBurnChatPostMessage instanceof ImageChatMessage) {
                explosionField.explode(mIvImgShow);

            } else if (mBurnChatPostMessage instanceof VoiceChatMessage) {
                AudioRecord.stopPlaying();
                resetVoicePlayView();
                explosionField.explode(mLlAudioPlay);

            } else if (mBurnChatPostMessage instanceof StickerChatMessage) {
                explosionField.explode(mStickerView);
            }

            mRlRoot.postDelayed(this::finish, 1000);

            ChatService.checkSendRemovedReceipts(getActivity());

        }
    }


    private void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mBurnChatPostMessage = (ChatPostMessage) bundle.getSerializable(BurnMessageDetailActivity.DATA_MESSAGE);
            String sessionId = bundle.getString(BurnMessageDetailActivity.DATA_SESSION);
            mSession = ChatSessionDataWrap.getInstance().getSession(sessionId, null);

            mTimeClockHandler = new TimeClockHandler(this, mBurnChatPostMessage);
            mIsReceiver = ChatMessageHelper.isReceiver(mBurnChatPostMessage);

            if (mIsReceiver) {
                mTimeClockHandler.startTimeClock();
            }
        }
    }

    private void refreshUI() {
        String burnTimeStr = (mBurnChatPostMessage.getReadTime()) + "";
        if (mIsReceiver) {
            refreshTimeClock(burnTimeStr);
            mTvTimeClock.setVisibility(View.VISIBLE);

        } else {
            mTvTimeClock.setVisibility(View.GONE);

        }

        if (mBurnChatPostMessage instanceof TextChatMessage) {
            refreshTextModeUI();
            return;

        }
        if (mBurnChatPostMessage instanceof ImageChatMessage) {
            refreshImgModeUI();
            return;

        }
        if (mBurnChatPostMessage instanceof VoiceChatMessage) {
            refreshVoiceModeUI(burnTimeStr);
            return;
        }
        if (mBurnChatPostMessage instanceof StickerChatMessage) {
            refreshSticker();
            return;
        }

    }

    private void refreshVoiceModeUI(String burnTimeStr) {
        VoiceChatMessage voipChatMessage = (VoiceChatMessage) mBurnChatPostMessage;
        if(mIsReceiver) {
            mTvAudioPlayTip.setVisibility(View.VISIBLE);
            mTvAudioPlayTip.setText(getString(R.string.burn_audio_play_tip, burnTimeStr));

        } else {
            mTvAudioPlayTip.setVisibility(View.GONE);

        }
        mTvPlayTime.setText(voipChatMessage.duration + "\"");

        showVoiceUI();
    }

    private void refreshImgModeUI() {
        ImageChatMessage imageChatMessage = (ImageChatMessage) mBurnChatPostMessage;

        if(imageChatMessage.isGif) {
            setGif(imageChatMessage, mIvImgShow);
        } else {
            setImage(imageChatMessage, mIvImgShow);
        }

        showImageUI();
        handleWatermark();
    }

    private void refreshSticker() {
        StickerChatMessage stickerChatMessage = (StickerChatMessage) mBurnChatPostMessage;
        ImageChatHelper.initStickerContent(stickerChatMessage, mStickerView);
        showStickerUI();
    }

    private void refreshTextModeUI() {
        TextChatMessage textChatMessage = (TextChatMessage) mBurnChatPostMessage;

        showTextUI();

        mTvBurnText.setText(textChatMessage.text);
        handleWatermark();
    }

    private void resetVoicePlayView() {
        mWaveView.stop();
        mLlAudioPlay.setBackgroundResource(R.drawable.round_burn_mode_time_clock);
        mTvAudioPlayTip.setVisibility(View.GONE);
    }

    private void handleWatermark() {
        if(AtworkConfig.BURN_MESSAGE_CONFIG.isCommandHideWatermark()) {
            return;
        }


        if ("show".equalsIgnoreCase(DomainSettingsManager.getInstance().handleUserWatermarkFeature())) {
            mVWaterCover.setVisibility(View.VISIBLE);
            int color = ContextCompat.getColor(getActivity(), R.color.watermark_text_color);
            color = ColorUtils.setAlphaComponent(color, 76);
            WaterMarkUtil.setLoginUserWatermark(getActivity(), mVWaterCover, -1, color, "");
        }
    }

    private void showVoiceUI() {
        mTvBurnText.setVisibility(View.GONE);
        mIvImgShow.setVisibility(View.GONE);
        mStickerView.setVisibility(View.GONE);
        mRlVoice.setVisibility(View.VISIBLE);
        mTvAudioPlayTip.setVisibility(View.VISIBLE);
    }

    private void showImageUI() {
        mTvBurnText.setVisibility(View.GONE);
        mRlVoice.setVisibility(View.GONE);
        mTvAudioPlayTip.setVisibility(View.GONE);
        mIvImgShow.setVisibility(View.VISIBLE);
        mStickerView.setVisibility(View.GONE);
    }

    private void showStickerUI() {
        mTvBurnText.setVisibility(View.GONE);
        mRlVoice.setVisibility(View.GONE);
        mTvAudioPlayTip.setVisibility(View.GONE);
        mIvImgShow.setVisibility(View.GONE);
        mStickerView.setVisibility(View.VISIBLE);
    }

    private void showTextUI() {
        mTvBurnText.setVisibility(View.VISIBLE);
        mRlVoice.setVisibility(View.GONE);
        mIvImgShow.setVisibility(View.GONE);
        mStickerView.setVisibility(View.GONE);
        mTvAudioPlayTip.setVisibility(View.GONE);
    }

    private void startPlayAudioAnimation() {
        mTimeClockHandler.stopTimeClock();
        mTvTimeClock.setVisibility(View.GONE);
        mTvAudioPlayTip.setVisibility(View.GONE);

        ViewCompat.setBackground(mLlAudioPlay, null);
        mWaveView.setMaxRadius(DensityUtil.dip2px(120));
        mWaveView.setColor(ContextCompat.getColor(getActivity(), R.color.burn_mode_time_clock_bg));
        mWaveView.start();
    }

    private void stopPlayAudioAnimation() {
        doReceiverFinish();
    }

    private void setImage(ImageChatMessage imageChatMessage, ImageView cellView) {
        Bitmap bitmap = BitmapCache.getBitmapCache().getBitmapFromMemCache(imageChatMessage.deliveryId + ImageChatMessage.ORIGINAL_SUFFIX);
        if (bitmap == null) {
            byte[] originalImage = ImageShowHelper.getOriginalImage(getActivity(), imageChatMessage.deliveryId);
            if (originalImage.length != 0) {
                bitmap = BitmapUtil.Bytes2Bitmap(originalImage);
                if (bitmap != null) {
                    cellView.setImageBitmap(bitmap);
                }
            }

            if (bitmap == null) {
                Bitmap thumbBitmap = BitmapCache.getBitmapCache().getThumbBitmap(mActivity, imageChatMessage.deliveryId, ImageShowHelper.getChatMsgImgThumbnail(imageChatMessage));

                if (null != thumbBitmap) {
                    cellView.setImageBitmap(thumbBitmap);
                }

                downloadOriginImg(imageChatMessage);

            }

        } else {
            cellView.setImageBitmap(bitmap);
        }
    }

    private void setGif(ImageChatMessage imageChatMessage, ImageView view) {
        byte[] gifByte = GifShowHelper.getGifByte(mActivity, imageChatMessage.deliveryId, imageChatMessage.mediaId);

        if (ArrayUtil.isEmpty(gifByte)) {
            downloadOriginGif(imageChatMessage);

        } else {
            showGIf(view, imageChatMessage, gifByte);

        }
    }

    private void showGIf(ImageView view, ImageChatMessage imageChatMessage, byte[] gifByte) {
        try {
            Bitmap thumbBitmap = BitmapCache.getBitmapCache().getContentBitmap(mActivity, imageChatMessage.deliveryId, imageChatMessage.mediaId, true, ImageShowHelper.getChatMsgImgThumbnail(imageChatMessage));
            view.setImageBitmap(thumbBitmap);

            GifDrawable gifDrawable = new GifDrawable(gifByte);
            Bitmap holderBitmap = gifDrawable.getCurrentFrame();
            view.setImageBitmap(holderBitmap);
            view.setImageDrawable(gifDrawable);

            holderBitmap.recycle();
            holderBitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void displayImg(String filePath, ItemEnlargeImageView preImageView) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(true);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        File file = new File(filePath);
        if(AtworkConfig.CHAT_IMG_SHOW_LIMIT < file.length()) {
            builder.imageScaleType(ImageScaleType.NONE_SAFE);

        } else {
            builder.imageScaleType(ImageScaleType.NONE);


        }
        DisplayImageOptions options = builder.build();

        ImageCacheHelper.displayImage(filePath, preImageView, options);
    }

    public void refreshTimeClock(String timeLeft) {
        mTvTimeClock.setText(timeLeft);
    }


    @Override
    protected boolean onBackPressed() {
        doFinish();
        return false;
    }

    @Override
    public void finish() {
        super.finish();

        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if (undoEventMessage != null && undoEventMessage.isMsgUndo(mBurnChatPostMessage.deliveryId)) {

            mTimeClockHandler.stopTimeClock();
            if(mIsPlayingAudio) {
                AudioRecord.stopPlaying();
            }

            PersonalShareInfo.getInstance().removeAcksNeedCheck(BaseApplicationLike.baseContext, ListUtil.makeSingleList(mAckPostMsgSaved.deliveryId));

            if (isAdded()) {
                showUndoDialog(getActivity(), undoEventMessage);
            }
        }

    }

    private void registerBroadcast() {

    }

    private void unregisterBroadcast() {

    }

    @Override
    protected void replay() {
        //do nothing
    }

    public void hideMaskLayer() {
        super.hideMaskLayer();
        mVMaskLayer.setVisibility(View.GONE);
    }

    public void showMaskLayer() {
        super.showMaskLayer();
        mVMaskLayer.setVisibility(View.VISIBLE);

    }


    private void downloadOriginImg(ChatPostMessage chatPostMessage) {
        String imgFilePath = ImageShowHelper.getChatMsgImgPath(mActivity, chatPostMessage);
        String mediaId = ImageShowHelper.getChatMsgImgMediaId(chatPostMessage);

        mProgressDialogHelper.show();
        //下载原图片
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return chatPostMessage.deliveryId;
            }

            @Override
            public void downloadSuccess() {
                mProgressDialogHelper.dismiss();

                //notify refresh
                try {
                    displayImg(imgFilePath, mIvImgShow);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                mProgressDialogHelper.dismiss();
                toast(R.string.download_org_image_fail);

            }

            @Override
            public void downloadProgress(double progress, double value) {

            }
        });

        mediaCenterNetManager.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(mediaId).setDownloadId(chatPostMessage.deliveryId).setDownloadPath(imgFilePath)
                        .setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.ORIGINAL)
        );
    }


    private void downloadOriginGif(ImageChatMessage imageChatMessage) {
        String mediaId = ImageShowHelper.getChatMsgImgMediaId(imageChatMessage);
        String filePath = ImageShowHelper.getChatMsgGifPath(mActivity, imageChatMessage);

        mProgressDialogHelper.show();
        //下载gif
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return imageChatMessage.deliveryId;
            }

            @Override
            public void downloadSuccess() {
                mProgressDialogHelper.dismiss();

                byte[] gifByte = FileStreamHelper.readFile(filePath);

                if (ArrayUtil.isEmpty(gifByte)) {
                    if (isAdded()) {
                        AtworkToast.showToast(getString(R.string.to_bitmap_fail));
                    }
                    return;
                }
                //notify refresh
                try {

                    showGIf(mIvImgShow, imageChatMessage, gifByte);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                mProgressDialogHelper.dismiss();
                if (isAdded()) {
                    AtworkToast.showToast(getString(R.string.download_org_image_fail));
                }
            }

            @Override
            public void downloadProgress(double progress, double value) {

            }
        });

        mediaCenterNetManager.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(mediaId).setDownloadId(imageChatMessage.deliveryId).setDownloadPath(filePath)
                        .setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.ORIGINAL)
        );
    }



    private static class TimeClockHandler extends Handler {

        private long mBurnTimeLeft = 0;
        private ChatPostMessage mChatPostMessage;
        private WeakReference<BurnMessageDetailFragment> mFragment;

        public TimeClockHandler(BurnMessageDetailFragment burnMessageDetailFragment, ChatPostMessage chatPostMessage) {
            this.mChatPostMessage = chatPostMessage;
            this.mFragment = new WeakReference<>(burnMessageDetailFragment);

            this.mBurnTimeLeft = mChatPostMessage.getReadTime() * 1000;

        }

        @Override
        public void handleMessage(Message msg) {
            if (0 == msg.what) {
                if (0 < mBurnTimeLeft) {
                    startTimeClock();
                    mBurnTimeLeft -= 1000;

                    LogUtil.e("burn", "time clock left -> " + mBurnTimeLeft / 1000);

                    BurnMessageDetailFragment fragment = mFragment.get();
                    if (null != fragment) {
                        fragment.refreshTimeClock((mBurnTimeLeft / 1000) + "");

                        if (0 == mBurnTimeLeft) {
                            doEndAction(fragment);
                        }
                    }
                }


            }
        }

        private void doEndAction(BurnMessageDetailFragment fragment) {
            if (mChatPostMessage instanceof VoiceChatMessage) {
                fragment.playAudio((VoiceChatMessage) mChatPostMessage);
            } else {
                fragment.doReceiverFinish();

            }
        }

        public void startTimeClock() {
            this.sendEmptyMessageDelayed(0, 1000);
        }

        public void stopTimeClock() {
            this.removeMessages(0);
        }
    }




}
