package com.foreveross.atwork.modules.downLoad.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.cache.BitmapCache;
import com.foreverht.db.service.daoService.FileDaoService;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.module.file_share.FileShareAction;
import com.foreverht.workplus.module.file_share.activity.FileShareActivity;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ArrayUtil;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.MicroVideoHelper;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.chat.component.MoviePlayerView;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.service.FileDownloadNotifyService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.downLoad.component.DownLoadFileAttrDialog;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.file.adapter.EnlargedImageAdapter;
import com.foreveross.atwork.modules.file.component.ItemLargeDetailViewPager;
import com.foreveross.atwork.modules.file.dao.RecentFileDaoService;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.downLoad.component.MoviePlayerHolder;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.modules.image.component.GestureDetector;
import com.foreveross.atwork.modules.image.component.ItemEnlargeImageView;
import com.foreveross.atwork.modules.image.component.ScaleGestureDetector;
import com.foreveross.atwork.modules.qrcode.service.QrcodeManager;
import com.foreveross.atwork.qrcode.zxing.decode.BitmapQrcodeDecoder;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.GifChatHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageChatHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;
import com.foreveross.watermark.core.WaterMarkUtil;
import com.google.zxing.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;

import static com.foreveross.atwork.component.DownloadPagerView.REFRESH_DOWN_LOAD_FILE_LIST;
import static com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity.REFRESH_DOWN_LOAD_VIEW_PAGER;
import static com.foreveross.atwork.modules.downLoad.component.DownloadFileDetailView.MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS;

/**
 * Created by wuzejie on 20/1/19.
 */
public class ImageSwitchInDownloadFragment extends BackHandledFragment {

    private static final String TGA = ImageSwitchInDownloadFragment.class.getSimpleName();

    public static final String IMAGE_DATA = "image_data";
    public static final String INDEX_SWITCH_IMAGE = "image_count";
    public static final String DATA_HIDE_INDEX_POS_UI = "DATA_HIDE_INDEX_POS_UI";
    public static final String ARGUMENT_SESSION = "session";
    public static final String FILE_DATA = "FILE_DATA";
    public static final String DATA_BING_ID = "DATA_BING_ID";

    private final boolean HAS_SET_GIF = true;
    boolean mOnScale = false;
    private ItemLargeDetailViewPager mViewPager;
    private List<ChatPostMessage> mChatMessagesList;
    private FileData mFileData;
    private String mBingId;

    private ImagePagerAdapter mImagePagerAdapter;
    private ProgressDialogHelper mProgressDialogHelper;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private boolean mOnPagerScroll = false;
    private boolean mPaused;
    private int mCurrentPos = 0;
    private int mTotal;

    private Bitmap mBitmap;

    private ArrayList<String> mLongClickList = new ArrayList<>();

    private BitmapQrcodeDecoder mQrcodeDecoder;

    private View mWatermarkView;

    private Session mSession;

    private int mWatermarkTextColor;

    private boolean mNeedHideIndexPosUI = false;

    private TextView mTvCounter;

    private RelativeLayout mRlDownloadFullImg;
    private TextView mTvDownloadFullImg;
    private TextView mTvFullDownloadProgress;
    private ImageView mIvFullDownloadCancel;

    private RelativeLayout mRlVideoPlayLayout;
    private ImageView mIvClosePageBtn;
    private ImageView mIvMoreBtn;
    private ImageView mIvVideoPauseBtn;
    private TextView mTvVideoPlayTime;
    private SeekBar mSeekVideoPlay;
    private TextView mTvVideoTotalTime;

    private int mCurrentSeekPos = 0;
    private MoviePlayerView mCurrentPlayerView;
    private ImageView mIvCurrentVideoPause;
    private int mMaxProgress = 0;
    private boolean mIsPause = false;
    /**
     * 弹框
     */
    private W6sSelectDialogFragment mW6sSelectDialogFragment;

    private Map<String, Integer> mFullDownloadProgressMap = new ConcurrentHashMap<>();

    private Handler mUpdateProgressHandler = new Handler();
    private Runnable mUpdateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentPlayerView == null) {
                return;
            }
            if (mCurrentPlayerView.isPlaying()) {
                mCurrentSeekPos = mCurrentPlayerView.getCurrentSeekPos();
                mSeekVideoPlay.setProgress(mCurrentSeekPos);
                mTvVideoPlayTime.setText(TimeUtil.generateTime(mCurrentSeekPos));
                mUpdateProgressHandler.postDelayed(mUpdateProgressRunnable, 1000);
                return;
            }
        }
    };

    private int mWhat;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {

                case MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS:
                    sendBroadCast();
                    mActivity.finish();
                    break;
            }
        }
    };

    public static void sendBroadCast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_DOWN_LOAD_VIEW_PAGER));
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_DOWN_LOAD_FILE_LIST));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_switcher, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerReceiver();
        initData();
        registerListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mQrcodeDecoder = new BitmapQrcodeDecoder(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        handleWatermark();
        int currentItem = getCurrentItem();
        mIvVideoPauseBtn.setBackgroundResource(R.mipmap.icon_play_video_white);
    }


    @Override
    public void onStop() {
        super.onStop();

        if (mCurrentPlayerView != null && mCurrentPlayerView.isPlaying()) {
            mIsPause = true;
            mCurrentPlayerView.pause();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUpdateProgressHandler.removeCallbacks(mUpdateProgressRunnable);
        cancelAllFullDownload();
    }

    @Override
    protected void findViews(View view) {
        mViewPager = view.findViewById(R.id.image_switcher_view_paper);
        mWatermarkView = view.findViewById(R.id.watermark_bg);
        mWatermarkTextColor = ColorUtils.setAlphaComponent(ContextCompat.getColor(getActivity(), R.color.watermark_text_color), 20);
        mTvCounter = view.findViewById(R.id.counter);
        mRlDownloadFullImg = view.findViewById(R.id.rl_full_download);
        mTvDownloadFullImg = view.findViewById(R.id.tv_full_download);
        mTvFullDownloadProgress = view.findViewById(R.id.tv_full_progress);
        mIvFullDownloadCancel = view.findViewById(R.id.iv_cancel_full_download);

        mRlVideoPlayLayout = view.findViewById(R.id.rl_micro_video_controller_area);
        mIvClosePageBtn = mRlVideoPlayLayout.findViewById(R.id.iv_close_page);
        mIvMoreBtn = mRlVideoPlayLayout.findViewById(R.id.iv_more);
        mIvVideoPauseBtn = mRlVideoPlayLayout.findViewById(R.id.iv_pause_video);
        mTvVideoPlayTime = mRlVideoPlayLayout.findViewById(R.id.tv_video_play_time);
        mSeekVideoPlay = mRlVideoPlayLayout.findViewById(R.id.seek_video);
        mTvVideoTotalTime = mRlVideoPlayLayout.findViewById(R.id.tv_video_total_time);
    }

    private void cancelAllFullDownload() {
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        Set<String> downloadIdList = mFullDownloadProgressMap.keySet();
        for (String downloadId : downloadIdList) {
            mediaCenterNetManager.brokenDownloadingOrUploading(downloadId);

        }
    }

    private void registerListener() {
        mRlDownloadFullImg.setOnClickListener(v -> {
            if (!CommonUtil.isFastClick(300)) {
                ImageChatMessage imageChatMessage = (ImageChatMessage) mChatMessagesList.get(getCurrentItem());
                if (mFullDownloadProgressMap.containsKey(imageChatMessage.deliveryId)) {
                    MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(getActivity());
                    mediaCenterNetManager.brokenDownloadingOrUploading(imageChatMessage.deliveryId);

                } else {
                    downloadFullImg(getCurrentItem(), imageChatMessage);

                }
            }
        });

        mIvClosePageBtn.setOnClickListener(view -> {
            finish();
        });

        mIvMoreBtn.setOnClickListener(view -> {
            popUp();
        });

        mIvVideoPauseBtn.setOnClickListener(view -> {
            if (mCurrentPlayerView == null) {
                return;
            }
            if (mCurrentPauseImageView != null) {
                mCurrentPauseImageView.setVisibility(View.GONE);
            }
            if (!mCurrentPlayerView.isPlaying()) {
                mIsPause = !mIsPause;
                mCurrentPlayerView.resume();
                mCurrentPlayerView.seekTo(mCurrentSeekPos);
                mIvVideoPauseBtn.setBackgroundResource(R.mipmap.icon_pause_video_white);
                mUpdateProgressHandler.postDelayed(mUpdateProgressRunnable, 0);
                return;
            }

            if (!mIsPause) {
                mIsPause = !mIsPause;
                mCurrentPlayerView.pause();
                mUpdateProgressHandler.removeCallbacks(mUpdateProgressRunnable);
                mIvVideoPauseBtn.setBackgroundResource(R.mipmap.icon_play_video_white);
                return;
            }

            mIsPause = !mIsPause;
            mIvVideoPauseBtn.setBackgroundResource(R.mipmap.icon_pause_video_white);
            mCurrentPlayerView.resume();
            mUpdateProgressHandler.postDelayed(mUpdateProgressRunnable, 0);
        });
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        int currentItem = getCurrentItem();
        if (undoEventMessage != null && currentItem < mChatMessagesList.size() && undoEventMessage.isMsgUndo(mChatMessagesList.get(currentItem).deliveryId)) {
            showUndoDialog(getActivity(), undoEventMessage);
        }
    }


    private void handleWatermark() {
        if (mSession == null) {
            return;
        }
        if (SessionType.User.equals(mSession.type) || SessionType.Service.equals(mSession.type)) {
            if (DomainSettingsManager.getInstance().handleChatFileWatermarkFeature()) {
                mWatermarkView.setVisibility(View.VISIBLE);
                WaterMarkUtil.setLoginUserWatermark(mActivity, mWatermarkView, -1, mWatermarkTextColor, "");
            }
            return;
        }
        if (SessionType.Discussion.equals(mSession.type)) {
            showDiscussionWatermark();
        }
    }

    private void showDiscussionWatermark() {
        boolean watermarkFeature = DomainSettingsManager.getInstance().handleChatFileWatermarkFeature();
        if (!watermarkFeature) {
            return;
        }
        mWatermarkView.setVisibility(View.VISIBLE);
        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(mActivity, mSession.identifier);
        if (discussion == null) {
            WaterMarkUtil.setLoginUserWatermark(mActivity, mWatermarkView, -1, mWatermarkTextColor, "");
            return;
        }
        if (TextUtils.isEmpty(discussion.getOrgCodeCompatible())) {
            WaterMarkUtil.setLoginUserWatermark(mActivity, mWatermarkView, -1, mWatermarkTextColor, "");
            return;
        }
        WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mWatermarkView, discussion.getOrgCodeCompatible(), -1, mWatermarkTextColor, -1, -1, 0);
    }

    private void registerReceiver() {

    }

    /**
     * 弹出旋转框
     */
    private void popUp() {
        ChatPostMessage chatMessage = mChatMessagesList.get(getCurrentItem());
        if (chatMessage instanceof ImageChatMessage || chatMessage instanceof FileTransferChatMessage) {
            handleQrCodeResult(chatMessage);
        } else if (chatMessage instanceof MicroVideoChatMessage) {
            popMessageSelectDialog(null, chatMessage);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private void handleQrCodeResult(final ChatPostMessage chatPostMessage) {

        new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... params) {
                return getQrCodeResult(chatPostMessage);

            }

            @Override
            protected void onPostExecute(Result result) {
                if (isAdded()) {
                    popMessageSelectDialog(result, chatPostMessage);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Nullable
    private Result getQrCodeResult(ChatPostMessage chatPostMessage) {
        //下面方法尝试去获取是否有二维码result
        Result result = null;

        Bitmap judgeBitmap = BitmapCache.getBitmapCache().getBitmapFromMemCache(chatPostMessage.deliveryId + ImageChatMessage.ORIGINAL_SUFFIX);
        byte[] originalImage = new byte[0];
        if (null == judgeBitmap) {
            originalImage = ImageShowHelper.getOriginalImage(mActivity, chatPostMessage.deliveryId);
            if (0 != originalImage.length) {
                //转化成合适二维码解码的尺寸
                byte[] compressBye = ImageShowHelper.compressImageForQrcodeRecognize(originalImage);
                judgeBitmap = BitmapUtil.Bytes2Bitmap(compressBye);
            }

        } else {
            //转化成合适二维码解码的尺寸
            byte[] compressBye = ImageShowHelper.compressImageForQrcodeRecognize(BitmapUtil.Bitmap2Bytes(judgeBitmap, false));
            judgeBitmap = BitmapUtil.Bytes2Bitmap(compressBye);
        }

        if (null != judgeBitmap) {

            result = mQrcodeDecoder.getRawResult(judgeBitmap);
        }

        return result;
    }

    private void popMessageSelectDialog(Result result, final ChatPostMessage chatPostMessage) {
        mLongClickList.clear();

        if (needTransfer(chatPostMessage)) {
            mLongClickList.add(getResources().getString(R.string.forwarding_item));
        }

        if (needDownload(chatPostMessage)) {
            mLongClickList.add(getResources().getString(R.string.save_to_mobile));
        }
        if(null != result) {
            mLongClickList.add(getResources().getString(R.string.qrcode_recognition));
        }

        if (!ListUtil.isEmpty(mLongClickList)){
            mW6sSelectDialogFragment = new W6sSelectDialogFragment();
            mW6sSelectDialogFragment.setData(new CommonPopSelectData(mLongClickList, null))
                    .setOnClickItemListener((position, item) ->  {
                        if (TextUtils.isEmpty(item)) {
                            return;
                        }
                        setPopDialogClickEvent(result, chatPostMessage, item);
                    })
                    .show(getChildFragmentManager(), "VIEW_IMAGE_DIALOG");
        }

    }


    private boolean needTransfer(ChatPostMessage chatPostMessage) {
        if (chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;
            if (TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), fileTransferChatMessage.expiredTime)) {
                return false;
            }
        }


        return DomainSettingsManager.getInstance().handleChatFileTransferEnabled();
    }


    private boolean needDownload(ChatPostMessage chatPostMessage) {
        if (!DomainSettingsManager.getInstance().handleChatFileDownloadEnabled()) {
            return false;
        }

        if (chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;
            if (TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), fileTransferChatMessage.expiredTime)) {
                return false;
            }
        }

        if (AtworkConfig.ENCRYPT_CONFIG.isImageSaveIgnoringEncrypt()) {
            return true;
        }

        return !AtworkConfig.OPEN_DISK_ENCRYPTION;
    }

    /**
     * Description:弹出框点击事件
     * @param result
     * @param chatPostMessage
     * @param item
     */
    private void setPopDialogClickEvent(Result result, final ChatPostMessage chatPostMessage, String item){
        if (isAdded()) {
            if (getResources().getString(R.string.forwarding_item).equals(item)) {
                transferMsg(chatPostMessage);

                return;
            }
            if (getResources().getString(R.string.save_to_mobile).equals(item)) {

                //保存图片
                if (chatPostMessage instanceof ImageChatMessage || chatPostMessage instanceof FileTransferChatMessage) {
                    handleSaveImg(chatPostMessage);

                } else if (chatPostMessage instanceof MicroVideoChatMessage) {
                    handleSaveVideo(chatPostMessage);
                }

                return;
            }
            if (getResources().getString(R.string.qrcode_recognition).equals(item)) {
                handleQrCode(result);
                return;
            }
        }
    }

    private void transferMsg(ChatPostMessage chatPostMessage) {
        //转发图片
        List<ChatPostMessage> chatPostMessages = new ArrayList<>();
        chatPostMessages.add(chatPostMessage);


        TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
        transferMessageControlAction.setSendMessageList(chatPostMessages);
        transferMessageControlAction.setSendMode(TransferMessageMode.FORWARD);
        Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

        startActivity(intent);
    }

    private void handleQrCode(Result result) {
        if (null != result) {

            String resultText = result.getText();

            final ProgressDialogHelper progressDialog = new ProgressDialogHelper(mActivity);
            progressDialog.show(mActivity.getResources().getString(R.string.loading));

            new Handler().postDelayed(() -> {

                progressDialog.dismiss();

                realHandleQrcodeResult(resultText);

            }, 1000L);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private void handleSaveVideo(ChatPostMessage chatPostMessage) {
        String path = getPathSaved(chatPostMessage);
        if (!FileUtil.isExist(path)) {
            AtworkToast.showResToast(R.string.save_micro_video_to_mobile_fail);
            return;
        }

        mProgressDialogHelper.show();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                String savedPath = MicroVideoHelper.saveVideoToGalleryAndGetPath(getActivity(), null, path);

                if (!StringUtils.isEmpty(savedPath)) {
                    FileDaoService.getInstance().insertRecentFile(savedPath);

                }
                return !StringUtils.isEmpty(savedPath);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mProgressDialogHelper.dismiss();
                if (result) {
                    if (chatPostMessage instanceof FileTransferChatMessage) {
                        FileDownloadNotifyService.handleFileDownloadSuccessfullyNotify((FileTransferChatMessage) chatPostMessage);

                    }

                    int galleryIndex = AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).indexOf(AtworkConfig.APP_FOLDER);
                    AtworkToast.showResToast(R.string.save_micro_video_to_mobile_success, AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).substring(galleryIndex));

                    updateStatus(chatPostMessage);


                    return;
                }

                AtworkToast.showResToast(R.string.save_micro_video_to_mobile_fail);

            }


        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private String getPathSaved(ChatPostMessage chatPostMessage) {
        String imgMediaId = StringUtils.EMPTY;

        if (chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;

            if (imageChatMessage.isFullImgExist()) {
                return imageChatMessage.fullImgPath;
            }

            imgMediaId = imageChatMessage.mediaId;


        } else if (chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = ((FileTransferChatMessage) chatPostMessage);
            if (FileUtil.isExist(fileTransferChatMessage.filePath)) {
                return fileTransferChatMessage.filePath;
            }

            imgMediaId = fileTransferChatMessage.mediaId;

        } else if (chatPostMessage instanceof MicroVideoChatMessage) {
            MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage) chatPostMessage;
            return FileHelper.getMicroExistVideoFilePath(BaseApplicationLike.baseContext, microVideoChatMessage);
        }

        String path = ImageShowHelper.getOriginalPath(getActivity(), imgMediaId);

        if (!FileUtil.isExist(path)) {
            path = ImageShowHelper.getOriginalPath(getActivity(), chatPostMessage.deliveryId);
        }

        return path;


    }

    @SuppressLint("StaticFieldLeak")
    private void handleSaveImg(ChatPostMessage chatPostMessage) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                String path;
                Boolean isGif = null;

                if (chatPostMessage instanceof ImageChatMessage) {
                    ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
                    isGif = imageChatMessage.isGif;


                } else if (chatPostMessage instanceof FileTransferChatMessage) {
                    FileTransferChatMessage fileTransferChatMessage = ((FileTransferChatMessage) chatPostMessage);
                    isGif = fileTransferChatMessage.isGifType();
                }

                path = getPathSaved(chatPostMessage);

                if (null == isGif) {
                    isGif = GifChatHelper.isGif(path);
                }

                if (!FileUtil.isExist(path)) {
                    return false;
                }

                mProgressDialogHelper.show();


                path = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false);
                String savedPath = ImageShowHelper.saveImageToGalleryAndGetPath(getActivity(), null, path, isGif, false);
                if (!StringUtils.isEmpty(savedPath)) {
                    FileDaoService.getInstance().insertRecentFile(savedPath);

                }
                return !StringUtils.isEmpty(savedPath);

            }

            @Override
            protected void onPostExecute(Boolean succeed) {

                mProgressDialogHelper.dismiss();

                if (succeed) {

                    if (chatPostMessage instanceof FileTransferChatMessage) {
                        FileDownloadNotifyService.handleFileDownloadSuccessfullyNotify((FileTransferChatMessage) chatPostMessage);

                    }

                    int galleryIndex = AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).indexOf(AtworkConfig.APP_FOLDER);
                    AtworkToast.showResToast(R.string.save_image_to_mobile_success, AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).substring(galleryIndex));

                    updateStatus(chatPostMessage);


                    return;
                }
                AtworkToast.showResToast(R.string.save_image_to_mobile_fail);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean updateStatus(ChatPostMessage chatPostMessage) {

        if (chatPostMessage instanceof FileTransferChatMessage) {
            updateFileMsgStatus((FileTransferChatMessage) chatPostMessage);
            return true;
        }
        return false;
    }

    private void updateFileMsgStatus(FileTransferChatMessage chatPostMessage) {
        FileTransferChatMessage fileTransferChatMessage = chatPostMessage;
        fileTransferChatMessage.fileStatus = FileStatus.DOWNLOADED;
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, fileTransferChatMessage);
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
    }


    private void realHandleQrcodeResult(String resultText) {
        QrcodeManager.getInstance().handleSelfProtocol(mActivity, resultText);
    }


    private void initData() {

        mProgressDialogHelper = new ProgressDialogHelper(getActivity());

        if (getArguments() != null) {
            mChatMessagesList = (List<ChatPostMessage>) getArguments().getSerializable(IMAGE_DATA);
            mFileData = (FileData)getArguments().getSerializable(FILE_DATA);
            mTotal = mChatMessagesList.size();
            mCurrentPos = getArguments().getInt(INDEX_SWITCH_IMAGE);
            mSession = getArguments().getParcelable(ARGUMENT_SESSION);

            mBingId = getArguments().getString(DATA_BING_ID);
            mNeedHideIndexPosUI = getArguments().getBoolean(DATA_HIDE_INDEX_POS_UI);
        }

        mImagePagerAdapter = new ImagePagerAdapter(position -> {
            final ChatPostMessage message = mChatMessagesList.get(position);

            if (message instanceof MicroVideoChatMessage) {
                View view = mImagePagerAdapter.views.get(position);

                if (null == view) {
                    return;
                }
                loadMicroVideo((MicroVideoChatMessage) message, view);
            }
        });
        mViewPager.setAdapter(mImagePagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnPageChangeListener(new ItemLargeDetailViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mOnPagerScroll = true;
            }

            @Override
            public void onPageSelected(int position, int prePosition) {
                final ChatPostMessage message = mChatMessagesList.get(position);
                resetVideoView();

                if (mFullDownloadProgressMap.containsKey(message.deliveryId)) {
                    showDownloadProgressUI();

                    int progress = mFullDownloadProgressMap.get(message.deliveryId);
                    mTvFullDownloadProgress.setText(progress + "%");

                } else {
                    refreshFloatUI(position, message);

                }

                if (message instanceof ImageChatMessage) {
                    ImageChatMessage imageChatMessage = (ImageChatMessage) message;
                    if (imageChatMessage.isGif) {
                        loadGif(position, imageChatMessage);
                        return;
                    }
                    if (imageChatMessage.isFullImgExist()) {
                        displayImg(imageChatMessage.fullImgPath, position);

                    } else {

                        loadImage(position, imageChatMessage);

                    }
                    return;
                }

                if (message instanceof MicroVideoChatMessage) {

                    View view = mImagePagerAdapter.views.get(position);
                    if (null == view) {
                        return;
                    }
                    release(prePosition);
                    loadMicroVideo((MicroVideoChatMessage) message, view);
                    return;
                }
                if (message instanceof FileTransferChatMessage) {
                    FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;

                    if (fileTransferChatMessage.isGifType()) {
                        loadGif(position, fileTransferChatMessage);
                        return;
                    }
                    loadImage(position, fileTransferChatMessage);
                    return;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    mOnPagerScroll = true;
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    mOnPagerScroll = false;
                } else {
                    mOnPagerScroll = false;
                }
            }
        });
        setupOnTouchListeners(mViewPager);
        mViewPager.setCurrentItem(mCurrentPos);
        if (mCurrentPos >= mChatMessagesList.size()) {
            return;
        }
        refreshFloatUI(mCurrentPos, mChatMessagesList.get(mCurrentPos));

    }

    private void refreshFloatUI(int position, ChatPostMessage message) {
        if(mNeedHideIndexPosUI) {
            mTvCounter.setVisibility(View.INVISIBLE);
        } else {
            mTvCounter.setVisibility(View.VISIBLE);

        }
        mTvCounter.setText((position + 1) + "/" + mTotal);

        if (message instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) message;
            if (imageChatMessage.hasFullImg()) {
                String originalPath = ImageShowHelper.getOriginalPath(mActivity, imageChatMessage.deliveryId);

                if (!FileUtil.isExist(originalPath) || imageChatMessage.isFullImgExist()) {
                    mRlDownloadFullImg.setVisibility(View.GONE);

                } else {
                    mTvDownloadFullImg.setText(getStrings(R.string.download_full_img, FileHelper.getFileSizeStr(imageChatMessage.info.size)));
                    mTvDownloadFullImg.setVisibility(View.VISIBLE);
                    mTvFullDownloadProgress.setVisibility(View.GONE);
                    mIvFullDownloadCancel.setVisibility(View.GONE);

                    mRlDownloadFullImg.setVisibility(View.VISIBLE);

                }
            } else {
                mRlDownloadFullImg.setVisibility(View.GONE);

            }
        } else {
            mRlDownloadFullImg.setVisibility(View.GONE);

        }
    }

    private void showDownloadProgressUI() {
        mTvFullDownloadProgress.setText("0%");

        mRlDownloadFullImg.setVisibility(View.VISIBLE);
        mTvDownloadFullImg.setVisibility(View.INVISIBLE);
        mTvFullDownloadProgress.setVisibility(View.VISIBLE);
        mIvFullDownloadCancel.setVisibility(View.VISIBLE);
    }

    private void release(int prePosition) {
        View preView = mImagePagerAdapter.views.get(prePosition);
        Object object = preView.getTag();
        if (object == null) {
            return;
        }
        if (object instanceof MoviePlayerHolder) {
            MoviePlayerHolder holder = (MoviePlayerHolder) object;
            if (holder == null) {
                return;
            }
            holder.mMoviePlayerView.release();
        }

    }


    private void setupOnTouchListeners(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            mScaleGestureDetector = new ScaleGestureDetector(getActivity(), new ImageOnScaleGestureListener());
        }
        mGestureDetector = new GestureDetector(getActivity(), new ImageGestureListener());

        View.OnTouchListener rootListener = (v, event) -> {
            if (!mOnScale) {
                if (!mOnPagerScroll) {
                    mGestureDetector.onTouchEvent(event);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
                if (!mOnPagerScroll) {

                    try {
                        mScaleGestureDetector.onTouchEvent(event);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (getCurrentImageView() instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) getCurrentImageView();
                if (imageView == null) {
                    return true;
                }

                if (imageView.mBitmapDisplayed.getBitmap() == null) {
                    return true;
                }
                if (!mOnScale) {
                    Matrix m = imageView.getImageViewMatrix();
                    RectF rect = new RectF(0, 0, imageView.mBitmapDisplayed
                            .getBitmap().getWidth(), imageView.mBitmapDisplayed
                            .getBitmap().getHeight());
                    m.mapRect(rect);

                    if (!(rect.right > imageView.getWidth() + 0.1 && rect.left < -0.1)) {
                        try {
                            mViewPager.onTouchEvent(event);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                }
            }

            if (getCurrentImageView() instanceof RelativeLayout) {
                View view = getCurrentImageView();
                if (view == null) {
                    return true;
                }
                if (!mOnScale) {
                    RectF rect = new RectF(0, 0, view.getWidth(), view.getHeight());
                    if (!(rect.right > view.getWidth() + 0.1 && rect.left < -0.1)) {
                        try {
                            mViewPager.onTouchEvent(event);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                }
            }

            return true;
        };

        rootView.setOnTouchListener(rootListener);
    }

    private void instantiateGif(ChatPostMessage chatPostMessage, ImageView cellView, int position) {
        byte[] gifByte = GifChatHelper.getChatMsgGifByte(mActivity, chatPostMessage);
        String mediaId = ImageShowHelper.getChatMsgImgMediaId(chatPostMessage);
        byte[] thumbnails = ImageShowHelper.getChatMsgImgThumbnail(chatPostMessage);


        Bitmap thumbBitmap = BitmapCache.getBitmapCache().getContentBitmap(mActivity, chatPostMessage.deliveryId, mediaId, true, thumbnails);
        if (null != thumbBitmap) {
            cellView.setImageBitmap(thumbBitmap);
        }

        if (!ArrayUtil.isEmpty(gifByte)) {
            showGif(cellView, gifByte);
        } else {
            //当初次点击进来是viewPaper 的第一个, 并不会触发 viewpager 的 onPageSelected,
            // 这时, 需要在该处初始化时开始下载gif
            if ((0 == position)
                    && getCurrentItem() == position) {
                downloadOriginGif(position, chatPostMessage);
            }

        }
    }

    private void instantiateImage(ChatPostMessage chatPostMessage, ItemEnlargeImageView cellView, int position) {


        //兼容处理
        if (makeGifCompatible(chatPostMessage, cellView, position)) return;

        if (chatPostMessage instanceof StickerChatMessage) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cellView.setLayoutParams(params);
            ImageChatHelper.initStickerContent((StickerChatMessage) chatPostMessage, cellView);
            return;
        }


        String imgPath = ImageShowHelper.getChatMsgImgPath(mActivity, chatPostMessage);
        if (FileUtil.isExist(imgPath)) {
            displayImg(imgPath, cellView);

        } else {
            Bitmap thumbBitmap = BitmapCache.getBitmapCache().getThumbBitmap(mActivity, chatPostMessage.deliveryId, ImageShowHelper.getChatMsgImgThumbnail(chatPostMessage));

            if (null != thumbBitmap) {
                cellView.setImageBitmap(thumbBitmap);
            }

            //当初次点击进来是viewPaper 的第一个, 并不会触发 viewpager 的 onPageSelected,
            // 这时, 需要在该处初始化时开始下载原图
            if ((0 == position)
                    && getCurrentItem() == position) {
                downloadOriginImg(position, chatPostMessage);
            }
        }

    }


    private boolean makeGifCompatible(ChatPostMessage chatPostMessage, ImageView cellView, int position) {
        String path = ImageShowHelper.getChatMsgGifPath(mActivity, chatPostMessage);

        if (!TextUtils.isEmpty(path) && GifChatHelper.isGif(path)) {
            instantiateGif(chatPostMessage, cellView, position);
            return true;
        }
        return false;
    }


    private void instantiateMicroVideo(MicroVideoChatMessage microVideoChatMessage, View view) {
        if (view == null) {
            return;
        }

        if (microVideoChatMessage.thumbnails != null) {
            mBitmap = BitmapUtil.Bytes2Bitmap(microVideoChatMessage.thumbnails);
        }
        if (mBitmap == null) {
            mBitmap = BitmapCache.getBitmapCache().getBitmapFromMemCache(microVideoChatMessage.deliveryId + ImageChatMessage.THUMBNAIL_SUFFIX);
        }
        if (mBitmap == null) {
            byte[] thumbnailBitmap = ImageShowHelper.getThumbnailImage(mActivity, microVideoChatMessage.deliveryId);

            if (thumbnailBitmap.length != 0) {
                mBitmap = BitmapUtil.Bytes2Bitmap(thumbnailBitmap);

            }

        }
        if (mBitmap != null) {
            MoviePlayerHolder holder = (MoviePlayerHolder) view.getTag();
            holder.mThumbnail.setImageBitmap(mBitmap);

            refreshPlayVideoViewSize(holder, mBitmap);

        }

    }

    private void refreshPlayVideoViewSize(MoviePlayerHolder holder, Bitmap bitmap) {
        if (null != bitmap) {
            ViewGroup.LayoutParams thumbLayout = holder.mThumbnail.getLayoutParams();
            thumbLayout.width = ScreenUtils.getScreenWidth(mActivity);
            thumbLayout.height = (int) (thumbLayout.width * (1f * bitmap.getHeight() / bitmap.getWidth()));
            holder.mThumbnail.setLayoutParams(thumbLayout);


            ViewGroup.LayoutParams movieLayout = holder.mMoviePlayerView.getLayoutParams();
            movieLayout.width = thumbLayout.width;
            movieLayout.height = thumbLayout.height;
            holder.mMoviePlayerView.setLayoutParams(movieLayout);


        }
    }

    private ImageView mCurrentPauseImageView;

    private void playMicroVideo(final String videoFilePath, final View view) {
        final Handler handler = new Handler(msg -> {
            MoviePlayerHolder holder = (MoviePlayerHolder) view.getTag();
            holder.mThumbnail.setVisibility(View.GONE);
            mCurrentPauseImageView = holder.mIvPlay;
            holder.mIvPlay.setVisibility(View.GONE);
            holder.mMoviePlayerView.setVisibility(View.VISIBLE);
            mCurrentPlayerView = holder.mMoviePlayerView;
            holder.mMoviePlayerView.play(videoFilePath, new MoviePlayerView.OnMoviePlayListener() {
                @Override
                public void onPlayCompletion() {
                    holder.mIvPlay.setVisibility(View.VISIBLE);
                    holder.mIvPlay.setOnClickListener(view -> {
                        holder.mIvPlay.setVisibility(View.GONE);
                        mIvVideoPauseBtn.callOnClick();
                    });
                    mCurrentSeekPos = 0;
                    mCurrentPlayerView.seekTo(0);
                    mSeekVideoPlay.setProgress(0);
                    mIvVideoPauseBtn.setBackgroundResource(R.mipmap.icon_play_video_white);
                    mTvVideoPlayTime.setText("00:00");
                    mRlVideoPlayLayout.setVisibility(View.GONE);
                }

                @Override
                public void onPrepareListener(int duration) {
                    mMaxProgress = duration;
                    mTvVideoTotalTime.setText(TimeUtil.generateTime(duration));
                    mSeekVideoPlay.setMax(duration);
                    mSeekVideoPlay.setSecondaryProgress(duration);
                }
            });
            setSeekListener(holder.mMoviePlayerView);
            mIvVideoPauseBtn.setBackgroundResource(R.mipmap.icon_pause_video_white);
            mUpdateProgressHandler.postDelayed(mUpdateProgressRunnable, 0);
            return false;
        });
        Executors.newScheduledThreadPool(1).schedule(() -> handler.obtainMessage(0).sendToTarget(), 1, TimeUnit.SECONDS);
    }

    private void showGif(ImageView view, byte[] gifByte) {
        try {
            GifDrawable gifDrawable = new GifDrawable(gifByte);
            Bitmap holderBitmap = gifDrawable.getCurrentFrame();
            view.setImageBitmap(holderBitmap);
            view.setImageDrawable(gifDrawable);
            view.setTag(HAS_SET_GIF);

            holderBitmap.recycle();
            holderBitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImage(final int position, ChatPostMessage chatPostMessage) {
        String imgPath = ImageShowHelper.getChatMsgImgPath(mActivity, chatPostMessage);

        if (!FileUtil.isExist(imgPath)) {
            downloadOriginImg(position, chatPostMessage);
        }
    }


    private void loadGif(final int position, ChatPostMessage chatPostMessage) {
        byte[] gifByte = GifChatHelper.getChatMsgGifByte(mActivity, chatPostMessage);

        if (ArrayUtil.isEmpty(gifByte)) {
            downloadOriginGif(position, chatPostMessage);

        } else {
            ItemEnlargeImageView view = (ItemEnlargeImageView) mImagePagerAdapter.views.get(position);

            if (null == view) {
                return;
            }

            if (null == view.getTag() || HAS_SET_GIF != ((Boolean) view.getTag()).booleanValue()) {
                showGif(view, gifByte);

            }
        }
    }

    private void loadMicroVideo(MicroVideoChatMessage microVideoChatMessage, View view) {


        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(FileHelper.getMicroExistVideoFilePath(mActivity, microVideoChatMessage), false, fileName -> {

            if (!FileUtil.isExist(fileName)) {
//                downloadMicroVideo(microVideoChatMessage, view);
                handleViewPlayBtnVisibility(view, View.VISIBLE);

                return;
            }

            playMicroVideo(fileName, view);
        });


    }

    private void downloadMicroVideo(final MicroVideoChatMessage microVideoChatMessage, final View view) {
        handleViewPlayBtnVisibility(view, View.GONE);
        mProgressDialogHelper.show();
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return microVideoChatMessage.deliveryId;
            }

            @Override
            public void downloadSuccess() {
                mProgressDialogHelper.dismiss();
                String videoFilePath = FileHelper.getMicroExistVideoFilePath(mActivity, microVideoChatMessage);

                EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(videoFilePath, false, fileName -> playMicroVideo(fileName, view));

            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                mProgressDialogHelper.dismiss();
                if (isAdded()) {
                    AtworkToast.showToast(getString(R.string.download_micro_video_fail));

                    handleViewPlayBtnVisibility(view, View.VISIBLE);
                }
            }

            @Override
            public void downloadProgress(double progress, double value) {
            }
        });

        mediaCenterNetManager.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(microVideoChatMessage.mediaId).setDownloadId(microVideoChatMessage.deliveryId)
                        .setDownloadPath(FileHelper.getMicroNewFilePath(mActivity, microVideoChatMessage)).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
        );
    }

    private void handleViewPlayBtnVisibility(View view, int visibility) {
        MoviePlayerHolder holder = (MoviePlayerHolder) view.getTag();
        if (null != holder) {
            holder.mIvPlay.setVisibility(visibility);
        }
    }

    private void downloadOriginGif(final int position, ChatPostMessage chatPostMessage) {
        String mediaId = ImageShowHelper.getChatMsgImgMediaId(chatPostMessage);
        String filePath = ImageShowHelper.getChatMsgGifPath(mActivity, chatPostMessage);

        mProgressDialogHelper.show();
        //下载gif
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return chatPostMessage.deliveryId;
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
                    ItemEnlargeImageView preImageView = (ItemEnlargeImageView) mImagePagerAdapter.views.get(position);
                    showGif(preImageView, gifByte);

                    //刷新聊天界面, 让 gif 动起来
                    ChatDetailExposeBroadcastSender.refreshMessageListViewUI();


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
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(mediaId).setDownloadId(chatPostMessage.deliveryId)
                        .setDownloadPath(filePath).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.ORIGINAL)
        );
    }

    private void downloadOriginImg(final int position, ChatPostMessage chatPostMessage) {
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
                if (handleDownloadGifSuccess(imgFilePath, position)) return;


                //notify refresh
                try {
                    ItemEnlargeImageView preImageView = (ItemEnlargeImageView) mImagePagerAdapter.views.get(position);
                    displayImg(imgFilePath, preImageView);

                    //刷新聊天界面, 避免图片模糊
                    ChatDetailExposeBroadcastSender.refreshMessageListViewUI();


                    if (position == getCurrentItem()) {
                        refreshFloatUI(position, mChatMessagesList.get(position));
                    }

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
                DownloadFileParamsMaker.Companion.newRequest().setDownloadId(chatPostMessage.deliveryId)
                        .setMediaId(mediaId).setDownloadPath(imgFilePath).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.ORIGINAL)
        );
    }

    private boolean handleDownloadGifSuccess(String imgPath, int position) {
        if (!TextUtils.isEmpty(imgPath) && GifChatHelper.isGif(imgPath)) {
            byte[] gifByte = FileStreamHelper.readFile(imgPath);
            //notify refresh
            try {
                ItemEnlargeImageView preImageView = (ItemEnlargeImageView) mImagePagerAdapter.views.get(position);
                showGif(preImageView, gifByte);
                //刷新聊天界面, 让 gif 动起来
                ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void downloadFullImg(final int position, ImageChatMessage imageChatMessage) {
        final String deliveryId = imageChatMessage.deliveryId;
        final String mediaId = imageChatMessage.fullMediaId;

        showDownloadProgressUI();
        //下载原图片
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        final String fullPath = ImageShowHelper.getFullPath(mActivity, deliveryId);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return deliveryId;
            }

            @Override
            public void downloadSuccess() {
                mFullDownloadProgressMap.remove(deliveryId);

                displayImg(fullPath, position);

                //update imgChatMessage
                imageChatMessage.fullImgPath = fullPath;

                if (getCurrentItem() == position) {
                    refreshFloatUI(position, imageChatMessage);
                }
                if (!imageChatMessage.isBingReplyType()) {
                    ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, imageChatMessage);
                }
            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                mFullDownloadProgressMap.remove(deliveryId);

                if (getCurrentItem() == position) {
                    refreshFloatUI(position, imageChatMessage);
                }

                if (-99 != errorCode && isAdded()) {
                    AtworkToast.showToast(getString(R.string.download_org_image_fail));
                }
            }

            @Override
            public void downloadProgress(double progress, double value) {
                int progressInt = (int) progress;
                mFullDownloadProgressMap.put(deliveryId, progressInt);

                if (getCurrentItem() == position) {
                    mTvFullDownloadProgress.setText(progressInt + "%");
                }
            }
        });

        mediaCenterNetManager.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(mediaId).setDownloadId(deliveryId)
                        .setDownloadPath(fullPath).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
        );
    }

    private void displayImg(String filePath, int position) {
        ItemEnlargeImageView preImageView = (ItemEnlargeImageView) mImagePagerAdapter.views.get(position);
        if (null == preImageView) {
            return;
        }

        displayImg(filePath, preImageView);
    }

    private void displayImg(String filePath, ItemEnlargeImageView preImageView) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(true);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        File file = new File(filePath);
        if (AtworkConfig.CHAT_IMG_SHOW_LIMIT < file.length()) {
            builder.imageScaleType(ImageScaleType.NONE_SAFE);

        } else {
            builder.imageScaleType(ImageScaleType.NONE);


        }
        DisplayImageOptions options = builder.build();

        ImageCacheHelper.displayImage(filePath, preImageView, options);
    }

    private View getCurrentImageView() {
        return mImagePagerAdapter.views.get(getCurrentItem());
    }

    private int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }


    private class ImagePagerAdapter extends EnlargedImageAdapter {
        public SparseArray<View> views = new SparseArray<>();
        public InstantiateItemListener mListener;
        //这个参数是处理刚从视频点击进来，因为onPagerSelected的回调比这个快，导致不会自动播放
        public boolean mHasCallBack = false;
        public int count = 0;


        public ImagePagerAdapter(InstantiateItemListener listener) {
            mListener = listener;
        }

        @Override
        public int getCount() {
            return mChatMessagesList.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {

            final ChatPostMessage message = mChatMessagesList.get(position);
            if (message instanceof ImageChatMessage || message instanceof FileTransferChatMessage || message instanceof StickerChatMessage) {
                final ItemEnlargeImageView cellView = new ItemEnlargeImageView(getActivity());
                if (message instanceof ImageChatMessage) {
                    final ImageChatMessage imageChatMessage = (ImageChatMessage) message;
                    if (imageChatMessage.isGif) {
                        instantiateGif(imageChatMessage, cellView, position);

                    } else {
                        if (imageChatMessage.isFullImgExist()) {
                            displayImg(imageChatMessage.fullImgPath, cellView);

                        } else {
                            instantiateImage(imageChatMessage, cellView, position);

                        }
                    }
                }
                if (message instanceof FileTransferChatMessage) {
                    final FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
                    if (fileTransferChatMessage.isGifType()) {
                        instantiateGif(fileTransferChatMessage, cellView, position);
                    } else {
                        instantiateImage(fileTransferChatMessage, cellView, position);
                    }
                }

                if (message instanceof StickerChatMessage) {
                    StickerChatMessage stickerChatMessage = (StickerChatMessage) message;
                    if (stickerChatMessage.isGif()) {
                        //TODO...
                    }
                    instantiateImage(stickerChatMessage, cellView, position);
                }

                cellView.setScaleType(ImageView.ScaleType.MATRIX);
                cellView.setFocusableInTouchMode(true);
                ((ViewGroup) container).addView(cellView);
                views.put(position, cellView);
                return cellView;
            }


            if (message instanceof MicroVideoChatMessage) {

                int currentItem = getCurrentItem();
                final MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage) message;
                LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.movie_play_view, null);
                MoviePlayerHolder holder = new MoviePlayerHolder();
                holder.mMoviePlayerView = view.findViewById(R.id.moviePlayView);
                holder.mThumbnail = view.findViewById(R.id.video_thumbnail);
                holder.mIvPlay = view.findViewById(R.id.iv_play);
                holder.mMoviePlayerView.setOnClickListener(v -> {
                    mRlVideoPlayLayout.setVisibility(mRlVideoPlayLayout.isShown() ? View.GONE : View.VISIBLE);
                });

                holder.mMoviePlayerView.setOnLongClickListener(v -> {
                    //popUp();
                    handleMyDownloadItemLongClick(mFileData);
                    return false;
                });

                holder.mIvPlay.setOnClickListener(v -> {
                    handleClickPlayVideo(microVideoChatMessage, view);
                });


                view.setTag(holder);

                instantiateMicroVideo(microVideoChatMessage, view);
                ((ViewGroup) container).addView(view);
                views.put(position, view);

                if (!mHasCallBack && currentItem == position) {
                    mHasCallBack = true;
                    mListener.onInstantiateItemFinish(position);
                }

                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            resetVideoView();
            if (object instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) object;
                ((ViewGroup) container).removeView(imageView);
            }
            if (object instanceof RelativeLayout) {
                RelativeLayout layout = (RelativeLayout) object;
                handleChildView(layout);
                ((ViewGroup) container).removeView(layout);
            }


            views.remove(position);
        }

        @Override
        public void startUpdate(View container) {
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if (object instanceof ItemEnlargeImageView) {
                return view == object;
            }
            return view == object;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        private void handleChildView(RelativeLayout view) {
            for (int i = 0; i < view.getChildCount(); i++) {
                View childView = view.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                if (childView instanceof MoviePlayerView) {
                    ((MoviePlayerView) childView).release();
                }
            }
        }


    }

    private void setSeekListener(MoviePlayerView playerView) {
        mSeekVideoPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mCurrentSeekPos = seekBar.getProgress();
                if (playerView == null || !playerView.isPlaying()) {
                    return;
                }
                playerView.seekTo(mCurrentSeekPos);
            }
        });
    }


    private void handleClickPlayVideo(MicroVideoChatMessage microVideoChatMessage, View view) {
        if (NetworkStatusUtil.isWifiConnectedOrConnecting(AtworkApplicationLike.baseContext)) {
            downloadMicroVideo(microVideoChatMessage, view);
            return;
        }


        if (AtworkConfig.CHAT_MICRO_VIDEO_PLAY_NOT_CHECK_THRESHOLD_SIZE > microVideoChatMessage.size) {
            downloadMicroVideo(microVideoChatMessage, view);
            return;
        }


        AtworkAlertDialog alertDialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                .setContent(getStrings(R.string.micro_video_play_check_tip, FileHelper.getFileSizeStr(microVideoChatMessage.size)))
                .setBrightBtnText(R.string.continue_to_play)
                .setClickBrightColorListener(dialog -> downloadMicroVideo(microVideoChatMessage, view));

        alertDialog.show();
    }

    private class ImageGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("AAABBB", mOnScale + ":" + mOnPagerScroll);
            if (mOnScale) {
                return;
            }

           // popUp();
            handleMyDownloadItemLongClick(mFileData);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mOnScale) {
                return true;
            }
            if (mPaused) {
                return false;
            }
            if (getCurrentImageView() instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) getCurrentImageView();
                if (imageView == null) {
                    return true;
                }
                imageView.panBy(-distanceX, -distanceY);
                imageView.center(true, true);
            }
            if (getCurrentImageView() instanceof RelativeLayout) {
            }

            return true;
        }

        @Override
        public boolean onUp(MotionEvent e) {
            return super.onUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isAdded()) {
                int currentItem = getCurrentItem();
                ChatPostMessage message = mChatMessagesList.get(currentItem);
                if (message instanceof MicroVideoChatMessage) {
                    mRlVideoPlayLayout.setVisibility(mRlVideoPlayLayout.isShown() ? View.GONE : View.VISIBLE);
                    return true;
                }
                getActivity().finish();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mPaused) {
                return false;
            }
            if (getCurrentImageView() instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) getCurrentImageView();
                if (imageView == null) {
                    return true;
                }
                // Switch between the original scale and 3x scale.
                if (imageView.mBaseZoom < 1) {
                    if (imageView.getScale() > 2F) {
                        imageView.zoomTo(1f);
                    } else {
                        imageView.zoomToPoint(3f, e.getX(), e.getY());
                    }
                } else {
                    if (imageView.getScale() > (imageView.mMinZoom + imageView.mMaxZoom) / 2f) {
                        imageView.zoomTo(imageView.mMinZoom);
                    } else {
                        imageView.zoomToPoint(imageView.mMaxZoom, e.getX(),
                                e.getY());
                    }
                }
            }

            return true;
        }
    }

    private class ImageOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {


        float currentScale;
        float currentMiddleX;
        float currentMiddleY;

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (getCurrentImageView() instanceof ItemEnlargeImageView) {
                final ItemEnlargeImageView imageView = (ItemEnlargeImageView) getCurrentImageView();
                if (imageView == null) {
                    return;
                }

                if (currentScale > imageView.mMaxZoom) {
                    imageView.zoomToNoCenterWithAni(currentScale / imageView.mMaxZoom, 1, currentMiddleX, currentMiddleY);
                    currentScale = imageView.mMaxZoom;
                    imageView.zoomToNoCenterValue(currentScale, currentMiddleX, currentMiddleY);
                } else if (currentScale < imageView.mMinZoom) {
                    imageView.zoomToNoCenterWithAni(currentScale, imageView.mMinZoom, currentMiddleX, currentMiddleY);
                    currentScale = imageView.mMinZoom;
                    imageView.zoomToNoCenterValue(currentScale, currentMiddleX, currentMiddleY);
                } else {
                    imageView.zoomToNoCenter(currentScale, currentMiddleX, currentMiddleY);
                }

                imageView.center(true, true);

                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOnScale = false;
                    }
                }, 1000);
            }

        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mOnScale = true;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector, float mx, float my) {
            if (getCurrentImageView() instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) getCurrentImageView();
                if (imageView == null) {
                    return true;
                }
                float ns = imageView.getScale() * detector.getScaleFactor();
                currentScale = ns;
                currentMiddleX = mx;
                currentMiddleY = my;

                if (detector.isInProgress()) {
                    imageView.zoomToNoCenter(ns, mx, my);
                }
            }

            return true;
        }
    }


    @Override
    protected boolean onBackPressed() {
        getActivity().finish();
        return false;
    }

    public interface InstantiateItemListener {
        void onInstantiateItemFinish(int pos);
    }

    private void resetVideoView() {
        if (mCurrentPlayerView != null) {
            mCurrentPlayerView.release();
        }

        mCurrentPlayerView = null;
        mCurrentSeekPos = 0;
        mRlVideoPlayLayout.setVisibility(View.GONE);
        mUpdateProgressHandler.removeCallbacks(mUpdateProgressRunnable);
        mIsPause = false;
        mIvVideoPauseBtn.setBackgroundResource(R.mipmap.icon_pause_video_white);
        mTvVideoTotalTime.setText("00:00");
        mTvVideoPlayTime.setText("00:00");
        mSeekVideoPlay.setProgress(0);

    }

    /**
     * Description:初始化长按弹窗
     */

    private void handleMyDownloadItemLongClick(FileData fileData) {

        FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

        List<String> items = new ArrayList<>();
        items.add(mActivity.getString(R.string.forwarding_item));
        items.add(mActivity.getString(R.string.share));
        items.add(mActivity.getString(R.string.save_to_dropbox));
        items.add(mActivity.getString(R.string.send_email));
        items.add(mActivity.getString(R.string.file_attr));
        items.add(mActivity.getString(R.string.delete));

        ArrayList<String> itemList = new ArrayList<>();
        itemList.addAll(items);
        W6sSelectDialogFragment w6sSelectDialogFragment = new W6sSelectDialogFragment();
        w6sSelectDialogFragment.setData(new CommonPopSelectData(itemList, null));
        w6sSelectDialogFragment.setDialogWidth(160);
        w6sSelectDialogFragment.setOnClickItemListener(new W6sSelectDialogFragment.OnClickItemListener() {
            @Override
            public void onClick(int position, @NotNull String value) {
                if (position == 0) {

                    List<ChatPostMessage> messages = new ArrayList<>();

                    User LoginUser = AtworkApplicationLike.getLoginUserSync();
                    long overtime = DomainSettingsManager.getInstance().handleChatFileExpiredFeature() ? TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().getChatFileExpiredDay()) : -1;
                    FileTransferChatMessage message = FileTransferChatMessage.newFileTransferChatMessage(AtworkApplicationLike.baseContext, fileData, LoginUser, "",
                            ParticipantType.User, ParticipantType.User, "", "", "", BodyType.File, "", overtime, null);
                    message.mediaId = fileData.mediaId;
                    messages.add(message);


                    TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                    transferMessageControlAction.setSendMessageList(messages);
                    transferMessageControlAction.setSendMode(TransferMessageMode.SEND);
                    Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

                    mActivity.startActivity(intent);
                }
                if (position == 1) {
                    shareFile(fileData);
                }

                if (position == 2) {
                    FileTransferChatMessage fileTransferChatMessage = FileTransferChatMessage.getFIleTransferChatMessageFromFileData(fileData);
                    Dropbox dropboxTransfer = Dropbox.convertFromChatPostMessage(mActivity, fileTransferChatMessage);
                    Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropboxTransfer, fileTransferChatMessage);
                    mActivity.startActivityForResult(intent, UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX);
                }
                if (position == 3) {
                    Dropbox dropbox = Dropbox.convertFromFilePath(getContext(), fileData.filePath, fileData.mediaId);
                    doCommandSendEmail(mActivity, fragmentActivity.getSupportFragmentManager(), dropbox);
                }
                if (position == 4) {
                    doCommandFileAttr(fileData);
                }
                if (position == 5) {
                    deleteFile(fileData);
                }

            }

        });

        w6sSelectDialogFragment.show(fragmentManager, "TEXT_POP_DIALOG");
    }
    /**
     * 通过邮件发送
     */
    public void doCommandSendEmail(Activity context, FragmentManager fragmentManager, Dropbox dropbox) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                DropboxManager.getInstance().doCommandSendEmail(mActivity, fragmentManager, dropbox);

            }

            @Override
            public void onDenied(String permission) {
                final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mActivity, permission);
                alertDialog.setOnDismissListener(dialog -> {
                    if(alertDialog.shouldHandleDismissEvent) {
                        DropboxManager.getInstance().doCommandSendEmail(mActivity, fragmentManager, dropbox);


                    }

                });

                alertDialog.show();
            }
        });
    }

    /**
     * 查看文件属性
     */
    public void doCommandFileAttr(FileData fileData) {
        FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        DownLoadFileAttrDialog dialog = new DownLoadFileAttrDialog();
        dialog.setArguments(dialog.setData(fileData));
        dialog.show(fragmentManager, "download_attr");
    }

    /**
     * Description:删除本地数据库记录
     * @param fileData
     */
    public void deleteFile(FileData fileData){
        AtworkAlertDialog dialog = new AtworkAlertDialog(mActivity);
        dialog.setTitleText(R.string.delete_file);
        dialog.setContent(R.string.delete_file_tip);
        dialog.setBrightBtnText(R.string.ok);
        dialog.setDeadBtnText(R.string.cancel);
        dialog.setClickDeadColorListener(dialog1 -> dialog.dismiss());
        dialog.setClickBrightColorListener(dialog1 -> {
            RecentFileDaoService.getInstance().deleteDownloadFileByFileId(mHandler, fileData.mediaId, fileData.filePath);
        });
        dialog.show();
    }
    /**
     * 分享文件
     */
    public void shareFile(FileData fileData){
        FileShareAction fileShareAction = new FileShareAction();
        fileShareAction.setDomainId(AtworkConfig.DOMAIN_ID);
        fileShareAction.setOpsId(LoginUserInfo.getInstance().getLoginUserId(mActivity));
        fileShareAction.setType("file_id");
        fileShareAction.setSourceType(Dropbox.SourceType.User);
        fileShareAction.setFileId(fileData.mediaId);
        FileShareActivity.Companion.startActivity(mActivity, fileShareAction);
    }

}
